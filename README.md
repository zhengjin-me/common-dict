# common-dict

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/zhengjin-me/common-dict/Gradle%20Package?style=flat-square)
[![Maven Central](https://img.shields.io/maven-central/v/me.zhengjin/common-dict.svg?style=flat-square&color=brightgreen)](https://maven-badges.herokuapp.com/maven-central/me.zhengjin/common-dict/)
![GitHub](https://img.shields.io/github/license/zhengjin-me/common-dict?style=flat-square)

```
dependencies {
    implementation "me.zhengjin:common-dict:version"
}
```

### 自定义字典内容实现方式

实现`DictAdapter`接口, type为`system_`开头才会走系统查询, 否则转入适配器查询

```kotlin
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
     * @param pageable      分页参数
     *
     * 字典查询, 必须实现 searchType === CODE_EXACT 精准查询适配
     */
    fun dictHandler(searchData: String, searchType: DictSearchType, dictType: String, pageable: Pageable): Page<Dict.CodeName>
```