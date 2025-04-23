package org.example.core;

import org.example.Main;
import org.example.test.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {

    private final Vector2d previousPos, currentPos;
    private final Vector2f displVec;
    private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false;
    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d( 0,  0);
        displVec = new Vector2f();
    }
    public void init() {
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindowHandler(), (window, xpos, ypos) -> {

        currentPos.x = xpos;
        currentPos.y = ypos;
        });

        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindowHandler(), (window, entered) -> {
        inWindow = entered;
        });




        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindowHandler(), (window, button, action, mods) -> {
            leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
    }

    public void input() {
        displVec.x = 0;
        displVec.y = 0;
        if(previousPos.x > 0 && previousPos. y > 0 && inWindow) {

        double x = currentPos.x ;
        double y = currentPos.y;
        boolean rotateX = x != 0;
        boolean rotateY = y != 0;
        if(rotateX)
            displVec.y = (float) x;
        if(rotateY)
            displVec.x = (float) y;

        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;


    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public boolean isLeftButtonPress() {
        return leftButtonPress;
    }

    public boolean isRightButtonPress() {
        return rightButtonPress;
    }
}
