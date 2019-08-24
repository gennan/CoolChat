package com.gennan.summer.ui.view.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gennan.summer.R
import com.gennan.summer.adapter.StatusAdapter
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.event.StatusImageItemClickEvent
import com.gennan.summer.ui.mvvm.viewModel.SquareViewModel
import com.gennan.summer.util.ClickUtil
import com.mredrock.cyxbs.freshman.lin.util.Dip2pxUtil
import kotlinx.android.synthetic.main.activity_square.*

/**
 *Created by Gennan.
 */
class SquareActivity : BaseActivity(), StatusAdapter.OnStatusImageClickListener {
    private var squareViewModel: SquareViewModel? = null
    private var statusAdapter: StatusAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_square)
        squareViewModel = ViewModelProviders.of(this).get(SquareViewModel::class.java)
        squareViewModel?.getRecentStatus()
        observeLiveData()
        initView()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        squareViewModel?.getRecentStatus()
    }

    private fun initView() {
        swipe_fresh_layout_activity_square.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )

        val layoutManager = LinearLayoutManager(this)
        rv_show_status_square.layoutManager = layoutManager
        statusAdapter = StatusAdapter(this)
        rv_show_status_square.adapter = statusAdapter
        statusAdapter!!.setOnStatusImageClickListener(this)
        rv_show_status_square.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(@NonNull outRect: Rect, @NonNull view: View, @NonNull parent: RecyclerView, @NonNull state: RecyclerView.State) {
                outRect.top = Dip2pxUtil.dip2px(view.context, 8.0)
                outRect.bottom = Dip2pxUtil.dip2px(view.context, 8.0)
                outRect.left = Dip2pxUtil.dip2px(view.context, 8.0)
                outRect.right = Dip2pxUtil.dip2px(view.context, 8.0)
                //数据太容易变了 就不弄成常量了
            }
        })
    }

    private fun initEvent() {
        //返回按钮
        iv_back_square.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            finish()
        }
        iv_add_status_square.setOnClickListener {
            if (!ClickUtil.isFastClick()) {
                return@setOnClickListener
            }
            //发布动态
            val intent = Intent(this, StatusSendActivity::class.java)
            startActivity(intent)
        }
        swipe_fresh_layout_activity_square.setOnRefreshListener {
            squareViewModel?.getRecentStatus()
        }
    }

    private fun observeLiveData() {
        squareViewModel?.getStatusLiveData?.observe(this, Observer {
            statusAdapter?.setData(it)
            statusAdapter?.notifyDataSetChanged()
            if (swipe_fresh_layout_activity_square.isRefreshing) {
                swipe_fresh_layout_activity_square.isRefreshing = false
                Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show()
            }
        })

        squareViewModel?.notGetStatusLiveData?.observe(this, Observer {
            if (swipe_fresh_layout_activity_square.isRefreshing) {
                swipe_fresh_layout_activity_square.isRefreshing = false
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 在状态列表点击image的事件
     */
    override fun onStatusImageItemClick(imageUrl: String) {
        CoolChatApp.getAppEventBus().postSticky(StatusImageItemClickEvent(imageUrl))
        val intent = Intent(this, PhotoActivity::class.java)
        startActivity(intent)
    }

}
