package com.haruu.notesome.presenter

import com.haruu.notesome.model.ShortText

interface ShortTextContract {

    interface View : BaseView<Presenter> {

        fun showList(shortTextList: List<ShortText>)

        fun showItem(vararg shortText: ShortText)

        fun removeItem(vararg shortText: ShortText)

        fun showAdd()

        fun showRemove()
    }

    interface Presenter : BasePresenter {

//        fun result(requestCode: Int, resultCode: Int)

        fun getAll()

        fun add(vararg shortText: ShortText)

        fun remove(vararg shortText: ShortText)
    }
}