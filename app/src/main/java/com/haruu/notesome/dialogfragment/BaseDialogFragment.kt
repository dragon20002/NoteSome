package com.haruu.notesome.dialogfragment

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View


open class BaseDialogFragment : DialogFragment() {
    data class DialogButton(val text: String, val listener: DialogInterface.OnClickListener)

    private var mMessage: String? = null
    private var mView: View? = null
    private var mPositiveButton: DialogButton? = null
    private var mNegativeButton: DialogButton? = null
    private var mNeutralButton: DialogButton? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setMessage(mMessage)
        if (mView != null)
            builder.setView(mView)
        if (mPositiveButton != null)
            builder.setPositiveButton(mPositiveButton?.text, mPositiveButton?.listener)
        if (mNegativeButton != null)
            builder.setNegativeButton(mNegativeButton?.text, mNegativeButton?.listener)
        if (mNeutralButton != null)
            builder.setNeutralButton(mNeutralButton?.text, mNeutralButton?.listener)

        isCancelable = false
        return builder.create()
    }

    open fun setMessage(message: String): BaseDialogFragment {
        mMessage = message
        return this
    }

    fun setDialogView(view: View): BaseDialogFragment {
        mView = view
        return this
    }

    fun setPositiveButton(text: String, listener: DialogInterface.OnClickListener): BaseDialogFragment {
        mPositiveButton = DialogButton(text, listener)
        return this
    }

//    fun setNegativeButton(text: String, listener: DialogInterface.OnClickListener): BaseDialogFragment {
//        mNegativeButton = DialogButton(text, listener)
//        return this
//    }

    fun setNeutralButton(text: String, listener: DialogInterface.OnClickListener): BaseDialogFragment {
        mNeutralButton = DialogButton(text, listener)
        return this
    }
}