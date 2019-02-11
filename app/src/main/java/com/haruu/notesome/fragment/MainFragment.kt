package com.haruu.notesome.fragment

import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteConstraintException
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.haruu.notesome.R
import com.haruu.notesome.adapter.ShortTextRecyclerAdapter
import com.haruu.notesome.adapter.SoundRecyclerAdapter
import com.haruu.notesome.dao.AppDatabase
import com.haruu.notesome.dao.ShortTextDao
import com.haruu.notesome.dao.SoundDao
import com.haruu.notesome.dialogfragment.BaseDialogFragment
import com.haruu.notesome.dialogfragment.FileChooserDialogFragment
import com.haruu.notesome.dialogfragment.TextInputDialogFragment
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.model.Sound
import java.util.*

class ShortTextMainFragment : Fragment() {
    private lateinit var mShortTextDao: ShortTextDao
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mNumChecked: TextView
    private var mBtnDelete: MenuItem? = null
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private val mDataSet: ArrayList<ShortText> = ArrayList()
    private val mCheckedData: ArrayList<Int> = ArrayList()
    private val mViewAdapter: RecyclerView.Adapter<*> =
            ShortTextRecyclerAdapter(mDataSet, mCheckedData) {
                if (mCheckedData.size > 0) {
                    mNumChecked.text = String.format(Locale.KOREAN, "%d 선택", mCheckedData.size)
                    mBtnDelete?.isVisible = true
                } else {
                    mNumChecked.text = ""
                    mBtnDelete?.isVisible = false
                }
            }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Thread(Runnable {
            context?.let {
                AppDatabase.getInstance(it)?.shortTextDao()?.let { dao ->
                    mShortTextDao = dao
                    if (mDataSet.addAll(dao.getAll()))
                        activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                }
            }
        }).start()

        return inflater.inflate(R.layout.fragment_short_text, container, false).apply {
            setHasOptionsMenu(true)

            mRecyclerView = this.findViewById<RecyclerView>(R.id.recycler).apply {
                setHasFixedSize(true)
                mViewManager = LinearLayoutManager(context) //or Grid, Staggered Layout
                layoutManager = mViewManager
                adapter = mViewAdapter
            }
            mNumChecked = this.findViewById(R.id.numChecked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.short_text, menu)
        mBtnDelete =
                menu?.findItem(R.id.delete_selected)?.apply { isVisible = mCheckedData.isNotEmpty() }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add_short_text) {
            TextInputDialogFragment()
                    .setMessage("추가할 텍스트를 입력하세요.")
                    .setPositiveButton("확인", listener = { text ->
                        Thread(Runnable {
                            val some = ShortText(title = text)
                            try {
                                if (mShortTextDao.insertAll(some).isNotEmpty() && mDataSet.add(some))
                                    activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                            } catch (e: SQLiteConstraintException) {
                                e.printStackTrace()
                            }
                        }).start()
                    })
                    .setNeutralButton("취소", listener = {})
                    .show(fragmentManager, "add text")
            return true
        } else if (item?.itemId == R.id.delete_selected) {
            BaseDialogFragment()
                    .setMessage("선택된 항목들을 삭제합니다.")
                    .setPositiveButton("삭제", DialogInterface.OnClickListener { _, _ ->
                        val checkedList: ArrayList<ShortText> = ArrayList()
                        for (position: Int in mCheckedData)
                            checkedList.add(mDataSet[position])
                        mCheckedData.clear()
                        mNumChecked.text = ""
                        mBtnDelete?.isVisible = false
                        for (shortText: ShortText in checkedList)
                            mDataSet.remove(shortText)
                        Thread(Runnable {
                            mShortTextDao.deleteAll(*checkedList.toTypedArray())
                            activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                        }).start()
                    })
                    .setNeutralButton("취소", DialogInterface.OnClickListener { _, _ -> })
                    .show(fragmentManager, "delete text")
            return true
        }
        return false
    }
}

class SoundMainFragment : Fragment() {
    private var mCurrentSound: Sound? = null
    private lateinit var mSoundDao: SoundDao
    private lateinit var mSeekBar: SeekBar
    private var seekBarUpdateThread: Thread? = null
    private lateinit var mMediaController: LinearLayout
    private var mMediaPlayer: MediaPlayer? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mNumChecked: TextView
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private var mBtnDelete: MenuItem? = null
    private val mDataSet: ArrayList<Sound> = ArrayList()
    private val mCheckedData: ArrayList<Int> = ArrayList()
    private val mViewAdapter: SoundRecyclerAdapter = SoundRecyclerAdapter(mDataSet, mCheckedData, {
        if (mCheckedData.size > 0) {
            mNumChecked.text = String.format(Locale.KOREAN, "%d 선택", mCheckedData.size)
            mBtnDelete?.isVisible = true
        } else {
            mNumChecked.text = ""
            mBtnDelete?.isVisible = false
        }
    }, { sound -> playSound(sound) })

