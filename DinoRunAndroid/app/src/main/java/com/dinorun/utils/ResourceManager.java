package com.dinorun.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

public class ResourceManager {
    private static ResourceManager instance;
    private Context context;

    private ResourceManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ResourceManager getInstance(Context context) {
        if (instance == null) {
            instance = new ResourceManager(context);
        }
        return instance;
    }

    public Drawable getDrawableFromResource(int resourceId) {
        return ContextCompat.getDrawable(context, resourceId);
    }
}
