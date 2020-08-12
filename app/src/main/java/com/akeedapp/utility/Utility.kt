package com.diagnal.utils

import android.app.Activity
import android.util.DisplayMetrics

class Utility {

    companion object {
        fun getDisplayMetrics(activity: Activity): DisplayMetrics {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics
        }
    }

}