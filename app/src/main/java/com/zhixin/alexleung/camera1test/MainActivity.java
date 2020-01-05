package com.zhixin.alexleung.camera1test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Button mBtnStart01;
    private final int maxCameraNumbers = 2;
    private SurfaceView[] mSurfaceView = new SurfaceView[maxCameraNumbers];
    private SurfaceHolder[] mSurfaceHolder = new SurfaceHolder[maxCameraNumbers];
    private Camera[] camera = new Camera[maxCameraNumbers];
    private Camera.Parameters[] parameters = new Camera.Parameters[maxCameraNumbers];
    private int maxCameraNumber = maxCameraNumbers;
    private boolean[] cameraStatus = new boolean[maxCameraNumbers];
    Button mBtnHJ;
    Button mBtnRG;
    int camera0Sup = 0;
    int camera1Sup = 0;

    //test
    public static List<Camera.Size> supportedVideoSizes;
    public static List<Camera.Size> previewSizes;

    public String getCameraInfo() {

        int cameracount = 0;//摄像头数量
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  //获取摄像头信息
        cameracount = Camera.getNumberOfCameras();
        Log.i("MainActivity", "摄像头数量" + String.valueOf(cameracount));
        for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            Camera camera = Camera.open(cameraId); //开启摄像头获得一个Camera的实例
            Camera.Parameters params = camera.getParameters();  //通过getParameters获取参数
            supportedVideoSizes = params.getSupportedPictureSizes();
            previewSizes = params.getSupportedPreviewSizes();
            camera.release();//释放摄像头

            //重新排列后设下摄像头预设分辨率在所有分辨率列表中的地址，用以选择最佳分辨率（保证适配不出错）
            int index = bestVideoSize(previewSizes.get(0).width);
            Log.i("MainActivity", "预览分辨率地址: " + index);
            if (null != previewSizes && previewSizes.size() > 0) {  //判断是否获取到值，否则会报空对象
                Log.i("MainActivity", "摄像头最高分辨率宽: " + String.valueOf(supportedVideoSizes.get(0).width));  //降序后取最高值，返回的是int类型
                Log.i("MainActivity", "摄像头最高分辨率高: " + String.valueOf(supportedVideoSizes.get(0).height));
                Log.i("MainActivity", "摄像头分辨率全部: " + cameraSizeToSting(supportedVideoSizes, index));
            } else {
                Log.i("MainActivity", "没取到值啊什么鬼");
                Log.i("MainActivity", "摄像头预览分辨率: " + String.valueOf(previewSizes.get(0).width));
            }
        }
        return cameraSizeToSting(supportedVideoSizes, 2);
    }

    public static int bestVideoSize(int _wid) {

        //降序排列
        Collections.sort(supportedVideoSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width > rhs.width) {
                    return -1;
                } else if (lhs.width == rhs.width) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for (int i = 0; i < supportedVideoSizes.size(); i++) {
            if (supportedVideoSizes.get(i).width < _wid) {
                return i;
            }
        }
        return 0;
    }


    public String cameraSizeToSting(Iterable<Camera.Size> sizes, int cameraIndex) {
        StringBuilder s = new StringBuilder();
        int count = 0;
        for (Camera.Size size : sizes) {
            if (s.length() != 0)
                s.append(",\n");
            s.append(size.width).append('x').append(size.height);
            count++;
        }
        if (cameraIndex == 0) {
            camera0Sup = count;
        } else if (cameraIndex == 1) {
            camera1Sup = count;
        }
        Log.i(TAG, "count: " + count);
        return s.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int cameraNumbers = Camera.getNumberOfCameras();
        Log.d(TAG, "camera numbers: " + cameraNumbers);
        try{
            mBtnStart01 = findViewById(R.id.takePhoto);
            mBtnHJ = findViewById(R.id.openHJ);
            mBtnRG = findViewById(R.id.openRG);

            mSurfaceView[0] = findViewById(R.id.surfaceView01);
            mSurfaceView[1] = findViewById(R.id.surfaceView02);

            cameraStatus[0] = false;
            cameraStatus[1] = false;

            mBtnHJ.setOnClickListener(this);
            mBtnRG.setOnClickListener(this);

            mBtnStart01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < maxCameraNumber; i++)
                        if (camera[i] != null) {
                            camera[i].takePicture(null, null, new PictureModule(i).getJpeg());
                        }
                }
            });
            mSurfaceView[1].setZOrderOnTop(true);
