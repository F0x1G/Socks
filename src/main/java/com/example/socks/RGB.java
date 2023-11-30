package com.example.socks;

public class RGB {
    private int r;
    private int g;
    private int b;

    public RGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setR(com.example.socks.RGB rgb,int r){
        rgb.r = r;
    }
    public void setG(com.example.socks.RGB rgb,int g){
        rgb.g = g;
    }
    public void setB(com.example.socks.RGB rgb,int b){
        rgb.b = b;
    }
    public int getR(com.example.socks.RGB rgb){
        return rgb.r;
    }
    public int getG(com.example.socks.RGB rgb){
        return rgb.g;
    }
    public int getB(com.example.socks.RGB rgb){
        return rgb.b;
    }


}

