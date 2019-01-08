package com.haruu.notesome.dialogfragment

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.haruu.notesome.R

private const val REQ_FILE_SELECT: Int = 1

class FileChooserDialogFragment : BaseDialogFragment() {

    private lateinit var mDialog: AlertDialog
    private lateinit var mTextView: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val linearLayout = LinearLayout(activity)
        linearLayout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        linearLayout.orientation = LinearLayout.VERTICAL
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, resources.displayMetrics).toInt()
        linearLayout.setPadding(padding, 0, padding, 0)

        val commonsLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val button = Button(context)
        button.layoutParams = commonsLayoutParams
        button.text = getString(R.string.title_audio)
        button.setOnClickListener { showFileChooser() }

        mTextView = TextView(context)
        mTextView.layoutParams = commonsLayoutParams

        linearLayout.addView(button)
        linearLayout.addView(mTextView)
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

    fun setPositiveButton(text: String, listener: (value: String) -> String): FileChooserDialogFragment {
        super.setPositiveButton(text, DialogInterface.OnClickListener { _, _ -> listener(mTextView.text.toString()) })
        return this
    }

//    fun setNegativeButton(text: String, listener: () -> String): FileChooserDialogFragment {
//        super.setNegativeButton(text, DialogInterface.OnClickListener { _, _ -> listener() })
//        return this
//    }

    fun setNeutralButton(text: String, listener: () -> String): FileChooserDialogFragment {
        super.setNeutralButton(text, DialogInterface.OnClickListener { _, _ -> listener() })
        return this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_FILE_SELECT) {
            if (resultCode == -1) {
                val uri: Uri? = data?.data
                Log.i("debug", "파일 Uri : " + uri?.toString())
                val path: String? = uri?.path
                Log.i("debug", "파일 Path : $path")
                mTextView.text = path
            } else {
                Toast.makeText(context, "파일 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            startActivityForResult(Intent.createChooser(intent, "음성 파일 선택"), REQ_FILE_SELECT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "파일 매니저를 설치해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkMissingValues(): Boolean {
        return mTextView.text.isNotEmpty()
    }

}