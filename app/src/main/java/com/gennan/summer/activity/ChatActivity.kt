package com.gennan.summer.activity

import android.content.Context
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {
    //这里需要传进来一个 需要和哪个人聊天的对象 eg：我和tom对象 就需要把tom传进来
    //todo：MessageFragment那边怎么获得当前的聊天列表


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        clientOpen()
        initEvent()
    }

    private fun clientOpen() {
        CoolChatApp.avImClient?.open(object : AVIMClientCallback() {
            override fun done(client: AVIMClient?, e: AVIMException?) {
                if (e == null) {
                    LogUtil.d("ChatActivity", "client 登录成功 ----> 1")
                }
            }
        })
    }

    private fun initEvent() {
        //设置点击语音显示播放按钮
        iv_voice_chat.setOnClickListener {

            //判断当前软键盘是否展开 展开的话就关闭软键盘
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                val v = window.peekDecorView()
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }

            if (rl_press_to_say_voice.isVisible) {
                rl_press_to_say_voice.visibility = GONE
            } else {
                rl_press_to_say_voice.visibility = VISIBLE
            }

        }

        //设置长按录制语音  然后松手时发送语音
        rounded_image_view_press_to_say.setOnLongClickListener {
            //todo:从这里开始需要开始计算时间并录取声音
            LogUtil.d("ChatActivity", "长按成功")
            true
        }

        //设置点击到EditText时的事件的处理
        et_send_message_chat.setOnClickListener {
            //如果当前情况下的录制语音的那个界面是展开的 就让它点击到EditText的时候消失
            if (rl_press_to_say_voice.isVisible) {
                rl_press_to_say_voice.visibility = GONE
            }
        }

        //这个发送只负责发送文字消息
        tv_send_message_chat.setOnClickListener {
            if (et_send_message_chat.text.toString() == "" && et_send_message_chat.text == null) {
                //就是发送的消息为空 感觉也没有要弹Toast的必要
            } else {
                val messageWillSend: String = et_send_message_chat.text.toString()
                //todo:当发送成功了之后就把EditText里的东西全换成""
            }
        }

    }

    override fun onBackPressed() {
        //设置在语音展开的情况下返回事件的处理
        if (rl_press_to_say_voice.isVisible) {
            rl_press_to_say_voice.visibility = GONE
        } else {
            super.onBackPressed()
        }
    }


}
