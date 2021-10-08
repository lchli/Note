package com.lch.note

import android.content.Context
import android.util.Log
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class NoteDataStore(val context: Context) : NoteRepo {


    override suspend fun getNotes(): List<NoteMetaData> {
        return withContext(Dispatchers.IO) {
            val list = ArrayList<NoteMetaData>()
            val rootDir = NoteConst.getNoteRootDir(context)

            File(rootDir).listFiles()?.forEach {
                try {
                    if (!it.isDirectory) {
                        FileUtils.delete(it)
                        return@forEach
                    }
                    val metaFile = File(it, NoteConst.NOTE_META_FILE)
                    if (!metaFile.exists()) {
                        FileUtils.delete(it)
                        return@forEach
                    }
                    val str = FileIOUtils.readFile2String(metaFile)
                    if (str.isNullOrEmpty()) {
                        FileUtils.delete(it)
                        return@forEach
                    }

                    JSONObject(str).apply {
                        val id = optString("id")
                        val title = optString("title")
                        val modifyTime = optLong("modifyTime")
                        if (!id.isNullOrEmpty() && !title.isNullOrEmpty() && modifyTime != 0L) {
                            list.add(NoteMetaData(id, title, modifyTime))
                        }
                    }


                } catch (e: Throwable) {
                    e.printStackTrace()
                    FileUtils.delete(it)
                }

            }

            return@withContext list
        }
    }

    override suspend fun save(noteMeta: NoteMetaData, content: List<Any>): Boolean {
        return withContext(Dispatchers.IO) {
            var isSuccess = false
            val noteDir = File(NoteConst.getNoteRootDir(context) + noteMeta.id)
            noteDir.mkdirs()

            if(!noteDir.exists()){
                Log.e("sss","create note dir fail.")
                return@withContext isSuccess
            }

            try {
                val metaFile = File(noteDir, NoteConst.NOTE_META_FILE)
                FileUtils.createOrExistsFile(metaFile)
                if(!metaFile.exists()){
                    Log.e("sss","create note metaFile fail.")
                    return@withContext isSuccess
                }

                val contentFile = File(noteDir, NoteConst.NOTE_CONTENT_FILE)
                FileUtils.createOrExistsFile(contentFile)

                val metaJson = JSONObject().apply {
                    put("id", noteMeta.id)
                    put("title", noteMeta.title)
                    put("modifyTime", noteMeta.modifyTimeMills)
                }

                FileIOUtils.writeFileFromString(metaFile, metaJson.toString())

                val usedRes = ArrayList<String>()

                val contentJson = JSONArray()
                content.forEach {
                    when (it) {
                        is NoteContentText -> {
                            JSONObject().apply {
                                put("type", NoteConst.TEXT)
                                put("text", it.text)
                                contentJson.put(this)
                            }

                        }

                        is NoteContentImg -> {
                            val resFile = File(it.resPath)
                            if (resFile.parent != noteDir.absolutePath) {
                                val newFile = File(noteDir, UUID.randomUUID().toString())
                                val suc = FileUtils.copy(resFile, newFile)
                                if (!suc) {
                                    isSuccess = false
                                    return@withContext isSuccess
                                }

                                it.resPath = newFile.absolutePath
                            }

                            usedRes.add(it.resPath)

                            JSONObject().apply {
                                put("type", NoteConst.IMG)
                                put("resPath", it.resPath)
                                contentJson.put(this)
                            }

                        }

                        is NoteContentAudio -> {
                            val resFile = File(it.resPath)
                            if (resFile.parent != noteDir.absolutePath) {
                                val newFile = File(noteDir, UUID.randomUUID().toString())
                                val suc = FileUtils.copy(resFile, newFile)
                                if (!suc) {
                                    isSuccess = false
                                    return@withContext isSuccess
                                }

                                it.resPath = newFile.absolutePath
                            }

                            usedRes.add(it.resPath)

                            JSONObject().apply {
                                put("type", NoteConst.AUDIO)
                                put("resPath", it.resPath)
                                contentJson.put(this)
                            }

                        }

                        is NoteContentVideo -> {
                            val resFile = File(it.resPath)
                            if (resFile.parent != noteDir.absolutePath) {
                                val newFile = File(noteDir, UUID.randomUUID().toString())
                                val suc = FileUtils.copy(resFile, newFile)
                                if (!suc) {
                                    isSuccess = false
                                    return@withContext isSuccess
                                }

                                it.resPath = newFile.absolutePath
                            }

                            usedRes.add(it.resPath)

                            JSONObject().apply {
                                put("type", NoteConst.VIDEO)
                                put("resPath", it.resPath)
                                contentJson.put(this)
                            }

                        }
                    }
                }

                FileIOUtils.writeFileFromString(contentFile, contentJson.toString())

                val dirFiles = noteDir.listFiles()
                dirFiles?.forEach {
                    if (it.absolutePath != metaFile.absolutePath && it.absolutePath != contentFile.absolutePath &&
                            !usedRes.contains(it.absolutePath)) {
                        FileUtils.delete(it)
                    }

                }


                isSuccess = true
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {

                if (!isSuccess) {
                    FileUtils.delete(noteDir)
                }
            }

            return@withContext isSuccess
        }
    }


    override suspend fun getNoteContent(id: String): List<Any>? {
        return withContext(Dispatchers.IO) {
            val noteDir = File(NoteConst.getNoteRootDir(context) + id)
            if (!noteDir.exists()) {
                return@withContext null
            }
            if (!noteDir.isDirectory) {
                return@withContext null
            }
            val contentFile = File(noteDir, NoteConst.NOTE_CONTENT_FILE)
            if (!contentFile.exists()) {
                return@withContext null
            }

            val str = FileIOUtils.readFile2String(contentFile)
            if (str.isNullOrEmpty()) {
                return@withContext null
            }
            val list = ArrayList<Any>()
            val jarr = JSONArray(str)
            for (i in 0 until jarr.length()) {
                val obj = jarr.optJSONObject(i) ?: continue
                val type = obj.optString("type")
                if (type.isNullOrEmpty()) {
                    continue
                }

                when (type) {
                    NoteConst.TEXT -> {
                        list.add(NoteContentText(obj.optString("text")))
                    }
                    NoteConst.IMG -> {
                        list.add(NoteContentImg(obj.optString("resPath")))
                    }
                    NoteConst.AUDIO -> {
                        list.add(NoteContentAudio(obj.optString("resPath")))
                    }
                    NoteConst.VIDEO -> {
                        list.add(NoteContentVideo(obj.optString("resPath")))
                    }
                }

            }

            return@withContext list
        }
    }

}