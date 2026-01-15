package com.dinorun.utils;

import android.content.res.Resources;

public class Constants {
    // Screen dimensions (will be updated dynamically)
    public static int SCREEN_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static int SCREEN_HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;

    // Game speed settings
    public static final int GAME_SPEED = 16; // Milliseconds between updates (~60 FPS)

    // Dino settings
    public static final int DINO_WIDTH = 88;
    public static final int DINO_HEIGHT = 94;
    public static final int DINO_X_POSITION = SCREEN_WIDTH / 4;

    // Cactus settings
    public static final int CACTUS_WIDTH = 50;
    public static final int CACTUS_HEIGHT = 100;
    public static final int CACTUS_MIN_HEIGHT = 80;
    public static final int CACTUS_MAX_HEIGHT = 120;
    public static final int CACTUS_SPEED = 12;
    public static final int CACTUS_SPAWN_DISTANCE = 600;

    // Cloud settings
    public static final float CLOUD_VELOCITY = -2.5f;
    public static final int CLOUD_SPACING = 800;
    public static final int MIN_Y_POSITION = 100;
    public static final int MAX_Y_POSITION = 400;

    // Earth settings
    public static final float EARTH_Y_POSITION = SCREEN_HEIGHT - 200;
    public static final float EARTH_GROUND_STROKE_WIDTH = 8f;

    // Debug mode
    public static final boolean DEBUG_MODE = false; // Set to true to see collision boxes

    // Auto-play AI mode
    public static final boolean AUTO_PLAY_ENABLED = true; // AI plays the game automatically
    public static final int AUTO_JUMP_DETECTION_RANGE = 250; // Base detection range in pixels
}
