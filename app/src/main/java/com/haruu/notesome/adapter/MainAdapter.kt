package com.haruu.notesome.adapter

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haruu.notesome.R
import com.haruu.notesome.activity.NUM_OF_PAGES
import com.haruu.notesome.activity.SETTINGS_PAGE
import com.haruu.notesome.activity.SHORT_TEXT_PAGE
import com.haruu.notesome.activity.SOUND_PAGE
import com.haruu.notesome.fragment.SettingsMainFragment
import com.haruu.notesome.fragment.ShortTextMainFragment
import com.haruu.notesome.fragment.SoundMainFragment
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.model.Sound
import kotlinx.android.synthetic.main.item_main_recycler.view.*

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = NUM_OF_PAGES

    override fun getItem(i: Int): Fragment {
        return when (i) {
            SHORT_TEXT_PAGE -> ShortTextMainFragment()
            SOUND_PAGE -> SoundMainFragment()
            SETTINGS_PAGE -> SettingsMainFragment()
            else -> ShortTextMainFragment()
        }
    }

}


class ShortTextRecyclerAdapter(private val mDataSet: ArrayList<ShortText>) :
        RecyclerView.Adapter<ShortTextRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_main_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mDataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.title.text = mDataSet[position].title
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}


class SoundRecyclerAdapter(private val mDataSet: ArrayList<Sound>) :
        RecyclerView.Adapter<SoundRecyclerAdapter.ViewHolder>() {

    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_main_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mDataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            title.text = mDataSet[position].title
            setOnClickListener {
                context.getFileStreamPath(mDataSet[position].title)?.let {
                    mMediaPlayer?.stop()
                    mMediaPlayer = MediaPlayer.create(context, Uri.fromFile(it))
                    mMediaPlayer?.start()
                }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
