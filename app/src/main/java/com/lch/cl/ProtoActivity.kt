package com.lch.cl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.SPUtils
import com.materialstudies.owl.databinding.ProtoDialogActivityBinding
import com.materialstudies.owl.databinding.ProtoFragmentBinding

class ProtoActivity : AppCompatActivity() {
    private lateinit var binding: ProtoFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow(this)

        binding = ProtoFragmentBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.lifecycleOwner = this

        intent?.apply {
            binding.title.text = getStringExtra("title")
            binding.protoContent.text = Html.fromHtml(getStringExtra("content"))

            binding.back.setOnClickListener {
                finish()
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


    companion object {
        fun start(context: Context, title: String, content: String) {
            Intent(context, ProtoActivity::class.java).apply {
                putExtra("title", title)
                putExtra("content", content)
                context.startActivity(this)
            }
        }
    }
}