package com.lch.cl

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
import com.google.android.gms.ads.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lch.cl.*
import com.lch.cl.ad.AdIds
import com.lch.cl.util.SpringAddItemAnimator
import com.lch.cl.util.log
import com.lch.cln.R
import com.lch.cln.databinding.FileOpPopupBinding
import com.lch.cln.databinding.FragmentMyCoursesBinding
import java.util.*
import java.util.concurrent.TimeUnit

class FileListFragment : Fragment() {
    private val noteListVm: FileListVm by viewModels()
    private lateinit var mMyCoursesAdapter: MyCoursesAdapter
    private lateinit var binding: FragmentMyCoursesBinding
    private val loading = LoadingHelper()
    private var dialog: Dialog? = null

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
                binding.emptyContainer.visibility = View.VISIBLE
                return@observe
            }

            binding.emptyContainer.visibility = View.GONE

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
                loading.showLoading(requireActivity(), "scanning,please waiting...")
            } else {
                loading.hideLoading()
            }
        }
        noteListVm.checkedLive.observe(viewLifecycleOwner) {
            mMyCoursesAdapter.notifyDataSetChanged()
        }

        loadBannerAd()
    }

    private fun loadBannerAd() {
        try {

            val adView = AdView(requireActivity())
            adView.adSize = AdSize.BANNER
            adView.adUnitId = AdIds.banner_ad
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    log("onAdLoaded")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    log("onAdFailedToLoad:$p0")
                }
            }
            binding.adContainer.addView(adView)

            adView.loadAd(AdRequest.Builder().build())

        } catch (e: Exception) {
        }

    }

    override fun onResume() {
        super.onResume()
        tryRefresh()
    }

    private fun tryRefresh() {
        if (hasSdPermission()) {
            noteListVm.listenScan()
        }
    }

    private fun showNoPermission() {
        if (dialog != null && dialog!!.isShowing) {
            return
        }

        dialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle("alert")
            .setMessage("file scanner need read external storage permission,continue?")
            .setOnDismissListener {
                dialog = null
            }
            .setNegativeButton("cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("continue") { dialog, which ->
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
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) &&
                SPUtils.getInstance().getBoolean(SpKey.is_per_denyed, false)
            ) {
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
                        ToastUtils.showLong("grant permission fail.")
                        SPUtils.getInstance().put(SpKey.is_per_denyed, true)
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
        val items = arrayOf("none", "file size", "file type", "file last modify time")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("choose filter way")
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
            .setTitle("choose file size")
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
        val items = arrayOf("video", "audio", "image", "zip", "PDF", "doc", "apk", "other")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("choose file type")
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
                .setTitleText("please choose date range")
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
        val items = arrayOf("file size", "file last modify time")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("choose sort way")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> sortBySize()
                    1 -> sortByDate()
                }
            }
            .show()
    }

    private fun sortByDate() {
        val items = arrayOf("asc", "desc")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("choose sort direction")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> noteListVm.sort(FileSortType.Time(FileConst.SORT_DIRECTION_ASC))
                    1 -> noteListVm.sort(FileSortType.Time(FileConst.SORT_DIRECTION_DESC))
                }
            }
            .show()
    }

    private fun sortBySize() {
        val items = arrayOf("asc", "desc")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("choose sort direction")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> noteListVm.sort(FileSortType.Size(FileConst.SORT_DIRECTION_ASC))
                    1 -> noteListVm.sort(FileSortType.Size(FileConst.SORT_DIRECTION_DESC))
                }
            }
            .show()
    }

    private fun doRefresh() {
        if (hasSdPermission()) {
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
