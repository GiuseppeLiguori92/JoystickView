package com.giuseppeliguori.joystick;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by giuseppeliguori on 21/04/2017.
 */

public class JoystickView extends View implements View.OnTouchListener{

    private static final String TAG = "JoystickView";

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
    private float mAngle                = 0;
    private float mStrength             = 0;
    private float mStrengthX            = 0;
    private float mStrengthY            = 0;
    private long mStartPressedTime      = 0;
    private long mPressedTime           = 0;

    // Listener
    private OnJoystickMoveListener onJoystickMoveListener = null;

    // Animation
    public static final int ANIMATION_TO_CENTER_DURATION = 100;

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
                resetValues();
                mStartPressedTime = System.currentTimeMillis();
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                mPositionInternalCircle[0] = mWidth/2.0f;
                mPositionInternalCircle[1] = mHeight/2.0f;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                resetValues();

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

                calculateValues(mStartX, mStartY, mCurrentX, mCurrentY, mExternalRadius);
                moveJoystick(mStartX, mStartY, mCurrentX, mCurrentY, mExternalRadius);
                break;
        }
        return true;
    }

    private void calculateValues(float startX, float startY, float currentX, float currentY, float radius) {
        calculateStrengths(startX, startY, currentX, currentY, radius);
        calculateAngle(startX, startY, currentX, currentY);
    }

    private void resetValues() {
        mStartX = 0;
        mStartY = 0;
        mCurrentX = 0;
        mCurrentY = 0;
        mStrength = 0;
        mStrengthX = 0;
        mStrengthY = 0;
        mPressedTime = 0;
        mStartPressedTime = 0;
    }

    private void updateListener() {
        if (onJoystickMoveListener != null) {
            onJoystickMoveListener.onJoystickMoveListener(mStrengthX,
                    mStrengthY,
                    mStrength,
                    (float) Math.toDegrees(mAngle),
                    mPressedTime);
        }
    }

    private void calculateStrengths(float startX, float startY, float currentX, float currentY, float radius) {
        float deltaX = currentX - startX;
        float deltaY = currentY - startY;

        float strengths[] = JoystickCore.getInstance().calculateStrengthsPercentage(deltaX, deltaY, radius);
        mStrength = strengths[0];
        mStrengthX = strengths[1];
        mStrengthY = strengths[2];
    }

    public void calculateAngle(float startX, float startY, float currentX, float currentY) {
        float deltaX = currentX - startX;
        float deltaY = currentY - startY;

        mAngle = (float) JoystickCore.getInstance().calculateAngle(deltaX, deltaY);
    }

    public void moveJoystick(float startX, float startY, float currentX, float currentY, float radius) {
        float deltaX = currentX - startX;
        float deltaY = currentY - startY;

        float centerX = mWidth/2.0f;
        float centerY = mHeight/2.0f;
        mPositionInternalCircle = JoystickCore.getInstance().getAdjustedPositions(deltaX, deltaY, radius, centerX, centerY, mAngle);
        invalidate();
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

    public float getAngle() { return mAngle; }

    public float getPower() {
        return mStrength;
    }

    public float getPowerX() {
        return mStrengthX;
    }

    public float getPowerY() {
        return mStrengthY;
    }

    public long getPressedTime() { return mPressedTime; }

    public interface OnJoystickMoveListener {
        void onJoystickMoveListener(float powerX, float powerY, float power, float angle, long pressedTime);
    }
}


