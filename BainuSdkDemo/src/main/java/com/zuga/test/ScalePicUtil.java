package com.zuga.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

public class ScalePicUtil {
    private byte[] getimage(String srcPath, Context context) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        srcPath = srcPath.replaceAll("file://", "");
        BitmapFactory.decodeFile(srcPath, newOpts);

        int picW = newOpts.outWidth;
        int picH = newOpts.outHeight;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int phoneHeight = wm.getDefaultDisplay().getHeight();
        int phoneWidth = wm.getDefaultDisplay().getWidth();
        int scaleY = picH / phoneHeight;
        int scaleX = picW / phoneWidth;
        int scale = Math.max(scaleX, scaleY);
        if (scale < 1) {
            scale = 1;
        }
        BitmapFactory.Options newOpts1 = new BitmapFactory.Options();
        newOpts1.inJustDecodeBounds = false;
        newOpts1.inSampleSize = scale;
        Log.e("MainActivity", "scale: " + scale);
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts1);
        return compressImage(bitmap);
    }

    private byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
        }
        byte[] bytes = baos.toByteArray();
        baos.reset();
        return bytes;
    }
}
