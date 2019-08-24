package com.gennan.summer.ui.mvp.presenter

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaRecorder
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.ui.mvp.model.bean.AVIMMessageBean
import com.gennan.summer.event.MusicPlayFinishedEvent
import com.gennan.summer.ui.mvp.contract.IChatPresenter
import com.gennan.summer.ui.mvp.contract.IChatViewCallback
import com.gennan.summer.util.LogUtil
import java.io.File


/**
 *Created by Gennan on 2019/8/18.
 * 以为SDK封装好了就不用再写了 其实应该自己再弄个Model层的
 */
class ChatPresenter : IChatPresenter {

    var mediaPlayer: MediaPlayer? = null
    private val TAG = "ChatPresenter"
    var oldestMessage: AVIMMessage = AVIMMessage()
    val callbacks = mutableListOf<IChatViewCallback>()
    var file: File? = null
    var fileName: String? = null
    var filePath: String? = null
    var recorder: MediaRecorder? = null


    companion object {
        val instance: ChatPresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ChatPresenter() }
    }

    /**
     * 下拉刷新消息
     */
    override fun getMsgHistory(oldestMsg: AVIMMessage, messageNumber: Int, conversation: AVIMConversation) {
        conversation.queryMessages(
            oldestMsg.messageId,
            oldestMsg.timestamp,
            messageNumber,
            object : AVIMMessagesQueryCallback() {
                override fun done(messages: MutableList<AVIMMessage>?, e: AVIMException?) {
                    if (e == null) {
                        if (messages?.size != 0) {
                            for (callback in callbacks) {
                                callback.onHistoryMsgLoadedSucceeded(messages, messages!![0])
                            }
                        } else {
                            for (callback in callbacks) {
                                callback.onHistoryMsgIsNull()
                            }
                        }
                    } else {
                        for (callback in callbacks) {
                            callback.onHistoryMsgLoadedFailed(e)
                        }
                    }
                }
            })
    }

    /**
     * 加载对话的最近十条消息
     */
    override fun getFirstTenMsg(messageNumber: Int, conversation: AVIMConversation) {
        conversation.queryMessages(messageNumber, object : AVIMMessagesQueryCallback() {
            override fun done(messages: MutableList<AVIMMessage>?, e: AVIMException?) {
                if (e == null) {
                    if (messages?.size != 0) {
                        for (callback in callbacks) {
                            callback.onFirstTenMsgLoadedSucceeded(messages, messages!![0])
                        }
                    } else {
                        for (callback in callbacks) {
                            callback.onFirstTenMsgIsNull()
                        }
                    }
                } else {
                    for (callback in callbacks) {
                        callback.onFirstTenMsgLoadedFailed(e)
                    }
                }
            }
        })
    }

    /**
     * 发送文字消息
     */
    override fun sendTextMessage(text: String, conversation: AVIMConversation) {
        val msg = AVIMTextMessage()
        msg.text = text
        conversation.sendMessage(msg, object : AVIMConversationCallback() {
            override fun done(e: AVIMException?) {
                if (e == null) {
                    for (callback in callbacks) {
                        callback.onTextMessageSendSucceeded(msg)
                    }
                } else {
                    for (callback in callbacks) {
                        callback.onTextMessageSendFailed()
                    }
                }
            }
        })
    }

    /**
     * 发送图片消息
     */
    override fun sendImgMessage(avimImageMessage: AVIMImageMessage, conversation: AVIMConversation) {
        conversation.sendMessage(avimImageMessage, object : AVIMConversationCallback() {
            override fun done(e: AVIMException?) {
                if (e == null) {
                    for (callback in callbacks) {
                        callback.onImgMessageSendSucceeded(avimImageMessage)
                    }
                } else {
                    for (callback in callbacks) {
                        LogUtil.d(TAG, "发送图片消息失败----> $e")
                        callback.onImgMessageSendFailed()
                    }
                }
            }
        })
    }

    /**
     * 给录音文件设置文件名和路径
     */
    private fun setFileNameAndPath() {
        fileName = "/cool_chat_recording_" + (System.currentTimeMillis()) + ".mp4"
        filePath = CoolChatApp.getAppContext()?.codeCacheDir?.absolutePath
        filePath += fileName
        file = File(filePath)
        LogUtil.d(TAG, "语音文件的filePath ----> $filePath")
    }

    /**
     * 开始录制语音
     */
    @SuppressLint("CheckResult")
    override fun startRecordAudio(activity: BaseActivity) {
        try {
            setFileNameAndPath()
            if (recorder == null) {
                recorder = MediaRecorder()
                recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)//设置音频源
                recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)//设置输出格式
                recorder?.setOutputFile(file?.absolutePath)//设置文件路径
                recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)//设置音频编码格式
                recorder?.setAudioChannels(1)
                recorder?.setAudioSamplingRate(44100)//设置采样率
                recorder?.setAudioEncodingBitRate(192000)
                recorder?.prepare()
                recorder?.start()
            }
        } catch (e: Exception) {
            LogUtil.d(TAG, "录制语音失败 ----> $e")
        }
    }

    /**
     * 停止录制语音
     */
    override fun stopRecordAudio() {
        //短按结束录音
        recorder?.setOnErrorListener(null)
        recorder?.setOnInfoListener(null)
        recorder?.setPreviewDisplay(null)
        try {
            if (recorder != null) {
                recorder?.stop()
            }
            recorder?.reset()
            recorder?.release()
            recorder = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (callback in callbacks) {
            callback.onAudioFilePathGetSucceeded(filePath!!)
        }
    }

    /**
     * 发送语音消息
     */
    override fun sendAudioMessage(msg: AVIMAudioMessage, conversation: AVIMConversation) {
        conversation.sendMessage(msg, object : AVIMConversationCallback() {
            override fun done(e: AVIMException?) {
                if (e == null) {
                    for (callback in callbacks) {
                        callback.onAudioMessageSendSucceeded(msg)
                    }
                } else {
                    for (callback in callbacks) {
                        callback.onAudioMessageSendFailed()
                    }
                }
            }
        })
    }

    /**
     * 开始播放语音消息
     */
    override fun playReceivedAudioMessage(msgBean: AVIMMessageBean) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(msgBean._lcfile.url)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            CoolChatApp.getAppEventBus().postSticky(MusicPlayFinishedEvent())
        }

    }

    /**
     * 停止播放语音消息
     */
    override fun stopReceivedAudioMessage() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }

    override fun attachViewCallback(t: IChatViewCallback) {
        callbacks.add(t)
    }

    override fun unAttachViewCallback(t: IChatViewCallback) {
        callbacks.remove(t)
    }
}