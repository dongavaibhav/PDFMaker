package com.example.makerpdf;

public class PdfDirc {
    double fileSize;
    private String pdf_date;
    private String pdf_name;
    private String pdf_path;
    private String pdf_size;

    public double getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(double d) {
        this.fileSize = d;
    }

    public String getPdf_name() {
        return this.pdf_name;
    }

    public String getPdf_date() {
        return this.pdf_date;
    }

    public String getPdf_size() {
        return this.pdf_size;
    }

    public String getPdf_path() {
        return this.pdf_path;
    }

    public void setPdf_name(String str) {
        this.pdf_name = str;
    }

    public void setPdf_date(String str) {
        this.pdf_date = str;
    }

    public void setPdf_size(String str) {
        this.pdf_size = str;
    }

    public void setPdf_path(String str) {
        this.pdf_path = str;
    }

    public PdfDirc(String str, String str2, String str3, String str4, double d) {
        this.pdf_name = str;
        this.pdf_date = str2;
        this.pdf_size = str3;
        this.pdf_path = str4;
        this.fileSize = d;
    }

    public Object length() {
        return getPdf_size();
    }
}
