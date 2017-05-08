package com.giuseppeliguori.joystick;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by giuseppeliguori on 21/04/2017.
 */

public class JoystickView extends View implements View.OnTouchListener{

    private static final String TAG = "JoystickView";

    public static final int ANIMATION_TO_CENTER_DURATION = 100;
    // Width & Height definition
    private float mHeight = 0;
    private float mWidth  = 0;

    // Radius
    private float mExternalRadius = 0;
    private float mInternalRadius = 0;

    // Paint
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float STROKE_WIDTH = 6.0f;

    // Variables
    private float mStartX   = 0.0f;
    private float mStartY   = 0.0f;
    private float mCurrentX = 0.0f;
    private float mCurrentY = 0.0f;
    private float mPositionInternalCircle[] = new float[2];

    // Values
    private float mAngle        = 0;
    private float mPower        = 0;
    private float mPowerX       = 0;
    private float mPowerY       = 0;
    private long mStartPressedTime      = 0;
    private long mPressedTime           = 0;

    // Listener
    private OnJoystickMoveListener onJoystickMoveListener = null;

    // Updater
    private Handler mHandler = new Handler();

    public JoystickView(Context context) {
        super(context);
        init();
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnTouchListener(this);
        mHandler.post(mUiUpdater);
    }

    private Runnable mUiUpdater = new Runnable() {
        @Override
        public void run() {
            if (mStartPressedTime != 0) {
                mPressedTime = System.currentTimeMillis()-mStartPressedTime;
            }
            updateListener();
            mHandler.postDelayed(this, 10);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawExternalCircle(canvas);

        drawInternalCircle(canvas);

        //drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        canvas.drawLine(mWidth/2.0f, mHeight/2.0f, mPositionInternalCircle[0], mPositionInternalCircle[1], mPaint);
    }

    private void drawInternalCircle(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#77000000"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mWidth/2.0f, mHeight/2.0f, mExternalRadius, mPaint);

        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawCircle(mWidth/2.0f, mHeight/2.0f, mExternalRadius - STROKE_WIDTH/2.0f, mPaint);
    }

    private void drawExternalCircle(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#77000000"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mPositionInternalCircle[0], mPositionInternalCircle[1], mInternalRadius, mPaint);

        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawCircle(mPositionInternalCircle[0], mPositionInternalCircle[1], mInternalRadius - STROKE_WIDTH/2.0f, mPaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetVariables();
                mStartPressedTime = System.currentTimeMillis();
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                mPositionInternalCircle[0] = mWidth/2.0f;
                mPositionInternalCircle[1] = mHeight/2.0f;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                resetVariables();
                ValueAnimator xValueAnimator = ValueAnimator.ofFloat(mPositionInternalCircle[0], mWidth/2.0f);
                xValueAnimator.setDuration(ANIMATION_TO_CENTER_DURATION);
                xValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mPositionInternalCircle[0] = (float)animation.getAnimatedValue();
                        invalidate();
                    }
                });
                xValueAnimator.start();
                ValueAnimator yValueAnimator = ValueAnimator.ofFloat(mPositionInternalCircle[1], mHeight/2.0f);
                yValueAnimator.setDuration(ANIMATION_TO_CENTER_DURATION);
                yValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mPositionInternalCircle[1] = (float)animation.getAnimatedValue();
                        invalidate();
                    }
                });
                yValueAnimator.start();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentX = event.getRawX();
                mCurrentY = event.getRawY();
                mPositionInternalCircle = move(mStartX, mStartY, mCurrentX, mCurrentY, mExternalRadius, mInternalRadius);
                invalidate();
                break;
        }
        return true;
    }

    private void resetVariables() {
        mStartX = 0;
        mStartY = 0;
        mCurrentX = 0;
        mCurrentY = 0;
        mPower = 0;
        mPowerX = 0;
        mPowerY = 0;
        mPressedTime = 0;
        mStartPressedTime = 0;
    }

    private void updateListener() {
        if (onJoystickMoveListener != null) {
            onJoystickMoveListener.onJoystickMoveListener(mPowerX,
                    mPowerY,
                    mPower,
                    (float) Math.toDegrees(mAngle),
                    mPressedTime);
        }
    }

    public float[] move(float startX, float startY, float currentX, float currentY, float externalRadius, float internalRadius) {
        int position[] = new int[2];
        getLocationOnScreen(position);

        float xDiff = currentX - startX;
        float yDiff = currentY - startY;
        double hypotenuse = Math.sqrt(xDiff*xDiff + yDiff*yDiff);

        mPower = (float) (hypotenuse / externalRadius * 100.0f);
        if (mPower >= 100.0f) { mPower = 100.0f; }

        mPowerX = xDiff / mExternalRadius * 100;
        if (mPowerX >= 100.0f) { mPowerX = 100.0f; }
        if (mPowerX <= -100.0f) { mPowerX = -100.0f; }
        mPowerY =  yDiff / mExternalRadius * 100;
        if ( mPowerY >= 100.0f) { mPowerY = 100.0f; }
        if ( mPowerY <= -100.0f) { mPowerY = -100.0f; }

        mAngle = (float) calculateAngle(xDiff, yDiff);

        if (hypotenuse > externalRadius) {
            float calculatedX = (float) (mWidth/2.0f + (externalRadius) * Math.cos(mAngle));
            float calculatedY = (float) (mHeight/2.0f + (externalRadius) * Math.sin(mAngle));
            return new float[]{calculatedX, calculatedY};
        } else {
            return new float[]{currentX-startX+mWidth/2.0f, currentY-startY+mHeight/2.0f};
        }
    }

    private double calculateAngle(float deltaX, float deltaY) {
        if (deltaX >= 0 && deltaY >= 0)
            return Math.atan(deltaY / deltaX);
        else if (deltaX >= 0 && deltaY < 0)
            return Math.atan(deltaY / deltaX) + Math.PI*2.0f;
        else if (deltaX < 0 && deltaY >= 0)
            return Math.atan(deltaY / deltaX) + Math.PI;
        else if (deltaX < 0 && deltaY < 0)
            return Math.atan(deltaY / deltaX) + Math.PI;
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mExternalRadius = mWidth < mHeight ? mWidth/3.5f : mHeight/3.5f;
        mInternalRadius = mWidth < mHeight ? mWidth/5.0f : mHeight/5.0f;

        mPositionInternalCircle[0] = mWidth/2.0f;
        mPositionInternalCircle[1] = mHeight/2.0f;
    }

    public void setOnJoystickMoveListener(OnJoystickMoveListener onJoystickMoveListener) {
        this.onJoystickMoveListener = onJoystickMoveListener;
    }

    public float getAngle() {
        return mAngle;
    }

    public float getPower() {
        return mPower;
    }

    public float getPowerX() {
        return mPowerX;
    }

    public float getPowerY() {
        return mPowerY;
    }

    public long getPressedTime() { return mPressedTime; }

    public interface OnJoystickMoveListener {
        void onJoystickMoveListener(float powerX, float powerY, float power, float angle, long pressedTime);
    }
}


