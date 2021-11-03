package com.lch.cl

import android.graphics.drawable.Drawable
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.materialstudies.owl.R
import com.materialstudies.owl.ui.mycourses.MyCoursesFragmentDirections
import java.io.File

class FileListUiState(val filePath:String,val vm: FileListVm) {

    fun itemClick(view: View){
        val extras = FragmentNavigatorExtras(
            view to "shared_element"
        )
        val action = MyCoursesFragmentDirections.actionOnboardingToLearn("courseId")
        view.findNavController().navigate(action, extras)
    }

    val fileName:String? by lazy {
        return@lazy File(filePath).name
    }

    val fileSize:String? by lazy {
        return@lazy CommonUtil.formatFileSize(File(filePath).length(), false)
    }

    val typeIcon:Drawable? by lazy {
        return@lazy vm.context?.getDrawable(R.drawable.course_image_placeholder)
    }
}