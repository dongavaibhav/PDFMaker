package com.example.makerpdf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GeneratedPdfs extends AppCompatActivity {

    static RelativeLayout coordinatorLayoutView;
    static File[] files;
    static int len;
    public static ArrayList<Integer> multiselect_list = new ArrayList<>();
    boolean flag = false;
    public GeneratedPdfAdapter generatedPdfAdapter;

    ArrayList<PdfDirc> pdfList;
    ProgressBar progressBar;
    public RecyclerView recyclerView;
    LinearLayout tv1;

    private class pdfDocumentList extends AsyncTask<String, String, String> {
        private pdfDocumentList() {
        }

        public void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @SuppressLint("WrongThread")
        public String doInBackground(String... strArr) {
            if (GeneratedPdfs.files != null) {
                GeneratedPdfs.len = GeneratedPdfs.files.length;
                if (GeneratedPdfs.len > 0) {
                    tv1.setVisibility(View.GONE);
                }
                generatedPdfAdapter = new GeneratedPdfAdapter(get_directory(Constant.targetPath), GeneratedPdfs.multiselect_list, getApplication());
            } else {
                GeneratedPdfs.len = 0;
                tv1.setVisibility(View.VISIBLE);
            }
            return "";
        }

        public void onPostExecute(String str) {
            super.onPostExecute(str);
            try {
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(generatedPdfAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }


    public GeneratedPdfs() {
        multiselect_list.clear();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.fragment_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((CharSequence) "");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon((int) R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        pdfList = new ArrayList<>();
        coordinatorLayoutView = (RelativeLayout) findViewById(R.id.home_fragment);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv1 = (LinearLayout) findViewById(R.id.tv1);
        recyclerView = (RecyclerView) findViewById(R.id.pdflist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        File file = new File(Constant.targetPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        files = file.listFiles();
        new pdfDocumentList().execute(new String[0]);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplication(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            public void onItemClick(View view, int i) {

            }

            public void onItemLongClick(View view, int i) {
                if (!flag) {
                    Show_Dialog(i);
                }
            }
        }));
    }

    public void Show_Dialog(final int i) {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.pdf_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCanceledOnTouchOutside(true);
            TextView textView = (TextView) dialog.findViewById(R.id.open);
            TextView textView2 = (TextView) dialog.findViewById(R.id.delete1);
            TextView textView3 = (TextView) dialog.findViewById(R.id.rename1);
            TextView textView4 = (TextView) dialog.findViewById(R.id.share1);
            TextView textView5 = (TextView) dialog.findViewById(R.id.txt);
            final String pdf_path = ((PdfDirc) pdfList.get(i)).getPdf_path();
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    openFile(pdf_path);
                }
            });
            textView2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new Builder(GeneratedPdfs.this, R.style.MyAlertDialogStyle)
                            .setTitle("Delete PDF")
                            .setMessage("Are you sure you want to delete this PDF?")
                            .setPositiveButton(getResources().getText(R.string.action_delete), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    generatedPdfAdapter.remove(i);
                                    if (generatedPdfAdapter.getItemCount() == 0) {
                                        tv1.setVisibility(View.VISIBLE);
                                    }
                                    generatedPdfAdapter.notifyDataSetChanged();
                                }
                            }).setNeutralButton(getResources().getText(R.string.action_cancle), null)
                            .show();
                }
            });
            textView3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    renameFile(pdf_path, i);
                    generatedPdfAdapter.notifyDataSetChanged();
                }
            });
            textView4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    shareFile(pdf_path);
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openFile(String str) {
        File file = new File(str);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String str2 = "application/pdf";
        if (VERSION.SDK_INT > 23) {
            intent.setDataAndType(FileProvider.getUriForFile(this, "com.example.makerpdf.easyphotopicker.fileprovider", file), str2);
        } else {
            intent.setDataAndType(Uri.fromFile(file), str2);
        }
        try {
            startActivity(Intent.createChooser(intent, "Open file"));
        } catch (ActivityNotFoundException unused) {
            CustomSnackBar("No app to read PDF File");
        }
    }

    public void shareFile(String str) {
        File file = new File(str);
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (VERSION.SDK_INT > 23) {
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, "com.example.makerpdf.easyphotopicker.fileprovider", file));
        } else {
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
        }
        try {
            startActivity(Intent.createChooser(intent, "Share File "));
        } catch (ActivityNotFoundException unused) {
            CustomSnackBar("No app to read PDF File");
        }
    }

    public void renameFile(String str, int i) {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.pdf_name_prompt_menu);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(false);
            Button button = (Button) dialog.findViewById(R.id.cancel);
            Button button2 = (Button) dialog.findViewById(R.id.ok);
            final EditText editText = (EditText) dialog.findViewById(R.id.inputpdfname);
            final String str2 = str;
            final int i2 = i;
            final Dialog dialog2 = dialog;
            View.OnClickListener r0 = new View.OnClickListener() {
                public void onClick(View view) {
                    String obj = editText.getText().toString();
                    if (obj.equals("")) {
                        CustomSnackBar("Name cannot be blank");
                        return;
                    }
                    File file = new File(str2);
                    StringBuilder sb = new StringBuilder();
                    sb.append(Constant.targetPath);
                    sb.append("/");
                    sb.append(obj);
                    sb.append(".pdf");
                    File file2 = new File(sb.toString());
                    if (file2.exists()) {
                        CustomSnackBar("File name already exists");
                    } else if (file.renameTo(file2)) {
                        CustomSnackBar("File renamed");
                        ((PdfDirc) pdfList.get(i2)).setPdf_name(obj);
                        generatedPdfAdapter.notifyDataSetChanged();
                        dialog2.dismiss();
                    } else {
                        CustomSnackBar("File can't be renamed");
                    }
                    ((InputMethodManager) getApplication().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            };
            button2.setOnClickListener(r0);
            button.setOnClickListener(new View.OnClickListener() {
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
    }

    public ArrayList<PdfDirc> get_directory(String str) {
        double d;
        String str2;
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File file, File file2) {
                if (file.lastModified() > file2.lastModified()) {
                    return -1;
                }
                return file.lastModified() < file2.lastModified() ? 1 : 0;
            }
        });
        for (int i = 0; i < len; i++) {
            File file = files[i];
            double length = (double) file.length();
            Double.isNaN(length);
            double d2 = length / 1024.0d;
            double d3 = d2 / 1024.0d;
            if (d3 > 1.0d) {
                str2 = new DecimalFormat("0.00").format(d3).concat("MB");
                d = d3;
            } else {
                d = d2;
                str2 = new DecimalFormat("0").format(Math.round(d2)).concat("KB");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            ArrayList<PdfDirc> arrayList = pdfList;
            PdfDirc pdfList2 = new PdfDirc(file.getName(), simpleDateFormat.format(Long.valueOf(file.lastModified())), str2, file.getAbsolutePath(), d);
            arrayList.add(pdfList2);
        }
        return pdfList;
    }

    public void CustomSnackBar(String str) {
        try {
            Snackbar.make((View) coordinatorLayoutView, (CharSequence) str, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
