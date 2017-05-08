package com.giuseppeliguori.joystick;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.MotionEvent;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by giuseppeliguori on 26/04/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private JoystickView joystickView;
    private float radius;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Before
    public void init() {
        System.out.println("Init called");
        joystickView = (JoystickView) mActivityRule.getActivity().findViewById(R.id.joystickRight);
        Field field;
        try {
            field = joystickView.getClass().getDeclaredField("mExternalRadius");
            field.setAccessible(true);
            radius = (float) field.get(joystickView);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isJoystickDisplayed() {
        onView(withId(R.id.joystickRight)).check(matches(isDisplayed()));
        assertNotNull(joystickView);
        assertTrue(joystickView.isShown());
    }

    @Test
    public void checkZeroAngle() {
        assertNotNull(joystickView);
        assertEquals(joystickView.getAngle(), 0.0f, 0.0f);
    }

    @Test
    public void checkZeroPower() {
        assertNotNull(joystickView);
        assertEquals(joystickView.getPower(), 0.0f, 0.0f);
    }

    @Test
    public void checkZeroPowerX() {
        assertNotNull(joystickView);
        assertEquals(joystickView.getPowerX(), 0.0f, 0.0f);
    }

    @Test
    public void checkZeroPowerY() {
        assertNotNull(joystickView);
        assertEquals(joystickView.getPowerY(), 0.0f, 0.0f);
    }

    @Test
    public void touchFromCenterToRightAndCheckAngle() {
        //onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.joystickRight)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Send touch events.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                // Get view absolute position
                int[] location = new int[2];
                view.getLocationOnScreen(location);
//
                float[] startCoordinates = new float[] { joystickView.getWidth()/2.0f + location[0], joystickView.getHeight()/2.0f + location[1] };
                float[] endCoordinates = new float[] { joystickView.getWidth()/2.0f + location[0] + radius, joystickView.getHeight()/2.0f + location[1] };
                float[] precision = new float[] { 1f, 1f };

                MotionEvent down = MotionEvents.sendDown(uiController, startCoordinates, precision).down;
                uiController.loopMainThreadForAtLeast(1000);
                MotionEvents.sendMovement(uiController, down, endCoordinates);
                uiController.loopMainThreadForAtLeast(1000);
                MotionEvents.sendUp(uiController, down, endCoordinates);
            }
        });
        assertEquals(joystickView.getAngle(), 0.0f, 0.001f);
    }

    @Test
    public void touchFromCenterToLeftAndCheckAngle() {
        //onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.joystickRight)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Send touch events.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                // Get view absolute position
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                float[] startCoordinates = new float[] { joystickView.getWidth()/2.0f + location[0], joystickView.getHeight()/2.0f + location[1] };
                float[] endCoordinates = new float[] { joystickView.getWidth()/2.0f + location[0] - radius, joystickView.getHeight()/2.0f + location[1] };
                float[] precision = new float[] { 1f, 1f };

                MotionEvent down = MotionEvents.sendDown(uiController, startCoordinates, precision).down;
                uiController.loopMainThreadForAtLeast(50);
                MotionEvents.sendMovement(uiController, down, endCoordinates);
                uiController.loopMainThreadForAtLeast(1000);
                MotionEvents.sendUp(uiController, down, endCoordinates);
            }
        });
        assertEquals(joystickView.getAngle(), Math.PI, 0.001f);
    }

    @Test
    public void touchFromCenterToTopAndCheckAngle() {
        //onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.joystickRight)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Send touch events.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                // Get view absolute position
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                float[] startCoordinates = new float[] { joystickView.getWidth()/2.0f + location[0], joystickView.getHeight()/2.0f + location[1] };
                float[] endCoordinates = new float[] { joystickView.getWidth()/2.0f + location[0], joystickView.getHeight()/2.0f + location[1] - radius };
                float[] precision = new float[] { 1f, 1f };

                MotionEvent down = MotionEvents.sendDown(uiController, startCoordinates, precision).down;
                uiController.loopMainThreadForAtLeast(50);
                MotionEvents.sendMovement(uiController, down, endCoordinates);
                uiController.loopMainThreadForAtLeast(1000);
                MotionEvents.sendUp(uiController, down, endCoordinates);
            }
        });
        assertEquals(joystickView.getAngle(), 0.0f, 0.001f);
    }

    /**
     * Perform action of waiting for a specific time.
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

}

