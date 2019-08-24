package com.gennan.summer.ui.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.gennan.summer.event.*
import com.gennan.summer.ui.mvp.contract.IChatViewCallback
import com.gennan.summer.ui.mvp.model.bean.AVIMMessageBean
import com.gennan.summer.ui.mvp.presenter.ChatPresenter
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.Constants.Companion.FILE_PROVIDER_AUTHORITY
import com.gennan.summer.util.Constants.Companion.IMG_CAN_CHOOSE
import com.gennan.summer.util.Constants.Companion.MESSAGE_NUMBER_LOADED
import com.gennan.summer.util.Constants.Companion.MESSAGE_NUMBER_REFRESH
import com.gennan.summer.util.LogUtil
import com.gennan.summer.util.UriToRealPathUtil.getRealFilePath
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *Created by Gennan.
 */
class ChatActivity : BaseActivity(), IChatViewCallback, ChatAdapter.OnVoiceItemClickListener,
    ChatAdapter.OnImageItemClickListener {

    private var oldestMsg: AVIMMessage = AVIMMessage()//当前情况下ChatActivity的最早的消息
    private var messageList = mutableListOf<AVIMMessage>()//对话的消息列表
    private val chatPresenter = ChatPresenter.instance
    private val tag = "ChatActivity"
    private lateinit var chatAdapter: ChatAdapter
    private val imageRequestCode = 0//选择图片时用于匹配结果的值
    private var sendVoiceLongClick = false
    //这里使用lateinit 刚进入页面时若数据还没加载会报错 通过添加dialog来解决这个快速点击没有赋值的情况
    lateinit var conversation: AVIMConversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        CoolChatApp.getAppEventBus().register(this)
        chatPresenter.attachViewCallback(this)
        initView()
        initEvent()
        chatPresenter.getFirstTenMsg(MESSAGE_NUMBER_LOADED, conversation)
    }

    override fun onDestroy() {
        super.onDestroy()
        CoolChatApp.getAppEventBus().unregister(this)
        chatPresenter.unAttachViewCallback(this)
    }

    override fun onBackPressed() {
        //设置在语音展开的情况下返回事件的处理
        if (rl_press_to_say_voice.isVisible) {
            rl_press_to_say_voice.visibility = GONE
        } else {
            super.onBackPressed()
            conversation.read()//设置已读
            CoolChatApp.getAppEventBus().postSticky(BackFromChatToMessageEvent())
        }
    }

    private fun initView() {
        val manager = LinearLayoutManager(this)
        rv_chat.layoutManager = manager
        chatAdapter = ChatAdapter(messageList, this)
        rv_chat.adapter = chatAdapter
        chatAdapter.setOnVoiceItemClickListener(this)
        chatAdapter.setOnImageItemClickListener(this)
        swipe_refresh_layout_activity_chat.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.colorPrimary)
        )
    }

    private fun initEvent() {
        //设置点击语音显示播放按钮
        iv_voice_chat.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            val rxPermissions = RxPermissions(this)
            rxPermissions
                .request(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .subscribe { granted ->
                    if (granted) {
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
                    } else {

                    }
                }
        }
        //设置点击到EditText时的事件的处理
        et_send_message_chat.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            //如果当前情况下的录制语音的那个界面是展开的 就让它点击到EditText的时候消失
            if (rl_press_to_say_voice.isVisible) {
                rl_press_to_say_voice.visibility = GONE
            }
        }
        //设置长按录制语音  然后松手时发送语音
        rounded_image_view_press_to_say.setOnLongClickListener {
            tv_press_to_say.text = "松开发送语音"
            sendVoiceLongClick = true
            LogUtil.d(tag, "长按成功 ----> 1")
            chatPresenter.startRecordAudio(this)
            false//加入短按的点击事件就是停止语音的录制
        }
        rounded_image_view_press_to_say.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            if (sendVoiceLongClick) {
                LogUtil.d(tag, "短按成功 ----> 1")
                chatPresenter.stopRecordAudio()
                sendVoiceLongClick = false
            }
        }
        //这个发送负责发送文字消息
        tv_send_message_chat.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
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
                MESSAGE_NUMBER_REFRESH,
                conversation
            )
        }
        //设置点击发送图片
        iv_send_img_chat.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            val rxPermissions = RxPermissions(this)
            rxPermissions
                .request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .subscribe { granted ->
                    if (granted) {
                        Matisse.from(this)
                            .choose(MimeType.allOf())
                            .countable(false)//勾选后不显示数字 显示勾号
                            .maxSelectable(IMG_CAN_CHOOSE)
                            .capture(true)//选择照片时，是否显示拍照
                            .captureStrategy(CaptureStrategy(true, FILE_PROVIDER_AUTHORITY))
                            .theme(R.style.CoolChatMatisse)
                            .imageEngine(MyGlideEngine())
                            .forResult(imageRequestCode)
                    } else {

                    }
                }
        }
        //设置点击返回消息列表
        iv_back_chat.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            conversation.read()//设置已读
            finish()
        }
    }


    /**
     * 从MessageFragment那边传过来的标题
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onConversationTitleEvent(event: ConversationTitleEvent) {
        tv_title_chat.text = event.conversationTitle
    }

    /**
     * 从UserFragment跳转到ChatActivity时的event的处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFriendItemClickEvent(event: FriendItemClickEvent) {
        conversation = event.conversation!!
        conversation.read()//设置已读
        CoolChatApp.getAppEventBus().removeStickyEvent(FriendItemClickEvent::class.java)
    }

    /**
     * 从MessageFragment跳转到ChatActivity时的event的处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onConversationObjectEvent(event: ConversationObjectEvent) {
        if (event.conversation != null) {
            conversation = event.conversation
            conversation.read()//设置已读
            CoolChatApp.getAppEventBus().removeStickyEvent(ConversationObjectEvent::class.java)
            LogUtil.d(tag, "获得到的ConversationId ----> ${conversation.conversationId}")
        }
    }

    /**
     * 语音播放完成的event的处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMusicPlayFinishedEvent(event: MusicPlayFinishedEvent) {
        //todo:这是语音播放完成的事件 想下要怎么处理
        chatAdapter.isVoicePlaying = false
        CoolChatApp.getAppEventBus().removeStickyEvent(MusicPlayFinishedEvent::class.java)
    }

    /**
     * 有新消息时的event的处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNewMessageEventHandle(event: NewMessageEvent) {
        if (event.conversation == conversation) {
            messageList.add(event.message)
            chatAdapter.notifyItemInserted(messageList.size - 1)
            rv_chat.scrollToPosition(messageList.size - 1)
            LogUtil.d(tag, "成功接收到新消息 ----> 1")
        }
    }

    /**
     * 文字消息发送成功的回调
     */
    override fun onTextMessageSendSucceeded(msg: AVIMMessage) {
        et_send_message_chat.setText("")
        messageList.add(msg)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        rv_chat.scrollToPosition(messageList.size - 1)
        LogUtil.d("ChatActivity", "消息发送成功")
    }

    /**
     * 文字消息发送失败的回调
     */
    override fun onTextMessageSendFailed() {
        LogUtil.d("ChatActivity", "消息发送失败")
    }

    /**
     * 下拉刷新更多消息成功的回调
     */
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

    /**
     * 下拉刷新更多消息失败的回调
     */
    override fun onHistoryMsgLoadedFailed(e: AVIMException?) {
        LogUtil.d(tag, "历史消息加载失败 ----> $e")
    }


    /**
     * 下拉刷新没有更多消息的回调 即显示的已经是最早的消息了
     */
    override fun onHistoryMsgIsNull() {
        if (swipe_refresh_layout_activity_chat.isRefreshing) {
            swipe_refresh_layout_activity_chat.isRefreshing = false
            Toast.makeText(this, "没有更多消息了", Toast.LENGTH_SHORT).show()
        }
        LogUtil.d(tag, "onHistoryMsgIsNull ----> 没有更多消息了")
    }

    /**
     * 第一次加载对话成功的回调
     */
    override fun onFirstTenMsgLoadedSucceeded(messages: MutableList<AVIMMessage>?, oldestMsg: AVIMMessage) {
        if (messages != null) {
            this.oldestMsg = oldestMsg
            messageList.addAll(messages)
            for (x in 0 until messages.size - 1) {
                chatAdapter.notifyItemInserted(x)
            }
            //修改了前面直接移动到itemCount导致没有移动到最后一条消息的情况
            rv_chat.scrollToPosition(chatAdapter.itemCount - 1)
            LogUtil.d(tag, "前十条消息获取成功 ----> 1")
        }
    }

    /**
     * 第一次加载对话失败的回调
     */
    override fun onFirstTenMsgLoadedFailed(e: AVIMException?) {
        LogUtil.d(tag, "前十条消息获取失败 ----> $e")
    }

    /**
     * 第一次加载对话为空的回调
     */
    override fun onFirstTenMsgIsNull() {
        LogUtil.d(tag, "第一次加载的消息为空 ----> 1")
    }


    /**
     * 选择图片返回的结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode && resultCode == RESULT_OK) {
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

    /**
     * 图片消息发送成功的回调
     */
    override fun onImgMessageSendSucceeded(msg: AVIMMessage) {
        messageList.add(msg)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        rv_chat.scrollToPosition(messageList.size - 1)
        LogUtil.d(tag, "图像消息发送成功 ----> 1")
        Toast.makeText(this, "图片发送成功", Toast.LENGTH_SHORT).show()
    }

    /**
     * 图片消息发送失败的回调
     */
    override fun onImgMessageSendFailed() {
        LogUtil.d(tag, "图像消息发送失败 ----> 0")
        Toast.makeText(this, "图片发送失败 请重新试试", Toast.LENGTH_SHORT).show()
    }

    /**
     * 点击Item图片
     */
    override fun onImgItemClicked(url: String) {
        CoolChatApp.getAppEventBus().postSticky(ImageItemClickEvent(url))
        val intent = Intent(this, PhotoActivity::class.java)
        startActivity(intent)
    }

    /**
     * 语音消息发送成功的回调
     */
    override fun onAudioMessageSendSucceeded(msg: AVIMMessage) {
        messageList.add(msg)
        chatAdapter.notifyItemInserted(messageList.size - 1)
        rv_chat.scrollToPosition(messageList.size - 1)
        LogUtil.d(tag, "语音发送成功 ----> 1")
    }

    /**
     * 语音消息发送失败的回调
     */
    override fun onAudioMessageSendFailed() {
        LogUtil.d(tag, "语音消息发送失败 ----> 0")
        Toast.makeText(this, "语音发送失败 请重新试试", Toast.LENGTH_SHORT).show()
    }

    /**
     * 获得语音消息文件的路径
     */
    override fun onAudioFilePathGetSucceeded(filePath: String) {
        tv_press_to_say.text = "按住说话"
        LogUtil.d(tag, "获取到了语音文件的地址 ----> $filePath")
        val fileName =
            filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
        val file = AVFile.withAbsoluteLocalPath(fileName, filePath)
        val audioMessage = AVIMAudioMessage(file)
        chatPresenter.sendAudioMessage(audioMessage, conversation)
    }

    /**
     * 开始播放语音
     */
    override fun onVoiceItemPlayStart(msgBean: AVIMMessageBean) {
        chatPresenter.playReceivedAudioMessage(msgBean)
    }

    /**
     * 停止播放语音
     */
    override fun onVoiceItemPlayStop() {
        chatPresenter.stopReceivedAudioMessage()
    }

}