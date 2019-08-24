package com.gennan.summer.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.avos.avoscloud.AVOSCloud
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMMessageManager
import com.gennan.summer.util.LogUtil
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

/**
 *Created by Gennan.
 */
class CoolChatApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var sContext: Context? = null
        private var sGson: Gson? = null
        var avUser: AVUser? = null
        var avImClient: AVIMClient? = null
        var openedClient: AVIMClient? = null
        var isAutoLogin = true//是否自动登录

        fun getAppContext(): Context? {
            return sContext
        }


        fun getAppGson(): Gson? {
            return sGson
        }

        fun getAppEventBus(): EventBus {
            return EventBus.getDefault()
        }
    }

    override fun onCreate() {
        super.onCreate()
        sContext = baseContext//获取全局Context
        sGson = Gson()
        //初始化Leancloud=======================================================
        // 初始化参数依次为 this，App Id，App Key
        AVOSCloud.initialize(this, "pSmpgrLy4yH2kMVqJ72TqXhT-gzGzoHsz", "vqPwr2GqEjFecNbA8KppFGhs")
        AVOSCloud.setDebugLogEnabled(false)
        AVIMMessageManager.registerDefaultMessageHandler(CoolChatMessageEventHandler())
        AVIMMessageManager.setConversationEventHandler(CoolChatConversationEventHandler())
        AVIMClient.setUnreadNotificationEnabled(true)//设置之后AVIMConversation会维护未读消息的数目
        //=====================================================================
        LogUtil.init(this.packageName, false)//初始化LogUtil 设置为true后关闭Log
    }
}

