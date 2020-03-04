package com.example.makerpdf;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

public class GeneratedPdfAdapter extends Adapter<GeneratedPdfAdapter.GeneratedPdf> {
    private Context context;
    public ArrayList<PdfDirc> dirList = new ArrayList<>();
    public ArrayList<Integer> selected_usersList = new ArrayList<>();
    String targetPath;
    PdfDirc user;

    public class GeneratedPdf extends ViewHolder {
        ImageView check;
        RelativeLayout pdf;
        TextView pdfdate;
        ImageView pdflogo;
        TextView pdfname;
        TextView pdfsize;

        public GeneratedPdf(View view) {
            super(view);
            pdf = (RelativeLayout) view.findViewById(R.id.pdf);
            pdflogo = (ImageView) view.findViewById(R.id.pdf_logo);
            check = (ImageView) view.findViewById(R.id.check);
            pdfname = (TextView) view.findViewById(R.id.pdfname);
            pdfsize = (TextView) view.findViewById(R.id.pdfsize);
            pdfdate = (TextView) view.findViewById(R.id.date);
        }
    }

    public GeneratedPdfAdapter(ArrayList<PdfDirc> arrayList, ArrayList<Integer> arrayList2, Context context2) {
        dirList = arrayList;
        selected_usersList = arrayList2;
        context = context2;
    }

    public int getItemCount() {
        return dirList.size();
    }

    public GeneratedPdf onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new GeneratedPdf(LayoutInflater.from(context).inflate(R.layout.pdf_display_layout, null));
    }

    public void onBindViewHolder(GeneratedPdf generatedPdf, int i) {
        user = (PdfDirc) dirList.get(i);
        generatedPdf.pdfname.setText(user.getPdf_name());
        generatedPdf.pdfdate.setText(user.getPdf_date());
        generatedPdf.pdfsize.setText(user.getPdf_size());
        if (selected_usersList.contains(Integer.valueOf(i))) {
            generatedPdf.check.setVisibility(View.VISIBLE);
        } else {
            generatedPdf.check.setVisibility(View.GONE);
        }

    }

    public void remove(int i) {
        targetPath = ((PdfDirc) dirList.get(i)).getPdf_path();
        File file = new File(targetPath);
        try {
            if (!file.exists()) {
                return;
            }
            if (file.delete()) {
                dirList.remove(i);
                Toast.makeText(context, "File deleted.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(context, "File can't be deleted.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
