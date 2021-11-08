package com.lch.cl

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.SPUtils
import com.lch.cl.util.log

class Launcher: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log("is Open:"+AdUtil.isAdOpen())

        if(SPUtils.getInstance().getBoolean(SpKey.is_proto_agreed,false)){
            startActivity(Intent(this,Splash::class.java))
        }else{
            startActivity(Intent(this,ProtoDialogActivity::class.java))
        }

        finish()
    }
}