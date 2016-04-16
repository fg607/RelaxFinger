package com.hardwork.fg607.relaxfinger.utils;

/**
 * Created by fg607 on 16-4-16.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

import java.nio.Buffer;

/**
 * Created by omerjerk on 17/2/16.
 */
public class Screenshotter implements ImageReader.OnImageAvailableListener {

    private static final String TAG = "LibScreenshotter";

    private VirtualDisplay virtualDisplay;

    private int width;
    private int height;

    private Context context;

    private int resultCode;
    private Intent data;

    private ScreenshotCallback cb;
    private static Screenshotter mInstance;

    private ImageReader mImageReader;

    private MediaProjection mMediaProjection;
    /**
     * Get the single instance of the Screenshotter class.
     * @return the instance
     */
    public static Screenshotter getInstance() {
        if (mInstance == null) {
            mInstance = new Screenshotter();
        }
        return mInstance;
    }

    private Screenshotter() {}

    /**
     * Takes the screenshot of whatever currently is on the default display.
     * @param resultCode The result code returned by the request for accessing MediaProjection permission
     * @param data The intent returned by the same request
     */
    public Screenshotter takeScreenshot(Context context, int resultCode, Intent data, final ScreenshotCallback cb) {
        this.context = context;
        this.cb = cb;
        this.resultCode = resultCode;
        this.data = data;

        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888,2);
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        try {
            virtualDisplay = mMediaProjection.createVirtualDisplay("Screenshotter",
                    width, height, 50,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
            mImageReader.setOnImageAvailableListener(Screenshotter.this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Set the size of the screenshot to be taken
     * @param width width of the requested bitmap
     * @param height height of the request bitmap
     * @return the singleton instance
     */
    public Screenshotter setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        final Image.Plane[] planes = image.getPlanes();
        final Buffer buffer = planes[0].getBuffer().rewind();
        int offset = 0;
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        // create bitmap
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        cb.onScreenshot(bitmap);
        if(virtualDisplay!= null){

            virtualDisplay.release();
            virtualDisplay = null;
        }

        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        image.close();
        mImageReader = null;
    }
}