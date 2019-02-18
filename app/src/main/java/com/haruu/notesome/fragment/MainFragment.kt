package com.haruu.notesome.fragment

import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteConstraintException
import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.LinearLayout
import android.widget.SeekBar
import com.haruu.notesome.R
import com.haruu.notesome.adapter.ShortTextRecyclerAdapter
import com.haruu.notesome.adapter.SoundRecyclerAdapter
import com.haruu.notesome.dao.AppDatabase
import com.haruu.notesome.dao.ShortTextDao
import com.haruu.notesome.dao.SoundDao
import com.haruu.notesome.databinding.FragmentShortTextBinding
import com.haruu.notesome.databinding.FragmentSoundBinding
import com.haruu.notesome.dialogfragment.BaseDialogFragment
import com.haruu.notesome.dialogfragment.FileChooserDialogFragment
import com.haruu.notesome.dialogfragment.TextInputDialogFragment
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.model.Sound
import java.io.File

class ShortTextMainFragment : Fragment() {
    private lateinit var mShortTextDao: ShortTextDao
    private var mBtnDelete: MenuItem? = null
    private val mViewAdapter: ShortTextRecyclerAdapter = ShortTextRecyclerAdapter { selectedNum ->
        mBtnDelete?.isVisible = selectedNum > 0
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Thread(Runnable {
            context?.let {
                AppDatabase.getInstance(it)?.shortTextDao()?.let { dao ->
                    mShortTextDao = dao
                    mViewAdapter.mDataSet.addAll(dao.getAll())
                    activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                }
            }
        }).start()

        return DataBindingUtil.inflate<FragmentShortTextBinding>(
                inflater, R.layout.fragment_short_text, container, false
        ).apply {
            selectedShortTextList = mViewAdapter.mSelectedData

            setHasOptionsMenu(true)

            recycler.apply {
                setHasFixedSize(true)
                adapter = mViewAdapter
            }
        }.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.option_short_text, menu)
        mBtnDelete =
                menu?.findItem(R.id.delete_selected)?.apply { isVisible = mViewAdapter.mSelectedData.isNotEmpty() }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add_short_text) {
            TextInputDialogFragment().setMessage("추가할 텍스트를 입력하세요.")
                    .setPositiveButton("확인", listener = { text ->
                        Thread(Runnable {
                            val some = ShortText(title = text)
                            try {
                                if (mShortTextDao.insertAll(some).isNotEmpty()) {
                                    mViewAdapter.mDataSet.add(some)
                                    activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                                }
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
                        // DB 이용 시 비동기처리를 하므로 리스트와 DB 사이의 버퍼용 리스트를 사용한다.
                        val checkedList: ArrayList<ShortText> = ArrayList(mViewAdapter.mSelectedData)
                        mViewAdapter.mSelectedData.clear()
                        mBtnDelete?.isVisible = false
                        Thread(Runnable {
                            mShortTextDao.deleteAll(*checkedList.toTypedArray())
                            mViewAdapter.mDataSet.removeAll(checkedList)
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
    private lateinit var mSoundDao: SoundDao
    private lateinit var mSeekBar: SeekBar
    private var mSeekBarUpdateThread: Thread? = null
    private lateinit var mMediaController: LinearLayout
    private var mMediaPlayer: MediaPlayer? = null
    private var mBtnDelete: MenuItem? = null
    private val mViewAdapter: SoundRecyclerAdapter = SoundRecyclerAdapter({ selectedNum ->
        mBtnDelete?.isVisible = selectedNum > 0
    }, { sound -> playSound(sound) })

    private fun playSound(sound: Sound) {
        context?.getFileStreamPath(sound.title)?.let { file ->
            mMediaPlayer?.stop()
            mSeekBarUpdateThread?.interrupt()
            mMediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))?.also { mp ->
                mSeekBar.max = mp.duration

                mSeekBarUpdateThread = Thread {
                    while (true) {
                        activity?.runOnUiThread {
                            mp.currentPosition.let { pos -> mSeekBar.progress = pos }
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
                        mSeekBarUpdateThread?.interrupt()
                        mMediaPlayer = null
                    }
                    mViewAdapter.mCurrentSound = sound
                    this.setBackgroundResource(R.drawable.ic_pause_black_24dp)
                    mp.start()
                    mViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Thread(Runnable {
            context?.let {
                AppDatabase.getInstance(it)?.soundDao()?.let { dao ->
                    mSoundDao = dao
                    if (mViewAdapter.mDataSet.addAll(dao.getAll()) && mViewAdapter.mDataSet.isNotEmpty()) {
                        activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                        mViewAdapter.mCurrentSound = mViewAdapter.mDataSet[0]
                    }
                }
            }
        }).start()

        return DataBindingUtil.inflate<FragmentSoundBinding>(
                inflater, R.layout.fragment_sound, container, false
        ).apply {
            selectedSoundList = mViewAdapter.mSelectedData

            setHasOptionsMenu(true)

            recycler.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mViewAdapter
            }
            mSeekBar = seekBar.apply {
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    private var progress: Int = 0

                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        this.progress = progress
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        mMediaPlayer?.seekTo(progress)
                    }
                })
            }
            mMediaController = mediaController.apply {
                getChildAt(0).setOnClickListener {
                    if (mViewAdapter.mDataSet.isEmpty()) return@setOnClickListener
                    var position = mViewAdapter.mDataSet.indexOf(mViewAdapter.mCurrentSound) - 1
                    if (position == -1) position = mViewAdapter.mDataSet.size - 1
                    playSound(mViewAdapter.mDataSet[position])
                }
                getChildAt(1).setOnClickListener { v ->
                    if (mViewAdapter.mDataSet.isEmpty()) return@setOnClickListener
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
                    mViewAdapter.mCurrentSound?.let { sound ->
                        playSound(sound)
                        return@setOnClickListener
                    }
                    mViewAdapter.mCurrentSound = mViewAdapter.mDataSet[0].also { sound -> playSound(sound) }
                }
                getChildAt(2).setOnClickListener {
                    if (mViewAdapter.mDataSet.isEmpty()) return@setOnClickListener
                    var position = mViewAdapter.mDataSet.indexOf(mViewAdapter.mCurrentSound) + 1
                    if (position == mViewAdapter.mDataSet.size) position = 0
                    playSound(mViewAdapter.mDataSet[position])
                }
            }
        }.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.option_sound, menu)
        mBtnDelete =
                menu?.findItem(R.id.delete_selected)?.apply { isVisible = mViewAdapter.mSelectedData.isNotEmpty() }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add_sound) {
            FileChooserDialogFragment().setMessage("업로드할 음성 파일을 선택하세요.")
                    .setPositiveButton("확인", listener = { filename, inputStream ->
                        Thread(Runnable {
                            context?.openFileOutput(filename, Context.MODE_PRIVATE)?.let { outputStream ->
                                outputStream.write(inputStream.readBytes())
                                outputStream.close()

                                val sound = Sound(title = filename)
                                try {
                                    if (mSoundDao.insertAll(sound).isNotEmpty()) {
                                        mViewAdapter.mDataSet.add(sound)
                                        activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                                        if (mViewAdapter.mCurrentSound == null && mViewAdapter.mDataSet.isNotEmpty())
                                            mViewAdapter.mCurrentSound = mViewAdapter.mDataSet[0]
                                    }
                                } catch (e: SQLiteConstraintException) {
                                    e.printStackTrace()
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
                        // DB 이용 시 비동기처리를 하므로 리스트와 DB 사이의 버퍼용 리스트를 사용한다.
                        val checkedList: ArrayList<Sound> = ArrayList(mViewAdapter.mSelectedData)
                        mViewAdapter.mSelectedData.clear()
                        mBtnDelete?.isVisible = false
                        Thread(Runnable {
                            mSoundDao.deleteAll(*checkedList.toTypedArray())
                            mViewAdapter.mDataSet.removeAll(checkedList)
                            if (mViewAdapter.mDataSet.isEmpty())
                                mViewAdapter.mCurrentSound = null
                            checkedList.forEach { File(context?.filesDir, it.title).delete() }
                            activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                        }).start()
                    })
                    .setNeutralButton("취소", DialogInterface.OnClickListener { _, _ -> })
                    .show(fragmentManager, "delete audio")
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