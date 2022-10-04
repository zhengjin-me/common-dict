package me.zhengjin.common.dict.enums

/**
 * 适配器查询类型
 * @author fangzhengjin
 * @create 2022-09-30 17:25
 **/
enum class DictSearchType {
    /**
     * CODE 精准查询
     */
    CODE_EXACT,

    /**
     * CODE 模糊查询
     */
    CODE_LIKE,

    /**
     * NAME 精准查询
     */
    NAME_EXACT,

    /**
     * NAME 模糊查询
     */
    NAME_LIKE,

    /**
     * 未指定
     */
    NONE
}
