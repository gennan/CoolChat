package com.gennan.summer.bean

/**
 *Created by Gennan on 2019/8/17.
 * 找了好久 踩了好多坑才搞了个这个 先用着了
 */
data class AVIMMessageBean(
    val _lcattrs: Lcattrs,
    val _lcfile: Lcfile,
    val _lctext: String,//消息的文字说明  文字消息的话就是文本内容
    val _lctype: Int//消息的类型 -1:文本    -2:图像      -3:音频      -4:视频
)

data class Lcfile(
    val metaData: MetaData,
    val objId: String,
    val url: String//图像 音频 视频的url
)

data class MetaData(
    val format: String,
    val height: Int,
    val name: String,
    val size: Int,
    val width: Int
)

data class Lcattrs(
    val a: String,
    val b: Boolean,
    val c: Int
)