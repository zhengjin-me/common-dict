package me.zhengjin.common.dict.enums

import me.zhengjin.common.utils.SpringBeanUtils

/**
 * 字典序列化方式
 * @author fangzhengjin
 * @create 2022-09-30 16:58
 **/
enum class DictSerializeEnum {
    STRING {
        override fun serialize(type: String, code: String, separator: String): Any {
            SpringBeanUtils.getBean("")
            return ""
        }
    },
    DICT_DATA {
        override fun serialize(type: String, code: String, separator: String): Any {
            return ""
        }
    };

    abstract fun serialize(type: String, code: String, separator: String): Any
}
