package com.lch.cl

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.SPUtils
import com.materialstudies.owl.databinding.ProtoDialogActivityBinding

class ProtoActivity:AppCompatActivity() {
    private lateinit var binding:ProtoDialogActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ProtoDialogActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.tvDes.text=ProtoHelper.getSplashAgreeDes(this)

        binding.tvConfirm.setOnClickListener {
            SPUtils.getInstance().put(SpKey.is_proto_agreed,true)
            startActivity(Intent(this,Splash::class.java))
            finish()
        }
        binding.tvDisAgree.setOnClickListener {
            finish()
        }
    }
}