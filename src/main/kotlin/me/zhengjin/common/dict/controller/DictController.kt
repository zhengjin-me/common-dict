package me.zhengjin.common.dict.controller

import me.zhengjin.common.core.entity.HttpResult
import me.zhengjin.common.core.entity.PageResult
import me.zhengjin.common.dict.controller.vo.DictSearchVO
import me.zhengjin.common.dict.po.Dict
import me.zhengjin.common.dict.service.DictService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 数据字典
 */
@RestController
@RequestMapping("/dict", produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class DictController(
    private val dictService: DictService
) {
    @PostMapping("/list")
    fun dictList(@RequestBody vo: DictSearchVO): HttpResult<PageResult<Dict>> {
        return HttpResult.page(dictService.list(vo))
    }

    @PostMapping("/{type}/list")
    fun dictList(@RequestBody vo: DictSearchVO?, @PathVariable type: String?): HttpResult<PageResult<Dict.CodeName>> {
        val dictSearchData by lazy {
            vo ?: DictSearchVO()
        }
        dictSearchData.type = type
        return HttpResult.page(dictService.dictSearch(dictSearchData))
    }

    @PostMapping("/saveOrUpdate")
    fun saveOrUpdate(@RequestBody vo: Dict): HttpResult<String> {
        dictService.saveOrUpdateDictOrType(vo)
        return HttpResult.ok("操作成功")
    }

    @PostMapping("/delete")
    fun delete(@RequestBody ids: List<Long>): HttpResult<String> {
        dictService.removeDictOrType(ids)
        return HttpResult.ok("操作成功")
    }

    @GetMapping("/check")
    fun check(
        @RequestParam checkType: String,
        @RequestParam(required = false) dictType: String?,
        @RequestParam(required = false) data: String?,
        @RequestParam(required = false) id: Long?
    ): HttpResult<String> {
        dictService.check(checkType, dictType, data, id)
        return HttpResult.ok(null)
    }
}
