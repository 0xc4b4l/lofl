package com.candroid.lofl.api;

import android.content.Context;
import android.os.Environment;

import com.candroid.lofl.data.db.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;

public class Storage {

    public static class Files{

        public static boolean createTextFile(Context context, String fileName, String content){
            File file;
            try {
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
                fos.write(content.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            catch (IOException ioe){
                ioe.printStackTrace();
                return false;
            }
            return true;
        }

        public static File[] getFilesForDirectory(String path) {
            return new File(path).listFiles();
        }

        public static boolean isImage(File file) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            return mimeType != null && mimeType.startsWith("image");
        }

        public static boolean isVideo(File file) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            return mimeType != null && mimeType.startsWith("video");
        }

        public static boolean isText(File file) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            return mimeType != null && mimeType.startsWith("text") && !mimeType.endsWith("iif");
        }

        public static boolean isSpreadsheet(File file) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType != null && mimeType.contains("excel")) {
                return true;
            } else if (mimeType != null && mimeType.contains("oasis.opendocument.spreadsheet")) {
                return true;
            } else {
                return false;
            }
        }

        public static boolean isQuickbooks(File file) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType != null && mimeType.contains("qbooks")) {
                return true;
            } else if (mimeType != null && mimeType.equals("text/iif")) {
                return true;
            } else if (mimeType != null && mimeType.equals("application/vnd.intu.qbo")) {
                return true;
            } else {
                return false;
            }
        }

        public static File getPicturesDirectory() {
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
        }

        public static File getDcimDirectory() {
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());
        }

        public static boolean isExternalStorageReadable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return true;
            }
            return false;
        }

        public static byte[] fileToBytes(File f) {
            ByteArrayOutputStream bos = null;
            File file = new File(f.getPath());
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                bos = new ByteArrayOutputStream();
                for (int len = 0; (len = fis.read((buffer))) != -1; ) {
                    bos.write(buffer, 0, len);
                }
                fis.close();
                return bos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static boolean deleteFile(Context context, String name){
            String[] fileNames = context.fileList();
            for(String fileName : fileNames){
                if(fileName.equalsIgnoreCase(name)){
                    context.deleteFile(fileName);
                    return true;
                }
            }
            return false;
        }

        public static File getDatabaseFile(Context context){
            return context.getDatabasePath(DatabaseHelper.NAME);
        }

    }

}
