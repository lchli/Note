/*
 *   Copyright (c) 2019 Google Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under the License
 *
 *   is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *   or implied. See the License for the specific language governing permissions and limitations under
 *   the License.
 */
package com.materialstudies.owl.ui.mycourses

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.Px
import androidx.core.app.ActivityCompat
import androidx.core.util.Pair
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lch.cl.*
import com.lch.note.NoteListViewModel
import com.materialstudies.owl.R
import com.materialstudies.owl.databinding.FileOpPopupBinding
import com.materialstudies.owl.databinding.FragmentMyCoursesBinding
import com.materialstudies.owl.model.courses
import com.materialstudies.owl.util.SpringAddItemAnimator
import java.util.*
import java.util.concurrent.TimeUnit

class MyCoursesFragment : Fragment() {
    private val noteListVm: FileListVm by viewModels()
    private lateinit var mMyCoursesAdapter: MyCoursesAdapter
    private lateinit var binding: FragmentMyCoursesBinding
    private val loading = LoadingHelper()
    private var dialog:Dialog?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCoursesBinding.inflate(inflater, container, false)

        postponeEnterTransition(1000L, TimeUnit.MILLISECONDS)
        loading.container = binding.loadingContainer

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteListVm.attachContext(requireActivity())
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = noteListVm

        binding.emptyContainer.setOnClickListener {
            showNoPermission()
        }

        mMyCoursesAdapter = MyCoursesAdapter(noteListVm)

