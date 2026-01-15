package com.dinorun;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dinorun.models.Cactus;
import com.dinorun.models.Cloud;
import com.dinorun.models.Dino;
import com.dinorun.models.Earth;
import com.dinorun.utils.CollisionDetector;
import com.dinorun.utils.Constants;
import com.dinorun.utils.ResourceManager;
import com.dinorun.utils.WebSocketClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, WebSocketClient.WebSocketEventListener {

    private static final String TAG = "GameView";

    // WebSocket configuration (UPDATED FOR REAL DEVICE - Use your computer's local
    // IP)
    private static final String WEBSOCKET_SERVER = "ws://10.238.40.77:8080";

    private GameThread gameThread;
    private Earth earth;
    private Dino dino;
    private List<Cactus> cacti;
    private boolean isPaused;
    private boolean isJumping;
    private boolean isDucking;
    private int speedBoostTimer = 0;

    // Auto-play AI mode
    private boolean autoPlay = Constants.AUTO_PLAY_ENABLED;
    private boolean giftOverride = false;
    private int giftOverrideCounter = 0;
    private static final int GIFT_OVERRIDE_DURATION = 60; // frames (~1 second at 60fps)
    private final int SPEED_BOOST_DURATION = 100; // frames

    private Paint scorePaint;
    private Paint highScorePaint;
    private int score;
    private int highScore;
    private boolean isGameOver;
    private Paint gameOverPaint;
    private Paint replayPaint;
    private List<Cloud> clouds;
    private int NUM_INITIAL_CLOUDS = 2;

    private float replayX, replayY;

    // WebSocket client
    private WebSocketClient webSocketClient;

    public GameView(Context context) {
        super(context);
        createClouds();
        createEarth();
        initScore();
        gameOverStatus();

        getHolder().addCallback(this);
        setFocusable(true);

        // Initialize WebSocket
        initWebSocket();
    }

    private void initWebSocket() {
        try {
            webSocketClient = new WebSocketClient(WEBSOCKET_SERVER, this);
            webSocketClient.connect();
            Log.d(TAG, "WebSocket initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize WebSocket: " + e.getMessage());
        }
    }

    private void createClouds() {
        clouds = new ArrayList<>();
        float y = getRandomYPosition();

        for (int i = 0; i < NUM_INITIAL_CLOUDS; i++) {
            Cloud cloud = new Cloud(getContext());
            float x = getNextCloudXPosition(i);
            cloud.setPosition(x, y);
            clouds.add(cloud);
        }
    }

    private void createEarth() {
        float earthX = 0f;
        float earthY = Constants.EARTH_Y_POSITION;
        earth = new Earth(earthX, earthY, getResources().getColor(R.color.object));
    }

    private void initScore() {
        score = 0;
        highScore = 0;

        scorePaint = new Paint();
        scorePaint.setColor(getResources().getColor(R.color.object));
        scorePaint.setTextSize(48);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setAntiAlias(true);

        highScorePaint = new Paint();
        highScorePaint.setColor(getResources().getColor(R.color.object));
        highScorePaint.setTextSize(36);
        highScorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        highScorePaint.setAntiAlias(true);
    }

    private void gameOverStatus() {
        isGameOver = false;

        gameOverPaint = new Paint();
        gameOverPaint.setColor(getResources().getColor(R.color.object));
        gameOverPaint.setTextSize(64);
        gameOverPaint.setTypeface(Typeface.DEFAULT_BOLD);
        gameOverPaint.setAntiAlias(true);

        replayPaint = new Paint();
        replayPaint.setColor(getResources().getColor(R.color.object));
        replayPaint.setTextSize(48);
        replayPaint.setTypeface(Typeface.DEFAULT_BOLD);
        replayPaint.setAntiAlias(true);
    }

    private float getNextCloudXPosition(int index) {
        return (index + 1) * Constants.CLOUD_SPACING;
    }

    private float getRandomYPosition() {
        return (float) (Math.random() * (Constants.MAX_Y_POSITION - Constants.MIN_Y_POSITION)
                + Constants.MIN_Y_POSITION);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ResourceManager.getInstance(getContext());

        dino = new Dino(getContext(), Constants.SCREEN_WIDTH / 4);
        cacti = new ArrayList<>();
        isPaused = false;

        gameThread = new GameThread(holder);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Not implemented
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                gameThread.setRunning(false);
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }

        // Clean up WebSocket
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
    }

    // ==================== EXTERNAL TRIGGER METHODS ====================

    /**
     * External jump trigger - callable from WebSocket or other sources
     */
    public void externalJumpTrigger() {
        Log.d(TAG, "External jump triggered!");
        if (!isGameOver && !isPaused && dino != null) {
            // Gift override: disable AI temporarily
            giftOverride = true;
            giftOverrideCounter = GIFT_OVERRIDE_DURATION;

            dino.jump();
        }
    }

    /**
     * External restart trigger - callable from WebSocket or other sources
     */
    public void externalRestartTrigger() {
        Log.d(TAG, "External restart triggered!");
        if (isGameOver) {
            restartGame();
        }
    }

    /**
     * External speed boost trigger - temporarily increases game speed
     */
    public void externalSpeedBoost() {
        Log.d(TAG, "External speed boost triggered!");
        if (!isGameOver && !isPaused) {
            // Gift override: disable AI temporarily
            giftOverride = true;
            giftOverrideCounter = GIFT_OVERRIDE_DURATION * 2; // Longer for speed boost

            speedBoostTimer = SPEED_BOOST_DURATION;
            // Speed will be increased in update() method
        }
    }

    /**
     * External duck trigger - makes dino duck (placeholder for future
     * implementation)
     */
    public void externalDuck() {
        Log.d(TAG, "External duck triggered!");
        if (!isGameOver && !isPaused) {
            isDucking = true;
            // Duck implementation can be added in Dino model
        }
    }

    // ==================== WEBSOCKET EVENT CALLBACKS ====================

    @Override
    public void onJump() {
        externalJumpTrigger();
    }

    @Override
    public void onRestart() {
        externalRestartTrigger();
    }

    @Override
    public void onSpeedBoost() {
        externalSpeedBoost();
    }

    @Override
    public void onDuck() {
        externalDuck();
    }

    // ==================== GAME LOOP ====================

    private class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private boolean isRunning;

        public GameThread(SurfaceHolder holder) {
            this.surfaceHolder = holder;
            this.isRunning = false;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            while (isRunning) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        update();
                        draw(canvas);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                // Sleep for a short duration to control the game speed (~60 FPS)
                try {
                    Thread.sleep(Constants.GAME_SPEED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            canvas.drawColor(getResources().getColor(R.color.background));

            if (isPaused) {
                if (Constants.DEBUG_MODE) {
                    Paint debugPaint = new Paint();
                    debugPaint.setColor(Color.RED);
                    debugPaint.setStyle(Paint.Style.STROKE);
                    debugPaint.setStrokeWidth(2f);

                    Rect dinoRect = dino.getBoundingBox();
                    canvas.drawRect(dinoRect, debugPaint);

                    for (Cactus cactus : cacti) {
                        Rect cactusRect = cactus.getBoundingBox();
                        canvas.drawRect(cactusRect, debugPaint);
                    }
                }
            }

            drawClouds(canvas);
            dino.draw(canvas);
            for (Cactus cactus : cacti) {
                cactus.draw(canvas);
            }
            earth.draw(canvas);

            // Draw the score and high score
            String scoreText = "Score: " + score;
            String highScoreText = "High Score: " + highScore;

            float scoreX = getWidth() - scorePaint.measureText(scoreText) - 100;
            float scoreY = 100;
            canvas.drawText(scoreText, scoreX, scoreY, scorePaint);

            float highScoreX = getWidth() - highScorePaint.measureText(highScoreText) - 100;
            float highScoreY = scoreY + highScorePaint.getTextSize() + 16;
            canvas.drawText(highScoreText, highScoreX, highScoreY, highScorePaint);

            // Draw speed boost indicator
            if (speedBoostTimer > 0) {
                Paint boostPaint = new Paint();
                boostPaint.setColor(Color.YELLOW);
                boostPaint.setTextSize(36);
                boostPaint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText("SPEED BOOST!", 50, 100, boostPaint);
            }

            if (isGameOver) {
                String gameOverText = "Game Over";
                String replayText = "Tap to Replay";

                float gameOverX = (getWidth() - gameOverPaint.measureText(gameOverText)) / 2;
                float gameOverY = getHeight() / 2 - gameOverPaint.getTextSize() - getHeight() / 4;

                replayX = (getWidth() - replayPaint.measureText(replayText)) / 2;
                replayY = getHeight() / 2 + replayPaint.getTextSize() - getHeight() / 4;

                canvas.drawText(gameOverText, gameOverX, gameOverY, gameOverPaint);
                canvas.drawText(replayText, replayX, replayY, replayPaint);
            }
        }
    }

    public void update() {
        if (!isPaused) {
            dino.update();

            // Handle gift override countdown
            if (giftOverride) {
                giftOverrideCounter--;
                if (giftOverrideCounter <= 0) {
                    giftOverride = false;
                    Log.d(TAG, "Gift override ended - AI resumed");
                }
            }

            // Handle speed boost
            if (speedBoostTimer > 0) {
                speedBoostTimer--;
            }

            // Calculate current speed multiplier
            float speedMultiplier = (speedBoostTimer > 0) ? 2.0f : 1.0f;

            // Update cacti with speed multiplier
            for (int i = cacti.size() - 1; i >= 0; i--) {
                Cactus cactus = cacti.get(i);

                // Apply speed boost if active
                for (int j = 0; j < (int) speedMultiplier; j++) {
                    cactus.update();
                }

                if (cactus.getX() + cactus.getWidth() < 0) {
                    cacti.remove(i);
                } else if (CollisionDetector.isCollision(dino, cactus)) {
                    gameOver();
                }
            }

            // ========== AUTO-PLAY AI MODE ==========
            if (autoPlay && !giftOverride && !isGameOver) {
                // Smart detection range based on game speed
                int detectionRange = Constants.AUTO_JUMP_DETECTION_RANGE;
                if (speedBoostTimer > 0) {
                    detectionRange = (int) (detectionRange * 1.5); // Farther detection during speed boost
                }

                // Check if any cactus is in range and dino is not jumping
                for (Cactus cactus : cacti) {
                    int dinoX = dino.getX();
                    int cactusX = cactus.getX();

                    // If cactus is approaching and within detection range
                    if (cactusX > dinoX && cactusX < dinoX + detectionRange && !dino.isJumping()) {
                        dino.jump();
                        isJumping = true;
                        Log.d(TAG, "AI auto-jump! Distance: " + (cactusX - dinoX));
                        break; // Only jump once per frame
                    }
                }
            }

            // Reset jumping flag when dino lands (handled in Dino.update())
            if (!dino.isJumping()) {
                isJumping = false;
            }

            // Add new cacti periodically
            if (cacti.isEmpty()
                    || cacti.get(cacti.size() - 1).getX() < Constants.SCREEN_WIDTH - Constants.CACTUS_SPAWN_DISTANCE) {
                int randomHeight = (int) (Math.random()
                        * (Constants.CACTUS_MAX_HEIGHT - Constants.CACTUS_MIN_HEIGHT + 1)
                        + Constants.CACTUS_MIN_HEIGHT);
                cacti.add(new Cactus(getContext(), Constants.SCREEN_WIDTH, randomHeight));
            }
            updateScore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() >= replayX && event.getX() <= replayX + replayPaint.measureText("Tap to Replay") &&
                    event.getY() >= replayY - replayPaint.getTextSize() && event.getY() <= replayY) {
                restartGame();
                return true;
            }
        } else if (!isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isPaused) {
                if (!isJumping) {
                    dino.jump();
                    isJumping = true;
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isJumping = false;
            isDucking = false;
        }

        return true;
    }

    private void gameOver() {
        isGameOver = true;
        isPaused = true;
        speedBoostTimer = 0; // Reset speed boost on game over
    }

    private void restartGame() {
        isGameOver = false;
        isPaused = false;
        score = 0;
        speedBoostTimer = 0;

        // Remove all cacti
        if (cacti != null && cacti.size() > 0) {
            Iterator<Cactus> iterator = cacti.iterator();
            while (iterator.hasNext()) {
                Cactus cactus = iterator.next();
                if (CollisionDetector.isCollision(dino, cactus)) {
                    iterator.remove();
                }
            }
        }
    }

    private void drawClouds(Canvas canvas) {
        for (Cloud cloud : clouds) {
            if (!isPaused)
                cloud.update();
            cloud.draw(canvas);
        }
    }

    private void updateScore() {
        score++;
        if (score > highScore) {
            highScore = score;
        }
    }
}
