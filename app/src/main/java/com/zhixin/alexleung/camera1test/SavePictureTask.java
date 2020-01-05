package com.zhixin.alexleung.camera1test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavePictureTask extends AsyncTask<PictureModule.CameraData, String, String> {
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(PictureModule.CameraData... params) {
        byte[] data = params[0].data;
        int camera_number = params[0].cameraNumber;
        try {
            //BitmapFactory.Options ops = new BitmapFactory.Options();
            //ops.outHeight = 720;
            //ops.outWidth = 1280;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //自定义文件保存路径  以拍摄时间区分命名
            String filepath = "/sdcard/" + "testDemo" + camera_number + ".jpg";
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
        /*
        String filepath = "/sdcard/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "." + params[0].cameraNumber + ".jpg";
        try {

            FileOutputStream fout = new FileOutputStream(filepath);
            fout.write(params[0].data);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        return null;
    }
}

