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

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.Px
import androidx.core.util.Pair
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lch.cl.FileConst
import com.lch.cl.FileFilterType
import com.lch.cl.FileListVm
import com.lch.cl.FileSortType
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMyCoursesBinding.inflate(inflater, container, false)


        postponeEnterTransition(1000L, TimeUnit.MILLISECONDS)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteListVm.attachContext(requireActivity())
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
            popupWindow.showAsDropDown(it)
        }
        binding.list.apply {
            itemAnimator = SpringAddItemAnimator()
            addItemDecoration(
                BottomSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.grid_2))
            )
            adapter = mMyCoursesAdapter
        }

        noteListVm.state.observe(viewLifecycleOwner) { list ->
            mMyCoursesAdapter.submitList(list)

            binding.list.post {
               binding.list.smoothScrollToPosition(0)
            }

//            binding.list.doOnNextLayout {
//                startPostponedEnterTransition()
//
//            }

        }
        noteListVm.isFinished.observe(viewLifecycleOwner) {
            ToastUtils.showLong("finished")
        }

        noteListVm.listenScan()
    }


    private fun doFilter() {
        val items = arrayOf("文件大小", "文件类型", "修改时间")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择过滤方式")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> filterBySize()
                    1 -> filterByType()
                    2 -> filterByDate()
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
        val items = arrayOf("视频","音频","图片","其它")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择类型")
            .setItems(items) { dialog, which ->
                when(which){
                    0->noteListVm.filter(FileFilterType.Category(0))
                    1->noteListVm.filter(FileFilterType.Category(0))
                    2->noteListVm.filter(FileFilterType.Category(0))
                    3->noteListVm.filter(FileFilterType.Category(0))
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
            noteListVm.filter(FileFilterType.Date(it.first,it.second))
        }

        dateRangePicker.show(childFragmentManager,"date-picker")
    }

    private fun doSort() {
        val items = arrayOf("文件大小", "修改时间")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择排序方式")
            .setItems(items) { dialog, which ->
               when(which){
                   0->sortBySize()
                   1->sortByDate()
               }
            }
            .show()
    }

    private fun sortByDate() {
        val items = arrayOf("升序", "降序")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择排序方式")
            .setItems(items) { dialog, which ->
                when(which){
                    0->noteListVm.sort(FileSortType.Time(FileConst.SORT_DIRECTION_ASC))
                    1->noteListVm.sort(FileSortType.Time(FileConst.SORT_DIRECTION_DESC))
                }
            }
            .show()
    }

    private fun sortBySize() {
        val items = arrayOf("升序", "降序")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择排序方式")
            .setItems(items) { dialog, which ->
                when(which){
                    0->noteListVm.sort(FileSortType.Size(FileConst.SORT_DIRECTION_ASC))
                    1->noteListVm.sort(FileSortType.Size(FileConst.SORT_DIRECTION_DESC))
                }
            }
            .show()
    }

    private fun doRefresh() {
        noteListVm.refresh()
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
