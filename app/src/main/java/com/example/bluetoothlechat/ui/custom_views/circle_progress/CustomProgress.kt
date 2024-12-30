package com.example.bluetoothlechat.ui.custom_views.circle_progress

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.example.bluetoothlechat.R

class CustomProgress {

    private var mDialog: Dialog?=null

    fun show(activity: Activity) {
        mDialog = Dialog(activity)
        mDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog?.window?.setBackgroundDrawable(
            ColorDrawable(0)
        )
        mDialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        mDialog?.setContentView(R.layout.dialog_progress_circle)
        mDialog?.setCancelable(false)
        mDialog?.show()
    }

    fun hide() {
        mDialog?.dismiss()
    }
}