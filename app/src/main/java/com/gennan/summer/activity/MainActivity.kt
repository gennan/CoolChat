package com.gennan.summer.activity

import android.os.Bundle
import com.gennan.summer.R
import com.gennan.summer.base.BaseApplication
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.base.BaseMvpActivity
import com.gennan.summer.event.AVUserEvent
import com.gennan.summer.fragment.MessageFragment
import com.gennan.summer.fragment.SettingFragment
import com.gennan.summer.fragment.UserFragment
import com.gennan.summer.util.Constants.Companion.MESSAGE_FRAGMENT
import com.gennan.summer.util.Constants.Companion.SETTING_FRAGMENT
import com.gennan.summer.util.Constants.Companion.USER_FRAGMENT
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseMvpActivity() {
    private val fragments = mutableListOf<BaseFragment>()//创建一个存放fragment的集合
    private var lastFragment = -1//刚开始先设置一个和代表三个界面的整数值无关的-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BaseApplication.getAppEventBus().register(this)
        initView()
    }

    private fun initView() {
        val messageFragment = MessageFragment()
        val userFragment = UserFragment()
        val settingFragment = SettingFragment()
        fragments.add(messageFragment)
        fragments.add(userFragment)
        fragments.add(settingFragment)
        lastFragment = MESSAGE_FRAGMENT
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout_main, messageFragment).commit()
        combineFragmentWithBottomNavigation()
    }

    /**
     * 使创建的碎片和底部的导航能够联动
     */
    private fun combineFragmentWithBottomNavigation() {
        bottom_navigation_view_main.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_message -> {
                    if (MESSAGE_FRAGMENT != lastFragment) {
                        switchFragment(lastFragment, MESSAGE_FRAGMENT)
                        lastFragment = MESSAGE_FRAGMENT
                    }
                    true
                }
                R.id.navigation_user -> {
                    if (USER_FRAGMENT != lastFragment) {
                        switchFragment(lastFragment, USER_FRAGMENT)
                        lastFragment = USER_FRAGMENT
                    }
                    true
                }
                R.id.navigation_setting -> {
                    if (SETTING_FRAGMENT != lastFragment) {
                        switchFragment(lastFragment, SETTING_FRAGMENT)
                        lastFragment = SETTING_FRAGMENT
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.getAppEventBus().unregister(this)
    }

    /**
     * 简单的通过fragment的隐藏和显示来达到切换fragment的效果
     */
    private fun switchFragment(lastFragment: Int, index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragments[lastFragment])
        if (!fragments[index].isAdded) {
            transaction.add(R.id.frame_layout_main, fragments[index])
        }
        transaction.show(fragments[index]).commitAllowingStateLoss()
    }

    /**
     * 登录成功后获取登录的AVUser 通过EventBus来进行通信
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun handleAVUserEvent(avUserEvent: AVUserEvent) {
        LogUtil.d("MainActivity", "avUser.username---->${avUserEvent.avUser.username}")
        //todo:数据传过来了之后怎么用
    }
}
