package com.haruu.notesome.presenter

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.haruu.notesome.dao.SoundDao
import com.haruu.notesome.model.Sound
import java.io.File
import java.io.InputStream

class SoundPresenter(private val dao: SoundDao, private val view: SoundContract.View) : SoundContract.Presenter {
    private var mediaPlayer: MediaPlayer? = null
    private var playingThread: Thread? = null

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

    override fun add(vararg sound: Sound) {
        Thread(Runnable {
            try {
                if (dao.insert(*sound).isNotEmpty())
                    Handler(Looper.getMainLooper()).post { view.showItem(*sound) }
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
            }
        }).start()
    }

    override fun remove(vararg sound: Sound) {
        Thread(Runnable {
            if (dao.delete(*sound) > 0) {
                Handler(Looper.getMainLooper()).post { view.removeItem(*sound) }
            }
        }).start()
    }

    override fun saveFile(context: Context?, filename: String, inputStream: InputStream) {
        context?.openFileOutput(filename, Context.MODE_PRIVATE)?.apply {
            write(inputStream.readBytes())
            close()
        }
    }

    override fun deleteFile(context: Context?, filename: String) {
        context?.filesDir?.let { dir ->
            File(dir, filename).delete()
        }
    }

    override fun loadFile(context: Context?, filename: String): File? {
        return context?.getFileStreamPath(filename)
    }

    override fun play(context: Context?, sound: Sound) {
        loadFile(context, sound.title)?.let { file ->
            stop()
            mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))?.also { mp ->
                playingThread = Thread {
                    while (true) {
                        Handler(Looper.getMainLooper()).post { view.updateSeekBarProgress(mp.currentPosition) }

                        try {
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                            break
                        }
                    }
                }.apply { start() }

                view.updateSeekBarMax(mp.duration)
                view.updateMediaController(true)
                view.updateCurrentSound(sound)

                mp.setOnCompletionListener {
                    playingThread?.interrupt()
                    view.updateMediaController(false)
                    view.updateCurrentSound(null)
                }
                mp.start()
            }
        }
    }

    override fun stop() {
        playingThread?.interrupt()
        mediaPlayer?.stop()
        mediaPlayer = null
        view.updateMediaController(false)
        view.updateCurrentSound(null)
    }

    override fun restartOrPause() {
        mediaPlayer?.apply {
            if (isPlaying) pause()
            else start()
            view.updateMediaController(isPlaying)
        }
    }

    override fun seek(millis: Int) {
        mediaPlayer?.seekTo(millis)
    }
}