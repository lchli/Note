package com.lch.cl

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.SPUtils
import com.lch.cln.databinding.ProtoDialogActivityBinding
import com.materialstudies.owl.databinding.ProtoDialogActivityBinding

class ProtoDialogActivity:AppCompatActivity() {
    private lateinit var binding:ProtoDialogActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ProtoDialogActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.tvDes.movementMethod= LinkMovementMethod.getInstance()
        binding.tvDes.text=ProtoHelper.getSplashAgreeDes(this)

        binding.tvConfirm.setOnClickListener {
            SPUtils.getInstance().put(SpKey.is_proto_agreed,true)
            UMUtil.init(applicationContext)

            startActivity(Intent(this,Splash::class.java))
            finish()
        }
        binding.tvDisAgree.setOnClickListener {
            finish()
        }
    }
}