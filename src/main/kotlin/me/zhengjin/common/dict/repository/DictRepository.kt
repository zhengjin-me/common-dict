package me.zhengjin.common.dict.repository

import me.zhengjin.common.core.repository.QuerydslBaseRepository
import me.zhengjin.common.dict.po.Dict

interface DictRepository : QuerydslBaseRepository<Dict, Long> {

    fun findAllByTypeNullAndDeleteFalse(): List<Dict>

    fun findByIdAndDeleteFalse(id: Long): Dict?

    fun findAllByIdInAndDeleteFalse(ids: List<Long>): List<Dict>

    fun findByTypeAndDeleteFalse(type: String): List<Dict>

    fun findByCodeAndTypeAndDeleteFalse(code: String, type: String): Dict?

    fun findAllByNameAndTypeAndDeleteFalse(name: String, type: String): List<Dict>

    fun findAllByNameIsAndTypeIsAndDeleteFalse(name: String, type: String): Dict?

    fun findAllByEnglishNameAndTypeAndDeleteFalse(englishName: String, type: String): Dict?

    fun findByCodeInAndTypeAndDeleteFalse(codes: List<String>, type: String): List<Dict>
}
