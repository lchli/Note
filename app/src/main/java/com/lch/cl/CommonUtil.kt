package com.lch.cl

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object CommonUtil {
    fun isAppInstalled(context: Context, pkgName: String?): Boolean {
        val pm = context.packageManager
        if (pm != null) {
            try {
                pm.getPackageInfo(pkgName!!, 0)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
            }
        }
        return false
    }

    fun hasSDCard(): Boolean {
        return try {
            Environment.getExternalStorageState() == "mounted"
        } catch (e: java.lang.Exception) {
            false
        }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getAppLabel(context: Context, packageName: String?): CharSequence? {
        val manager = context.packageManager
        val applicationInfo = manager.getApplicationInfo(
            packageName!!,
            PackageManager.GET_META_DATA
        )
        return applicationInfo.loadLabel(manager)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getAppIcon(context: Context, packageName: String?): Drawable? {
        val manager = context.packageManager
        val applicationInfo = manager.getApplicationInfo(
            packageName!!,
            PackageManager.GET_META_DATA
        )
        return applicationInfo.loadIcon(manager)
    }

    var dateFormatHM = "HH:mm"


    fun getFriendlyTime(time: Long): String? {
        val date = Date()
        date.time = time
        val timeInMillis =
            (Calendar.getInstance().timeInMillis / 86400000 - date.time / 86400000).toInt()
        return if (timeInMillis <= 0) {
            formatData(dateFormatHM, time)
        } else timeInMillis.toString() + "天前"
    }


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

    fun parseTime(seconds: Long, showSeconds: Boolean): String {
        val day = TimeUnit.SECONDS.toDays(seconds).toInt()
        val hours = TimeUnit.SECONDS.toHours(seconds) - TimeUnit.DAYS.toHours(
            TimeUnit.SECONDS.toDays(seconds)
        )
        val minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(
            TimeUnit.SECONDS.toHours(seconds)
        )
        val second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.SECONDS.toMinutes(seconds)
        )
        val builder = StringBuilder()
        if (day > 0) {
            builder.append(day).append("天")
        }
        if (hours > 0) {
            builder.append(hours).append("小时")
        }
        if (minute > 0) {
            builder.append(minute).append("分钟")
        }
        if (second > 0 && showSeconds) {
            builder.append(second).append("秒")
        }
        return builder.toString()
    }

    fun isSystemApp(info: ApplicationInfo): Boolean {
        return info.flags and 1 > 0
    }

    fun formatFileSize(j: Long, z: Boolean): String? {
        val strings = formatFileSizeArray(j, z)
        return strings[0] + strings[1]
    }

    fun formatSizeThousand(size: Long, pointed: Boolean): String? {
        val strings = formatSizeWithThousand(size, pointed)
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

    // 检查是否已添加Widget, 返回appWidgetId则代表已添加
    fun isWidgetAdded(context: Context?, cls: Class<*>?): Boolean {
        val instance = AppWidgetManager.getInstance(context)
        val myProvider = ComponentName(context!!, cls!!)
        val appWidgetIds = instance.getAppWidgetIds(myProvider)
        return appWidgetIds.isNotEmpty() && appWidgetIds[0] > 0
    }


    private fun requestPinWidget(context: Context, cls: Class<*>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val myProvider = ComponentName(context, cls)
                try {
                    appWidgetManager.requestPinAppWidget(myProvider, null, null)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }







}