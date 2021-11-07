package com.lch.cl
import android.content.Context
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.blankj.utilcode.util.ToastUtils
import com.materialstudies.owl.R
import com.materialstudies.owl.util.log

object ProtoHelper {

    //服务条款
    private const val SERVICE_POLICY_RES_ID = R.string.user_proto

    //隐私协议
    private const val PRIVACY_POLICY_RES_ID = R.string.pri_proto

    fun getSplashAgreeDes(context: Context): CharSequence {
        val serviceContent = context.getString(SERVICE_POLICY_RES_ID)
        val privacyPolicyContent = context.getString(PRIVACY_POLICY_RES_ID)
        val res = context.resources.getString(R.string.splash_agree, serviceContent, privacyPolicyContent)
        val ssb = SpannableStringBuilder(HtmlCompat.fromHtml(res, HtmlCompat.FROM_HTML_MODE_COMPACT))
        changeUrlSpanStyle(ssb)
        return ssb
    }

    private fun changeUrlSpanStyle(ssb: SpannableStringBuilder) {
        val spans = ssb.getSpans(0, ssb.length, URLSpan::class.java)
        for (span in spans) {
            setLinkClickable(ssb, span, R.color.owl_blue_200, false)
        }
    }


    private fun setLinkClickable(
        clickableHtmlBuilder: SpannableStringBuilder,
        urlSpan: URLSpan,
        @ColorRes colorResId: Int,
        underLine: Boolean
    ) {
        log("URLSpan:"+urlSpan.url)
        val start = clickableHtmlBuilder.getSpanStart(urlSpan)
        val end = clickableHtmlBuilder.getSpanEnd(urlSpan)
        val flags = clickableHtmlBuilder.getSpanFlags(urlSpan)
        val span: ClickableSpan = object : ClickableSpan() {

            override fun onClick(v: View) {
               log("onclcik:"+urlSpan.url)

                if(urlSpan.url.equals(v.context.getString(R.string.user_proto))){
                    ProtoActivity.start(v.context,"用户协议",v.resources.getString(R.string.user_proto_detail))
                }else if(urlSpan.url.equals(v.context.getString(R.string.pri_proto))){
                    ProtoActivity.start(v.context,"隐私政策",v.resources.getString(R.string.pri_proto_detail))
                }

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                Contexter.getContext().apply {
                    ds.color = ContextCompat.getColor(this, colorResId)
                    ds.isUnderlineText = underLine
                }

            }
        }
        clickableHtmlBuilder.removeSpan(urlSpan)

        clickableHtmlBuilder.setSpan(span, start, end, flags)
    }
}