package me.zhengjin.common.dict.service

import me.zhengjin.common.core.exception.ServiceException
import me.zhengjin.common.dict.adapter.DictAdapter
import me.zhengjin.common.dict.controller.vo.DictSearchVO
import me.zhengjin.common.dict.po.Dict
import me.zhengjin.common.dict.po.QDict
import me.zhengjin.common.dict.repository.DictRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import javax.persistence.criteria.Predicate

@Service
class DictService(
    private val dictRepository: DictRepository,
    private val dictAdapters: List<DictAdapter>
) {

    private val dictDomain = QDict.dict

    fun getDictNameByCode(code: String?, type: String): String? {
        if (code.isNullOrBlank()) return null
        return dictRepository.findByCodeAndTypeAndDeleteFalse(code, type)?.name
    }

    fun list(vo: DictSearchVO): Page<Dict> {
        return dictRepository.findAll(
            { r, q, cb ->
                val predicates: MutableList<Predicate> = mutableListOf()
                if (!vo.type.isNullOrBlank()) {
                    predicates.add(cb.equal(r.get<String>("type"), vo.type))
                } else {
                    predicates.add(cb.isNull(r.get<String>("type")))
                }

                if (!vo.name.isNullOrBlank()) {
                    predicates.add(cb.like(r.get<String>("name"), "%${vo.name}%"))
                }

                if (!vo.englishName.isNullOrBlank()) {
                    predicates.add(cb.like(r.get<String>("englishName"), "%${vo.englishName}%"))
                }

                if (!vo.searchData.isNullOrBlank()) {
                    predicates.add(
                        cb.or(
                            cb.like(r.get<String>("code"), "%${vo.searchData}%"),
                            cb.like(r.get<String>("name"), "%${vo.searchData}%"),
                            cb.like(r.get<String>("englishName"), "%${vo.searchData}%" + "%")
                        )
                    )
                }

                // 如果提供了所有者信息则在自有词典与系统词典中共同查找, 如未提供则只查询系统词典
                // if (AuthUtils.isAuthenticated() && !AuthUtils.isSystemAdmin()) {
                //     // 如果只是查询字典项 允许查询系统词典
                //     if (!StringUtils.isEmpty(vo.type)) {
                //         predicates.add(r.get<String>("owner").`in`("system", AuthUtils.currentTenant().id))
                //     } else {
                //         predicates.add(cb.equal(r.get<String>("owner"), AuthUtils.currentTenant().id))
                //     }
                // } else {
                //     predicates.add(cb.equal(r.get<String>("owner"), "system"))
                // }

                predicates.add(cb.isFalse(r.get<Boolean>("delete")))
                q.where(cb.and(*predicates.toTypedArray()))
                q.orderBy(cb.asc(r.get<Int>("sort")), cb.asc(r.get<Date>("createdTime")))
                q.restriction
            },
            vo.getPageable()
        )
    }

    /**
     * 查询字典项
     * 当未找到自定义字典项时, 使用系统字典查询
     */
    fun dictSearch(vo: DictSearchVO): Page<Dict.CodeName> {
        dictAdapters.forEach {
            if (it.support(vo.type!!)) {
                return it.handler(vo)
            }
        }
        val dict = list(vo)
        val dictContent = dict.content.map { x -> x.generatorCodeName(vo.nameType) }
        return PageImpl(dictContent, vo.getPageable(), dict.totalElements)
    }

    /**
     * 新增或修改
     */
    @Transactional
    fun saveOrUpdateDictOrType(vo: Dict) {
        ServiceException.requireNotNullOrBlank(vo.code) { "代码不能为空!" }
        ServiceException.requireNotNullOrBlank(vo.name) { "中文名称不能为空!" }
        if (vo.id.isNullOrBlank()) {
            check("code", vo.type, vo.code)
            // 如果是字典类型 自动处理排序序号
            if (vo.type.isNullOrBlank()) {
                val list = dictRepository.findAllByTypeNullAndDeleteFalse()
                vo.sort = list.size + 1
            }
            dictRepository.save(vo)
        } else {
            val dict = dictRepository.findByIdAndDeleteFalse(vo.id!!) ?: throw ServiceException(message = "dict can not be found", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
            dict.code = vo.code
            dict.name = vo.name
            dict.englishName = vo.englishName
            dict.type = vo.type
            dict.sort = vo.sort
            dict.remark = vo.remark
            dictRepository.save(dict)
        }
    }

    /**
     * 移除
     */
    @Transactional
    fun removeDictOrType(ids: List<String>) {
        dictRepository.softDelete(ids.toMutableList())
    }

    fun check(checkType: String, dictType: String?, data: String?, id: String? = null) {
        if (data.isNullOrBlank()) return
        ServiceException.requireNotNullOrBlank(checkType) { "类型不能为空" }
        if (!arrayOf("code", "name", "englishName").contains(checkType)) {
            throw IllegalStateException("不支持的类型: $checkType")
        }
        var expression = dictDomain.delete.isFalse.and(dictDomain.type.eq(dictType))
        expression = when (checkType) {
            "code" -> expression.and(dictDomain.code.eq(data))
            "name" -> expression.and(dictDomain.name.eq(data))
            "englishName" -> expression.and(dictDomain.englishName.eq(data))
            else -> throw ServiceException(message = "不支持的类型: $checkType", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
        }
        if (!id.isNullOrBlank()) {
            expression = expression.and(dictDomain.id.ne(id))
        }
        val exists = dictRepository.exists(expression)
        if (exists) {
            when (checkType) {
                "code" -> throw ServiceException(message = "代码 [$data] 已存在", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
                "name" -> throw ServiceException(message = "中文名称 [$data] 已存在", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
                "englishName" -> throw ServiceException(message = "英文名称 [$data] 已存在", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
                else -> throw ServiceException(message = "不支持的类型: $checkType", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
            }
        }
    }
}
