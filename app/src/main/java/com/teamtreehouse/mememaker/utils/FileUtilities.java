package com.teamtreehouse.mememaker.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.teamtreehouse.mememaker.MemeMakerApplicationSettings;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Evan Anger on 7/28/14.
 */
public class FileUtilities {

    public static void saveAssetImage(Context context, String assetName) {
//        File fileDirectory = context.getFilesDir();
        // get internal file directory
        File fileDirectory = getFileDirectory(context);
        // <fileDirectory>/<assetName>
        File fileToWrite = new File(fileDirectory, assetName);

        AssetManager assetManager = context.getAssets();

        try {
            // stream to read our asset File
            InputStream in = assetManager.open(assetName);
            // stream to writes to fileToWrite
            FileOutputStream out = new FileOutputStream(fileToWrite,false);

            // Deprecated
//            FileOutputStream out = new FileOutputStream(fileToWrite.getAbsoluteFile(), Context.MODE_PRIVATE);
            // copy asset file from assets folder(fileDirectory) to another file directory as fileToWrite
            copyFile(in, out);
            // always close streams
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get file directory, depending on the chosen storage method
    public static File getFileDirectory(Context context) {
        // retrieve storage type from shared preferences by using our helper class
        MemeMakerApplicationSettings settings = new MemeMakerApplicationSettings(context);
        String storageType = settings.getStoragePreference();
        // depending on selected storage type return according file directory
        if(storageType.equals(StorageType.INTERNAL)) {
            // internal storage
            // data/data/com.teamtreehouse.mememaker/files
            return context.getFilesDir();
        } else {
            if(isExternalStorageAvailable()) {
                if(storageType.equals(StorageType.PRIVATE_EXTERNAL)) {
                    return context.getExternalFilesDir(null);
                } else {
                    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                }
            } else {
                return context.getFilesDir();
            }
        }
    }

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Copy file from input stream to outputstream
     * @param in
     * @param out
     * @throws IOException
     */
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        // write bytes into our buffer in portions of 1025 bytes per time
        // and repeat until there is nothing to read
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static File [] listFiles(Context context) {
        // get file directory
        File fileDirectory = getFileDirectory(context);
        // filter to get files that are .jpg images
        File [] filteredFiles = fileDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.getAbsolutePath().contains(".jpg")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        return filteredFiles;
    }

    public static Uri saveImageForSharing(Context context, Bitmap bitmap,  String assetName) {
        File fileToWrite = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), assetName);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return Uri.fromFile(fileToWrite);
        }
    }

    // instead of writing from "in" file to "out" file, writes bitmap image into "out" file
    public static void saveImage(Context context, Bitmap bitmap, String name) {
        File fileDirectory = getFileDirectory(context);
        File fileToWrite = new File(fileDirectory, name);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            // compress image and assign output stream to write it
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
