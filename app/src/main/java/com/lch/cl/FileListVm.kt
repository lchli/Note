package com.lch.cl

import android.app.Activity
import android.os.Environment
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lch.cl.ad.RewardAdUtil
import com.lch.cl.util.LifecycleScopeObject
import com.lch.cl.util.getStrings
import com.lch.cl.util.log
import com.lch.cln.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FileListVm : BaseVm(), Observer<Pair<Boolean, MutableList<String>>> {
    val state = MutableLiveData<Pair<Boolean, List<String>>>()
    val loading = MutableLiveData(false)
    val isCheckAll = MutableLiveData(false)
    val checkedLive = MutableLiveData<MutableList<String>>()
    private var finishedList: List<String>? = null
    private var preSort: FileSortType? = null
    private var preFilter: FileFilterType? = null
    private val checked = mutableListOf<String>()
    val checkedCount: LiveData<String> = Transformations.map(checkedLive) {
       context?.getString(R.string.choosed_items,it.size)
    }

    val delEnable: LiveData<Boolean> = Transformations.map(checkedLive) {
        it.isNotEmpty()
    }

    val delAlpha: LiveData<Float> = Transformations.map(checkedLive) {
        if (it.isNotEmpty()) {
            return@map 1.0F
        }
        return@map 0.7F
    }

    init {
        FileScanner.fileState.observeForever(this)
        checkedLive.value = checked
    }

    fun isChecked(path: String): Boolean = checked.contains(path)


    fun checkedChanged(view: View) {
        if ((view as CompoundButton).isChecked) {
            checkedAll()
        } else {
            uncheckedAll()
        }

    }

    fun listenScan() {
        loading.value = true
        if (FileScanner.isHaveData()) {
            FileScanner.sendDataChanged()
        } else {
            refresh()
        }
    }

    fun refresh() {
        loading.value = true
        preSort = null
        preFilter = null
        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())
    }


    fun sort(type: FileSortType = FileSortType.Size(FileConst.SORT_DIRECTION_ASC)) {
        loading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            if (finishedList.isNullOrEmpty()) {
                loading.postValue(false)
                return@launch
            }
            val retlist = mutableListOf<String>()

            if (preFilter != null) {
                finishedList!!.filter {
                    preFilter!!.isMatch(File(it))
                }.apply { retlist.addAll(this) }
            } else {
                retlist.addAll(finishedList!!)
            }

            type.sort(retlist)

            preSort = type

            state.postValue(Pair(true, retlist))
            loading.postValue(false)

        }

    }

    fun filter(type: FileFilterType = FileFilterType.Size(10 * 1024 * 1024L)) {
        loading.postValue(true)
        uncheckedAll()
        isCheckAll.postValue(false)

        viewModelScope.launch(Dispatchers.IO) {
            if (finishedList.isNullOrEmpty()) {
                loading.postValue(false)
                return@launch
            }
            finishedList!!.filter {
                type.isMatch(File(it))
            }.apply {
                val ml = this.toMutableList()
                if (preSort != null) {
                    preSort!!.sort(ml)
                }

                preFilter = type

                state.postValue(Pair(true, ml))
            }

            loading.postValue(false)

        }

    }


    override fun onCleared() {
        super.onCleared()
        FileScanner.fileState.removeObserver(this)
    }

    override fun onChanged(it: Pair<Boolean, MutableList<String>>?) {
        if (it == null) {
            return
        }
        uncheckedAll()
        isCheckAll.postValue(false)

        if (it.first) {//finished
            this.finishedList = it.second
            loading.postValue(false)
        }
        var retlist = it.second

        preFilter?.apply {
            retlist = retlist.filter {
                isMatch(File(it))
            } as MutableList<String>
        }
        preSort?.sort(retlist)

        state.postValue(Pair(false, retlist))
    }


    fun checked(path: String) {
        if (!checked.contains(path)) {
            checked.add(path)

            checkedLive.postValue(checked)
        }

    }

    fun uncheked(path: String) {
        checked.remove(path)

        checkedLive.postValue(checked)
    }

    fun checkedAll() {
        val p = state.value ?: return
        checked.clear()
        checked.addAll(p.second)

        checkedLive.postValue(checked)
    }

    fun uncheckedAll() {
        checked.clear()

        checkedLive.postValue(checked)
    }

    private fun deleteChecked() {
        if (checked.isNullOrEmpty()) {
            return
        }

        loading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            FileScanner.delMulti(checked)

            loading.postValue(false)
        }

    }

    private fun loadDelAd() {
        if (context == null) {
            log("loadDelAd context == null")
            deleteChecked()
            return
        }

        (LifecycleScopeObject.of(MainActivity::class.java).getData(RewardAdUtil::class.java.name) as? RewardAdUtil)?.show(
            context!!
        ) {
            deleteChecked()
        }
    }

    fun showDelDialog(v: View) {
        if (context !is Activity) {
            return
        }
        MaterialAlertDialogBuilder(context!!)
            .setTitle(getStrings(R.string.alert))
            .setMessage(getStrings(R.string.cannot_recovery))
            .setNegativeButton(getStrings(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getStrings(R.string.continues)) { dialog, which ->
                loadDelAd()
                dialog.dismiss()
            }
            .show()
    }


}