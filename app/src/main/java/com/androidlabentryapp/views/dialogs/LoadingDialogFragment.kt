package com.androidlabentryapp.views.dialogs

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.androidlabentryapp.R

class LoadingDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val activity = requireActivity()
        val layout = activity.layoutInflater.inflate(R.layout.dialog_loading, null)
        return AlertDialog.Builder(activity).create().apply {
            setCanceledOnTouchOutside(false)
            setView(layout)
        }
    }
}