package com.example.jh.pintu;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/9/30.
 */

public class GameData {

    public int x;
    public int y;
    public int p_x;
    public int p_y;
    public Bitmap bm;

    public GameData(Bitmap bm, int x, int y) {
        this.bm = bm;
        this.p_y = y;
        this.p_x = x;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "x: " + x + "/ y: " + y + "/ p_x: " + p_x + "/ p_y: " + p_y;
    }
}
