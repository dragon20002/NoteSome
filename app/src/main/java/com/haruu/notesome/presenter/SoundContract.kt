package com.haruu.notesome.presenter

import android.content.Context
import com.haruu.notesome.model.Sound
import java.io.File
import java.io.InputStream

interface SoundContract {

    interface View : BaseView<Presenter> {

        fun showList(soundList: List<Sound>)

        fun showItem(vararg sound: Sound)

        fun removeItem(vararg sound: Sound)

        fun showAdd()

        fun showRemove()

        fun updateSeekBarMax(max: Int)

        fun updateSeekBarProgress(currentPosition: Int)

        fun updateMediaController(isPlaying: Boolean)

        fun updateCurrentSound(currentSound: Sound?)
    }

    interface Presenter : BasePresenter {

//        fun result(requestCode: Int, resultCode: Int)

        fun getAll()

        fun add(vararg sound: Sound)

        fun remove(vararg sound: Sound)

        fun saveFile(context: Context?, filename: String, inputStream: InputStream)

        fun deleteFile(context: Context?, filename: String)

        fun loadFile(context: Context?, filename: String): File?

        fun play(context: Context?, sound: Sound)

        fun stop()

        fun restartOrPause()

        fun seek(millis: Int)
    }
}