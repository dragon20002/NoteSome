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
import com.haruu.notesome.databinding.ItemShortTextRecyclerBinding
import com.haruu.notesome.databinding.ItemSoundRecyclerBinding
import com.haruu.notesome.fragment.ShortTextMainFragment
import com.haruu.notesome.fragment.SoundMainFragment
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.model.Sound

class MainPagerAdapter(fm: FragmentManager, private val fragments: Array<Fragment>) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = fragments.size

    override fun getItem(i: Int): Fragment {
        return fragments[i]
    }

}


class ShortTextRecyclerAdapter(private val itemListener: ShortTextMainFragment.ItemListener) :
    RecyclerView.Adapter<ShortTextRecyclerAdapter.ViewHolder>() {
    val list: MutableList<ShortText> = ArrayList(0)
    val selectedList: ObservableArrayList<ShortText> = ObservableArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DataBindingUtil.inflate<ItemShortTextRecyclerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_short_text_recycler, parent, false
        ).let { ViewHolder(it) }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            selected = selectedList.contains(list[position])
            shortText = list[position]
            checkbox.setOnClickListener {
                selectedList.apply {
                    if (contains(list[position]))
                        remove(list[position])
                    else
                        add(list[position])
                }
                itemListener.onCheckBoxClick(selectedList.size)
            }
        }
    }

    class ViewHolder(val binding: ItemShortTextRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
}


class SoundRecyclerAdapter(private val itemListener: SoundMainFragment.ItemListener) :
    RecyclerView.Adapter<SoundRecyclerAdapter.ViewHolder>() {
    var current: Sound? = null
    val list: MutableList<Sound> = ArrayList(0)
    val selectedList: ObservableArrayList<Sound> = ObservableArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DataBindingUtil.inflate<ItemSoundRecyclerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_sound_recycler, parent, false
        ).let { ViewHolder(it) }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            current = this@SoundRecyclerAdapter.current
            selected = selectedList.contains(list[position])
            sound = list[position]
            checkbox.setOnClickListener {
                selectedList.apply {
                    if (contains(list[position]))
                        remove(list[position])
                    else
                        add(list[position])
                }
                itemListener.onCheckBoxClick(selectedList.size)
            }
            title.setOnClickListener {
                itemListener.onTitleClick(list[position])
            }
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
