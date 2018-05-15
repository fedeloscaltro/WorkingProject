package com.gibbo.salvatore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

public class ImageLoader extends AsyncTask<Void, Integer, Drawable> {
    private Context context;

    public ImageLoader(Context context) {
        this.context = context;
    }

    @Override
    protected Drawable doInBackground(Void... voids) {
        Drawable d = this.context.getResources().getDrawable(R.drawable.background_login);
        return d;
    }
}