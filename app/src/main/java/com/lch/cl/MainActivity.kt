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

package com.lch.cl

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.lch.cln.R
import com.lch.cln.databinding.ActivityMainBinding
import com.lch.cl.util.contentView
import com.lch.cl.util.hide
import com.lch.cl.util.show

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by contentView(R.layout.activity_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow(this)

        binding.apply {

            val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host)
            bottomNav.setupWithNavController(navController)

            // Hide bottom nav on screens which don't require it
            lifecycleScope.launchWhenResumed {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.onboarding, R.id.featured, R.id.search -> bottomNav.show()
                        else ->  bottomNav.hide()
                    }
                }
            }
        }
    }

    private fun setupWindow(activity: Activity?) {
        if (activity != null && activity.window != null) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = Color.TRANSPARENT
            }
        }
    }
}
