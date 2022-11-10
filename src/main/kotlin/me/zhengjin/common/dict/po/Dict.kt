package me.zhengjin.common.dict.po

import me.zhengjin.common.core.encryptor.annotation.IdDecrypt
import me.zhengjin.common.core.encryptor.annotation.IdEncrypt
import me.zhengjin.common.core.entity.BaseEntity
import me.zhengjin.common.core.jpa.comment.annotation.JpaComment
import me.zhengjin.common.dict.enums.DictNameType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * 数据字典
 */
@Entity
@Table(name = "dict")
@JpaComment("数据字典")
class Dict : BaseEntity() {
    /**
     * type为null时   => 字典类型
     * type不为null时 => 字典code
     */
    @JpaComment("字典类型/code")
    @Column(name = "code")
    var code: String? = null

    /**
     * 字典中文名称
     */
    @JpaComment("字典中文名称")
    @Column(name = "name")
    var name: String? = null

    /**
     * 字典英文名称
     */
    @JpaComment("字典英文名称")
    @Column(name = "english_name")
    var englishName: String? = null

    /**
     * type留空为顶层字典类型
     */
    @JpaComment("type留空为顶层字典类型")
    @Column(name = "type")
    var type: String? = null

    /**
     * 排序
     */
    @JpaComment("排序")
    @Column(name = "sort")
    var sort: Int? = null

    /**
     * 备注
     */
    @JpaComment("备注")
    @Column(name = "remark")
    var remark: String? = null

    class CodeName(
        var code: String? = null,
        var name: String? = null,
        @field:[
            IdEncrypt
            IdDecrypt
        ]
        var id: Long? = null
    )

    fun generatorCodeName(nameType: DictNameType?): CodeName {
        return when (nameType) {
            DictNameType.ENGLISH -> CodeName(code, englishName, id)
            DictNameType.CODE -> CodeName(code, code, id)
            DictNameType.NAME -> CodeName(code, name, id)
            else -> CodeName(code, name, id)
        }
    }
}
