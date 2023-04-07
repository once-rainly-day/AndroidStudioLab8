// MyGLSurfaceView.java

package com.example.opengllesson;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Контейнер, у якому графіка OpenGL ES може бути виведена на екран.
 * Також можна використовувати для перехоплення подій дотику, наприклад, коли користувач
 * взаємодіє з об'єктами.
 */
public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer mRenderer;
    public MyGLSurfaceView(Context context) {
        super(context);
        // Створення контексту OpenGL ES 2.0
        setEGLContextClientVersion(2);
        // Підключення Renderer для рисування на поверхні GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
    }

    // Координати останньої точки
    float mPrevX;
    float mPrevY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // Отримання дистанції
                float dx = x - mPrevX;
                float dy = y - mPrevY;
                // обчислення кута
                mRenderer.setAngle(mRenderer.getAngle() + ((dx + dy) * 180.0f / 320));
                requestRender(); // запит на рисування
        }
        mPrevX = x;
        mPrevY = y;
        return true;
    }
}