package com.example.arcueid.cameratest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by arcueid on 16/05/05.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback{

    private Camera mCam;

    private byte[] mFrameBuffer;
    private int[] mBitmapBuffer;

    private SurfaceHolder mHolder;

    private Bitmap mBitmap;

    /**
     * コンストラクタ
     */
    public CameraPreview(Context context, Camera cam) {
        super(context);

        mCam = cam;

        // サーフェスホルダーの取得とコールバック通知先の設定
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * SurfaceView 生成
     */
    public void surfaceCreated(SurfaceHolder holder) {
//        try {
            // カメラインスタンスに、画像表示先を設定
            //mCam.setPreviewDisplay(holder);
            mCam.setPreviewCallbackWithBuffer(this);
            // プレビュー開始
            //mCam.startPreview();
//        } catch (IOException e) {
            //
//        }
    }

    /**
     * SurfaceView 破棄
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCam.setPreviewCallback(null);
    }

    /**
     * SurfaceHolder が変化したときのイベント
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 画面回転に対応する場合は、ここでプレビューを停止し、
        // 回転による処理を実施、再度プレビューを開始する。

        Camera.Parameters parameters = mCam.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(sizes,width,height);
        parameters.setPreviewSize(optimalSize.width,optimalSize.height);

//        ViewGroup.LayoutParams paramLayout;
//        //ビューサイズをプレビューサイズに合わせる
//        paramLayout = getLayoutParams();
//        paramLayout.width = width;
//        paramLayout.height = height;
//        setLayoutParams(paramLayout);

        parameters.setPreviewSize(optimalSize.width, optimalSize.height);

        //色空間
        parameters.setPreviewFormat(ImageFormat.NV21);
        mCam.setParameters(parameters);

        int bufferSize = optimalSize.width * optimalSize.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())/8;

        //プレビュー画像バッファ
        mFrameBuffer = new byte[bufferSize];
        //ビットマップ作成バッファ(int型で１ピクセルを表すので*4は不要)
        mBitmapBuffer = new int[optimalSize.width * optimalSize.height];

        //バッファ変更
        mCam.addCallbackBuffer(mFrameBuffer);

        //ビットマップの作成
        mBitmap = Bitmap.createBitmap(optimalSize.width, optimalSize.height, Bitmap.Config.ARGB_8888);

        mCam.startPreview();

    }

    //アスペクト比を保持した最適なサイズを返す(これ以外のサイズで表示しようとすると画像が崩れる）
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void onPreviewFrame(byte[] data, Camera camera)
    {
        mCam.addCallbackBuffer(mFrameBuffer);

        Camera.Parameters parameters = mCam.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        //試しにグレースケール
        for( int i = 0; i < mBitmapBuffer.length; i++)
        {
            int gray = data[i] & 0xff;
            mBitmapBuffer[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
        }

        mBitmap.setPixels( mBitmapBuffer, 0, size.width, 0, 0, size.width, size.height);

        Rect srcRect = new Rect(0, 0, size.width, size.height);
        Canvas canvas = mHolder.lockCanvas();
        Rect dstRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
//        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawBitmap( mBitmap, srcRect, dstRect, null);
        mHolder.unlockCanvasAndPost(canvas);
    }

}