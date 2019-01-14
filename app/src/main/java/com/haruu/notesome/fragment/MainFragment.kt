package com.haruu.notesome.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haruu.notesome.R
import com.haruu.notesome.adapter.MainRecyclerAdapter
import com.haruu.notesome.dialogfragment.FileChooserDialogFragment
import com.haruu.notesome.dialogfragment.TextInputDialogFragment
import com.haruu.notesome.model.Some
import com.haruu.notesome.service.FileManager
import com.haruu.notesome.service.ServerManager

sealed class MainFragment : Fragment() {
    protected abstract val mLayoutResourceId: Int
    protected abstract fun initView(rootView: View)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(mLayoutResourceId, container, false)
        initView(rootView)
        return rootView
    }
}

class ShortTextMainFragment : MainFragment() {
    override val mLayoutResourceId: Int = R.layout.fragment_short_text

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private val mDataSet: ArrayList<Some> = ArrayList()
    private val mViewAdapter: RecyclerView.Adapter<*> = MainRecyclerAdapter(mDataSet)
    private lateinit var mFloatingActionButton: FloatingActionButton

    override fun initView(rootView: View) {
        mViewManager = LinearLayoutManager(context) //or Grid, Staggered Layout
        mRecyclerView = rootView.findViewById<RecyclerView>(R.id.recycler).apply {
            setHasFixedSize(true)
            layoutManager = mViewManager
            adapter = mViewAdapter
        }

        mFloatingActionButton = rootView.findViewById<FloatingActionButton>(R.id.floating).apply {
            setOnClickListener {
                TextInputDialogFragment()
                    .setMessage("추가할 텍스트를 입력하세요.")
                    .setPositiveButton("확인", listener = { text ->
                        ServerManager.saveText(text) { some -> mDataSet.add(some) }
                    })
                    .setNeutralButton("취소", listener = {})
                    .show(fragmentManager, "add text")
            }
        }

        FileManager.readTexts { mDataSet.addAll(it) }
        ServerManager.loadTexts { mDataSet.addAll(it) }
    }
}

class AudioMainFragment : MainFragment() {
    override val mLayoutResourceId: Int = R.layout.fragment_audio

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mViewManager: RecyclerView.LayoutManager
    private val mDataSet: ArrayList<Some> = ArrayList()
    private val mViewAdapter: RecyclerView.Adapter<*> = MainRecyclerAdapter(mDataSet)
    private lateinit var mFloatingActionButton: FloatingActionButton

    override fun initView(rootView: View) {
        mViewManager = LinearLayoutManager(context)
        mRecyclerView = rootView.findViewById<RecyclerView>(R.id.recycler).apply {
            setHasFixedSize(true)
            layoutManager = mViewManager
            adapter = mViewAdapter
        }

        mFloatingActionButton = rootView.findViewById<FloatingActionButton>(R.id.floating).apply {
            setOnClickListener {
                FileChooserDialogFragment()
                    .setMessage("업로드할 음성 파일을 선택하세요.")
                    .setPositiveButton("확인", listener = { filename ->
                        ServerManager.saveAudio(filename) { some -> mDataSet.add(some) }
                    })
                    .setNeutralButton("취소", listener = {})
                    .show(fragmentManager, "add audio")
            }
        }

        FileManager.readAudios { mDataSet.addAll(it) }
        ServerManager.loadAudios { mDataSet.addAll(it) }
    }
}

class SettingsMainFragment : MainFragment() {
    override val mLayoutResourceId: Int = R.layout.fragment_settings

    override fun initView(rootView: View) {}
}