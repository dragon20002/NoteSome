package com.haruu.notesome.presenter

interface BaseView<T> {
    var presenter: T
}

interface BasePresenter {
    fun start()
}