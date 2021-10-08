package com.lch.note;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bilibili.boxing.loader.IBoxingCallback;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.bumptech.glide.Glide;

public class IBoxingMediaLoaderImpl implements IBoxingMediaLoader {
    @Override
    public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width, int height) {
        Glide.with(img).load(absPath).into(img);
    }

    @Override
    public void displayRaw(@NonNull ImageView img, @NonNull String absPath, int width, int height, IBoxingCallback callback) {

        Glide.with(img).load(absPath).into(img);
    }
}
