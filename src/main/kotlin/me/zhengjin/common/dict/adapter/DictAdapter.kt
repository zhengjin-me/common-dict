package me.zhengjin.common.dict.adapter

import me.zhengjin.common.dict.controller.vo.DictSearchVO
import me.zhengjin.common.dict.po.Dict
import org.springframework.data.domain.Page

interface DictAdapter {

    /**
     * @param type 字典类型, 如果支持此类型查询则返回true
     *
     * 是否支持对传入的字典类型进行查询
     */
    fun support(type: String): Boolean

    /**
     * @param dictSearchVO 字典查询时的传入内容, 实现类应尽可能只使用searchData作为查询项目, 字典分页时, 通过getPageable获取分页参数
     *
     * 字典查询
     */
    fun handler(dictSearchVO: DictSearchVO): Page<Dict.CodeName>
}
