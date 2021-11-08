package com.lch.cl

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CompoundButton
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.lch.cln.R

import java.io.File

class FileListUiState(val filePath: String, val vm: FileListVm) {


    fun checkedChanged(view: View) {
        if ((view as CompoundButton).isChecked) {
            vm.checked(filePath)
        } else {
            vm.uncheked(filePath)
        }
    }

    fun itemClick(view: View) {

        val extras = FragmentNavigatorExtras(
            view to "shared_element"
        )
        val action = FileListFragmentDirections.actionOnboardingToLearn(filePath)
        view.findNavController().navigate(action, extras)
    }

    val isChecked: Boolean by lazy {
        vm.isChecked(filePath)
    }


    val fileName: String? by lazy {
        return@lazy File(filePath).name
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