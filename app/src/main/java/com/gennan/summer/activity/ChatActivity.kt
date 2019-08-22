package com.gennan.summer.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.avos.avoscloud.AVFile
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage
import com.gennan.summer.MyGlideEngine
import com.gennan.summer.R
import com.gennan.summer.adapter.ChatAdapter
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.bean.AVIMMessageBean
import com.gennan.summer.event.*
import com.gennan.summer.mvp.contract.IChatViewCallback
import com.gennan.summer.mvp.presenter.ChatPresenter
import com.gennan.summer.util.LogUtil
import com.gennan.summer.util.UriToRealPathUtil.getRealFilePath
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ChatActivity : BaseActivity(), IChatViewCallback, ChatAdapter.OnVoiceItemClickListener {
    var objectId: String? = null
    var oldestMsg: AVIMMessage = AVIMMessage()
    var messageList = mutableListOf<AVIMMessage>()
    private val chatPresenter = ChatPresenter.instance
    private val TAG = "ChatActivity"
    lateinit var chatAdapter: ChatAdapter
    private val IMG_REQUEST_CODE = 0
    var sendVoiceLongClick = false
    lateinit var conversation: AVIMConversation//todo：这里使用lateinit 快速点击时会没有赋值 可以通过添加dialog来解决这个快速点击没有赋值的情况

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        CoolChatApp.getAppEventBus().register(this)
        chatPresenter.attachViewCallback(this)
        initView()
        chatPresenter.getFirstTenMsg(
            10, conversation
        )
        initEvent()
    }

    private fun initView() {
        val manager = LinearLayoutManager(this)
        rv_chat.layoutManager = manager
        chatAdapter = ChatAdapter(messageList, this)
        rv_chat.adapter = chatAdapter
        chatAdapter.setOnVoiceItemClickListener(this)
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
            tv_press_to_say.text = "松开发送语音"
            sendVoiceLongClick = true
            LogUtil.d(TAG, "长按成功")
            chatPresenter.startRecordAudio(this)
            false//加入短按的点击事件就是停止语音的录制
        }
        rounded_image_view_press_to_say.setOnClickListener {
            if (sendVoiceLongClick) {
                LogUtil.d(TAG, "短按成功")
                chatPresenter.stopRecordAudio()
                sendVoiceLongClick = false
            }
        }
        //这个发送负责发送文字消息
        tv_send_message_chat.setOnClickListener {
            if (et_send_message_chat.text.toString().trim() == "" || et_send_message_chat.text.trim().isEmpty() || et_send_message_chat.text == null) {
                //就是发送的消息为空 感觉也没有要弹Toast的必要
            } else {
                val messageWillSend: String = et_send_message_chat.text.toString()
                LogUtil.d("zz", "message ----> $messageWillSend")
                if (CoolChatApp.avImClient != null) {
                    chatPresenter.sendTextMessage(messageWillSend, conversation)
                }
            }
        }
        //下拉刷新
        swipe_refresh_layout_activity_chat.setOnRefreshListener {
            chatPresenter.getMsgHistory(
                oldestMsg,
                15,
                conversation
            )
        }
        //设置点击发送图片
        iv_send_img_chat.setOnClickListener {
            Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(false)//勾选后不显示数字 显示勾号
                .maxSelectable(1)
                .capture(true)//选择照片时，是否显示拍照
                .captureStrategy(CaptureStrategy(true, "com.gennan.summer.fileprovider"))
                .theme(R.style.CoolChatMatisse)
                .imageEngine(MyGlideEngine())
                .forResult(IMG_REQUEST_CODE)
        }
        //设置点击返回消息列表
        iv_back_chat.setOnClickListener {
            finish()
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFriendItemClickEvent(event: FriendItemClickEvent) {
        conversation = event.conversation!!
        CoolChatApp.getAppEventBus().removeStickyEvent(FriendItemClickEvent::class.java)
    }

    /**
     * 接收从MainActivity那边client.open的事件 然后开始接收聊天信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onClientOpenEvent(event: ClientOpenEvent) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onConversationObjectEvent(event: ConversationObjectEvent) {
        if (event.conversation != null) {
            conversation = event.conversation
            CoolChatApp.getAppEventBus().removeStickyEvent(ConversationObjectEvent::class.java)
            LogUtil.d(TAG, "ConversationId ----> ${conversation.conversationId}")
        }
    }

    override fun onTextMessageSendSucceeded(msg: AVIMMessage) {
        et_send_message_chat.setText("")
        messageList.add(msg)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        rv_chat.scrollToPosition(messageList.size - 1)
        LogUtil.d("ChatActivity", "消息发送成功")
    }

    override fun onTextMessageSendFailed() {
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
        if (event.conversation == conversation) {
            messageList.add(event.message)
            chatAdapter.notifyItemInserted(messageList.size - 1)
            rv_chat.scrollToPosition(messageList.size - 1)
            LogUtil.d("ChatActivity", "成功接收到新消息")
        }
    }

    /**
     * 选择图片返回的结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = Matisse.obtainResult(data)
            val filePath = getRealFilePath(this, result[0])
            val fileName =
                filePath?.substring(filePath.lastIndexOf("/") + 1, filePath.length)
            val imgFile = AVFile.withAbsoluteLocalPath(fileName, filePath)
            val avimImageMessage = AVIMImageMessage(imgFile)
            if (CoolChatApp.avImClient != null) {
                chatPresenter.sendImgMessage(avimImageMessage, conversation)
            }
        }
    }

    override fun onImgMessageSendSucceeded(msg: AVIMMessage) {
        messageList.add(msg)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        rv_chat.scrollToPosition(messageList.size - 1)
        LogUtil.d(TAG, "图像消息发送成功")
        Toast.makeText(this, "图片发送成功", Toast.LENGTH_SHORT).show()
    }

    override fun onImgMessageSendFailed() {
        LogUtil.d(TAG, "图像消息发送失败")
        Toast.makeText(this, "图片发送失败 请重新试试", Toast.LENGTH_SHORT).show()
    }

    override fun onAudioMessageSendSucceeded(msg: AVIMMessage) {
        messageList.add(msg)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        rv_chat.scrollToPosition(messageList.size - 1)
        LogUtil.d(TAG, "语音发送成功")
    }

    override fun onAudioMessageSendFailed() {
        LogUtil.d(TAG, "语音消息发送失败")
        Toast.makeText(this, "语音发送失败 请重新试试", Toast.LENGTH_SHORT).show()
    }

    override fun onAudioFilePathGetSucceeded(filePath: String) {
        tv_press_to_say.text = "按住说话"
        LogUtil.d(TAG, "获取到了语音 ----> $filePath")
        val fileName =
            filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
        val file = AVFile.withAbsoluteLocalPath(fileName, filePath)
        val audioMessage = AVIMAudioMessage(file)
        chatPresenter.sendAudioMessage(audioMessage, conversation)
    }

    override fun onVoiceItemPlayStop() {
        chatPresenter.stopReceivedAudioMessage()
    }

    override fun onVoiceItemPlayStart(msgBean: AVIMMessageBean) {
        chatPresenter.playReceivedAudioMessage(msgBean)
    }
}