package com.haruu.notesome.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haruu.notesome.R
import com.haruu.notesome.model.Some
import kotlinx.android.synthetic.main.item_main_recycler.view.*

class MainRecyclerAdapter(private val mDataSet: ArrayList<Some>) :
    RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_main_recycler, parent, false)

        return MainViewHolder(view)
    }

    override fun getItemCount() = this.mDataSet.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.view.title.text = this.mDataSet[position].title
    }

    class MainViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}