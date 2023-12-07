package com.example.socks;

import java.awt.*;

public class ThreadGuide {
    private Color[] ColorList;
    private int usingColor;
    private boolean work;
    public ThreadGuide(int Size){
        this.ColorList = new Color[Size];
        this.work = false;
        this.usingColor = 0;
    }
    public boolean isWork(){
        return work;
    }
    public void setWork(boolean b){
        work=b;
    }
     public Color getColor(int position){
        Color color = ColorList[position];
        return color;
     }
    public void setColor(int position, Color color){
        ColorList[position] = color;
    }
    public int getUsingColor(){
        return usingColor;
    }
    public void setUsingColor(int i){
        usingColor = i;
    }
}
