package com.haruu.notesome.presenter

import android.database.sqlite.SQLiteConstraintException
import android.os.Handler
import android.os.Looper
import com.haruu.notesome.dao.ShortTextDao
import com.haruu.notesome.model.ShortText

class ShortTextPresenter(
    val dao: ShortTextDao,
    val view: ShortTextContract.View
) : ShortTextContract.Presenter {

    init {
        view.presenter = this
    }

    override fun start() {
        getAll()
    }

//    override fun result(requestCode: Int, resultCode: Int) {
//    }

    override fun getAll() {
        Thread(Runnable {
            val list = dao.get()
            Handler(Looper.getMainLooper()).post { view.showList(list) }
        }).start()
    }

    override fun add(vararg shortText: ShortText) {
        Thread(Runnable {
            try {
                if (dao.insert(*shortText).isNotEmpty())
                    Handler(Looper.getMainLooper()).post { view.showItem(*shortText) }
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
            }
        }).start()
    }

    override fun remove(vararg shortText: ShortText) {
        Thread(Runnable {
            if (dao.delete(*shortText) > 0)
                Handler(Looper.getMainLooper()).post { view.removeItem(*shortText) }
        }).start()
    }
}