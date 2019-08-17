package com.gennan.summer.activity

import android.os.Bundle
import com.gennan.summer.R
import com.gennan.summer.base.BaseFragment
import com.gennan.summer.base.BaseActivity
import com.gennan.summer.fragment.MessageFragment
import com.gennan.summer.fragment.SettingFragment
import com.gennan.summer.fragment.UserFragment
import com.gennan.summer.util.Constants.Companion.MESSAGE_FRAGMENT
import com.gennan.summer.util.Constants.Companion.SETTING_FRAGMENT
import com.gennan.summer.util.Constants.Companion.USER_FRAGMENT
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    private val fragments = mutableListOf<BaseFragment>()//创建一个存放fragment的集合
    private var lastFragment = -1//刚开始先设置一个和代表三个界面的整数值无关的-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()//创建Fragment
    }


    /**
     * 创建碎片 然后让碎片和底部的Navigation联动
     */
    private fun initView() {
        //创建一些碎片 在这里才创建的Fragment 因此在LoginActivity设置的avUser不会为空
        val messageFragment = MessageFragment()
        val userFragment = UserFragment()
        val settingFragment = SettingFragment()
        fragments.add(messageFragment)
        fragments.add(userFragment)
        fragments.add(settingFragment)
        lastFragment = MESSAGE_FRAGMENT
        //先默认进入的界面
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
