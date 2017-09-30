package com.example.jh.pintu;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;  // 被弃用
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //利用二维数组创建若干个游戏小方块
    private ImageView[][] gameImageviews = new ImageView[5][5];
    //为了显示原图而存储的最初views
    private ImageView[][] oriviews = new ImageView[5][5];
    ;
    private GridLayout mGridLayout;
    private ImageView mNullIv;
    private GestureDetector mGestureDetector;
    private Button mbtnShowOri;
    private Button mbtnRandom;
    private Button mbtnComplete;
    // 监听接口
    private MyOnTouchListener myOnTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListenter();
        // 初始化游戏的若干个小方块
        initData();
        // 初始化游戏主界面，并添加若干个小方块
        changeToNow();
        // 设置某个方块为黑色区域，默认右下角
        setNullImageView(gameImageviews[4][4]);
        //手势监听
        mGestureDetector = new GestureDetector(this, new MyOnGestureListener());

//
        mbtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 复原
                changeToOri();
            }
        });
        mbtnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // 显示目前显示的view
                changeToNow();
                // 打乱顺序
                randomViews();

            }
        });

        mbtnShowOri.setOnTouchListener(myOnTouchListener);
        myOnTouchListener.setOnMyAction(new MyOnTouchListener.onMyAction() {
            @Override
            public void onMyActionDown() {
                //此时显示原图，更换views即可
                changeToOri();
            }

            @Override
            public void onMyAcitonUp() {
                changeToNow();
            }
        });
    }

    private void initData() {
        Bitmap bigMap = ((BitmapDrawable) getResources().getDrawable(R.drawable.iphone)).getBitmap();
        int width = bigMap.getWidth() / 5;
        int height = bigMap.getHeight() / 5;
        Log.d("宽度 =", String.valueOf(width));  // 275
        Log.d("高度 =", String.valueOf(height));  // 224
        for (int i = 0; i < gameImageviews.length; i++) {
            for (int j = 0; j < gameImageviews[0].length; j++) {
                /**
                 * j * width:第一个像素的x坐标
                 * i * width:第一个像素的y坐标
                 */
                Bitmap bm = Bitmap.createBitmap(bigMap, j * width, i * height, width, height);
                gameImageviews[i][j] = myNewImageView(bm, i, j);
                oriviews[i][j] = myNewImageView(bm, i, j);

            }
        }
    }

    private void initView() {
        mbtnShowOri = (Button) findViewById(R.id.btn_show_ori);
        mbtnRandom = (Button) findViewById(R.id.btn_random);
        mbtnComplete = (Button) findViewById(R.id.btn_complete);
        mGridLayout = (GridLayout) findViewById(R.id.gl_main_game);
        myOnTouchListener = new MyOnTouchListener();
    }

    private void setListenter() {
    }

    /**
     * row indices (start + span) mustn't exceed the row count
     * 行索引(start + span)不能超过行数
     * i ,j 最大是4
     */
    private void changeToNow() {
        mGridLayout.removeAllViews();
        for (int i = 0; i < gameImageviews.length; i++) {
            for (int j = 0; j < gameImageviews[0].length; j++) {
                mGridLayout.addView(gameImageviews[i][j]);
                Log.d("changeToNow =", String.valueOf(i) + String.valueOf(j));
            }
        }
    }

    /**
     * 更改为原图
     */
    private void changeToOri() {
        mGridLayout.removeAllViews();
        for (int i = 0; i < oriviews.length; i++) {
            for (int j = 0; j < oriviews[0].length; j++) {
                mGridLayout.addView(oriviews[i][j]);
                Log.d("changeToOri =", String.valueOf(i) + String.valueOf(j));
            }
        }
    }

    //
    private ImageView myNewImageView(Bitmap bm, int i, int j) {
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(bm);
        iv.setPadding(2, 2, 2, 2);
        iv.setTag(new GameData(bm, i, j));
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "图片被点击", Toast.LENGTH_SHORT).show();
                Boolean isNear = isNearOfNull((ImageView) view);
                //Toast.makeText(MainActivity.this, "Is the view near the null view? "+isNear,
                // Toast.LENGTH_SHORT).show();
                if (isNear)
                    changeDataByImageView((ImageView) view);
            }
        });
        return iv;
    }
