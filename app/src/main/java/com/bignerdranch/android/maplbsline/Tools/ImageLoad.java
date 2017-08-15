package com.bignerdranch.android.maplbsline.Tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bignerdranch.android.imageloadingwan.CheckNetwork;
import com.bignerdranch.android.imageloadingwan.CircleImageViewWan;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import static android.content.ContentValues.TAG;

/**
 * Created by LENOVO on 2017/7/8.
 */

public class ImageLoad {
    public static void getImage(final Activity activity, final String imageURL, final ImageView imageView) {
        if (isImageExists(imageURL)) {
            final Bitmap bitmap= BitmapFactory.decodeFile(getImageFilePath(imageURL));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showImage(activity,imageView,bitmap);
                }
            });
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection;
                    BufferedReader bufferedReader;
                    CheckNetwork checkNetwork = new CheckNetwork(activity);

                    try{
                        URL url=new URL(imageURL);
                        connection=(HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setReadTimeout(8000);
                        connection.setConnectTimeout(8000);
                        InputStream inputStream = connection.getInputStream();

                        Bitmap bitmap= null;

                        if (saveBitmapToSD(bitmap, imageURL, inputStream) != null) {
                            bitmap = saveBitmapToSD(bitmap, imageURL, inputStream);

                            showImage(activity, imageView, bitmap);
                            Log.d(TAG, "run: " + bitmap.getHeight() + bitmap.getWidth());


                        } else {
                            Toast.makeText(activity, "图片保存失败", Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }            }
            }).start();
        }
    }

    public static void getCircleImage(final Activity activity, final String imageURL, final CircleImageViewWan circleImageView){

        if (isImageExists(imageURL)) {
            final Bitmap bitmap= BitmapFactory.decodeFile(getImageFilePath(imageURL));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    circleImageView.setImageBitmap(bitmap);
                    circleImageView.init();
                    showImage(activity,circleImageView,bitmap);
                }
            });
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection;
                    BufferedReader bufferedReader;
                    CheckNetwork checkNetwork = new CheckNetwork(activity);

                    try{
                        URL url=new URL(imageURL);
                        connection=(HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setReadTimeout(8000);
                        connection.setConnectTimeout(8000);
                        final InputStream inputStream=connection.getInputStream();

                        final Bitmap[] bitmap = {null};
                        if (saveBitmapToSD(bitmap[0], imageURL, inputStream) != null) {
                            bitmap[0] = saveBitmapToSD(bitmap[0], imageURL, inputStream);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    circleImageView.setImageBitmap(bitmap[0]);
                                    circleImageView.init();
                                    showImage(activity, circleImageView, bitmap[0]);
                                }
                            });
                        } else {
                            Toast.makeText(activity, "图片保存失败", Toast.LENGTH_LONG).show();
                        }
//                    bitmap[0] = BitmapFactory.decodeStream(inputStream);
//                    showImage(activity, circleImageView, bitmap[0]);
                    }catch (Exception e){
                        e.printStackTrace();
                    }            }
            }).start();
        }
    }

    private static void showImage(Activity activity, final ImageView imageView, final Bitmap bitmap){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    private static Bitmap saveBitmapToSD(Bitmap bitmap,String imageURL,InputStream inputStream){
        File f= Environment.getExternalStorageDirectory();
        String filename=f.toString()+File.separator+getMD5(imageURL)+".png";
        Log.d("saveBitmapToSD", "saveBitmapToSD: The name saved in SD card is "+filename);
        File file=new File(filename);
        FileOutputStream fos=null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream bao = null;
        if (file.exists()) {

            bitmap= BitmapFactory.decodeFile(filename);
//            bitmap = getCompressedBitmap(filename, 200, 200);
            Log.d("BITMAPFACTORYRETURN", "saveBitmapToSD: "+BitmapFactory.decodeFile(filename));
            return bitmap;
        }else {
            bis= new BufferedInputStream(inputStream);
            bao =new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = bis.read(buffer, 0, buffer.length) ) > 0) {
                    bao.write(buffer, 0, len);
                }
                byte[] imageData = bao.toByteArray();

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
                options.inSampleSize = calcuteImageScale(options, 100, 100);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    fos.flush();
                    fos.close();
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }



    private static String getMD5(String url){
        String rec=null;
        try {
            MessageDigest messageDigest=MessageDigest.getInstance("MD5");
            byte [] data=messageDigest.digest(url.getBytes());
            StringBuilder stringBuilder=new StringBuilder();

            for (byte b:data){
                int ib=b& 0x0FF;
                String s=Integer.toHexString(ib);
                stringBuilder.append(s);
            }
            rec=stringBuilder.toString();
            Log.d("RUNUI+MD5", "getMD5:--------------- "+rec);
            return rec;
        }catch (Exception e){
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static String getImageFilePath(String imageURL) {
        File f = Environment.getExternalStorageDirectory();
        String filename = f.toString()+File.separator+getMD5(imageURL)+".png";
        return filename;
    }

    public static boolean isImageExists(String imageUrl) {
        File file = new File(getImageFilePath(imageUrl));
        if (file.exists()) {
            return  true;
        } else {
            return false;
        }
    }

    public static int calcuteImageScale(BitmapFactory.Options options, int hopeHeihent, int hopeWidth) {
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int scale = 1;
        if (imageHeight > hopeHeihent || imageWidth > hopeWidth) {
            final int heightScale = Math.round((float) imageHeight / (float) hopeHeihent);
            final int widthScale = Math.round((float) imageWidth / (float) hopeWidth);
            scale = heightScale < widthScale ? heightScale : widthScale;
        }
        return scale;
    }

}
