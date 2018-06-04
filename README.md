# soundlinks_test


**有个录音权限需要动态申请，暂时没加**



##效果

	1.按住按钮说话，声音越大，产生的圆环颜色越深
	2.滑动slider，越往右侧，产生的圆环颜色越深
	3.圆环在变化过程中，半径逐渐增大，透明度逐渐降低
	
##具体实现

自定义控件**MyRingWave.java**
	
onDraw方法如下：

	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < pointList.size(); i++) {
            Point p = pointList.get(i);
            canvas.drawCircle(p.x, p.y, p.radius, p.p);
        }
    }  
	
由于绘制出来的效果是动态变化的，则需要不断的去出发onDraw方法的调用，同时绘制的圆环参数（半径，透明度）是要不断变化的，参数变化的代码如下
	
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
            p.radius += RADIUS_INCREASE_DEGRESS;
            p.p.setStrokeWidth(p.radius / 3);
        }
        Log.i(TAG, "flushDate size:" + pointList.size());
        if (pointList.size() == 0) {
            isRunning = false;//圆环数量变为0时，不再进行绘制，此时onDraw不再被调用
        }
    }
	

通过以下的代码，实现不断调用flushDate方法，同时出发onDraw方法刷新视图
	
	Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            flushData();
            invalidate();

            if (isRunning) {//继续刷新视图
                handler.sendEmptyMessageDelayed(0, REFRESH_FREQUENCE);
            }
        }
    };


	/**
     * 
     * @param x 圆点横坐标
     * @param y 圆点纵坐标
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
            if (Math.abs(p.x - x) > SAMLLEST_DISTANCE || Math.abs(p.x - x) > SAMLLEST_DISTANCE) {//控制圆点之间的距离，不至于太小
                addPointToList(x, y, paint);
            }
        }
    }


	
设计思路如下图：
	
![](http://chuantu.biz/t6/324/1528103009x-1566673363.png)