//

    /**
     * 随机打乱图片顺序
     */
    private void randomViews() {
        for (int i = 0; i < 100; i++) {
            int type = (int) (((Math.random() * 1234 + 1) % 4) + 1);
            changeByDir(type, false); // type 1：上；2：下；3：左；4：右
            Log.d("test", "randomViews type: " + type);
        }
    }

    private void changeByDir(int type) {
        changeByDir(type, true);
    }

    /**
     * 根据返回值确定滑动方向然后指定imageview进行移动
     * @param type 1：上；2：下；3：左；4：右
     */
    private void changeByDir(int type, boolean isAnimator) {
        GameData data = (GameData) mNullIv.getTag();
        switch (type) {
            case 1:
                if (data.x < 4)
                    changeDataByImageView(gameImageviews[data.x + 1][data.y], isAnimator);
                break;
            case 2:
                if (data.x > 0)
                    changeDataByImageView(gameImageviews[data.x - 1][data.y], isAnimator);
                break;
            case 3:
                if (data.y < 4)
                    changeDataByImageView(gameImageviews[data.x][data.y + 1], isAnimator);
                break;
            case 4:
                if (data.y > 0)
                    changeDataByImageView(gameImageviews[data.x][data.y - 1], isAnimator);
                break;
            default:
                break;
        }
    }

    private void changeDataByImageView(final ImageView imageView) {
        changeDataByImageView(imageView, true);
    }

    /**
     * 设置动画，交换数据
     *
     * @param imageView
     */
    private void changeDataByImageView(final ImageView imageView, boolean isAnimator) {
        //不需要动画，直接进行数据交换
        if (!isAnimator) {
            imageView.clearAnimation();
            GameData from_data = (GameData) imageView.getTag();
            GameData null_data = (GameData) mNullIv.getTag();
            mNullIv.setImageBitmap(from_data.bm);
            null_data.bm = from_data.bm;
            null_data.p_x = from_data.p_x;
            null_data.p_y = from_data.p_y;
            setNullImageView(imageView);
            return;
        }
        //创建一个动画，设置好方向，移动的距离
        TranslateAnimation animation = null;
        if (imageView.getX() > mNullIv.getX()) {
            animation = new TranslateAnimation(0, -imageView.getWidth(), 0, 0);
        } else if (imageView.getX() < mNullIv.getX()) {
            animation = new TranslateAnimation(0, imageView.getWidth(), 0, 0);
        } else if (imageView.getY() < mNullIv.getY()) {
            animation = new TranslateAnimation(0, 0, 0, imageView.getWidth());
        } else if (imageView.getY() > mNullIv.getY()) {
            animation = new TranslateAnimation(0, 0, 0, -imageView.getWidth());
        }

        //设置动画时常
        animation.setDuration(70);
        //设置动画结束之后是否停留
        animation.setFillAfter(true);
        //动画结束之后真正交换数据
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.clearAnimation();
                GameData from_data = (GameData) imageView.getTag();
                GameData null_data = (GameData) mNullIv.getTag();
                mNullIv.setImageBitmap(from_data.bm);
                null_data.bm = from_data.bm;
                null_data.p_x = from_data.p_x;
                null_data.p_y = from_data.p_y;
                setNullImageView(imageView);
                if (isCompleted())
                    Toast.makeText(MainActivity.this, "你赢了！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animation);
    }

    /**
     * @return
     * isCompleted 游戏完成的的判断
     */
    private boolean isCompleted() {
        GameData null_data = (GameData) mNullIv.getTag();
        if (null_data.x != 4 || null_data.y != 4) {
            return false;
        }
        GameData from_data;
        for (int i = 0; i < gameImageviews.length; i++) {
            for (int j = 0; j < gameImageviews[0].length; j++) {
                if (mNullIv == gameImageviews[i][j])
                    continue;
                from_data = (GameData) gameImageviews[i][j].getTag();
                if (from_data.x != from_data.p_x || from_data.y != from_data.p_y)
                    return false;
            }
        }
        return true;
    }
//

    /**
     * 设置某个方块为空白
     *
     * @param iv：传入的实例
     */
    private void setNullImageView(ImageView iv) {
        iv.setImageBitmap(null);
        mNullIv = iv;
    }

    /**
     * 指定方块是否与空方块相邻
     *
     * @param imageView：指定的方块
     * @return true：相邻；false：不相邻
     */
    private boolean isNearOfNull(ImageView imageView) {
        GameData from_data = (GameData) imageView.getTag();
        GameData null_data = (GameData) mNullIv.getTag();
        Log.d("test", "from_data: " + from_data);
        Log.d("test", "null_data: " + null_data);
        if (from_data.y == null_data.y)
            if (from_data.x - null_data.x == 1 || from_data.x - null_data.x == -1)
                return true;
        if (from_data.x == null_data.x)
            if (from_data.y - null_data.y == 1 || from_data.y - null_data.y == -1)
                return true;


        return false;
    }

    /**
     * 手势事件监听
     */
    private class MyOnGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            int type = getDireByGes(motionEvent.getX(), motionEvent.getY(),
                    motionEvent1.getX(), motionEvent1.getY());
            changeByDir(type);
            Toast.makeText(MainActivity.this, "Type:" + type, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 根据起止坐标判断向哪个方向滑动
     *
     * @param start_x
     * @param start_y
     * @param end_x
     * @param end_y
     * @return 1：上；2：下；3：左；4：右
     */
    private int getDireByGes(float start_x, float start_y, float end_x, float end_y) {
        boolean isHorizontal = (Math.abs(start_x - end_x) > Math.abs(start_y - end_y)) ? true : false;
        if (isHorizontal) {
            return start_x > end_x ? 3 : 4;
        } else {
            return start_y > end_y ? 1 : 2;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
