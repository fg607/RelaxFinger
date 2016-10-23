package com.hardwork.fg607.relaxfinger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fg607 on 15-11-26.
 */
public class ImageUtils {

    // 将byte[]转换成InputStream
    public static InputStream Byte2InputStream(byte[] b) {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        return bais;
    }

    // 将InputStream转换成byte[]
    public static byte[] InputStream2Bytes(InputStream is) {
        String str = "";
        byte[] readByte = new byte[1024];
        int readCount = -1;
        try {
            while ((readCount = is.read(readByte, 0, 1024)) != -1) {
                str += new String(readByte).trim();
            }
            return str.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 将Bitmap转换成InputStream
    public static InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    // 将Bitmap转换成InputStream
    public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    // 将InputStream转换成Bitmap
    public static Bitmap InputStream2Bitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    // Drawable转换成InputStream
    public static InputStream Drawable2InputStream(Drawable d) {
        Bitmap bitmap = drawable2Bitmap(d);
        return Bitmap2InputStream(bitmap);
    }

    // InputStream转换成Drawable
    public static Drawable InputStream2Drawable(InputStream is) {
        Bitmap bitmap = InputStream2Bitmap(is);
        return bitmap2Drawable(bitmap);
    }

    // Drawable转换成byte[]
    public static byte[] Drawable2Bytes(Drawable d) {
        Bitmap bitmap = drawable2Bitmap(d);
        return Bitmap2Bytes(bitmap);
    }

    // byte[]转换成Drawable
    public static Drawable Bytes2Drawable(byte[] b) {
        Bitmap bitmap = Bytes2Bitmap(b);
        return bitmap2Drawable(bitmap);
    }

    // Bitmap转换成byte[]
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // byte[]转换成Bitmap
    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    // Drawable转换成Bitmap
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // Bitmap转换成Drawable
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable d = (Drawable) bd;
        return d;
    }

    /**
     * 缩放图标
     * @param filename
     * @param scaleWidth
     * @param scaleHeight
     * @return
     */
    public static Bitmap scaleBitmap(String filename,int scaleWidth,int scaleHeight) {

        if(scaleWidth==0 || scaleHeight==0 ){

            Log.e("ImageUtils","scaleBitmap scaleWidth or scaleHeight can not be 0");
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filename, options);


        //缩放系数
        int inSampleSize = 1;

        //bitmap 实际尺寸
        int height = options.outHeight;
        int width = options.outWidth;

        //根据scalewidth 和scaleheight计算缩放系数
        if(width>scaleWidth || height>scaleHeight){

            int halfWidht = width/2;
            int halfHeight = height/2;


            while ((halfHeight/inSampleSize)>=scaleHeight && (halfWidht/inSampleSize)>=scaleWidth){

                inSampleSize *=2;
            }

        }


        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap outputbitmap = BitmapFactory.decodeFile(filename, options);

        return outputbitmap;
    }

    public static Bitmap ScaleBitmap(Bitmap bitmap,int scaleWidth,int scaleHeight){

        if(scaleWidth==0 || scaleHeight==0 ){

            Log.e("ImageUtils","scaleBitmap scaleWidth or scaleHeight can not be 0");
            return null;
        }
        Bitmap outputBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //测量bitmap实际尺寸
        options.inJustDecodeBounds = true;

        byte[] bitmapArray = Bitmap2Bytes(bitmap);

        BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length,options);

        //缩放系数
        int inSampleSize = 1;

        //bitmap 实际尺寸
        int height = options.outHeight;
        int width = options.outWidth;

        //根据scalewidth 和scaleheight计算缩放系数
        if(width>scaleWidth || height>scaleHeight){

            int halfWidht = width/2;
            int halfHeight = height/2;

            while (halfHeight/inSampleSize>=scaleHeight && halfWidht/inSampleSize>=scaleWidth){

                inSampleSize *=2;
            }

        }

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;

        outputBitmap = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length,options);

        return outputBitmap;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {

        if(bitmap==null){
            return null;
        }
        Bitmap output = Bitmap.createBitmap( bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas( output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect( 0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias( true);
        paint.setFilterBitmap( true);
        paint.setDither( true);
        canvas.drawARGB( 0, 0, 0, 0);
        paint.setColor( color);
        //在画布上绘制一个圆
        canvas.drawCircle( bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap( bitmap, rect, rect, paint);
        return output;
    }
}
