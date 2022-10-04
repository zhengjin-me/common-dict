package me.zhengjin.common.dict

import cn.hutool.cache.CacheUtil
import me.zhengjin.common.dict.controller.vo.DictSearchVO
import me.zhengjin.common.dict.enums.DictNameType
import me.zhengjin.common.dict.enums.DictSearchType
import me.zhengjin.common.dict.po.Dict
import me.zhengjin.common.dict.service.DictService
import me.zhengjin.common.utils.SpringBeanUtils

/**
 *
 * @author fangzhengjin
 * @create 2022-09-30 17:56
 **/
object DictCacheUtils {
    // 外层key字典code 内层key字典项code
    private val dictCache = CacheUtil.newLRUCache<String, MutableMap<String, Dict.CodeName>>(1000)

    fun put(dictType: String, nameType: DictNameType, data: Dict.CodeName) {
        val key = "${data.code}|${nameType.name}"
        // 字典类型+返回类型存在
        if (dictCache.get(dictType) != null) {
            dictCache.get(dictType)[key] = data
        } else {
            dictCache.put(dictType, mutableMapOf(key to data))
        }
    }

    fun get(dictType: String, nameType: DictNameType, dictCode: String): Dict.CodeName {
        val cache = dictCache.get(dictType)
        if (cache != null) {
            val key = "$dictCode|${nameType.name}"
            if (cache.containsKey(key)) {
                return cache[key]!!
            }
        }
        // 无缓存
        val dictService = SpringBeanUtils.getBean(DictService::class.java)
        val result = dictService.dictSearch(DictSearchVO(type = dictType, nameType = nameType, searchType = DictSearchType.CODE_EXACT, searchData = dictCode))
        if (result.isEmpty) {
            throw IllegalArgumentException("未查询到字典[$dictType]中存在code=[$dictCode]的信息")
        }
        put(dictType, nameType, result.first())
        return result.first()
    }

    fun remove(dictType: String) {
        dictCache.remove(dictType)
    }
}
