package com.gennan.summer.bean

/**
 *Created by Gennan on 2019/8/17.
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


// {
//    "_lctype":    -2,                    //必要参数
//    "_lctext":    "图像的文字说明",
//    "_lcattrs": {
//      "a":        "_lcattrs 是用来存储用户自定义的一些键值对",
//      "b":        true,
//      "c":        12
//    },
//    "_lcfile": {
//      "url":      "http://ac-p2bpmgci.clouddn.com/246b8acc-2e12-4a9d-a255-8d17a3059d25", //必要参数
//      "objId":    "54699d87e4b0a56c64f470a4//文件对应的AVFile.objectId",
//      "metaData": {
//        "name":   "IMG_20141223.jpeg",   //图像的名称
//        "format": "png",                 //图像的格式
//        "height": 768,                   //单位：像素
//        "width":  1024,                  //单位：像素
//        "size":   18                     //单位：b
//      }
//    }
//  }