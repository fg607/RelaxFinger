package com.hardwork.fg607.relaxfinger.view;

import android.content.Context;
import android.content.res.TypedArray;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.hardwork.fg607.relaxfinger.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liurongchan on 15/3/17.
 */
public class CombinationImageView extends View {


    private  List<TinyPic> tinyPics = new ArrayList<>();

    private static int[] default_images_reses = new int[] {R.styleable.CombinationImageView_first_image_src, R.styleable.CombinationImageView_second_image_src, R.styleable.CombinationImageView_third_image_src, R.styleable.CombinationImageView_fourth_image_src};

    public CombinationImageView(Context context) {
        super(context);
    }

    public CombinationImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.combinationImageView);
    }

    public CombinationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CombinationImageView,
                defStyleAttr, 0);

        for (int default_image_res : default_images_reses) {
            Drawable drawable = attributes.getDrawable(default_image_res);
            if (drawable != null) {
                Bitmap bitmap = drawableToBitmap(drawable);
                TinyPic tinyPic = new TinyPic(bitmap);
                addImageView(tinyPic);
            }
        }
        attributes.recycle();
    }

    public void addImageView(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        addImageView(bitmap);
    }

    public void addImageView(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        addImageView(bitmap);
    }

    public void addImageView(Bitmap bitmap) {
        if (bitmap != null) {
            TinyPic tinyPic = new TinyPic(bitmap);
            tinyPics.add(tinyPic);
        }
    }

    private void addImageView(TinyPic tinyPic) {
        if (tinyPic != null && tinyPic.bitmap != null) {
            tinyPics.add(tinyPic);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec,true), measure(heightMeasureSpec,false));
    }

    private int measure(int measureSpec,boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth?getPaddingLeft()+getPaddingRight():getPaddingTop()+getPaddingBottom();
        if(mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if(mode == MeasureSpec.AT_MOST){
                if(isWidth) {
                    result = Math.max(result, size);
                }
                else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float total_width = getWidth();
        float total_height = getHeight();
        int total_bitmap_size = tinyPics.size();
        adjustBitmapsSize(total_width, total_height);
        determinLeftAndTop(total_width, total_height, total_bitmap_size);

        for (TinyPic tinyPic : tinyPics) {
            canvas.drawBitmap(tinyPic.bitmap, tinyPic.left, tinyPic.top, new Paint(Paint.ANTI_ALIAS_FLAG));
        }
    }

    public Bitmap getCombinationImage(int total_width,int total_height){


        Bitmap output = Bitmap.createBitmap(total_width, total_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int total_bitmap_size = tinyPics.size();
        adjustBitmapsSize(total_width, total_height);
        determinLeftAndTop(total_width, total_height, total_bitmap_size);

        for (TinyPic tinyPic : tinyPics) {
            canvas.drawBitmap(tinyPic.bitmap, tinyPic.left, tinyPic.top, new Paint(Paint.ANTI_ALIAS_FLAG));
        }


        return output;
    }


    private void adjustBitmapsSize(float totalWidth, float totalHeight) {
        int scaled_width;
        int scaled_height;
        switch (tinyPics.size()) {
            case 2:
            case 3:
            case 4:
                scaled_width = (int)totalWidth / 2;
                scaled_height = (int)totalHeight / 2;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                default:
                    scaled_width = (int)totalWidth / 3;
                    scaled_height = (int)totalHeight / 3;
                    break;

        }
        for (int i = 0; i < tinyPics.size(); i++) {
             TinyPic tinyPic = tinyPics.get(i);
             Bitmap b = tinyPic.bitmap;
             tinyPic.bitmap = Bitmap.createScaledBitmap(b, scaled_width, scaled_height, true);
        }

    }

    private void determinLeftAndTop(float total_width, float total_height, int total_bitmap_size) {
        switch (total_bitmap_size) {
            case 2:
                for (int i = 0 ; i < total_bitmap_size; i++) {
                    TinyPic tinyPic = tinyPics.get(i);
                    tinyPic.left = getPaddingLeft() + i * total_width / 2;
                    tinyPic.top = getPaddingTop() + total_height / 4;
                }
                break;
            case 3:
                for (int i = 0 ; i < total_bitmap_size; i++) {
                    TinyPic tinyPic = tinyPics.get(i);
                    tinyPic.left = getPaddingLeft() + (i % 2) * total_width / 2 + (i / 2 ) * total_width / 4;
                    tinyPic.top = getPaddingTop() + (1 - i / 2) * total_height / 2;
                }
                break;
            case 4:
                for (int i = 0 ; i < total_bitmap_size; i++) {
                    TinyPic tinyPic = tinyPics.get(i);
                    tinyPic.left = getPaddingLeft() + (i % 2) * total_width / 2;
                    tinyPic.top = getPaddingTop() + (1 - i / 2) * total_height / 2;
                }
                break;
            case 5:
                for (int i = 0 ; i < total_bitmap_size; i++) {
                    TinyPic tinyPic = tinyPics.get(i);
                    tinyPic.left = getPaddingLeft() + (i % 3) * total_width / 3 + (i / 3) * total_width / 6;
                    tinyPic.top = getPaddingTop() + (1 - i / 3) * total_height / 3 + total_height / 6;
                }
                break;
            case 6:
                for (int i = 0 ; i < total_bitmap_size; i++) {
                    TinyPic tinyPic = tinyPics.get(i);
                    tinyPic.left = getPaddingLeft() + (i % 3) * total_width / 3;
                    tinyPic.top = getPaddingTop() + (1 - i / 3) * total_height / 3 + total_height / 6;
                }
                break;
            case 7:
                for (int i = 0 ; i < total_bitmap_size; i++) {
                    TinyPic tinyPic = tinyPics.get(i);
                    tinyPic.left = getPaddingLeft() + (i % 3) * total_width / 3 + (i / 6) * total_width / 3;
                    tinyPic.top = getPaddingTop() + (2 - i / 3) * total_height / 3;
                }
                break;
            case 8:
                for (int i = 0 ; i < total_bitmap_size; i++) {
                    TinyPic tinyPic = tinyPics.get(i);
                    tinyPic.left = getPaddingLeft() + (i % 3) * total_width / 3 + (i / 6) * total_width / 6;
                    tinyPic.top = getPaddingTop() + (2 - i / 3) * total_height / 3;
                }
                break;
            case 9:
            default:
                for (int i = 0 ; i < total_bitmap_size; i++) {
                    TinyPic tinyPic = tinyPics.get(i);
                    tinyPic.left = getPaddingLeft() + (i % 3) * total_width / 3;
                    tinyPic.top = getPaddingTop() + (2 - i / 3) * total_height / 3;
                }
                break;
        }
    }

    public int getViewsCount() {
        return tinyPics.size();
    }


    public void removeView(int position) {
        if (position >= 0 && position < tinyPics.size()) {
            tinyPics.remove(position);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void removeAllViews() {
        tinyPics.clear();
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    private class TinyPic {
        public Bitmap bitmap;
        public float left;
        public float top;

        public TinyPic(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

}
