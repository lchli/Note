

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
import com.lch.cl.ad.InterAdUtil
import com.lch.cl.ad.RewardAdUtil
import com.lch.cl.util.ActivityScopeStore
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

        ActivityScopeStore.of(this::class.java)[RewardAdUtil::class.java] = RewardAdUtil()
        ActivityScopeStore.of(this::class.java)[InterAdUtil::class.java] = InterAdUtil()

        binding.apply {

            val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host)
            bottomNav.setupWithNavController(navController)

            // Hide bottom nav on screens which don't require it
            lifecycleScope.launchWhenResumed {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.onboarding, R.id.search -> bottomNav.show()
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
