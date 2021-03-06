package com.gennan.summer.ui.view.activity

import android.os.Bundle
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.gennan.summer.R
import com.gennan.summer.app.CoolChatApp
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.event.ClientOpenEvent
import com.gennan.summer.ui.view.fragment.FriendFragment
import com.gennan.summer.ui.view.fragment.MessageFragment
import com.gennan.summer.ui.view.fragment.SettingFragment
import com.gennan.summer.util.Constants.Companion.MESSAGE_FRAGMENT
import com.gennan.summer.util.Constants.Companion.NULL_FRAGMENT
import com.gennan.summer.util.Constants.Companion.SETTING_FRAGMENT
import com.gennan.summer.util.Constants.Companion.USER_FRAGMENT
import com.gennan.summer.util.LogUtil
import kotlinx.android.synthetic.main.activity_main.*

/**
 *Created by Gennan.
 */
class MainActivity : BaseActivity() {
    private val fragments = mutableListOf<BaseFragment>()//创建一个存放fragment的集合
    private var lastFragment = NULL_FRAGMENT//设置一个不存在的值
    val tag ="MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clientOpen()//先进行client.open()获得open后的client
        initView()//创建Fragment
    }

    /**
     * Leancloud的SDK要先调用client.open()才能执行其他操作
     */
    private fun clientOpen() {
        CoolChatApp.avImClient?.open(object : AVIMClientCallback() {
            override fun done(client: AVIMClient?, e: AVIMException?) {
                if (e == null) {
                    CoolChatApp.openedClient = client
                    CoolChatApp.getAppEventBus().postSticky(ClientOpenEvent())
                } else {
                    LogUtil.d(tag, "Client.open()方法调用失败 ----> $e")
                }
            }
        })
    }

    /**
     * 创建Fragment 然后让Fragment和底部的Navigation联动
     */
    private fun initView() {
        //创建一些碎片 在这里才创建的Fragment 因此在LoginActivity设置的avUser不会为空
        val messageFragment = MessageFragment()
        val userFragment = FriendFragment()
        val settingFragment = SettingFragment()
        fragments.add(messageFragment)
        fragments.add(userFragment)
        fragments.add(settingFragment)
        lastFragment = MESSAGE_FRAGMENT
        //先默认进入的界面
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout_main, messageFragment)
            .commit()
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
}
