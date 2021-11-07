package com.lch.cl

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.materialstudies.owl.BuildConfig
import com.materialstudies.owl.R
import com.materialstudies.owl.ui.mycourses.MyCoursesFragmentDirections
import com.materialstudies.owl.util.log
import java.io.File

class FileDetailUiState(val filePath: String, private val vm: FileDetailVm) {

    fun backClick(view: View) {
        vm.backClick(view)
    }

    fun showDelDialog(v: View) {
        vm.showDelDialog(v, filePath)
    }

    fun sendClick(v: View) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        val picFile = File(filePath)
        if (picFile.exists()) {
            val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    v.context,
                    "${BuildConfig.APPLICATION_ID}.file.provider",
                    picFile
                )
            } else {
                Uri.fromFile(picFile)
            }

            intent.putExtra(Intent.EXTRA_STREAM, uri)
        }
        intent.type = File(filePath).mimeType()

        (v.context as? Activity)?.startActivityForResult(intent, 101)
    }

    fun openClick(v: View) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory("android.intent.category.DEFAULT");
            intent.type = File(filePath).mimeType()
            intent.data = FileProvider.getUriForFile(
                v.context,
                "${BuildConfig.APPLICATION_ID}.file.provider", File(filePath)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            v.context.startActivity(intent)
        } catch (e: Throwable) {
            e.printStackTrace()
            ToastUtils.showLong("未找到能打开文件的应用")
        }
    }

    val fileName: String? by lazy {
        return@lazy File(filePath).name
    }

    val fileDate: String? by lazy {
        return@lazy TimeUtils.getFriendlyTimeSpanByNow(File(filePath).lastModified())
    }

    val fileSize: String? by lazy {
        return@lazy CommonUtil.formatFileSize(File(filePath).length(), false)
    }

    val typeIcon: Drawable? by lazy {
        val f = File(filePath)
        if (Mime.Video.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_video)
        } else if (Mime.Audio.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_audio)
        } else if (Mime.Img.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_img)
        } else if (Mime.Zip.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_zip)
        } else if (Mime.Pdf.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_pdf)
        } else if (Mime.Doc.isMatch(f)) {
            return@lazy vm.context?.getDrawable(R.drawable.mime_doc)
        }

        return@lazy vm.context?.getDrawable(R.drawable.mime_other)
    }
}