package com.gennan.summer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gennan.summer.GlideApp
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.event.ImageItemClickEvent
import com.gennan.summer.event.StatusImageItemClickEvent
import kotlinx.android.synthetic.main.activity_photo.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        CoolChatApp.getAppEventBus().register(this)

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onImageItemClickEventHandle(event: ImageItemClickEvent) {
        GlideApp.with(this).load(event.imageUrl).into(photo_view)
        CoolChatApp.getAppEventBus().removeStickyEvent(ImageItemClickEvent::class.java)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onStatusImageItemClickEventHandle(event: StatusImageItemClickEvent) {
        GlideApp.with(this).load(event.url).into(photo_view)
        CoolChatApp.getAppEventBus().removeStickyEvent(StatusImageItemClickEvent::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        CoolChatApp.getAppEventBus().unregister(this)
    }
}
