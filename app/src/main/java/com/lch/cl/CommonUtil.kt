package com.lch.cl

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object CommonUtil {


    var dateFormatHM = "HH:mm"


    fun formatData(str: String?, j: Long): String? {
        return if (j == 0L) {
            ""
        } else try {
            SimpleDateFormat(str).format(Date(j))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    fun formatFileSize(j: Long, z: Boolean): String? {
        val strings = formatFileSizeArray(j, z)
        return strings[0] + strings[1]
    }


    fun formatSizeWithThousand(size: Long, pointed: Boolean): Array<String?> {
        val decimalFormat = if (pointed) {
            DecimalFormat("0")
        } else {
            DecimalFormat("0.0")
        }
        decimalFormat.isGroupingUsed = false
        val strArray = arrayOfNulls<String>(2)
        if (size <= 0) {
            strArray[0] = "0"
            strArray[1] = "B"
            return strArray
        }
        if (size < 1000) {
            strArray[0] = decimalFormat.format(size)
            strArray[1] = "B"
            return strArray
        }
        if (size < 1000000) {
            strArray[0] = decimalFormat.format((size.toFloat() / 1000.0f).toDouble())
            strArray[1] = "KB"
            return strArray
        }
        if (size < 1000000000) {
            strArray[0] = decimalFormat.format((size * 1.0f / 1000000).toDouble())
            strArray[1] = "MB"
            return strArray
        }
        strArray[0] = DecimalFormat("0.0").format((size * 1.0f / 1000000000).toDouble())
        strArray[1] = "GB"
        return strArray
    }


    fun checkIsNextDay(lastTime: Long): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
        val lastDate = sdf.format(Date(lastTime))
        val currentDate = sdf.format(Date())
        return currentDate.toInt() > lastDate.toInt()
    }

    fun formatFileSizeArray(j: Long, z: Boolean): Array<String?> {
        val decimalFormat: DecimalFormat = if (z) {
            DecimalFormat("0")
        } else {
            DecimalFormat("0.0")
        }
        decimalFormat.isGroupingUsed = false
        val strArray = arrayOfNulls<String>(2)
        val str = "0B"
        if (j <= 0) {
            strArray[0] = "0"
            strArray[1] = "B"
            return strArray
        }
        if (j < 1024) {
            strArray[0] = decimalFormat.format(j)
            strArray[1] = "B"
            return strArray
        }
        if (j < 1024000) {
            strArray[0] = decimalFormat.format((j.toFloat() / 1024.0f).toDouble())
            strArray[1] = "KB"
            return strArray
        }
        if (j < 1048576000) {
            strArray[0] = decimalFormat.format(((j shr 10).toFloat() / 1024.0f).toDouble())
            strArray[1] = "MB"
            return strArray
        }
        strArray[0] = DecimalFormat("0.0").format(((j shr 20).toFloat() / 1024.0f).toDouble())
        strArray[1] = "GB"
        return strArray
    }


}