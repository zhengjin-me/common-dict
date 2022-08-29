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

实现`DictAdapter`接口

```kotlin
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
```