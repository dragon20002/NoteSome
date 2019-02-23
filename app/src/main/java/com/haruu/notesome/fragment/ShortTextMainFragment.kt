package com.haruu.notesome.fragment

import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.haruu.notesome.R
import com.haruu.notesome.adapter.ShortTextRecyclerAdapter
import com.haruu.notesome.databinding.FragmentShortTextBinding
import com.haruu.notesome.dialogfragment.BaseDialogFragment
import com.haruu.notesome.dialogfragment.TextInputDialogFragment
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.presenter.ShortTextContract

class ShortTextMainFragment : Fragment(), ShortTextContract.View {

    override lateinit var presenter: ShortTextContract.Presenter

    private var btnDel: MenuItem? = null

    private val listAdapter: ShortTextRecyclerAdapter =
        ShortTextRecyclerAdapter(object : ItemListener {
            override fun onCheckBoxClick(selectedNum: Int) {
                btnDel?.isVisible = selectedNum > 0
            }
        })

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentShortTextBinding>(
            inflater, R.layout.fragment_short_text, container, false
        ).apply {
            selectedShortTextList = listAdapter.selectedList

            setHasOptionsMenu(true)

            recycler.apply {
                setHasFixedSize(true)
                adapter = listAdapter
            }
        }.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.option_short_text, menu)
        btnDel =
            menu?.findItem(R.id.delete_selected)?.apply { isVisible = listAdapter.selectedList.isNotEmpty() }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add_short_text) {
            showAdd()
        } else if (item?.itemId == R.id.delete_selected) {
            showRemove()
        }
        return true
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        presenter.result(requestCode, resultCode)
//    }

    override fun showList(shortTextList: List<ShortText>) {
        with(listAdapter) {
            list.clear()
            selectedList.clear()
            list.addAll(shortTextList)
            notifyDataSetChanged()
        }
    }

    override fun showItem(vararg shortText: ShortText) {
        shortText.forEach {
            with(listAdapter) {
                list.add(it)
                notifyItemInserted(list.size - 1)
            }
        }
    }

    override fun removeItem(vararg shortText: ShortText) {
        shortText.forEach {
            with(listAdapter) {
                val position = list.indexOf(it)
                list.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    override fun showAdd() {
        TextInputDialogFragment().setMessage("추가할 텍스트를 입력하세요.")
            .setPositiveButton("확인", listener = { text -> presenter.add(ShortText(text)) })
            .setNeutralButton("취소", listener = {})
            .show(fragmentManager, "add text")
    }

    override fun showRemove() {
        BaseDialogFragment()
            .setMessage("선택된 항목들을 삭제합니다.")
            .setPositiveButton("삭제", DialogInterface.OnClickListener { _, _ ->
                val selectedList: List<ShortText> = ArrayList(listAdapter.selectedList.keys)
                listAdapter.selectedList.clear()
                btnDel?.isVisible = false
                presenter.remove(*selectedList.toTypedArray())
            })
            .setNeutralButton("취소", DialogInterface.OnClickListener { _, _ -> })
            .show(fragmentManager, "delete text")
    }

    interface ItemListener {
        fun onCheckBoxClick(selectedNum: Int)
    }
}
