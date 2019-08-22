package com.gennan.summer.fragment

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
import com.gennan.summer.activity.AboutActivity
import com.gennan.summer.activity.FeedbackActivity
import com.gennan.summer.activity.LoginActivity
import com.gennan.summer.activity.SquareActivity
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.mvvm.viewModel.SettingViewModel
import com.gennan.summer.util.LogUtil
import com.gennan.summer.util.UriToRealPathUtil
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy

class SettingFragment : BaseFragment() {

    var avatarIv: ImageView? = null
    var changeAvatarTv: TextView? = null
    var enterSquareTv: TextView? = null
    var provideFeedbackTv: TextView? = null
    var aboutTv: TextView? = null
    var changeAccountTv: TextView? = null
    private val IMG_REQUEST_CODE = 1
    val TAG = "SettingFragment"
    var settingViewModel: SettingViewModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        settingViewModel = ViewModelProviders.of(activity!!).get(SettingViewModel::class.java)
        initView(view)
        initEvent()
        addLiveDataObserve()
        return view.rootView
    }

    private fun addLiveDataObserve() {
        settingViewModel?.changeAvatarLiveData?.observe(activity!!, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()//显示下更改头像的结果
            GlideApp.with(activity!!).load(CoolChatApp.avUser?.getString("iconUrl"))
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(avatarIv!!)
        })
    }

    private fun initEvent() {
        changeAvatarTv?.setOnClickListener {
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
        enterSquareTv?.setOnClickListener {
            val intent = Intent(activity, SquareActivity::class.java)
            startActivity(intent)
        }
        provideFeedbackTv?.setOnClickListener {
            val intent = Intent(activity, FeedbackActivity::class.java)
            startActivity(intent)
        }
        aboutTv?.setOnClickListener {
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
        }
        changeAccountTv?.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun initView(view: View?) {
        avatarIv = view?.findViewById(R.id.iv_avatar_setting)
        changeAvatarTv = view?.findViewById(R.id.tv_change_avatar_setting)
        enterSquareTv = view?.findViewById(R.id.tv_enter_square_setting)
        provideFeedbackTv = view?.findViewById(R.id.tv_provide_feedback_setting)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_REQUEST_CODE && resultCode == FragmentActivity.RESULT_OK) {
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