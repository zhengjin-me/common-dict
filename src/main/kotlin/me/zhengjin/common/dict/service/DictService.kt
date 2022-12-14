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

                // ??????????????????????????????????????????????????????????????????????????????, ????????????????????????????????????
                // if (AuthUtils.isAuthenticated() && !AuthUtils.isSystemAdmin()) {
                //     // ??????????????????????????? ????????????????????????
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
     * ???????????????
     * ?????????????????????????????????, ????????????????????????
     */
    fun dictSearch(vo: DictSearchVO): Page<Dict.CodeName> {
        dictAdapters.forEach {
            if (it.dictSupport(vo.type!!)) {
                return it.dictHandler(vo.searchData, vo.searchType, vo.type!!, vo.nameType, vo.getPageable())
            }
        }
        return PageImpl(emptyList(), vo.getPageable(), 0)
    }

    /**
     * ???????????????
     */
    @Transactional
    fun saveOrUpdateDictOrType(vo: Dict) {
        ServiceException.requireNotNullOrBlank(vo.code) { "??????????????????!" }
        ServiceException.requireNotNullOrBlank(vo.name) { "????????????????????????!" }
        if (vo.id == null) {
            if (vo.type.isNullOrBlank()) {
                check("type", vo.code, vo.code)
            } else {
                check("code", vo.type, vo.code)
            }
            // ????????????????????? ????????????????????????
            if (vo.type.isNullOrBlank()) {
                val list = dictRepository.findAllByTypeNullAndDeleteFalse()
                vo.sort = list.size + 1
            }
            dictRepository.save(vo)
        } else {
            // ???????????????????????????
            check("code", vo.type, vo.code)
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
     * ??????
     */
    @Transactional
    fun removeDictOrType(ids: List<Long>) {
        dictRepository.softDelete(ids.toMutableList())
    }

    fun check(checkType: String, dictType: String?, data: String?, id: Long? = null) {
        if (data.isNullOrBlank()) return
        ServiceException.requireNotNullOrBlank(checkType) { "??????????????????" }
        var expression = dictDomain.delete.isFalse.and(dictDomain.type.eq(dictType))
        expression = when (checkType) {
            "code" -> expression.and(dictDomain.code.eq(data))
            "name" -> expression.and(dictDomain.name.eq(data))
            "englishName" -> expression.and(dictDomain.englishName.eq(data))
            "type" -> expression
            else -> throw ServiceException(message = "??????????????????: $checkType", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
        }
        if (id != null) {
            expression = expression.and(dictDomain.id.ne(id))
        }
        val exists = dictRepository.exists(expression)
        if (exists) {
            when (checkType) {
                "code" -> throw ServiceException(message = "?????? [$data] ?????????", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
                "name" -> throw ServiceException(message = "???????????? [$data] ?????????", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
                "englishName" -> throw ServiceException(message = "???????????? [$data] ?????????", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
                "type" -> throw ServiceException(message = "???????????? [$data] ?????????", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
                else -> throw ServiceException(message = "??????????????????: $checkType", type = ServiceException.Exceptions.ILLEGAL_ARGUMENT)
            }
        }
    }
}
