package me.zhengjin.common.dict.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import me.zhengjin.common.core.utils.IdEncryptionUtils
import me.zhengjin.common.dict.annotation.Dict
import me.zhengjin.common.dict.enums.DictSerializeEnum
import me.zhengjin.common.dict.utils.DictCacheUtils
import org.slf4j.LoggerFactory

/**
 * 字典序列化
 * @author fangzhengjin
 * @create 2022-09-30 16:48
 **/
class DictSerializer(
    private val dict: Dict? = null
) : JsonSerializer<Any>(), ContextualSerializer {
    companion object {
        private val logger = LoggerFactory.getLogger(DictSerializer::class.java)
    }

    override fun serialize(value: Any?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (dict == null) {
            logger.error("dict annotation not found")
            return
        }
        if (value == null || "null" == value) {
            gen?.writeNull()
            return
        }
        if (dict.separator.isBlank()) {
            val codeName = DictCacheUtils.get(dict.type, dict.nameType, value.toString(), dict.searchType)
            when (dict.serialize) {
                DictSerializeEnum.DICT_DATA -> gen?.writeObject(codeName)
                DictSerializeEnum.CODE_STRING -> gen?.writeString(codeName.code)
                DictSerializeEnum.NAME_STRING -> gen?.writeString(codeName.name)
                DictSerializeEnum.ID_STRING -> {
                    if (codeName.id == null) {
                        gen?.writeNull()
                    } else {
                        gen?.writeString(IdEncryptionUtils.encrypt(codeName.id!!))
                    }
                }
            }
        } else {
            val codeNames = value.toString().split(dict.separator).map {
                DictCacheUtils.get(dict.type, dict.nameType, it, dict.searchType)
            }
            when (dict.serialize) {
                DictSerializeEnum.DICT_DATA -> gen?.writeObject(codeNames)
                DictSerializeEnum.CODE_STRING -> gen?.writeString(codeNames.joinToString(dict.separator) { it.code!! })
                DictSerializeEnum.NAME_STRING -> gen?.writeString(codeNames.joinToString(dict.separator) { it.name!! })
                DictSerializeEnum.ID_STRING -> gen?.writeString(codeNames.joinToString(dict.separator) { IdEncryptionUtils.encrypt(it.id!!) })
            }
        }
    }

    override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*> {
        val annotation = property?.getAnnotation(Dict::class.java)
        return DictSerializer(annotation)
    }
}
