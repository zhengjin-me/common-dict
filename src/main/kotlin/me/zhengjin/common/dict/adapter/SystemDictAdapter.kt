package me.zhengjin.common.dict.adapter

import com.querydsl.core.types.Projections
import me.zhengjin.common.core.jpa.JpaHelper
import me.zhengjin.common.core.jpa.querydsl.applyPagination
import me.zhengjin.common.core.jpa.querydsl.fetchPage
import me.zhengjin.common.dict.enums.DictNameType
import me.zhengjin.common.dict.enums.DictSearchType
import me.zhengjin.common.dict.po.Dict
import me.zhengjin.common.dict.po.QDict
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 *
 * @author fangzhengjin
 * @create 2022-10-03 01:38
 **/
@Service
class SystemDictAdapter : DictAdapter {
    private val dictDomain = QDict.dict

    override fun dictSupport(type: String): Boolean {
        return type.startsWith("system_")
    }

    /**
     * @param searchData    字典查询时的传入内容
     * @param searchType    查询类型
     * @param dictType      字典类型
     * @param nameType      查询结果name取值
     * @param pageable      分页参数
     *
     * 字典查询, 必须实现 searchType === CODE_EXACT 精准查询适配
     */
    override fun dictHandler(
        searchData: String?,
        searchType: DictSearchType,
        dictType: String,
        nameType: DictNameType,
        pageable: Pageable
    ): Page<Dict.CodeName> {
        var condition = dictDomain.delete.isFalse.and(dictDomain.type.eq(dictType))
        if (!searchData.isNullOrBlank()) {
            condition = condition.and(
                when (searchType) {
                    DictSearchType.CODE_EXACT -> dictDomain.code.eq(searchData)
                    DictSearchType.CODE_LIKE -> dictDomain.code.like("%$searchData%")
                    DictSearchType.NAME_EXACT -> dictDomain.name.eq(searchData)
                    DictSearchType.NAME_LIKE -> dictDomain.name.like("%$searchData%")
                    DictSearchType.NONE -> dictDomain.code.like("%$searchData%").or(dictDomain.name.like("%$searchData%"))
                }
            )
        }
        return JpaHelper.getJPAQueryFactory()
            .select(
                Projections.bean(
                    Dict.CodeName::class.java,
                    dictDomain.code.`as`("code"),
                    when (nameType) {
                        DictNameType.CODE -> dictDomain.code.`as`("name")
                        DictNameType.NAME -> dictDomain.name.`as`("name")
                        DictNameType.ENGLISH -> dictDomain.englishName.`as`("name")
                    }
                )
            )
            .from(dictDomain)
            .where(condition)
            .applyPagination(pageable)
            .fetchPage()
    }
}
