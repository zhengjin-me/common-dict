package me.zhengjin.common.dict.controller.vo

import me.zhengjin.common.core.entity.PageableVO

class DictSearchVO : PageableVO() {
    /**
     * type为null时   => 字典类型
     * type不为null时 => 字典code
     */
    var code: String? = null

    var codes: List<String> = ArrayList()

    /**
     * 字典中文名称
     */
    var name: String? = null

    /**
     * 字典英文名称
     */
    var englishName: String? = null

    /**
     * type留空为顶层字典类型
     */
    var type: String? = null

    var searchData: String? = null

    var nameType: String = "name"
}
