package com.haruu.notesome.dialogfragment

import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.haruu.notesome.R

class TextInputDialogFragment : BaseDialogFragment() {

    private lateinit var mDialog: AlertDialog
    private lateinit var mEditText: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        mEditText = TextInputEditText(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setEms(10)
            hint = getString(R.string.title_short_text)
            inputType = InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mDialog.getButton(-1).isEnabled = checkMissingValues()
                }
            })
        }

        val textInputLayout = TextInputLayout(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            isCounterEnabled = true
            counterMaxLength = 64
            isHintEnabled = true
            addView(mEditText)
        }

        val linearLayout = LinearLayout(activity).apply {
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.HORIZONTAL
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, resources.displayMetrics).toInt().let {
                setPadding(it, 0, it, 0)
            }
            addView(textInputLayout)
        }

        setDialogView(linearLayout)
        mDialog = super.onCreateDialog(savedInstanceState)
        return mDialog
    }

    override fun onStart() {
        super.onStart()
        (context?.applicationContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
            ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        mDialog.getButton(-1).isEnabled = checkMissingValues()
    }

    override fun onPause() {
        (context?.applicationContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
            ?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        super.onPause()
    }

    override fun setMessage(message: String): TextInputDialogFragment {
        super.setMessage(message)
        return this
    }

    fun setPositiveButton(text: String, listener: (value: String) -> Unit): TextInputDialogFragment {
        super.setPositiveButton(
            text,
            DialogInterface.OnClickListener { _, _ -> listener(mEditText.text?.toString()?.trim()!!) })
        return this
    }

//    fun setNegativeButton(text: String, listener: () -> Unit): TextInputDialogFragment {
//        super.setNegativeButton(text, DialogInterface.OnClickListener { _, _ -> listener() })
//        return this
//    }

    fun setNeutralButton(text: String, listener: () -> Unit): TextInputDialogFragment {
        super.setNeutralButton(text, DialogInterface.OnClickListener { _, _ -> listener() })
        return this
    }

    private fun checkMissingValues(): Boolean {
        return mEditText.text?.isNotEmpty()!!
    }

}