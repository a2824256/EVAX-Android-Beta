package com.zhixin.alexleung.camera1test;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureModule {
    private int camera_number;
    public PictureModule(int number) {
        camera_number = number;
    }
    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            /*
            try {
                //BitmapFactory.Options ops = new BitmapFactory.Options();
                //ops.outHeight = 720;
                //ops.outWidth = 1280;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //自定义文件保存路径  以拍摄时间区分命名
                String filepath = "/sdcard/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + camera_number + ".jpg";
                File file = new File(filepath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                //FileOutputStream fout = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩的流里面
                bos.flush();
                bos.close();
                //fout.write(data);// 刷新此缓冲区的输出流
                //fout.close();// 关闭此输出流并释放与此流有关的所有系统资源
                //camera.stopPreview();//关闭预览 处理数据
                //camera.release();
                //camera.startPreview();//数据处理完后继续开始预览
                bitmap.recycle();//回收bitmap空间
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
            CameraData cameraData = new CameraData(camera_number, data);
            new SavePictureTask().execute(cameraData);
            camera.startPreview();
        }
    };

    public Camera.PictureCallback getJpeg() {
        return jpeg;
    }

    public class CameraData {
        int cameraNumber;
        byte[] data;

        public CameraData(int cameraId, byte[] dat) {
            cameraNumber = cameraId;
            data = dat;
        }
    }

}

