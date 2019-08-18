package com.gennan.summer.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import com.avos.avoscloud.AVOSCloud
import com.avos.avoscloud.AVUser
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMMessageManager
import com.gennan.summer.CoolChatMessageHandler
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
        sHandler = Handler()//创建全局handler
        sContext = baseContext//获取全局context
        sGson = Gson()


        //初始化Leancloud=======================================================
        // 初始化参数依次为 this，App Id，App Key
        AVOSCloud.initialize(this, "pSmpgrLy4yH2kMVqJ72TqXhT-gzGzoHsz", "vqPwr2GqEjFecNbA8KppFGhs")
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可 云端调试 感觉还用不到
        //AVOSCloud.setDebugLogEnabled(true)
        //=====================================================================

        LogUtil.init(this.packageName, false)//初始化LogUtil 设置为true后关闭log
    }

}