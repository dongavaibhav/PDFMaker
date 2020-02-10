package com.example.makerpdf;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constant {

    public static final String IMAGE_DIRECTORY_NAME = "MyCamera";
    public static final String UNIQUE_PREFERENCES_NAME = "MyPref";
    public static final String create_dir = "/JPGtoPDF/";
    public static File mediaFile;
    public static final String targetPath;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(create_dir);
        targetPath = sb.toString();
    }

    public static File getOutputMediaFile(int i) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), IMAGE_DIRECTORY_NAME);
        if (file.exists() || file.mkdirs()) {
            String format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            if (i != 1) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(file.getPath());
            sb.append(File.separator);
            sb.append("IMG_");
            sb.append(format);
            sb.append(".jpg");
            mediaFile = new File(sb.toString());
            return mediaFile;
        }
        Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create MyCamera directory");
        return null;
    }
}
