package com.haruu.notesome.dialogfragment

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import com.haruu.notesome.R

class TextInputDialogFragment : BaseDialogFragment() {

    private lateinit var mDialog: AlertDialog
    private lateinit var mEditText: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val linearLayout = LinearLayout(activity)
        linearLayout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, resources.displayMetrics).toInt()
        linearLayout.setPadding(padding, 0, padding, 0)

        val commonsLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val textInputLayout = TextInputLayout(context)
        textInputLayout.layoutParams = commonsLayoutParams
        textInputLayout.isCounterEnabled = true
        textInputLayout.counterMaxLength = 64
        textInputLayout.isHintEnabled = true

        mEditText = TextInputEditText(context)
        mEditText.layoutParams = commonsLayoutParams
        mEditText.setEms(10)
        mEditText.hint = getString(R.string.title_short_text)
        mEditText.inputType = InputType.TYPE_CLASS_TEXT
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        mEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mDialog.getButton(-1).isEnabled = checkMissingValues()
            }
        })

        textInputLayout.addView(mEditText)
        linearLayout.addView(textInputLayout)

        setDialogView(linearLayout)
        mDialog = super.onCreateDialog(savedInstanceState)
        return mDialog
    }

    override fun onStart() {
        super.onStart()
        mDialog.getButton(-1).isEnabled = checkMissingValues()
    }

    override fun setMessage(message: String): TextInputDialogFragment {
        super.setMessage(message)
        return this
    }

    fun setPositiveButton(text: String, listener: (value: String) -> String): TextInputDialogFragment {
        super.setPositiveButton(text, DialogInterface.OnClickListener { _, _ -> listener(mEditText.text?.toString()?.trim()!!) })
        return this
    }

//    fun setNegativeButton(text: String, listener: () -> String): TextInputDialogFragment {
//        super.setNegativeButton(text, DialogInterface.OnClickListener { _, _ -> listener() })
//        return this
//    }

    fun setNeutralButton(text: String, listener: () -> String): TextInputDialogFragment {
        super.setNeutralButton(text, DialogInterface.OnClickListener { _, _ -> listener() })
        return this
    }

    private fun checkMissingValues(): Boolean {
        return mEditText.text?.isNotEmpty()!!
    }

}