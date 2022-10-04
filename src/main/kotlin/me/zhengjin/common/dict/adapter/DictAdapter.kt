package me.zhengjin.common.dict.adapter

import me.zhengjin.common.dict.enums.DictNameType
import me.zhengjin.common.dict.enums.DictSearchType
import me.zhengjin.common.dict.po.Dict
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface DictAdapter {

    /**
     * @param type 字典类型, 如果支持此类型查询则返回true
     *
     * 是否支持对传入的字典类型进行查询
     */
    fun dictSupport(type: String): Boolean

    /**
     * @param searchData    字典查询时的传入内容
     * @param searchType    查询类型
     * @param dictType      字典类型
     * @param nameType      查询结果name取值
     * @param pageable      分页参数
     *
     * 字典查询, 必须实现 searchType === CODE_EXACT 精准查询适配
     */
    fun dictHandler(
        searchData: String,
        searchType: DictSearchType,
        dictType: String,
        nameType: DictNameType,
        pageable: Pageable
    ): Page<Dict.CodeName>
}
