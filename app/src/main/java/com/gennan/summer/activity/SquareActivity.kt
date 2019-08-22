package com.gennan.summer.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gennan.summer.R
import com.gennan.summer.adapter.StatusAdapter
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.mvvm.viewModel.SquareViewModel
import com.mredrock.cyxbs.freshman.lin.util.Dip2pxUtil
import kotlinx.android.synthetic.main.activity_square.*

class SquareActivity : BaseActivity() {

    var squareViewModel: SquareViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_square)
        squareViewModel = ViewModelProviders.of(this).get(SquareViewModel::class.java)
//        //测试
//        squareViewModel?.getRecentStatus()
        //这边再拿到数据以后再去显示adapter
        initView()
        initEvent()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(this)
        rv_show_status_square.layoutManager = layoutManager
        val statusAdapter = StatusAdapter()
        rv_show_status_square.adapter = statusAdapter
        rv_show_status_square.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(@NonNull outRect: Rect, @NonNull view: View, @NonNull parent: RecyclerView, @NonNull state: RecyclerView.State) {
                outRect.top = Dip2pxUtil.dip2px(view.context, 8.0)
                outRect.bottom = Dip2pxUtil.dip2px(view.context, 8.0)
                outRect.left = Dip2pxUtil.dip2px(view.context, 8.0)
                outRect.right = Dip2pxUtil.dip2px(view.context, 8.0)
            }
        })
    }

    private fun initEvent() {
        //返回按钮
        iv_back_square.setOnClickListener {
            finish()
        }
        iv_add_status_square.setOnClickListener {
            //发布动态
            val intent = Intent(this, StatusSendActivity::class.java)
            startActivity(intent)
        }
    }

}
