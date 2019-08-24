package com.gennan.summer.util

/**
 *Created by Gennan on 2019/8/15.
 */
class Constants {
    companion object {
        //APP的名字
        const val APP_NAME = "CoolChat"
        //代表mainActivity三个界面的整数值
        const val NULL_FRAGMENT = -1
        const val MESSAGE_FRAGMENT = 0
        const val USER_FRAGMENT = 1
        const val SETTING_FRAGMENT = 2
        //默认头像
        const val DEFAULT_ICON = "http://lc-psmpgrly.cn-n1.lcfile.com/cYl7XJJbUNLgc9znnupkO9an3jBSUCToWDFTuTtk.png"
        //默认每次加载对话的消息条数
        const val MESSAGE_NUMBER_LOADED = 10
        //默认每次在对话中下拉刷新的对话条数
        const val MESSAGE_NUMBER_REFRESH = 15
        //最多可以选择的图片数量
        const val IMG_CAN_CHOOSE = 1
        //FileProvider
        const val FILE_PROVIDER_AUTHORITY = "com.gennan.summer.fileprovider"
        //空字符串
        const val BLANK_STRING = ""
        //消息类型
        const val TEXT_MSG_TYPE = -1
        const val IMG_MSG_TYPE = -2
        const val AUDIO_MSG_TYPE = -3
    }
}