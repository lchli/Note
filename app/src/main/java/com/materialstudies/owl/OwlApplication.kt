/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.materialstudies.owl

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import com.bilibili.boxing.BoxingMediaLoader
import com.lch.cl.Contexter
import com.lch.cl.FileScanner
import com.lch.note.IBoxingMediaLoaderImpl

class OwlApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Contexter.bindContext(this)

        val nightMode = if (SDK_INT >= Q) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)

        BoxingMediaLoader.getInstance().init(IBoxingMediaLoaderImpl())

        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())
    }

}
