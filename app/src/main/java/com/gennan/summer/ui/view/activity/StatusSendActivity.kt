package com.gennan.summer.ui.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.avos.avoscloud.AVFile
import com.gennan.summer.GlideApp
import com.gennan.summer.MyGlideEngine
import com.gennan.summer.R
import com.gennan.summer.ui.mvvm.viewModel.StatusSendViewModel
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.Constants.Companion.BLANK_STRING
import com.gennan.summer.util.Constants.Companion.IMG_CAN_CHOOSE
import com.gennan.summer.util.UriToRealPathUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_status_send.*

/**
 *Created by Gennan.
 */
class StatusSendActivity : AppCompatActivity() {
    private var statusSendViewModel: StatusSendViewModel? = null
    private val imgRequestCode = 2
    private var imgUrl = BLANK_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_send)
        statusSendViewModel = ViewModelProviders.of(this).get(StatusSendViewModel::class.java)
        observeLiveData()
        initEvent()
    }

    private fun observeLiveData() {
        statusSendViewModel?.savedUrlLiveData?.observe(this, Observer {
            imgUrl = it
            GlideApp.with(this).load(it).into(iv_send_status)

        })
        statusSendViewModel?.sendStatusLiveData?.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            if (it == "动态发送成功") {
                finish()
            }
        })
    }

    private fun initEvent() {
        tv_send_status.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            val textWillSend = et_text_msg_will_send_status.text.toString()
            if (imgUrl == "" && textWillSend.isEmpty()) {
                Toast.makeText(this, "动态内容不能为空", Toast.LENGTH_SHORT).show()
            } else {
                statusSendViewModel?.sendStatus(textWillSend, imgUrl)
            }
        }
        iv_send_status.setOnClickListener {
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
                            .captureStrategy(CaptureStrategy(true, "com.gennan.summer.fileprovider"))
                            .theme(R.style.CoolChatMatisse)
                            .imageEngine(MyGlideEngine())
                            .forResult(imgRequestCode)
                    } else {

                    }
                }
        }
    }

    /**
     * 选择图片返回的结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imgRequestCode && resultCode == FragmentActivity.RESULT_OK) {
            val result = Matisse.obtainResult(data)
            val filePath = UriToRealPathUtil.getRealFilePath(this, result[0])
            val fileName =
                filePath?.substring(filePath.lastIndexOf("/") + 1, filePath.length)
            val imgFile = AVFile.withAbsoluteLocalPath(fileName, filePath)
            statusSendViewModel?.saveFile(imgFile)
        }
    }
}