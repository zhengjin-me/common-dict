package me.zhengjin.common.dict.annotation

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import me.zhengjin.common.dict.enums.DictNameType
import me.zhengjin.common.dict.enums.DictSearchType
import me.zhengjin.common.dict.enums.DictSerializeEnum
import me.zhengjin.common.dict.serializer.DictSerializer

/**
 * 字典注解(用于出参对象字段, 将其字典化返回)
 * @author fangzhengjin
 * @create 2022-09-30 16:23
 **/
@MustBeDocumented
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DictSerializer::class)
annotation class Dict(
    val type: String,
    val description: String = "",
    // 如果有多个值存到一个字段中, 可自主设置分隔符, 留空为不设置, 不开启
    val separator: String = "",
    // 根据那个字段匹配
    val searchType: DictSearchType = DictSearchType.CODE_EXACT,
    // 返回的name是什么
    val nameType: DictNameType = DictNameType.NAME,
    // 序列化成什么样的数据
    val serialize: DictSerializeEnum = DictSerializeEnum.DICT_DATA,
)