        binding.statusBar.apply {
            layoutParams.height = BarUtils.getStatusBarHeight()
        }
        binding.more.setOnClickListener {
            val popupWindow = PopupWindow(it.context)
            val bind = FileOpPopupBinding.inflate(LayoutInflater.from(it.context))
            bind.apply {
                filter.setOnClickListener {
                    popupWindow.dismiss()
                    doFilter()
                }
                sort.setOnClickListener {
                    popupWindow.dismiss()
                    doSort()
                }
                refresh.setOnClickListener {
                    popupWindow.dismiss()
                    doRefresh()
                }
            }
            popupWindow.contentView = bind.root
            popupWindow.isOutsideTouchable = true
            popupWindow.width = ConvertUtils.dp2px(150F)
            popupWindow.setBackgroundDrawable(null)
            popupWindow.elevation = 4F
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.TOP
            popupWindow.exitTransition = slideOut
            popupWindow.showAsDropDown(it)
        }
        binding.list.apply {
            itemAnimator = SpringAddItemAnimator()
            addItemDecoration(
                BottomSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.grid_2))
            )
            adapter = mMyCoursesAdapter
        }


        noteListVm.state.observe(viewLifecycleOwner) { pair ->
            if (hasSdPermission().not()) {
                binding.emptyContainer.visibility=View.VISIBLE
                return@observe
            }

            binding.emptyContainer.visibility=View.GONE

            mMyCoursesAdapter.submitList(pair.second)

            if (pair.first) {
                binding.list.postDelayed({
                    binding.list.scrollToPosition(0)
                }, 500)
            } else {
                binding.list.doOnNextLayout {
                    startPostponedEnterTransition()

                }

            }


        }

        noteListVm.loading.observe(viewLifecycleOwner) {
            if (it) {
                loading.showLoading(requireActivity(), "正在扫描中，请稍等...")
            } else {
                loading.hideLoading()
            }
        }
        noteListVm.checkedLive.observe(viewLifecycleOwner) {
            mMyCoursesAdapter.notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()
        tryRefresh()
    }

    private fun tryRefresh(){
        if(hasSdPermission()){
            noteListVm.listenScan()
        }
    }

    private fun showNoPermission() {
        if(dialog!=null&&dialog!!.isShowing){
            return
        }

        dialog=MaterialAlertDialogBuilder(requireActivity())
            .setTitle("提示")
            .setMessage("文件扫描需要授权读取手机存储的权限，是否继续？")
            .setOnDismissListener {
                dialog=null
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("继续") { dialog, which ->
                openPermission()
                dialog.dismiss()
            }.create()

        dialog?.show()



    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    private fun openPermission() {
        if (Build.VERSION.SDK_INT < 30) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)&&
                SPUtils.getInstance().getBoolean(SpKey.is_per_denyed,false)){
                PermissionUtils.launchAppDetailsSettings()
                return
            }
            PermissionUtils.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(object : PermissionUtils.FullCallback {
                    override fun onGranted(granted: MutableList<String>) {
                        tryRefresh()
                    }

                    override fun onDenied(
                        deniedForever: MutableList<String>,
                        denied: MutableList<String>
                    ) {
                        ToastUtils.showLong("授权失败")
                        SPUtils.getInstance().put(SpKey.is_per_denyed,true)
                    }

                }).request()

            return
        }


        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        startActivity(intent)
    }

    private fun hasSdPermission(): Boolean {
        if (Build.VERSION.SDK_INT < 30) {
            return PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return Environment.isExternalStorageManager()

    }


    private fun doFilter() {
        val items = arrayOf("无", "文件大小", "文件类型", "修改时间")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择过滤方式")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> noteListVm.filter(FileFilterType.No)
                    1 -> filterBySize()
                    2 -> filterByType()
                    3 -> filterByDate()
                }
            }
            .show()
    }

    private fun filterBySize() {
        val items = arrayOf("10m", "100m", "500m", "1g")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择文件大小")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> noteListVm.filter(FileFilterType.Size(10 * 1024 * 1024L))
                    1 -> noteListVm.filter(FileFilterType.Size(100 * 1024 * 1024L))
                    2 -> noteListVm.filter(FileFilterType.Size(500 * 1024 * 1024L))
                    3 -> noteListVm.filter(FileFilterType.Size(1024 * 1024 * 1024L))
                }
            }
            .show()
    }

    private fun filterByType() {
        val items = arrayOf("视频", "音频", "图片", "压缩文件", "PDF", "文档", "apk", "其它")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择类型")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> noteListVm.filter(FileFilterType.Category(Mime.Video))
                    1 -> noteListVm.filter(FileFilterType.Category(Mime.Audio))
                    2 -> noteListVm.filter(FileFilterType.Category(Mime.Img))
                    3 -> noteListVm.filter(FileFilterType.Category(Mime.Zip))
                    4 -> noteListVm.filter(FileFilterType.Category(Mime.Pdf))
                    5 -> noteListVm.filter(FileFilterType.Category(Mime.Doc))
                    6 -> noteListVm.filter(FileFilterType.Category(Mime.Apk))
                    7 -> noteListVm.filter(FileFilterType.Category(Mime.Other))
                }
            }
            .show()
    }

    private fun filterByDate() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("选择日期范围")
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()
        dateRangePicker.addOnPositiveButtonClickListener {
            noteListVm.filter(FileFilterType.Date(it.first, it.second))
        }

        dateRangePicker.show(childFragmentManager, "date-picker")
    }

    private fun doSort() {
        val items = arrayOf("文件大小", "修改时间")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择排序方式")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> sortBySize()
                    1 -> sortByDate()
                }
            }
            .show()
    }

    private fun sortByDate() {
        val items = arrayOf("升序", "降序")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择排序方式")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> noteListVm.sort(FileSortType.Time(FileConst.SORT_DIRECTION_ASC))
                    1 -> noteListVm.sort(FileSortType.Time(FileConst.SORT_DIRECTION_DESC))
                }
            }
            .show()
    }

    private fun sortBySize() {
        val items = arrayOf("升序", "降序")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择排序方式")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> noteListVm.sort(FileSortType.Size(FileConst.SORT_DIRECTION_ASC))
                    1 -> noteListVm.sort(FileSortType.Size(FileConst.SORT_DIRECTION_DESC))
                }
            }
            .show()
    }

    private fun doRefresh() {
        if(hasSdPermission()) {
            noteListVm.refresh()
        }
    }
}


/**
 * A [RecyclerView.ItemDecoration] which adds the given `spacing` to the bottom of any view,
 * unless it is the last item.
 */
class BottomSpacingItemDecoration(
    @Px private val spacing: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val lastItem = parent.getChildAdapterPosition(view) == state.itemCount - 1
        outRect.bottom = if (!lastItem) spacing else 0
    }
}
