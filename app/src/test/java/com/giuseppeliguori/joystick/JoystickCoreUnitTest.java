package com.giuseppeliguori.joystick;

import com.giuseppeliguori.joystick.Repeat.Repeat;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JoystickCoreUnitTest {

    private double getAngle(float deltaX, float deltaY) {
        double angle = JoystickCore.getInstance().calculateAngle(deltaX, deltaY);
        return Math.toDegrees(angle);
    }

    @Test
    public void angleIsZeroWhenDeltaXisZeroAndDeltaYisZero() {
        System.out.println("JoystickCoreUnitTest.angleIsZeroWhenDeltaXisZeroAndDeltaYisZero");
        assertEquals(0d, getAngle(0,0), 0d);
    }

    @Test
    public void angleIsNinetyWhenDeltaXisZeroAndDeltaYisOne() {
        System.out.println("JoystickCoreUnitTest.angleIsNinetyWhenDeltaXisZeroAndDeltaYisOne");
        assertEquals(90d, getAngle(0, 1), 0d);
    }

    @Test
    public void angleIsOneHundredEightyWhenDeltaXisMinusOneAndDeltaYisZero() {
        System.out.println("JoystickCoreUnitTest.angleIsOneHundredEightyWhenDeltaXisMinusOneAndDeltaYisZero");
        assertEquals(180d, getAngle(-1, 0), 0d);
    }

    @Test
    public void angleIsTwoHundredSeventyWhenDeltaXisZeroAndDeltaYisMinusOne() {
        System.out.println("JoystickCoreUnitTest.angleIsTwoHundredSeventyWhenDeltaXisZeroAndDeltaYisMinusOne");
        assertEquals(270d, getAngle(0, -1), 0d);
    }

    @Test
    public void angleIsTwoHundredTwentyfiveWhenDeltaXisMinusOneAndDeltaYisMinusOne() {
        System.out.println("JoystickCoreUnitTest.angleIsTwoHundredTwentyfiveWhenDeltaXisMinusOneAndDeltaYisMinusOne");
        assertEquals(225d, getAngle(-1, -1), 0d);
    }

    @Test
    public void angleIsWhenDeltaXisOneAndDeltaYisOne() {
        System.out.println("JoystickCoreUnitTest.angleIsTwoHundredTwentyfiveWhenDeltaXisMinusOneAndDeltaYisMinusOne");
        assertEquals(45d, getAngle(1, 1), 0d);
    }

    private float[] getStrengths(float deltaX, float deltaY, float radius) {
        return JoystickCore.getInstance().calculateStrengthsPercentage(deltaX, deltaY, radius);
    }

    @Test
    public void strengthsAreZeroWhenDeltaXIsZeroAndDeltaYIsZero() {
        System.out.println("JoystickCoreUnitTest.strengthIsZeroWhenDeltaXIsZeroAndDeltaYIsZero");
        float strengths[] =  getStrengths(0, 0, 100);
        assertEquals(strengths[0], 0 , 0);
        assertEquals(strengths[1], 0 , 0);
        assertEquals(strengths[2], 0 , 0);
    }

    @Test
    @Repeat(10)
    public void strengtIs7071AndStrengthXIs50AndStrengthYIs50WhenDeltaXIsFiftyAndDeltaYIsFifty() {
        System.out.println("JoystickCoreUnitTest.strengthIsZeroWhenDeltaXIsZeroAndDeltaYIsZero");
        float strengths[] =  getStrengths(50, 50, 100);
        // math sqrt (50*50) + (50*50) = 70.71
        assertEquals(70.71f, strengths[0] , 0.01f);
        assertEquals(50, strengths[1] , 0);
        assertEquals(50, strengths[2] , 0);
    }


}