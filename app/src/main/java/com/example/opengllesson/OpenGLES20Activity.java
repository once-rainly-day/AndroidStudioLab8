// OpenGLES20Activity.java

package com.example.opengllesson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.media.MediaPlayer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class OpenGLES20Activity extends AppCompatActivity {
    static final int MENU_PLAYPAUSE = Menu.FIRST + 1;
    static final int MENU_STOP = Menu.FIRST + 2;
    static final String TAG = "OpenGLES20Activity";
    GLSurfaceView mGLView;
    private MediaPlayer mediaPlayer;
    private MenuItem playpauseMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(this, R.raw.testmedia);

        // Створюємо і підключаємо компонент
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }
    /**
     * Створення контекстних меню для
     * використання MediaPlayer в застосунку
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        playpauseMenuItem = menu.add(Menu.NONE, MENU_PLAYPAUSE, Menu.NONE, R.string.menu_playpause);
        menu.add(Menu.NONE, MENU_STOP, Menu.NONE, R.string.menu_stop);
        return true;
    }

    /**
     * Реалізація функціоналу користування MediaPlayer
     * за допомогою його стандартних методів:
     * <p>start() - запуск мелодії;</p>
     * <p>pause() - пауза;</p>
     * stop() - вимкнення мелодії.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PLAYPAUSE:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    Toast.makeText(this, "Play song...", Toast.LENGTH_SHORT).show();
                    item.setTitle("Pause");
                }
                else {
                    mediaPlayer.pause();
                    Toast.makeText(this, "Song on pause...", Toast.LENGTH_SHORT).show();
                    item.setTitle("Play");
                }
                return true;
            case MENU_STOP:
                mediaPlayer.stop();
                /**
                 * Знову створюємо об'єкт класу MediaPLayer,
                 * так як, якщо ми після виклику методу stop()
                 * викличемо метод start(), він почне програш медіафайлу
                 * з останньої точки зупинки. Для stop() ця точка
                 * є кінцем файлу, тому він почне з кінця та моментально
                 * зупиниться. Щоб гарантовано програвати файл з початку,
                 * ми якраз і ствроюмо знову об'єкт MediaPlayer.
                 */
                mediaPlayer = MediaPlayer.create(this, R.raw.testmedia);
                Toast.makeText(this, "Stop song...", Toast.LENGTH_SHORT).show();
                playpauseMenuItem.setTitle("Play");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause(); // Зупиняємо потік малювання
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume(); // Старт потоку малювання
    }

    /**
     * Метод для компіляції OpenGL шейдеру
     * @param type - тип шейдера
     * @param shaderCode - текст шейдера
     * @return - повертає id для шейдера
     */
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    /**
     * Метод відладки OpenGL викликів
     * @param glOperation - назва OpenGL виклику для перевірки
     */
    public static void checkGlError(String glOperation) {
        int error; // одержання усіх помилок у циклі
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            // створення виключення
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Звільнюємо ресурси MediaPlayer при закритті Activity
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}