package com.haruu.notesome.utils

import android.util.DisplayMetrics
import android.util.TypedValue
import java.util.*

object Utils {

    fun dipToPx(dip: Float, metrics: DisplayMetrics): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics)
    }

}