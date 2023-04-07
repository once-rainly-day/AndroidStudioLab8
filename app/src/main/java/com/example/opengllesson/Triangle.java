// Triangle.java

package com.example.opengllesson;

import static com.example.opengllesson.OpenGLES20Activity.checkGlError;
import static com.example.opengllesson.OpenGLES20Activity.loadShader;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    // текст вершинного шейдеру
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    " gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // текст піксельного шейдеру
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "gl_FragColor = vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // число координат для кожної вершини
    static final int COORDS_PER_VERTEX = 3;
    float triangleCoords[] = {
            // проти часової стрілки
            0.0f, 0.622008459f, 0.1f, // верхній кут
            -0.5f, -0.311004243f, 0.1f, // зліва унизу
            0.5f, -0.311004243f, 0.1f // зправа унизу
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 байта для вершини
    float color[] = {0.863671875f, 0.876953125f, 0.22265625f, 0.0f};

    public Triangle() {
        // масив байтів для координат вершин
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4); // 4 байта для float
        // порядок байтів пристрою
        bb.order(ByteOrder.nativeOrder());
        // вершинний масив
        vertexBuffer = bb.asFloatBuffer();
        // додавання координат
        vertexBuffer.put(triangleCoords);
        // на початок масиву
        vertexBuffer.position(0);
        // компіляція шейдерів
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        // підключення шейдерів
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    /**
     * Містить інструкції OpenGL ES для малювання фігури
     * @param mvpMatrix - матриця для малювання
     */
    public void draw(float[] mvpMatrix) {
        // використовуємо програму з шейдерами
        GLES20.glUseProgram(mProgram);
        // вказівник до змінної vPosition у вершинному шейдері
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // включення
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // підготовка координат фігури
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,

                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // одержати вказівник до змінної vColor у піксельному шейдері
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        // вказівник на матрицю перетворення
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation");
        // Застосування трансформації проектування і виду
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        checkGlError("glUniformMatrix4fv");
        // малювання по вершинам
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // Відключення вершинного масиву
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}