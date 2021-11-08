package com.lch.cl

import android.os.Environment
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
    val fileState = MutableLiveData<Pair<Boolean, MutableList<String>>>()
    private val stopFlag = AtomicBoolean(true)
    private const val SHOW_MAX_FILE_COUNT = 1000
    private val taskCount = AtomicLong(0)

    private fun log(msg: String?) {
        //Log.e("sss", "${msg}")
    }

    private fun runWatchDog() {
        watchdogThread.execute {
            while (true) {
                if (taskCount.get() == 0L && queue.isEmpty()) {
                    stopFlag.set(true)

                    sendDataChanged()
                    log("scan finished==========")
                    break
                }
                SystemClock.sleep(1000)
            }
        }
    }


    private fun startScan(f: File, filter: (File) -> Boolean) {
        files.clear()
        queue.clear()
        taskCount.set(0)
        stopFlag.set(false)


        sendDataChanged()

        addTask {
            _scan(f, filter)

            taskCount.decrementAndGet()
        }

        runWatchDog()
    }

    private fun _scan(f: File, filter: (File) -> Boolean) {
        log("scanning===========")

        if (stopFlag.get()) {
            return
        }

        if (f.isDirectory.not()) {
            insertFile(f, filter)
            return
        }

        f.listFiles()?.forEach { it ->
            if (it.isDirectory.not()) {
                insertFile(it, filter)
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


    fun scanBigFile(f: File) {
        try {

            if (!stopFlag.get()) {
                log("scan already running============")
                return
            }

            startScan(f) {
                it.length() > 1 * 1024 * 1024
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun forceStop() {
        stopFlag.set(true)
        queue.clear()
        taskCount.set(0)
    }

    private fun removeAt(index: Int) {
        if (stopFlag.get()) {
            return
        }
        files.removeAt(index)
    }


    private fun add(index: Int, path: String) {
        if (stopFlag.get()) {
            return
        }
        files.add(index, path)
    }

    private fun get(index: Int): String {
        return files.get(index)
    }

    @Synchronized
    private fun insertFile(f: File, filter: (File) -> Boolean) {
        if (stopFlag.get()) {
            return
        }

        if (!filter(f)) {
            return
        }

        if (files.size > SHOW_MAX_FILE_COUNT) {
            if (f.length() > File(get(0)).length()) {
                removeAt(0)
            } else {
                return
            }

        }

        val index = searchInsert(files, f)
        if (index >= 0) {
            add(index, f.absolutePath)

            sendDataChanged()
        }


    }

    fun isHaveData():Boolean{
        return files.isNotEmpty()
    }

    fun sendDataChanged() {
        val ret = ArrayList(files)
        ret.reverse()
        fileState.postValue(Pair(stopFlag.get(), ret))
    }

    fun del(path:String):Boolean{//repeat file.todo
        if (!stopFlag.get()) {
            return false
        }
        File(path).delete()
        if(File(path).exists()){
            return false
        }
        files.remove(path)

        sendDataChanged()

        return true
    }

    fun delMulti(list:MutableList<String>){
        if (!stopFlag.get()) {
            return
        }
        if(list.isNullOrEmpty()){
            return
        }

        list.forEach {
            File(it).delete()
            if(!File(it).exists()){
                files.remove(it)
            }
        }

        sendDataChanged()
    }


    private fun searchInsert(nums: MutableList<String>, target: File): Int {
        if (stopFlag.get()) {
            return 0
        }

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
            if (stopFlag.get()) {
                break
            }
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