package com.gathin.tvhome;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextClock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/** Draws the Home time behind the skyline of the bundled wallpaper. */
public final class ScenicTextClock extends TextClock {
    // Normalized points along the part of the bundled ridge that can cross the digits.
    private static final float[] RIDGE = {
            0f, .46f, .25f, .416f, .28f, .406f, .30f, .411f, .33f, .43f,
            .36f, .411f, .385f, .403f, .399f, .388f, .414f, .384f, .42f, .380f,
            .44f, .386f, .452f, .401f, .469f, .406f, .49f, .427f, .515f, .419f,
            .543f, .441f, .565f, .441f, .587f, .421f, .61f, .428f, .64f, .443f,
            .67f, .438f, .70f, .447f, .75f, .456f, 1f, .46f
    };
    private static final byte[] WALLPAPER_SHA256 = {
            14, -104, 67, 100, 109, -15, -40, -57, -89, -75, -73, -74, 16, 87, 115, -95,
            -31, 53, -79, 51, -106, -89, 86, -85, 45, -67, 22, 76, -82, 13, -75, -99
    };

    private final Path sky = new Path();
    private final int[] clockLocation = new int[2];
    private final int[] sceneLocation = new int[2];
    private View scene;
    private boolean matchWallpaper;

    public ScenicTextClock(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    void setScene(View scene, File wallpaper) {
        this.scene = scene;
        matchWallpaper = isBundledWallpaper(wallpaper);
        updateInk(getHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        updateInk(height);
    }

    private void updateInk(int height) {
        if (scene == null || height == 0) return;
        getPaint().setShader(new LinearGradient(0, 0, 0, height,
                new int[] {0xFFF0EEF6, 0xFFF6F1EE, 0xCCE9DCD9},
                new float[] {0f, .68f, 1f}, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!matchWallpaper || scene.getWidth() == 0 || scene.getHeight() == 0) {
            super.onDraw(canvas);
            return;
        }

        getLocationInWindow(clockLocation);
        scene.getLocationInWindow(sceneLocation);
        // Match the wallpaper ImageView's center crop.
        float width = Math.max(scene.getWidth(), scene.getHeight() * (16f / 9f));
        float height = width * (9f / 16f);
        float left = sceneLocation[0] - clockLocation[0] + (scene.getWidth() - width) / 2f;
        float top = sceneLocation[1] - clockLocation[1] + (scene.getHeight() - height) / 2f;
        sky.rewind();
        sky.moveTo(left, top);
        sky.lineTo(left + width, top);
        for (int i = RIDGE.length - 2; i >= 0; i -= 2) {
            sky.lineTo(left + RIDGE[i] * width, top + RIDGE[i + 1] * height);
        }
        sky.close();

        int saved = canvas.save();
        canvas.clipPath(sky);
        super.onDraw(canvas);
        canvas.restoreToCount(saved);
    }

    private static boolean isBundledWallpaper(File wallpaper) {
        if (wallpaper == null || wallpaper.length() != 277412L) return false;
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(wallpaper.toPath()));
            return Arrays.equals(digest, WALLPAPER_SHA256);
        } catch (IOException | NoSuchAlgorithmException e) {
            return false;
        }
    }
}
