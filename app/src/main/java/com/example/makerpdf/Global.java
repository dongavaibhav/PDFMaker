package com.example.makerpdf;

import com.itextpdf.text.BaseColor;

public class Global {

    BaseColor basecolor = new BaseColor(0,0,255);

    private static final Global ourInstance = new Global();

    public static Global getInstance() {
        return ourInstance;
    }

    private Global() {

    }

    public BaseColor getColor() {
        return basecolor;
    }

    public void setColor(BaseColor s2) {
        basecolor = s2;
    }
}
