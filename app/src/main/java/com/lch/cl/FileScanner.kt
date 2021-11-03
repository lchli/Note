package com.lch.cl

import android.os.SystemClock
import android.util.Log
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

object FileScanner {
    private val queue = LinkedBlockingDeque<Runnable>()
    private val executor = ThreadPoolExecutor(
        5, 10, 60,
        TimeUnit.SECONDS, queue
    )
    private val watchdogThread = Executors.newSingleThreadExecutor()
    private val files = mutableListOf<String>()
    val fileState = MutableLiveData<Pair<Boolean,MutableList<String>>>()
    private val stopFlag = AtomicBoolean(false)
    private const val SHOW_MAX_FILE_COUNT = 500
    private val taskCount = AtomicLong(0)

    private fun log(msg: String?) {
        Log.e("sss", "${msg}")
    }

    private fun runWatchDog() {
        watchdogThread.execute {
            while (true) {
                if (taskCount.get() == 0L && queue.isEmpty()) {
                    sendDataChanged(true)
                    log("scan finished==========")
                    break
                }
                SystemClock.sleep(1000)
            }
        }
    }


    @UiThread
    private fun startScan(f: File, filter: (File) -> Boolean) {
        stopFlag.set(false)
        files.clear()
        sendDataChanged()

        addTask {
            _scan(f, filter)

            taskCount.decrementAndGet()
        }

        runWatchDog()
    }

    @WorkerThread
    private fun _scan(f: File, filter: (File) -> Boolean) {
        log("scanning===========")

        if (stopFlag.get()) {
            return
        }

        if (f.isDirectory.not()) {
            insertFile(f,filter)
            return
        }

        f.listFiles()?.forEach { it ->
                if (it.isDirectory.not()) {
                    insertFile(it,filter)
                } else {
                    addTask(Runnable {
                        _scan(it, filter)
                        taskCount.decrementAndGet()
                    })

                }

        }

    }

    private fun addTask(r: Runnable) {
        taskCount.incrementAndGet()
        executor.execute(r)
    }


    @Synchronized
    fun del(path: String) {
        files.remove(path)

        sendDataChanged()
    }

    @UiThread
    fun scanBigFile(f: File) {
        startScan(f){
            it.length()>1 * 1024 * 1024
        }
    }

    fun forceStop() {
        stopFlag.set(true)
    }

    @Synchronized
    private fun insertFile(f: File,filter: (File) -> Boolean) {
        if(!filter(f)){
            return
        }

        if (files.size > SHOW_MAX_FILE_COUNT) {
            if (f.length() > File(files.get(0)).length()) {
                files.removeAt(0)
            } else {
                return
            }

        }

        val index = searchInsert(files, f)
        if (index >= 0) {
            files.add(index, f.absolutePath)

            sendDataChanged()
        }


    }

    private fun sendDataChanged(isFinished:Boolean=false) {
        val ret = ArrayList(files)
        ret.reverse()
        fileState.postValue(Pair(isFinished,ret))
    }

    private fun searchInsert(nums: MutableList<String>, target: File): Int {
        if (nums.isEmpty()) {
            return 0
        }

        if (nums.size == 1) {
            if (File(nums[0]).length() >= target.length()) {
                return 0
            } else {
                return 1
            }
        }

        var left = 0
        var right = nums.size - 1

        while (left <= right) {
            val mid = (left + right) / 2
            if (File(nums[mid]).length() == target.length()) {
                return mid
            } else if (File(nums[mid]).length() < target.length()) {
                left = mid + 1
            } else {
                right = mid - 1
            }
        }

        return left
    }

}