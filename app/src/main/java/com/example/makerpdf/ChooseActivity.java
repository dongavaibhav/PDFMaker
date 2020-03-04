package com.example.makerpdf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChooseActivity extends AppCompatActivity {

    public Button camBtn;
    public Button gallaryBtn;
    String mCurrentPhotoPath;
    Cursor myCursor;
    List<String> path;
    public static ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        initFab();
        clickListner();
    }

    public void initFab() {
        camBtn = findViewById(R.id.camera);
        gallaryBtn = findViewById(R.id.gallary);
    }

    public void clickListner() {
        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ChooseActivity.this)
                        .setCameraOnly(true)
                        .start();
            }
        });

        gallaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ChooseActivity.this)
                        .setFolderMode(true)
                        .setToolbarColor("#008577")
                        .setStatusBarColor("#008577")
                        .setFolderTitle("Album")
                        .setMultipleMode(true)
                        .setSelectedImages(images)
                        .setMaxSize(5)
                        .setBackgroundColor("#ffffff")
                        .setAlwaysShowDoneButton(true)
                        .setRequestCode(0)
                        .setKeepScreenOn(true)
                        .start();
            }
        });
    }

    public File createImageFile() throws Exception {
        File file = null;
        try {
            String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            StringBuilder sb = new StringBuilder();
            sb.append("JPEG_");
            sb.append(format);
            sb.append("_");
            String sb2 = sb.toString();
            File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), Constant.IMAGE_DIRECTORY_NAME);
            if (!file2.exists() && !file2.mkdirs()) {
                return null;
            }
            File createTempFile = File.createTempFile(sb2, ".jpg", file2);
            try {
                mCurrentPhotoPath = createTempFile.getAbsolutePath();
                file = createTempFile;
            } catch (Exception e) {
                File file3 = createTempFile;
                e = e;
                file = file3;
                e.printStackTrace();
                return file;
            }
            return file;
        } catch (Exception e2) {
            Exception e = e2;
            e.printStackTrace();
            return file;
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 100 && i2 == -1) {
            try {
                Intent intent2 = new Intent(getApplicationContext(), DisplaySelectedImageActivity.class);
                intent2.putExtra("Capture_Image_Name", this.mCurrentPhotoPath);
                intent2.putExtra("Images", "Camera");
                startActivityForResult(intent2, 0);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return;
            }
        }
        if (i == 0 && i2 == -1 && intent != null) {
            try {
                String[] strArr = {"_id", "_data"};
                myCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, strArr, null, null, "_id DESC");
                String str = "";
                try {
                    myCursor.moveToFirst();
                    String string = myCursor.getString(myCursor.getColumnIndexOrThrow("_data"));
                    myCursor.close();
                    str = string;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    myCursor.close();
                }
                Intent intent3 = new Intent(getApplicationContext(), DisplaySelectedImageActivity.class);
                intent3.putExtra("Capture_Image_Name", str);
                intent3.putExtra("Images", "Camera");
                startActivityForResult(intent3, 0);
            } catch (IllegalArgumentException e3) {
                e3.printStackTrace();
            } catch (Throwable th) {
                myCursor.close();
                throw th;
            }
        }
        Intent intent4 = new Intent(this, DisplaySelectedImageActivity.class);
        intent4.putExtra("Images", "Album");
        images = intent.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
        path = new ArrayList();
        printImages(images);
        intent4.putStringArrayListExtra("Gallary_Image", (ArrayList) path);
        startActivityForResult(intent4, 0);
        super.onActivityResult(i, i2, intent);
    }

    public void printImages(List<Image> list) {
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append("");
                sb.append(((Image) list.get(i)).getPath());
                Log.d("image", sb.toString());
                path.add(((Image) list.get(i)).getPath());
            }
        }
    }

}
