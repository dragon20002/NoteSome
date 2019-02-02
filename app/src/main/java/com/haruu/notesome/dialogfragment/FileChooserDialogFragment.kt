package com.haruu.notesome.dialogfragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.haruu.notesome.R
import java.io.InputStream


private const val REQ_FILE_SELECT: Int = 1

class FileChooserDialogFragment : BaseDialogFragment() {

    private var mInputStream: InputStream? = null

    private lateinit var mDialog: AlertDialog
    private lateinit var mTxtDirPath: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        @SuppressLint("InflateParams")
        val linearLayout =
            LayoutInflater.from(context).inflate(R.layout.dialogfragment_filechooser, null)
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

    fun setPositiveButton(
        text: String,
        listener: (value: String, inputStream: InputStream) -> Unit
    ): FileChooserDialogFragment {
        super.setPositiveButton(
            text,
            DialogInterface.OnClickListener { _, _ ->
                mInputStream?.let { listener(mTxtDirPath.text.toString(), it) }
            })
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
                mInputStream = data?.data?.let {
                    mTxtDirPath.text = it.lastPathSegment?.substringAfterLast('/')
                    context?.contentResolver?.openInputStream(it)
                }

            } else {
                Toast.makeText(context, "파일 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showFileChooser() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
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