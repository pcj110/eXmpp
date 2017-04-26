package com.cn.lx.util;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xueliang on 2017/4/18.
 */

public class FileUtil {

    public final static  String APPNAME = "avat";

    public static String getBaseDir(){
         String path = Environment.getExternalStorageDirectory()+"/"+APPNAME;
        File file = null;
        if (!(file = new File(path)).exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getCacheDir(){

        String path = getBaseDir()+"/cache/";
        File file = null;
        if (!(file = new File(path)).exists()) {
            file.mkdirs();
        }
        return path;
    }
    public static String getCacheAvatarDir(){

        String path = getBaseDir()+"/cache/avatar/";
        File file = null;
        if (!(file = new File(path)).exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static byte[] fileToByte(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
    /**
     * 根据byte数组，生成文件
     */
    public static File getFile(byte[] bfile, String filePath,String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+"/"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
}
