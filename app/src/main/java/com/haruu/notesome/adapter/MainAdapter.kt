package com.haruu.notesome.adapter

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.graphics.Typeface
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.haruu.notesome.R
import com.haruu.notesome.activity.NUM_OF_PAGES
import com.haruu.notesome.activity.SETTINGS_PAGE
import com.haruu.notesome.activity.SHORT_TEXT_PAGE
import com.haruu.notesome.activity.SOUND_PAGE
import com.haruu.notesome.databinding.ItemSoundRecyclerBinding
import com.haruu.notesome.databinding.ItemTextRecyclerBinding
import com.haruu.notesome.fragment.SettingsMainFragment
import com.haruu.notesome.fragment.ShortTextMainFragment
import com.haruu.notesome.fragment.SoundMainFragment
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.model.Sound

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


class ShortTextRecyclerAdapter(private val onItemCheckBoxClick: (Int) -> Unit)
    : RecyclerView.Adapter<ShortTextRecyclerAdapter.ViewHolder>() {
    val mDataSet: ObservableArrayList<ShortText> = ObservableArrayList()
    val mSelectedData: ObservableArrayList<ShortText> = ObservableArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DataBindingUtil.inflate<ItemTextRecyclerBinding>(LayoutInflater.from(parent.context),
                R.layout.item_text_recycler, parent, false
        ).let { ViewHolder(it) }
    }

    override fun getItemCount() = mDataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            selected = mSelectedData.contains(mDataSet[position])
            shortText = mDataSet[position]
            checkbox.setOnClickListener {
                mSelectedData.apply {
                    if (contains(mDataSet[position]))
                        remove(mDataSet[position])
                    else
                        add(mDataSet[position])
                }
                onItemCheckBoxClick(mSelectedData.size)
            }
        }
    }

    class ViewHolder(val binding: ItemTextRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
}


class SoundRecyclerAdapter(private val onItemCheckBoxClick: (Int) -> Unit,
                           private val onItemTitleClick: (Sound) -> Unit
) : RecyclerView.Adapter<SoundRecyclerAdapter.ViewHolder>() {
    var mCurrentSound: Sound? = null
    val mDataSet: ObservableArrayList<Sound> = ObservableArrayList()
    val mSelectedData: ObservableArrayList<Sound> = ObservableArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DataBindingUtil.inflate<ItemSoundRecyclerBinding>(LayoutInflater.from(parent.context),
                R.layout.item_sound_recycler, parent, false
        ).let { ViewHolder(it) }
    }

    override fun getItemCount() = mDataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            current = mCurrentSound
            selected = mSelectedData.contains(mDataSet[position])
            sound = mDataSet[position]
            checkbox.setOnClickListener {
                mSelectedData.apply {
                    if (contains(mDataSet[position]))
                        remove(mDataSet[position])
                    else
                        add(mDataSet[position])
                }
                onItemCheckBoxClick(mSelectedData.size)
            }
            title.setOnClickListener { onItemTitleClick(mDataSet[position]) }
        }
    }

    class ViewHolder(val binding: ItemSoundRecyclerBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["current", "sound"])
        fun setTitleStyle(view: TextView, current: Sound?, sound: Sound) {
            view.typeface = when (current != null && current == sound) {
                true -> Typeface.DEFAULT_BOLD
                false -> Typeface.DEFAULT
            }
        }
    }
}
