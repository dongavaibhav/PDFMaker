package com.example.makerpdf;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DisplaySelectedImageActivity extends AppCompatActivity {

    ImageSamplesAdapter adapter;
    Context context;
    RelativeLayout coordinatorLayout;
    creatingPDF creatingpdf;
    Bitmap greyBmp;
    static ArrayList<String> imageuri;
    Image image;
    String imagename;

    public boolean isCanceled = false;
    boolean isOpenGallery = true;
    String mCurrentPhotoPath;
    private ItemTouchHelper mItemTouchHelper;
    int maxHeight = 850;
    int maxWidth = 550;
    Cursor myCursor;
    String path;
    ArrayList<String> path1;
    String pdfname;
    int quality;
    int selectedPos = 0;
    private RecyclerView mImageSampleRecycler;
    private BaseColor color;

    public class creatingPDF extends AsyncTask<String, String, String> {
        public NumberProgressBar bnp;
        private Button cancle;
        private TextView header;
        int i;
        ProgressDialog main_dialog;
        public TextView progrss;

        public creatingPDF() {
        }

        public void onPreExecute() {
            try {
                super.onPreExecute();
                main_dialog = new ProgressDialog(DisplaySelectedImageActivity.this);
                main_dialog.requestWindowFeature(1);
                main_dialog.setCancelable(false);
                main_dialog.setCanceledOnTouchOutside(false);
                main_dialog.show();
                main_dialog.setContentView(R.layout.progress_dialog_layout);
                header = (TextView) main_dialog.findViewById(R.id.title);
                progrss = (TextView) main_dialog.findViewById(R.id.progress);
                cancle = (Button) main_dialog.findViewById(R.id.cancel_button);
                bnp = (NumberProgressBar) main_dialog.findViewById(R.id.number_progress_bar);
                bnp.setMax(DisplaySelectedImageActivity.imageuri.size());
                TextView textView = progrss;
                StringBuilder sb = new StringBuilder();
                sb.append("0/");
                sb.append(DisplaySelectedImageActivity.imageuri.size());
                textView.setText(sb.toString());
                cancle.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        main_dialog.dismiss();
                        isCanceled = true;
                        creatingpdf.cancel(true);
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String doInBackground(String... strArr) {
            DisplaySelectedImageActivity selectedImagesActivity = DisplaySelectedImageActivity.this;
            StringBuilder sb = new StringBuilder();
            sb.append(Constant.targetPath);
            sb.append(pdfname);
            sb.append(".pdf");
            selectedImagesActivity.path = sb.toString();
//            Document document = new Document(PageSize.A4, 38.0f, 38.0f, 50.0f, 38.0f);
//            Rectangle pageSize = document.getPageSize();
//            pageSize.setBackgroundColor(new BaseColor(84, 141, 212));

            Rectangle pageSize = new Rectangle(PageSize.A4);
            pageSize.setBackgroundColor(Global.getInstance().getColor());
            Document document = new Document(pageSize);

            try {
                PdfWriter instance = PdfWriter.getInstance(document, new FileOutputStream(path));
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.UNIQUE_PREFERENCES_NAME, 0);
                String string = sharedPreferences.getString("PDF_Password", null);
                if (string != null) {
                    instance.setEncryption(string.getBytes(), string.getBytes(), 2052, 2);
                }
                document.open();
                i = 0;
                while (true) {
                    if (i >= DisplaySelectedImageActivity.imageuri.size()) {
                        break;
                    } else if (isCanceled) {
                        break;
                    } else {
                        Bitmap bitmap = decodeFile(new File((String) DisplaySelectedImageActivity.imageuri.get(i)));
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        if (Boolean.valueOf(sharedPreferences.getBoolean("grayscale_checked", false)).booleanValue()) {
                            greyBmp = DisplaySelectedImageActivity.grayScaleImage(bitmap);
                        } else {
                            greyBmp = bitmap;
                        }
                        String string2 = sharedPreferences.getString("imagecompress_checked", null);
                        if (string2 == null) {
                            quality = 80;
                        } else if (string2.equals(getString(R.string.low))) {
                            quality = 80;
                        } else if (string2.equals(getString(R.string.medium))) {
                            quality = 60;
                        } else if (string2.equals(getString(R.string.high))) {
                            quality = 25;
                        } else if (string2.equals(getString(R.string.nocompresssion))) {
                            quality = 100;
                        }
                        try {
                            greyBmp.compress(CompressFormat.JPEG, quality, byteArrayOutputStream);
                            image = Image.getInstance(byteArrayOutputStream.toByteArray());
                            greyBmp = scaleBitmap(greyBmp);
                            if (((float) greyBmp.getWidth()) <= pageSize.getWidth()) {
                                if (((float) greyBmp.getHeight()) <= pageSize.getHeight()) {
                                    image.scaleAbsolute((float) greyBmp.getWidth(), (float) greyBmp.getHeight());
                                    image.setAbsolutePosition((pageSize.getWidth() - image.getScaledWidth()) / 2.0f, (pageSize.getHeight() - image.getScaledHeight()) / 2.0f);
                                    final int i2 = i;
                                    document.add(image);
                                    document.newPage();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            TextView text = progrss;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append(i2 + 1);
                                            sb.append("/");
                                            sb.append(DisplaySelectedImageActivity.imageuri.size());
                                            text.setText(sb.toString());
                                            bnp.setProgress(i2 + 1);
                                        }
                                    });
                                    i++;
                                }
                            }
                            image.scaleAbsolute(pageSize.getWidth(), pageSize.getHeight());
                            image.setAbsolutePosition((pageSize.getWidth() - image.getScaledWidth()) / 2.0f, (pageSize.getHeight() - image.getScaledHeight()) / 2.0f);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final int i22 = i;
                        document.add(image);
                        document.newPage();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                TextView textview = progrss;
                                StringBuilder sb = new StringBuilder();
                                sb.append(i22 + 1);
                                sb.append("/");
                                sb.append(DisplaySelectedImageActivity.imageuri.size());
                                textview.setText(sb.toString());
                                bnp.setProgress(i22 + 1);
                            }
                        });
                        i++;
                    }
                }
                document.close();
                DisplaySelectedImageActivity.imageuri.clear();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(String str) {
            super.onPostExecute(str);
            if (main_dialog.isShowing() && main_dialog != null) {
                main_dialog.dismiss();
            }
            Intent intent = new Intent(DisplaySelectedImageActivity.this, GeneratedPdfs.class);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            startActivity(intent);
            finish();
        }
    }

    public static Bitmap grayScaleImage(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = 0; i < width; i++) {
            for (int i2 = 0; i2 < height; i2++) {
                int pixel = bitmap.getPixel(i, i2);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                double d = (double) red;
                Double.isNaN(d);
                double d2 = d * 0.299d;
                double d3 = (double) green;
                Double.isNaN(d3);
                double d4 = d2 + (d3 * 0.587d);
                double d5 = (double) blue;
                Double.isNaN(d5);
                int i3 = (int) (d4 + (d5 * 0.114d));
                createBitmap.setPixel(i, i2, Color.argb(alpha, i3, i3, i3));
            }
        }
        return createBitmap;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            context = this;
            setContentView((int) R.layout.activity_display_selected_image);
            imageuri = new ArrayList<>();
            Intent intent = getIntent();
            imagename = intent.getStringExtra("Images");
            if (imagename.equals("Camera")) {
                imageuri.add(intent.getStringExtra("Capture_Image_Name"));
            } else if (imageuri != null) {
                path1 = intent.getStringArrayListExtra("Gallary_Image");
                StringBuilder sb = new StringBuilder();
                sb.append("onCreate: ");
                sb.append(path1);
                Log.i("fileaa", sb.toString());
                for (int i = 0; i < path1.size(); i++) {
                    imageuri.add(path1.get(i));
                }
            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle((CharSequence) "");
            setSupportActionBar(toolbar);
            ((TextView) findViewById(R.id.title_text)).setText("Selected Images");
            toolbar.setNavigationIcon((int) R.drawable.back);
            toolbar.setNavigationOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            coordinatorLayout = (RelativeLayout) findViewById(R.id.display_iamges);
            mImageSampleRecycler = findViewById(R.id.images_sample1);
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < 10; i2++) {
                arrayList.add(String.valueOf(i2));
            }
            setupRecycler();
            setupImageSamples();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mImageSampleRecycler.setLayoutManager(gridLayoutManager);
        mImageSampleRecycler.setNestedScrollingEnabled(true);
        mImageSampleRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
            }

            public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
            }
        });
    }

    private void setupImageSamples() {
        Context context2 = context;
        if (context2 != null) {
            adapter = new ImageSamplesAdapter(context2, imageuri, new ImageSamplesAdapter.OnImageEdit() {
                public void onImageEdit(int i) {
                    DisplaySelectedImageActivity displaySelectedImageActivity = DisplaySelectedImageActivity.this;
                    displaySelectedImageActivity.selectedPos = i;
                    CropImage.activity(Uri.fromFile(new File(displaySelectedImageActivity.adapter.getmSelectedImages().get(i)))).start(DisplaySelectedImageActivity.this);
                }
            });
            mImageSampleRecycler.setAdapter(adapter);
            mImageSampleRecycler.setHasFixedSize(true);
        }
        mItemTouchHelper.attachToRecyclerView(mImageSampleRecycler);
        StringBuilder sb = new StringBuilder();
        sb.append("setupImageSamples: ");
        sb.append(adapter.getmSelectedImages());
        Log.i("setupImageSamples", sb.toString());
    }

    public Bitmap scaleBitmap(Bitmap bitmap) {
        int i;
        int i2;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > height) {
            float f = ((float) width) / ((float) maxWidth);
            i = maxWidth;
            i2 = (int) (((float) height) / f);
        } else if (height > width) {
            float f2 = ((float) height) / ((float) maxHeight);
            i = (int) (((float) width) / f2);
            i2 = maxHeight;
        } else {
            i2 = maxHeight;
            i = maxWidth;
        }
        return Bitmap.createScaledBitmap(bitmap, i, i2, true);
    }

    public Bitmap decodeFile(File file) {
        try {
            Options options = new Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            StringBuilder sb = new StringBuilder();
            sb.append("decodeFile: ");
            sb.append(file.getAbsolutePath());
            Log.i("DATA", sb.toString());
            FileInputStream fileInputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(fileInputStream, null, options);
            fileInputStream.close();
            int i2 = options.outWidth;
            int i3 = options.outHeight;
            while (true) {
                if (i2 / 2 < 512) {
                    break;
                } else if (i3 / 2 < 512) {
                    break;
                } else {
                    i2 /= 2;
                    i3 /= 2;
                    i *= 2;
                }
            }
            Options options2 = new Options();
            options2.inSampleSize = i;
            FileInputStream fileInputStream2 = new FileInputStream(file);
            Bitmap decodeStream = BitmapFactory.decodeStream(fileInputStream2, null, options2);
            fileInputStream2.close();
            return decodeStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public void createPdf() {
        if (imageuri.size() > 0) {
            try {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.pdf_name_prompt_menu);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(false);
                Button button = (Button) dialog.findViewById(R.id.cancel);
                final EditText editText = (EditText) dialog.findViewById(R.id.inputpdfname);
                ((Button) dialog.findViewById(R.id.ok)).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        pdfname = editText.getText().toString().trim();
                        if (pdfname.equals("")) {
                            CustomSnackBar("Name cannot be blank");
                            return;
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constant.targetPath);
                        sb.append("/");
                        sb.append(pdfname);
                        sb.append(".pdf");
                        if (!new File(sb.toString()).exists()) {
                            creatingpdf = new creatingPDF();
                            creatingpdf.execute(new String[0]);
                            return;
                        }
                        CustomSnackBar("File name already exists");
                    }
                });
                button.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CustomSnackBar("Select Images to Create PDF File");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_pdf, menu);
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (myCursor != null) {
            myCursor.close();
        }
    }

    boolean returnAfterCapture;
    private Uri fileUri;

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_create_pdf) {
            createPdf();
            return true;
        } else if (itemId == R.id.action_open) {
            if (this.isOpenGallery) {
                this.isOpenGallery = false;
                ImagePicker.with(DisplaySelectedImageActivity.this)
                        .setFolderMode(true)
                        .setToolbarColor("#008577")
                        .setStatusBarColor("#008577")
                        .setFolderTitle("Album")
                        .setMultipleMode(true)
                        .setSelectedImages(ChooseActivity.images)
                        .setMaxSize(5)
                        .setBackgroundColor("#ffffff")
                        .setAlwaysShowDoneButton(true)
                        .setRequestCode(0)
                        .setKeepScreenOn(true)
                        .start();
            }
            return true;
        } else {
            if (itemId == R.id.action_camera) {
                ImagePicker.with(DisplaySelectedImageActivity.this)
                        .setCameraOnly(true)
                        .start();
            }
            return super.onOptionsItemSelected(menuItem);
        }
    }

    private File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("JPEG_");
        sb.append(format);
        sb.append("_");
        String sb2 = sb.toString();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), Constant.IMAGE_DIRECTORY_NAME);
        if (file.exists() || file.mkdirs()) {
            File createTempFile = File.createTempFile(sb2, ".jpg", file);
            mCurrentPhotoPath = createTempFile.getAbsolutePath();
            return createTempFile;
        }
        Log.d(Constant.IMAGE_DIRECTORY_NAME, "Oops! Failed create MyCamera directory");
        return null;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        StringBuilder sb = new StringBuilder();
        sb.append(isOpenGallery);
        sb.append("");
        Log.e("Called==", sb.toString());
        if (i == 0 && i2 == -1) {
            imageuri.add(mCurrentPhotoPath);
            adapter.notifyDataSetChanged();
        }
        ChooseActivity.images = intent.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
        printImages(ChooseActivity.images);

        if (i == 0 && i2 == -1) {
            String[] strArr = {"_id", "_data"};
            myCursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, strArr, null, null, "_id DESC");
            try {
                myCursor.moveToFirst();
                imageuri.add(myCursor.getString(myCursor.getColumnIndexOrThrow("_data")));
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                myCursor.close();
                throw th;
            }
            myCursor.close();
        }
    }

    private void printImages(ArrayList<com.nguyenhoanglam.imagepicker.model.Image> list) {
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                imageuri.add(((com.nguyenhoanglam.imagepicker.model.Image) list.get(i)).getPath());
            }
            adapter.notifyDataSetChanged();
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void onBackPressed() {
        setResult(0, new Intent());
        super.onBackPressed();
    }

    public void onResume() {
        super.onResume();
        isOpenGallery = true;
    }

    public void CustomSnackBar(String str) {
        try {
            Snackbar.make((View) coordinatorLayout, (CharSequence) str, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
