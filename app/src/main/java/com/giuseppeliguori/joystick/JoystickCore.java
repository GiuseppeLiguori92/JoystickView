package com.giuseppeliguori.joystick;

/**
 * Created by giuseppeliguori on 26/05/2017.
 */

public class JoystickCore {

    private static JoystickCore instance;

    public static JoystickCore getInstance() {
        if (instance == null) {
            instance = new JoystickCore();
        }
        return instance;
    }

    private JoystickCore() {}

    /**
     * This method allow to calculate the angle when user moveJoystick the joystick
     * it will be calculate based on the distance X and Y from center
     * @param deltaX difference from current X position to center
     * @param deltaY difference from current Y position to center
     * @return angle value in radiant
     */
    public double calculateAngle(float deltaX, float deltaY) {
        if (deltaX == 0 && deltaY == 0)
            return 0;
        else if (deltaX >= 0 && deltaY >= 0)
            return Math.atan(deltaY / deltaX);
        else if (deltaX >= 0 && deltaY < 0)
            return Math.atan(deltaY / deltaX) + Math.PI*2.0f;
        else if (deltaX < 0 && deltaY >= 0)
            return Math.atan(deltaY / deltaX) + Math.PI;
        else if (deltaX < 0 && deltaY < 0)
            return Math.atan(deltaY / deltaX) + Math.PI;
        return 0;
    }

    public float[] calculateStrengthsPercentage(float deltaX, float deltaY, float radius) {
        // Pythagoras
        double hypotenuse = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

        float strength = (float) (hypotenuse / radius * 100.0f);
        if (strength >= 100.0f) { strength = 100.0f; }

        float strengthX = deltaX / radius * 100;
        if (strengthX >= 100.0f) { strengthX = 100.0f; }
        if (strengthX <= -100.0f) { strengthX = -100.0f; }

        float strengthY =  deltaY / radius * 100;
        if ( strengthY >= 100.0f) { strengthY = 100.0f; }
        if ( strengthY <= -100.0f) { strengthY = -100.0f; }

        return new float[] {strength, strengthX, strengthY};
    }

    public float[] getAdjustedPositions(float deltaX, float deltaY, float radius, float centerX, float centerY, float angle) {
        // Pythagoras
        double hypotenuse = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        if (hypotenuse > radius) {
            float adjustedX = (float) (centerX + radius * Math.cos(angle));
            float adjustedY = (float) (centerY + radius * Math.sin(angle));
            return new float[]{adjustedX, adjustedY};
        } else {
            return new float[]{deltaX+centerX, deltaY+centerY};
        }
    }
}