    private fun playSound(sound: Sound) {
        context?.let { context ->
            context.getFileStreamPath(sound.title)?.let { file ->
                mMediaPlayer?.stop()
                seekBarUpdateThread?.interrupt()
                mMediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))
                mMediaPlayer?.let { mp ->
                    mSeekBar.max = mp.duration

                    seekBarUpdateThread = Thread {
                        while (true) {
                            activity?.runOnUiThread {
                                mMediaPlayer?.currentPosition?.let { pos -> mSeekBar.progress = pos }
                            }

                            try {
                                Thread.sleep(1000)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                                break
                            }
                        }
                    }.apply { start() }

                    mMediaController.getChildAt(1).apply {
                        //재생버튼
                        mp.setOnCompletionListener {
                            this.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp)
                            seekBarUpdateThread?.interrupt()
                            mMediaPlayer = null
                        }
                        mCurrentSound = sound
                        this.setBackgroundResource(R.drawable.ic_pause_black_24dp)
                        mp.start()
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Thread(Runnable {
            context?.let {
                AppDatabase.getInstance(it)?.soundDao()?.let { dao ->
                    mSoundDao = dao
                    if (mDataSet.addAll(dao.getAll())) {
                        if (mDataSet.isNotEmpty())
                            mCurrentSound = mDataSet[0]
                        activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                    }
                }
            }
        }).start()

        return inflater.inflate(R.layout.fragment_sound, container, false).apply {
            setHasOptionsMenu(true)

            mRecyclerView = this.findViewById<RecyclerView>(R.id.recycler).apply {
                setHasFixedSize(true)
                mViewManager = LinearLayoutManager(context)
                layoutManager = mViewManager
                adapter = mViewAdapter
            }
            mNumChecked = this.findViewById(R.id.numChecked)
            mSeekBar = this.findViewById(R.id.seekBar)
            mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                private var progress: Int = 0

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    this.progress = progress
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mMediaPlayer?.seekTo(progress)
                }
            })
            mMediaController = this.findViewById(R.id.mediaController)
            mMediaController.apply {
                getChildAt(0).setOnClickListener {
                    if (mDataSet.isEmpty()) return@setOnClickListener
                    var position = mDataSet.indexOf(mCurrentSound) - 1
                    if (position == -1) position = mDataSet.size - 1
                    playSound(mDataSet[position])
                }
                getChildAt(1).setOnClickListener { v ->
                    if (mDataSet.isEmpty()) return@setOnClickListener
                    mMediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            v.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp)
                            mp.pause()
                        } else {
                            v.setBackgroundResource(R.drawable.ic_pause_black_24dp)
                            mp.start()
                        }
                        return@setOnClickListener
                    }
                    //mMediaPlayer == null
                    mCurrentSound?.let { sound ->
                        playSound(sound)
                        return@setOnClickListener
                    }
                    //mCurrentSound == null
                    mCurrentSound = mDataSet[0].also { sound -> playSound(sound) }
                }
                getChildAt(2).setOnClickListener {
                    if (mDataSet.isEmpty()) return@setOnClickListener
                    var position = mDataSet.indexOf(mCurrentSound) + 1
                    if (position == mDataSet.size) position = 0
                    playSound(mDataSet[position])
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.sound, menu)
        mBtnDelete =
                menu?.findItem(R.id.delete_selected)?.apply { isVisible = mCheckedData.isNotEmpty() }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add_sound) {
            FileChooserDialogFragment().setMessage("업로드할 음성 파일을 선택하세요.")
                    .setPositiveButton("확인", listener = { filename, inputStream ->
                        Thread(Runnable {
                            context?.let {
                                it.openFileOutput(filename, Context.MODE_PRIVATE)?.let { outputStream ->
                                    outputStream.write(inputStream.readBytes())
                                    outputStream.close()

                                    val sound = Sound(title = filename)
                                    try {
                                        if (mSoundDao.insertAll(sound).isNotEmpty()) {
                                            mDataSet.add(sound)
                                            if (mCurrentSound == null && mDataSet.isNotEmpty())
                                                mCurrentSound = mDataSet[0]
                                            activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                                        }
                                    } catch (e: SQLiteConstraintException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }).start()
                    })
                    .setNeutralButton("취소", listener = {})
                    .show(fragmentManager, "add audio")
            return true
        } else if (item?.itemId == R.id.delete_selected) {
            BaseDialogFragment()
                    .setMessage("선택된 항목들을 삭제합니다.")
                    .setPositiveButton("삭제", DialogInterface.OnClickListener { _, _ ->
                        val checkedList: ArrayList<Sound> = ArrayList()
                        for (position: Int in mCheckedData)
                            checkedList.add(mDataSet[position])
                        mCheckedData.clear()
                        mBtnDelete?.isVisible = false
                        mNumChecked.text = ""
                        for (sound: Sound in checkedList)
                            mDataSet.remove(sound)
                        Thread(Runnable {
                            mSoundDao.deleteAll(*checkedList.toTypedArray())
                            if (mDataSet.isEmpty())
                                mCurrentSound = null
                            activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                        }).start()
                    })
                    .setNeutralButton("취소", DialogInterface.OnClickListener { _, _ -> })
                    .show(fragmentManager, "delete text")
            return true
        }
        return false
    }
}

class SettingsMainFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}