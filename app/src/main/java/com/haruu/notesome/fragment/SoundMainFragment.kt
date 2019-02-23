package com.haruu.notesome.fragment

import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.widget.SeekBar
import com.haruu.notesome.R
import com.haruu.notesome.adapter.SoundRecyclerAdapter
import com.haruu.notesome.databinding.FragmentSoundBinding
import com.haruu.notesome.dialogfragment.BaseDialogFragment
import com.haruu.notesome.dialogfragment.FileChooserDialogFragment
import com.haruu.notesome.model.Sound
import com.haruu.notesome.presenter.SoundContract

class SoundMainFragment : Fragment(), SoundContract.View {

    override lateinit var presenter: SoundContract.Presenter

    private var btnDel: MenuItem? = null
    private lateinit var mSeekBar: SeekBar
    private lateinit var mMediaController: LinearLayout

    private val listAdapter: SoundRecyclerAdapter =
        SoundRecyclerAdapter(object : ItemListener {
            override fun onCheckBoxClick(selectedNum: Int) {
                btnDel?.isVisible = selectedNum > 0
            }

            override fun onTitleClick(sound: Sound) {
                presenter.play(context, sound)
            }
        })

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentSoundBinding>(
            inflater, R.layout.fragment_sound, container, false
        ).apply {
            selectedSoundList = listAdapter.selectedList

            setHasOptionsMenu(true)

            recycler.apply {
                setHasFixedSize(true)
                adapter = listAdapter
            }
            mSeekBar = seekBar.apply {
                isEnabled = false

                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    private var progress: Int = 0

                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        this.progress = progress
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        presenter.seek(progress)
                    }
                })
            }
            mMediaController = mediaController.apply {
                getChildAt(0).setOnClickListener {
                    if (listAdapter.list.isEmpty()) return@setOnClickListener
                    val position = when {
                        listAdapter.current != null -> {
                            val pos = listAdapter.list.indexOf(listAdapter.current!!) - 1
                            if (pos >= 0) pos
                            else listAdapter.list.size - 1
                        }
                        else -> 0
                    }
                    presenter.play(context, listAdapter.list[position])
                }
                getChildAt(1).setOnClickListener {
                    if (listAdapter.list.isNotEmpty()) {
                        if (listAdapter.current != null) //현재 재생되고 있거나 최근에 재생한 Sound
                            presenter.restartOrPause()
                        else
                            presenter.play(context, listAdapter.list[0])
                    }
                }
                getChildAt(2).setOnClickListener {
                    if (listAdapter.list.isEmpty()) return@setOnClickListener
                    val position = when {
                        listAdapter.current != null -> {
                            val pos = listAdapter.list.indexOf(listAdapter.current!!) + 1
                            if (pos < listAdapter.list.size) pos
                            else 0
                        }
                        else -> 0
                    }
                    presenter.play(context, listAdapter.list[position])
                }
            }
        }.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.option_sound, menu)
        btnDel =
            menu?.findItem(R.id.delete_selected)?.apply { isVisible = listAdapter.selectedList.isNotEmpty() }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add_sound) {
            showAdd()
        } else if (item?.itemId == R.id.delete_selected) {
            showRemove()
        }
        return true
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        presenter.result(requestCode, resultCode)
//    }

    override fun showList(soundList: List<Sound>) {
        with(listAdapter) {
            list.clear()
            selectedList.clear()
            list.addAll(soundList)
            notifyDataSetChanged()
        }
    }

    override fun showItem(vararg sound: Sound) {
        sound.forEach {
            with(listAdapter) {
                list.add(it)
                notifyItemInserted(list.size - 1)
            }
        }
    }

    override fun removeItem(vararg sound: Sound) {
        sound.forEach {
            with(listAdapter) {
                val position = list.indexOf(it)
                list.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    override fun showAdd() {
        FileChooserDialogFragment().setMessage("업로드할 음성 파일을 선택하세요.")
            .setPositiveButton("확인", listener = { filename, inputStream ->
                // save file
                context?.let { context -> presenter.saveFile(context, filename, inputStream) }
                // insert data
                presenter.add(Sound(filename))
            })
            .setNeutralButton("취소", listener = {})
            .show(fragmentManager, "add audio")
    }

    override fun showRemove() {
        BaseDialogFragment()
            .setMessage("선택된 항목들을 삭제합니다.")
            .setPositiveButton("삭제", DialogInterface.OnClickListener { _, _ ->
                btnDel?.isVisible = false

                val selectedList: List<Sound> = ArrayList(listAdapter.selectedList.keys)
                listAdapter.selectedList.clear()

                if (listAdapter.current != null && selectedList.contains(listAdapter.current!!))
                    presenter.stop()

                // delete data
                presenter.remove(*selectedList.toTypedArray())
                // delete file
                context?.let { context ->
                    selectedList.forEach {
                        presenter.deleteFile(context, it.title)
                    }
                }
            })
            .setNeutralButton("취소", DialogInterface.OnClickListener { _, _ -> })
            .show(fragmentManager, "delete audio")
    }

    override fun updateSeekBarMax(max: Int) {
        mSeekBar.max = max
    }

    override fun updateSeekBarProgress(currentPosition: Int) {
        mSeekBar.progress = currentPosition
    }

    override fun updateMediaController(isPlaying: Boolean) {
        mMediaController.getChildAt(1).apply {
            if (isPlaying)
                setBackgroundResource(R.drawable.ic_pause_black_24dp)
            else
                setBackgroundResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    override fun updateCurrentSound(currentSound: Sound?) {
        listAdapter.current = currentSound
        listAdapter.notifyDataSetChanged()
        mSeekBar.progress = 0
        mSeekBar.isEnabled = currentSound != null
    }

    interface ItemListener {
        fun onCheckBoxClick(selectedNum: Int)

        fun onTitleClick(sound: Sound)
    }
}
