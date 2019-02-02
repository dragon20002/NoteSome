package com.haruu.notesome.fragment

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.haruu.notesome.R
import com.haruu.notesome.adapter.ShortTextRecyclerAdapter
import com.haruu.notesome.adapter.SoundRecyclerAdapter
import com.haruu.notesome.dao.AppDatabase
import com.haruu.notesome.dao.ShortTextDao
import com.haruu.notesome.dao.SoundDao
import com.haruu.notesome.dialogfragment.FileChooserDialogFragment
import com.haruu.notesome.dialogfragment.TextInputDialogFragment
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.model.Sound

class ShortTextMainFragment : Fragment() {
    private lateinit var mShortTextDao: ShortTextDao
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private val mDataSet: ArrayList<ShortText> = ArrayList()
    private val mViewAdapter: RecyclerView.Adapter<*> = ShortTextRecyclerAdapter(mDataSet)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.short_text, menu)
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
        }
        return false
    }
}

class SoundMainFragment : Fragment() {
    private lateinit var mSoundDao: SoundDao
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private val mDataSet: ArrayList<Sound> = ArrayList()
    private val mViewAdapter: RecyclerView.Adapter<*> = SoundRecyclerAdapter(mDataSet)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Thread(Runnable {
            context?.let {
                AppDatabase.getInstance(it)?.soundDao()?.let { dao ->
                    mSoundDao = dao
                    if (mDataSet.addAll(dao.getAll()))
                        activity?.runOnUiThread { mViewAdapter.notifyDataSetChanged() }
                }
            }
        }).start()

        return inflater.inflate(R.layout.fragment_sound, container, false).apply {
            setHasOptionsMenu(true)

            mViewManager = LinearLayoutManager(context)
            mRecyclerView = this.findViewById<RecyclerView>(R.id.recycler).apply {
                setHasFixedSize(true)
                layoutManager = mViewManager
                adapter = mViewAdapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.sound, menu)
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
        }
        return false
    }
}

class SettingsMainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}