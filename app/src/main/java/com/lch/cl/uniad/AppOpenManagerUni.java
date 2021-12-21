package com.lch.cl.uniad;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.lch.ClnApplication;
import com.lch.cl.Launcher;
import com.lch.cl.ad.AdIds;

import java.util.Date;

import io.dcloud.adnative.UniAdManager;
import io.dcloud.adnative.model.ISplashADListener;
import io.dcloud.adnative.util.DoToast;
import io.dcloud.adnative.util.Logger;

/** Prefetches App Open Ads. */
public class AppOpenManagerUni implements Application.ActivityLifecycleCallbacks,LifecycleObserver {
  private static final String LOG_TAG = "AppOpenManager";

  private final ClnApplication myApplication;

  /** Constructor */
  public AppOpenManagerUni(ClnApplication myApplication) {
    this.myApplication = myApplication;
    this.myApplication.registerActivityLifecycleCallbacks(this);
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
  }


  /** Shows the ad if one isn't already showing. */
  public void showAdIfAvailable() {
    // Only show ad if there is not already an app open ad currently showing
    // and an ad is available.
    if(currentActivity!=null&& !(currentActivity instanceof Launcher)) {
      Launcher.Companion.start(currentActivity);
    }
  }

  private Activity currentActivity;

  /** ActivityLifecycleCallback methods */
  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

  @Override
  public void onActivityStarted(Activity activity) {
    currentActivity = activity;
  }

  @Override
  public void onActivityResumed(Activity activity) {
    currentActivity = activity;
  }

  @Override
  public void onActivityStopped(Activity activity) {}

  @Override
  public void onActivityPaused(Activity activity) {}

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

  @Override
  public void onActivityDestroyed(Activity activity) {
    currentActivity = null;
  }

  /** LifecycleObserver methods */
  @OnLifecycleEvent(ON_START)
  public void onStart() {
    showAdIfAvailable();
    Log.d(LOG_TAG, "onStart");
  }
}