//        mSurfaceView[1].setZOrderMediaOverlay(true);
//        mSurfaceView[0].setZOrderOnTop(false);
//        mSurfaceView[0].setZOrderMediaOverlay(false);
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            initSurface();
            String res = getCameraInfo();
            Log.i(TAG, "onCreate: " + res);
        }catch (Exception e){
            Log.i(TAG, "onCreate: " + e.toString());
            Log.i(TAG, "onCreate: " + e.getStackTrace()[0].getLineNumber());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openRG:
                openCameraRG();
                break;
            case R.id.openHJ:
                openCameraHJ();
                break;
        }
    }

    private void openCameraHJ() {
        int i = 1;
//        if(camera0Sup == 2){
//            i = 1;
//        }else{
//            i = 0;
//        }
        try {
            if (!cameraStatus[i]) {
                camera[i] = Camera.open(i);
                startCamera1(i, mSurfaceHolder[i]);
                cameraStatus[i] = true;
                mBtnHJ.setText("关闭喉镜");
            } else {
                stopCamera(i);
                cameraStatus[i] = false;
                mBtnHJ.setText("打开喉镜");
            }
        } catch (Exception e) {
            camera[0] = null;
            Toast.makeText(this, "无法打开喉镜", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "openCamera0: " + e.toString());
        }
    }

    private void openCameraRG() {
        int i = 0;
//        if(camera0Sup == 2){
//            i = 0;
//        }else{
//            i = 1;
//        }
        try {
            if (!cameraStatus[i]) {
                camera[i] = Camera.open(i);
                startCamera0(i, mSurfaceHolder[i]);
                cameraStatus[i] = true;
                mBtnRG.setText("关闭软管镜");
            } else {
                stopCamera(i);
                cameraStatus[i] = false;
                mBtnRG.setText("打开软管镜");
            }
        } catch (Exception e) {
            camera[i] = null;
            Toast.makeText(this, "无法打开软管镜", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "openCamera1: " + e.toString());
        }
    }

    private void startCamera0(int cameraId, SurfaceHolder holder) {
        if (camera != null) {
            try {
                parameters[cameraId] = camera[cameraId].getParameters();
                parameters[cameraId].setPictureFormat(ImageFormat.JPEG);
                parameters[cameraId].setPreviewSize(960, 720);
                parameters[cameraId].setPictureSize(1280, 720);
//                parameters[cameraId].setPreviewFrameRate(30);
                camera[cameraId].setParameters(parameters[cameraId]);
                camera[cameraId].setPreviewDisplay(holder);
                camera[cameraId].startPreview();
                //camera.unlock();
            } catch (IOException e) {
                Log.i(TAG, "startCamera: " + e.toString());
//                e.printStackTrace();
            }
        }
    }

    private void startCamera1(int cameraId, SurfaceHolder holder) {
        if (camera != null) {
            try {
                parameters[cameraId] = camera[cameraId].getParameters();
                parameters[cameraId].setPictureFormat(ImageFormat.JPEG);
                parameters[cameraId].setPreviewSize(1280, 720);
                parameters[cameraId].setPictureSize(1280, 720);
//                parameters[cameraId].setPreviewFrameRate(30);
                camera[cameraId].setParameters(parameters[cameraId]);
                camera[cameraId].setPreviewDisplay(holder);
                camera[cameraId].startPreview();
                //camera.unlock();
            } catch (IOException e) {
                Log.i(TAG, "startCamera: " + e.toString());
//                e.printStackTrace();
            }
        }
    }

    private void stopCamera(int cameraId) {
        if (camera[cameraId] != null) {
            camera[cameraId].stopPreview();
            camera[cameraId].release();
            camera[cameraId] = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < maxCameraNumber; i++) {
            stopCamera(i);
        }
    }

    private void initSurface() {
        for (int i = 0; i < maxCameraNumber; i++) {
            mSurfaceHolder[i] = mSurfaceView[i].getHolder();
            mSurfaceHolder[i].addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                }
            });
        }
    }

}

