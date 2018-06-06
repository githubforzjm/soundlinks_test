package com.example.wave.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.wave.model.ColorUtil;

import java.util.ArrayList;

/**
 * Created by jimin on 2018/5/30.
 */

public class MyRingWave1 extends View {
    private static final String TAG = "MyRingWave";

    private static final int SAMLLEST_DISTANCE = 40;// 两个圆环之间的最小间距
    private static final int REFRESH_FREQUENCE = 40;//每隔40ms刷新一次视图
    private static final int ALPHA_DECREASE_DEGRESS = -5;// 每隔40ms圆环透明度减少5
    private static final int RADIUS_INCREASE_DEGRESS = 5;//每隔40ms圆环半径增加3
    private static final int RADIUS_MAX = 80;//最大半径

    private static final double G = 980;
    private static final double LOSS_RATE = 0.6;

    private ArrayList<Point> pointList;// 所有point
    boolean isRunning = false;// 是否绘制视图

    private boolean bTouchEffect = true;//触摸动画效果

    public void setTouchEffect(boolean b) {
        this.bTouchEffect = true;
    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            flushData();
            invalidate();

            if (isRunning) {//继续刷新视图
                handler.sendEmptyMessageDelayed(0, REFRESH_FREQUENCE);
            }
        }
    };

    public MyRingWave1(Context context, AttributeSet attrs) {
        super(context, attrs);
        pointList = new ArrayList<Point>();
    }

    protected void flushData() {
        for (int i = 0; i < pointList.size(); i++) {
            Point p = pointList.get(i);
            int alpha = p.p.getAlpha();
            if (alpha == 0) {//当圆环的透明度小于0时，不在绘制此圆环，所以reomve掉
                pointList.remove(i);
                continue;
            }

			/*
             * alpha 值降低，radius值增加
			 */
            alpha += ALPHA_DECREASE_DEGRESS;
            if (alpha < 0) {
                alpha = 0;
            }
            p.p.setAlpha(alpha);
            if (p.radius < RADIUS_MAX)
                p.radius += RADIUS_INCREASE_DEGRESS;

            p.currY = p.getCurrY();

            Log.i(TAG, "Point: x:" + p.x + " y:" + p.y + " currY:" + p.currY + " speed:" + p.speed + " currSpeed:" + p.currSpeed);
        }
        if (pointList.size() == 0) {
            isRunning = false;//圆环数量变为0时，不再进行绘制，此时onDraw不再被调用
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < pointList.size(); i++) {
            Point p = pointList.get(i);
            canvas.drawCircle(p.x, p.currY, p.radius, p.p);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_MOVE:
                // 获取手指在屏幕上移动的坐标
                int x = (int) event.getX();
                int y = (int) event.getY();
                Log.i(TAG, "ACTION_MOVE x:" + x + " y:" + y);

                int rand = (int) (Math.random() * 100);

                Log.i(TAG, "ACTION_MOVE rand:" + rand);

                if (bTouchEffect)
                    addPoint(x, y, rand);
                break;

            default:
                break;
        }

        return true;
    }


    public void addPoint(int value) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0
                || height <= 0)
            return;


        int x = (int) (Math.random() * width);
        int y = (int) (Math.random() * height);

        addPoint(x, y, value);

    }


    /**
     * @param x      圆点横坐标
     * @param y      圆点纵坐标
     * @param volume 值越大，颜色越深
     */
    private void addPoint(int x, int y, int volume) {

        Paint paint = getPaint(volume);

        // 第一次添加执行动画（刷新视图）
        if (pointList.size() == 0) {
            addPointToList(x, y, paint);

            isRunning = true;
            handler.sendEmptyMessage(0);

        } else {//
            Point p = pointList.get(pointList.size() - 1);
            if (Math.abs(p.x - x) > SAMLLEST_DISTANCE && Math.abs(p.y - y) > SAMLLEST_DISTANCE) {//控制圆点之间的距离，不至于太小
                addPointToList(x, y, paint);
            }
        }
    }

    private void addPointToList(int x, int y, Paint p) {
        Point wave = new Point();
        wave.x = x;//起始 x
        wave.y = y;//起始 y
        wave.currX = x;
        wave.currY = y;
        wave.speed = 0;
        wave.currSpeed = 0;
        wave.p = p;
        wave.startTime = System.currentTimeMillis();
        pointList.add(wave);
    }

    private class Point {
        int x;// 起始 x
        int y;// 起始 y
        int speed;//起始 速度

        int currSpeed;//当前速度
        int currX;//当前 x
        int currY;//当前 y

        long startTime;
        Paint p;
        int radius;// 半径


        public int getCurrY() {
            int maxHeight = getHeight();

            if ((this.currY <= maxHeight) && this.currSpeed > 0) {//向下
                int h = maxHeight - this.y;
                this.speed = 0;

                double time = (System.currentTimeMillis() - this.startTime) / 1000.0;
                this.currSpeed = (int) (this.speed + G * time);
                int currH = (int) ((G * h - 0.5 * this.currSpeed * this.currSpeed) / G);

                this.currY = maxHeight - currH;
            } else if ((this.currY <= maxHeight)) {//向上
                int h = maxHeight - this.y;

                double time = (System.currentTimeMillis() - this.startTime) / 1000.0;
                this.currSpeed = (int) (this.speed + G * time);
                int currH = (int) ((G * h - 0.5 * this.currSpeed * this.currSpeed) / G);

                this.currY = maxHeight - currH;
                if (this.currSpeed > 0)//升到最高点下降
                    this.startTime = System.currentTimeMillis();

            } else {//反弹
                int h = maxHeight - this.y;
                h = (int) (h * LOSS_RATE);

                this.y = maxHeight - h;
                this.currY = maxHeight;
                this.speed = 0 - (int) Math.sqrt(G * h * 2);
                this.currSpeed = speed;
                this.startTime = System.currentTimeMillis();
            }

            return this.currY;
        }
    }

    public Paint getPaint(int volume) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Style.FILL);
        p.setColor(getColor(volume));
        return p;
    }

    private int getColor(int volume) {
        int index = volume / 6;
        if (index >= ColorUtil.RED_SYSTEM.length)
            return ColorUtil.RED_SYSTEM.length - 1;
        return Color.parseColor(ColorUtil.RED_SYSTEM[index]);
    }
}
