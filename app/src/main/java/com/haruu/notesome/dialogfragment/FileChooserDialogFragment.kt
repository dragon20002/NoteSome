package com.haruu.notesome.dialogfragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.haruu.notesome.R
import com.haruu.notesome.utils.RealPathUtil


private const val REQ_FILE_SELECT: Int = 1

class FileChooserDialogFragment : BaseDialogFragment() {

    private lateinit var mDialog: AlertDialog
    private lateinit var mTxtDirPath: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        @SuppressLint("InflateParams")
        val linearLayout = LayoutInflater.from(context).inflate(R.layout.dialogfragment_filechooser, null)
        linearLayout.apply {
            mTxtDirPath = findViewById(R.id.txtDirPath)
            findViewById<ImageButton>(R.id.btnSelectAudio).setOnClickListener { showFileChooser() }
        }
        setDialogView(linearLayout)

        mDialog = super.onCreateDialog(savedInstanceState)
        return mDialog
    }

    override fun onStart() {
        super.onStart()
        mDialog.getButton(-1).isEnabled = checkMissingValues()
    }

    override fun setMessage(message: String): FileChooserDialogFragment {
        super.setMessage(message)
        return this
    }

    fun setPositiveButton(text: String, listener: (value: String) -> Unit): FileChooserDialogFragment {
        super.setPositiveButton(text, DialogInterface.OnClickListener { _, _ -> listener(mTxtDirPath.text.toString()) })
        return this
    }

//    fun setNegativeButton(text: String, listener: () -> Unit): FileChooserDialogFragment {
//        super.setNegativeButton(text, DialogInterface.OnClickListener { _, _ -> listener() })
//        return this
//    }

    fun setNeutralButton(text: String, listener: () -> Unit): FileChooserDialogFragment {
        super.setNeutralButton(text, DialogInterface.OnClickListener { _, _ -> listener() })
        return this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_FILE_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                mTxtDirPath.text = data?.data?.let {
                    Log.i("debug", "uri=${it.path}")
                    RealPathUtil.getRealPath(this@FileChooserDialogFragment.context!!, it)
                }?.let {
                    Log.i("debug", "real-path=$it")
                    it
                }
//                ?.let {
//                    val mediaStorageDir = File(it)
//                    try {
//                        RandomAccessFile("${mediaStorageDir.path}${File.separator}0", "rw")
//                        mTxtDirPath.text = it
//                        File("${mediaStorageDir.path}${File.separator}0").delete()
//                    } catch (e: FileNotFoundException) {
//                        Toast.makeText(this, "해당 경로에 대한 권한이 부족합니다.", Toast.LENGTH_SHORT).show()
//                    }
//                }
            } else {
                Toast.makeText(context, "파일 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showFileChooser() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        try {
            startActivityForResult(Intent.createChooser(intent, "음성 파일 선택"), REQ_FILE_SELECT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "파일 매니저를 설치해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkMissingValues(): Boolean {
        return mTxtDirPath.text.isNotEmpty()
    }

}