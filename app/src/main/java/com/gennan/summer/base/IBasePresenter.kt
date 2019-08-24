package com.gennan.summer.base

/**
 *Created by Gennan.
 */
interface IBasePresenter<T> {
    fun attachViewCallback(t: T)//注册接口
    fun unAttachViewCallback(t: T)//反注册接口
}