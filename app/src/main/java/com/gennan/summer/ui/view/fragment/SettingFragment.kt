package com.gennan.summer.ui.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.avos.avoscloud.AVFile
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.gennan.summer.GlideApp
import com.gennan.summer.MyGlideEngine
import com.gennan.summer.R
import com.gennan.summer.ui.view.activity.AboutActivity
import com.gennan.summer.ui.view.activity.LoginActivity
import com.gennan.summer.ui.view.activity.SquareActivity
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.ui.mvvm.viewModel.SettingViewModel
import com.gennan.summer.util.ClickUtil
import com.gennan.summer.util.Constants.Companion.IMG_CAN_CHOOSE
import com.gennan.summer.util.LogUtil
import com.gennan.summer.util.UriToRealPathUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy

/**
 *Created by Gennan.
 */
class SettingFragment : BaseFragment() {

    private var avatarIv: ImageView? = null
    private var changeAvatarTv: TextView? = null
    private var enterSquareTv: TextView? = null

    private var aboutTv: TextView? = null
    private var changeAccountTv: TextView? = null
    private val imgRequestCode = 1
    val TAG = "SettingFragment"
    private var settingViewModel: SettingViewModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        settingViewModel = ViewModelProviders.of(activity!!).get(SettingViewModel::class.java)
        initView(view)
        initEvent()
        addLiveDataObserve()
        return view.rootView
    }

    private fun initView(view: View?) {
        avatarIv = view?.findViewById(R.id.iv_avatar_setting)
        changeAvatarTv = view?.findViewById(R.id.tv_change_avatar_setting)
        enterSquareTv = view?.findViewById(R.id.tv_enter_square_setting)
        aboutTv = view?.findViewById(R.id.tv_about_setting)
        changeAccountTv = view?.findViewById(R.id.tv_change_account_setting)
        GlideApp.with(activity!!)
            .load(CoolChatApp.avUser?.getString("iconUrl"))
            .apply(
                RequestOptions.bitmapTransform(
                    CircleCrop()
                )
            )
            .into(avatarIv!!)
    }

    private fun initEvent() {
        changeAvatarTv?.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            getImageUri()
        }
        enterSquareTv?.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            val intent = Intent(activity, SquareActivity::class.java)
            startActivity(intent)
        }
        aboutTv?.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
        }
        changeAccountTv?.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            CoolChatApp.isAutoLogin = false
            activity?.finish()
        }
        avatarIv?.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            getImageUri()
        }
    }

    private fun addLiveDataObserve() {
        settingViewModel?.changeAvatarLiveData?.observe(activity!!, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()//显示下更改头像的结果
            GlideApp.with(activity!!).load(CoolChatApp.avUser?.getString("iconUrl"))
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(avatarIv!!)
        })
    }

    /**
     * 获取图片
     */
    @SuppressLint("CheckResult")
    private fun getImageUri() {
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
                    LogUtil.d(TAG, "申请权限的请求被拒绝了")
                }
            }
    }

    /**
     * 图片选择的Result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imgRequestCode && resultCode == FragmentActivity.RESULT_OK) {
            val result = Matisse.obtainResult(data)
            val filePath = UriToRealPathUtil.getRealFilePath(activity!!, result[0])
            val fileName =
                filePath?.substring(filePath.lastIndexOf("/") + 1, filePath.length)
            LogUtil.d(TAG, "filePath ----> $filePath")
            val imgFile = AVFile.withAbsoluteLocalPath(fileName, filePath)
            settingViewModel?.saveImageFileAndChangeAvatar(imgFile)
        }
    }
}