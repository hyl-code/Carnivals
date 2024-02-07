package com.example.carnivals.Painter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.carnivals.R;

import java.util.Stack;

@SuppressLint("AppCompatCustomView")
public class ColorImageView extends ImageView {
    private static int mtype;           // 1：自定义 ； 2：随机
    private Bitmap mBitmap;
    private static int mColor = -1;     // 默认颜色为白色
    private static int oldColor;
    private int newColor;
    private int isWeight = 0;
    private int isHeight = 0;
    /**
     * 边界的颜色
     */
    private int mBorderColor = -1;
    private boolean hasBorderColor = false;
    private Stack<Point> mStacks = new Stack<Point>();

    public ColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ColorImageView);
        mBorderColor = ta.getColor(R.styleable.ColorImageView_borderColor, -1);
        hasBorderColor = (mBorderColor != -1);

        L.e("hasBorderColor = " + hasBorderColor + " , mBorderColor = " + mBorderColor);

        ta.recycle();
    }

    @Override
    protected  void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();

        // 以宽度为标准，等比例缩放 view 的高度
        setMeasuredDimension(viewWidth,
                getDrawable().getIntrinsicHeight() * viewWidth / getDrawable().getIntrinsicWidth());
        L.e("viewWidth = " + getMeasuredWidth() + " , viewHeight = " + getMeasuredHeight());

        // 根据 drawable，去得到一个和 view 一样大小的 bitmap
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();
        Bitmap bm = drawable.getBitmap();
        mBitmap = Bitmap.createScaledBitmap(bm, getMeasuredWidth(), getMeasuredHeight(), true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            fillColorToSameArea(x, y);
        }

        return super.onTouchEvent(event);
    }

    /**
     * 根据 x, y 获得改点颜色，进行填充
     *
     * @param x
     * @param y
     */
    private void fillColorToSameArea(int x, int y) {
        Bitmap bm = mBitmap;

        int pixel = bm.getPixel(x, y);
        if (pixel == Color.TRANSPARENT || (hasBorderColor && mBorderColor == pixel)) {
            return;
        }
        if (mtype == 1) {
            newColor = mColor;
        } else {
            newColor = randomColor();
        }

        int w = bm.getWidth();
        int h = bm.getHeight();

        // 拿到该 bitmap 的颜色数组
        int[] pixels = new int[w * h];
        bm.getPixels(pixels, 0, w, 0, 0, w, h);
        // 填色
        fillColor(pixels, w, h, pixel, newColor, x, y);
        // 重新设置 bitmap
        bm.setPixels(pixels, 0, w, 0, 0, w, h);
        setImageDrawable(new BitmapDrawable(bm));
    }

    public static void getColorId(int acolor) {
        mColor = acolor;
    }

    public static void getType(int type) {
        mtype = type;
    }

    /**
     * @param pixels    像素数组
     * @param w         宽度
     * @param h         高度
     * @param pixel     当前点的颜色
     * @param newColor  填充色
     * @param i         横坐标
     * @param j         纵坐标
     */
    private void fillColor(int[] pixels, int w, int h, int pixel, int newColor, int i, int j) {
        // 遍历该 bitmap 的颜色数组，将颜色为 pixel 的点都替换成新的颜色
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (pixels[x + y * w] == pixel) {
                    pixels[x + y * w] = newColor;
                }
            }
        }
    }

}
