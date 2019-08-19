package com.gennan.summer.activity

import android.content.Context
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.gennan.summer.R
import com.gennan.summer.adapter.ChatAdapter
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.event.ClientOpenEvent
import com.gennan.summer.event.ConversationObjectEvent
import com.gennan.summer.event.ConversationTitleEvent
import com.gennan.summer.event.NewMessageEvent
import com.gennan.summer.mvp.contract.IChatViewCallback
import com.gennan.summer.mvp.presenter.ChatPresenter
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ChatActivity : BaseActivity(), IChatViewCallback {

    var oldestMsg: AVIMMessage = AVIMMessage()
    var messageList = mutableListOf<AVIMMessage>()
    var conversationList = mutableListOf<AVObject>()
    var position = 0
    private val chatPresenter = ChatPresenter.instance
    private val TAG = "ChatActivity"
    lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        CoolChatApp.getAppEventBus().register(this)
        chatPresenter.attachViewCallback(this)
        initView()
        chatPresenter.getFirstTenMsg(10, CoolChatApp.avImClient!!.getConversation(conversationList[position].objectId))
        initEvent()
    }

    private fun initView() {
        val manager = LinearLayoutManager(this)
        rv_chat.layoutManager = manager
        chatAdapter = ChatAdapter(messageList)
        rv_chat.adapter = chatAdapter
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
        //设置点击到EditText时的事件的处理
        et_send_message_chat.setOnClickListener {
            //如果当前情况下的录制语音的那个界面是展开的 就让它点击到EditText的时候消失
            if (rl_press_to_say_voice.isVisible) {
                rl_press_to_say_voice.visibility = GONE
            }
        }
        //设置长按录制语音  然后松手时发送语音
        rounded_image_view_press_to_say.setOnLongClickListener {
            //todo:从这里开始需要开始计算时间并录取声音
            LogUtil.d("ChatActivity", "长按成功")
            true
        }
        //这个发送负责发送文字消息
        tv_send_message_chat.setOnClickListener {
            if (et_send_message_chat.text.toString().trim() == "" || et_send_message_chat.text.trim().isEmpty() || et_send_message_chat.text == null) {
                //就是发送的消息为空 感觉也没有要弹Toast的必要
            } else {
                val messageWillSend: String = et_send_message_chat.text.toString()
                LogUtil.d("zz", "message ----> $messageWillSend")
                if (CoolChatApp.avImClient != null) {
                    val conversation = CoolChatApp.avImClient!!.getConversation(conversationList[position].objectId)
                    chatPresenter.sendMessage(messageWillSend, conversation)
                }
            }
        }
        //下拉刷新
        swipe_refresh_layout_activity_chat.setOnRefreshListener {
            chatPresenter.getMsgHistory(
                oldestMsg,
                15,
                CoolChatApp.avImClient!!.getConversation(conversationList[position].objectId)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CoolChatApp.getAppEventBus().unregister(this)
        chatPresenter.unAttachViewCallback(this)
    }

    /**
     * 从MessageFragment那边传过来的标题
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onConversationTitleEvent(event: ConversationTitleEvent) {
        tv_title_chat.text = event.conversationTitle
    }


    /**
     * 接收从MainActivity那边client.open的事件 然后开始接收聊天信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onClientOpenEvent(event: ClientOpenEvent) {
        //todo:聊天信息有坑 巨毒
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onConversationObjectEvent(event: ConversationObjectEvent) {
        conversationList = event.conversationList
        position = event.position
    }

    override fun onMessageSendSucceeded(msg: AVIMMessage) {
        et_send_message_chat.setText("")
        messageList.add(msg)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        rv_chat.scrollToPosition(messageList.size - 1)
        LogUtil.d("ChatActivity", "消息发送成功")
    }

    override fun onMessageSendFailed() {
        LogUtil.d("ChatActivity", "消息发送失败")
    }

    override fun onBackPressed() {
        //设置在语音展开的情况下返回事件的处理
        if (rl_press_to_say_voice.isVisible) {
            rl_press_to_say_voice.visibility = GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun onHistoryMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage) {
        if (messages != null) {
            this.oldestMsg = oldestMsg
            messageList.addAll(0, messages)
            for (x in 0 until messages.size) {
                chatAdapter.notifyItemInserted(x)
            }
            rv_chat.scrollToPosition(chatAdapter.itemCount)
            if (swipe_refresh_layout_activity_chat.isRefreshing) {
                swipe_refresh_layout_activity_chat.isRefreshing = false
                Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onHistoryMsgLoadedFailed(e: AVIMException?) {
        LogUtil.d(TAG, "onHistoryMsgLoadedFailed ----> $e")
    }

    override fun onHistoryMsgIsNull() {
        if (swipe_refresh_layout_activity_chat.isRefreshing) {
            swipe_refresh_layout_activity_chat.isRefreshing = false
            Toast.makeText(this, "没有更多消息了", Toast.LENGTH_SHORT).show()
        }
        LogUtil.d(TAG, "onHistoryMsgIsNull ----> 没有更多消息了")
    }

    override fun onFirstTenMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage) {
        if (messages != null) {
            this.oldestMsg = oldestMsg
//            messages.reverse()
            messageList.addAll(messages)
            for (x in 0 until messages.size - 1) {
                chatAdapter.notifyItemInserted(x)
            }
            rv_chat.scrollToPosition(chatAdapter.itemCount)
            LogUtil.d(TAG, "onFirstTenMsgLoadedSucceeded ----> 1")
        }
    }

    override fun onFirstTenMsgLoadedFailed(e: AVIMException?) {
        LogUtil.d(TAG, "onFirstTenMsgLoadedFailed ----> $e")
    }

    override fun onFirstTenMsgIsNull() {
        LogUtil.d(TAG, "第一次加载的消息为空 ----> 就是还有发送过消息")
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNewMessageEventHandle(event: NewMessageEvent) {
        if (event.conversation == CoolChatApp.avImClient!!.getConversation(conversationList[position].objectId)) {
            messageList.add(event.message)
            chatAdapter.notifyItemInserted(messageList.size - 1)
            rv_chat.scrollToPosition(messageList.size - 1)
            LogUtil.d("ChatActivity", "成功接收到新消息")
        }
    }

}