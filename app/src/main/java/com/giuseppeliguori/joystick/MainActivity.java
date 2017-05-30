package com.giuseppeliguori.joystick;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final float K = 3.0f;

    // Layout params
    private RelativeLayout mLayout;
    private float mWidthLayout  = -1;
    private float mHeightLayout = -1;

    // Square view
    private View mView;
    private float mWidthView    = -1;
    private float mHeightView   = -1;

    // Joysticks
    private JoystickView joystickViewRight;
    private JoystickView joystickViewLeft;

    // Values output
    private TextView textViewJoystickRight;
    private TextView textViewJoystickLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = (RelativeLayout) findViewById(R.id.layout);
        mView = findViewById(R.id.view);

        ViewTreeObserver vto = mLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mWidthLayout  = mLayout.getMeasuredWidth();
                mHeightLayout = mLayout.getMeasuredHeight();
                mWidthView = mView.getMeasuredWidth();
                mHeightView = mView.getMeasuredHeight();
            }
        });

        textViewJoystickRight = (TextView) findViewById(R.id.valuesJoystickRight);
        textViewJoystickLeft = (TextView) findViewById(R.id.valuesJoystickLeft);

        joystickViewRight = (JoystickView) findViewById(R.id.joystickRight);
        joystickViewRight.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onJoystickMoveListener(final float strengthX, final float strengthY, final float strength, final float angle, final long pressedTime) {
                //Log.d(TAG, "onJoystickMoveListener() called with: powerX = [" + powerX + "], powerY = [" + powerY + "], power = [" + power + "], angle = [" + angle + "], pressedTime = [" + pressedTime + "]");
                //Log.d(TAG, "------------------------------------");

                if (mWidthLayout <= 0 || mHeightLayout <= 0) { return; }
                textViewJoystickRight.setText(
                        "PowerX: " + String.format("%.00f", strengthX) + "\n" +
                                "PowerY: " + String.format("%.00f", strengthY) + "\n" +
                                "Power: " + String.format("%.00f", strength) + "\n" +
                                "Angle: " + String.format("%.00f",angle) + "\n" +
                                "Time: " + pressedTime + "\n"
                );

                if (mView.getX() + strengthX /10.0f < mWidthView/4.0f) {
                    mView.setX(mWidthView/4.0f);
                } else if (mView.getX() + strengthX /10.0f + mWidthView > mWidthLayout - mWidthView/4.0f) {
                    mView.setX(mWidthLayout - mWidthView - mWidthView/4.0f);
                } else {
                    mView.setX(mView.getX() + strengthX /10.0f);
                }

                if (mView.getY() + strengthY /10.0f < mHeightView/4.0f) {
                    mView.setY(mHeightView/4.0f);
                } else if (mView.getY() + strengthY /10.0f + mHeightView > mHeightLayout - mHeightView/4.0f) {
                    mView.setY(mHeightLayout - mHeightView - mHeightView/4.0f);
                } else {
                    mView.setY(mView.getY() + strengthY /10.0f);
                }
            }
        });

        joystickViewLeft = (JoystickView) findViewById(R.id.joystickLeft);
        joystickViewLeft.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onJoystickMoveListener(final float strengthX, final float strengthY, final float strength, final float angle, final long pressedTime) {
//                Log.d(TAG, "onJoystickMoveListener() called with: powerX = [" + powerX + "], powerY = [" + powerY + "], power = [" + power + "], angle = [" + angle + "], pressedTime = [" + pressedTime + "]");
//                Log.d(TAG, "------------------------------------");

                if (mWidthLayout <= 0 || mHeightLayout <= 0) { return; }
                textViewJoystickLeft.setText(
                        "PowerX: " + String.format("%.00f", strengthX) + "\n" +
                                "PowerY: " + String.format("%.00f", strengthY) + "\n" +
                                "Power: " + String.format("%.00f", strength) + "\n" +
                                "Angle: " + String.format("%.00f",angle) + "\n" +
                                "Time: " + pressedTime + "\n"
                );
                mView.setRotation(angle+90.0f);
            }
        });
    }
}
