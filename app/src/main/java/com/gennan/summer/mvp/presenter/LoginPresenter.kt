package com.gennan.summer.mvp.presenter

import com.gennan.summer.mvp.contract.ILoginPresenter
import com.gennan.summer.mvp.contract.ILoginViewCallback

class LoginPresenter : ILoginPresenter {
    val callbacks = mutableListOf<ILoginViewCallback>()

    companion object {
        val instance: LoginPresenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { LoginPresenter() }
    }

    override fun login(username: String, password: String) {

    }

    override fun attachViewCallback(t: ILoginViewCallback) {
        callbacks.add(t)
    }

    override fun unAttachViewCallback(t: ILoginViewCallback) {
        callbacks.remove(t)
    }
}