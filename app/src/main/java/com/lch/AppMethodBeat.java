package com.lch;

import android.os.Looper;
import android.util.Log;

public class AppMethodBeat
{
   static long st=-1;

    public static void i(int methodId) {
        if(Looper.myLooper()!=Looper.getMainLooper()){
            return;
        }
        st=System.currentTimeMillis();

    }

    public static void o(String classname,String methodname,String desc) {
        if(Looper.myLooper()!=Looper.getMainLooper()){
            return;
        }
        if(st<0){
            return;
        }
        long spend=System.currentTimeMillis()-st;
        st=-1;

        if(spend<50){
            return;
        }
        StringBuilder sb=new StringBuilder();
        sb.append("slow method:")
                .append(spend)
                .append(",")
                .append(classname)
                .append("-")
                .append(methodname)
                .append("-")
                .append(desc);

        Log.e("sss",sb.toString());

    }
}
