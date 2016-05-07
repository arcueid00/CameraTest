package com.example.arcueid.cameratest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    //カメラインスタンス
    private Camera mCamera = null;

    private CameraPreview mCameraPreview = null;

    private TextureView mTextureView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mTextureView = (TextureView) findViewById(R.id.texture_view);
//        mTextureView.setSurfaceTextureListener(this);
//
//        mCamera = new Camera2(MainActivity.this,mTextureView);
        //カメラインスタンスの取得
        try {
            mCamera = Camera.open();
        }catch(Exception e){
            this.finish();
        }

        FrameLayout preview = (FrameLayout)findViewById(R.id.CameraPreview);
        mCameraPreview = new CameraPreview(this, mCamera);
        preview.addView( mCameraPreview, new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

//        Button cameraButton = (Button)findViewById(R.id.CameraButton);
//
//        cameraButton.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v)
//            {
//            }
//        });

    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera.open();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface)
    {
        Bitmap bmp = mTextureView.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] bytes = baos.toByteArray();
        //バイト配列操作
    }

    @Override
    protected void onPause()
    {
        super.onPause();
//        if( mCamera != null )
//        {
//            mCamera.release();
//            mCamera = null;
//        }
    }
}
