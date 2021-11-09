package com.lch.cl;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.lch.cln.R;


public class LoadingHelper {
    private View loadingLayout;
    public int paddingTop=0;
    public  int paddingBottom=0;
    public  int paddingLeft=0;
    public  int paddingRight=0;
    public ViewGroup container;


    public void showLoading(Activity activity) {
        showLoading(activity, "loading,please waiting...");
    }

    public void showLoading(Activity activity, String msg) {
        if (activity == null) {
            return;
        }

        if (loadingLayout == null) {
            loadingLayout = View.inflate(activity, R.layout.loading_helper, null);
            loadingLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //just use intercept click.
                }
            });
        }

        TextView msgView = loadingLayout.findViewById(R.id.msg_view);
        msgView.setText(msg);
        ViewParent p = loadingLayout.getParent();
        if (p instanceof ViewGroup) {
            ((ViewGroup) p).removeView(loadingLayout);
        }

        ViewGroup deco = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup.MarginLayoutParams lp=new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.MATCH_PARENT);
        lp.bottomMargin=paddingBottom;
        lp.topMargin=paddingTop;
        lp.leftMargin=paddingLeft;
        lp.rightMargin=paddingRight;

        if(container!=null){
            container.addView(loadingLayout,lp);
        }else {
            deco.addView(loadingLayout, lp);
        }
    }

    public void hideLoading() {
        if (loadingLayout != null) {
            ViewParent p = loadingLayout.getParent();
            if (p instanceof ViewGroup) {
                ((ViewGroup) p).removeView(loadingLayout);
            }

        }
    }
}