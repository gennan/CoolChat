package com.gennan.summer.ui.view.activity

import android.os.Bundle
import com.gennan.summer.GlideApp

import com.gennan.summer.R
import com.gennan.summer.base.BaseActivity


import kotlinx.android.synthetic.main.activity_about.*

/**
 *Created by Gennan.
 */
class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initData()
        initEvent()
    }

    private fun initData() {
        GlideApp.with(this).load(R.mipmap.ic_launcher).into(iv_logo_about)
    }

    private fun initEvent() {
        iv_back_about.setOnClickListener {
            finish()
        }
    }
}
