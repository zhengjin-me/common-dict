package me.zhengjin.common.dict.enums

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 字典查询结果返回的name定制
 * @author fangzhengjin
 * @create 2022-09-30 17:45
 **/
enum class DictNameType {
    @JsonProperty("code")
    CODE,

    @JsonProperty("name")
    NAME,

    @JsonProperty("english")
    ENGLISH;
}
