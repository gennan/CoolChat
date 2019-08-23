package com.gennan.summer.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import com.avos.avoscloud.AVOSCloud
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMMessageManager
import com.gennan.summer.CoolChatConversationEventHandler
import com.gennan.summer.CoolChatMessageEventHandler
import com.gennan.summer.util.LogUtil
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class CoolChatApp : Application() {

    companion object {
        private var sHandler: Handler? = null
        @SuppressLint("StaticFieldLeak")
        private var sContext: Context? = null
        private var sGson: Gson? = null
        var avUser: AVUser? = null
        var avImClient: AVIMClient? = null
        var openedClient: AVIMClient? = null
        var isAutoLogin = true

        fun getAppContext(): Context? {
            return sContext
        }

        fun getAppHandler(): Handler? {
            return sHandler
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
        sHandler = Handler()//创建全局Handler
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

