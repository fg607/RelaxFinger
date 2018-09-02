package com.hardwork.fg607.relaxfinger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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

    public static void releaseBitmap(Drawable drawable){

        Bitmap bitmap;

        if(drawable instanceof BitmapDrawable){

            bitmap = ((BitmapDrawable)drawable).getBitmap();

            if(bitmap != null && !bitmap.isRecycled()){

                bitmap = null;
            }
        }
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap
     *            传入Bitmap对象
     *
     * @param size
     *            大小
     * @return
     */
    public static Bitmap drawCircleView(Bitmap bitmap,int size){

        //前面同上，绘制图像分别需要bitmap，canvas，paint对象
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);//==========创建的图片的长宽为128
        Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);//此处的长宽应该与上一行保持一致
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //这里需要先画出一个圆
        canvas.drawCircle(size/2, size/2, size/2, paint);//===========此处应该为上面长宽的一半
        //圆画好之后将画笔重置一下
        paint.reset();
        //设置图像合成模式，该模式为只在源图像和目标图像相交的地方绘制源图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bm;
    }

    public static Drawable toRoundDrawable(Drawable drawable,int size){

        Bitmap bitmap = null;

        if(drawable instanceof BitmapDrawable){

            BitmapDrawable bd = (BitmapDrawable) drawable;

            bitmap = bd.getBitmap();


        }else {

            bitmap = drawable2Bitmap(drawable);
        }

        bitmap = drawCircleView(bitmap,size);

        return bitmap2Drawable(bitmap);
    }
}
