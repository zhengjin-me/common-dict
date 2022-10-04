package me.zhengjin.common.dict.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import me.zhengjin.common.dict.DictCacheUtils
import me.zhengjin.common.dict.annotation.Dict
import org.slf4j.LoggerFactory

/**
 * 字典序列化
 * @author fangzhengjin
 * @create 2022-09-30 16:48
 **/
class DictSerializer(
    private val dict: Dict? = null
) : JsonSerializer<String>(), ContextualSerializer {
    companion object {
        private val logger = LoggerFactory.getLogger(DictSerializer::class.java)
    }

    override fun serialize(value: String?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (dict == null) {
            logger.error("dict annotation not found")
            return
        }
        if (value == null) {
            return
        }
        if ("" == dict.separator) {
            gen?.writeObject(DictCacheUtils.get(dict.type, dict.nameType, value))
        } else {
            gen?.writeObject(
                value.split(dict.separator).map {
                    DictCacheUtils.get(dict.type, dict.nameType, it)
                }
            )
        }
    }

    override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*> {
        val annotation = property?.getAnnotation(Dict::class.java)
        return DictSerializer(annotation)
    }
}
