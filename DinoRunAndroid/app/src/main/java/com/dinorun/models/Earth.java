package com.dinorun.models;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import com.dinorun.utils.Constants;

public class Earth {
    private float yPosition;
    private Paint solidLinePaint;
    private Paint dottedLinePaint;
    private PathEffect dottedLineEffect;

    public Earth(float xPosition, float yPosition, int color) {
        this.yPosition = yPosition;

        solidLinePaint = new Paint();
        solidLinePaint.setColor(color);
        solidLinePaint.setStrokeWidth(Constants.EARTH_GROUND_STROKE_WIDTH);

        dottedLinePaint = new Paint();
        dottedLinePaint.setColor(color);
        dottedLinePaint.setStrokeWidth(Constants.EARTH_GROUND_STROKE_WIDTH);
        dottedLineEffect = new DashPathEffect(new float[] { 20f, 40f }, 0f);
        dottedLinePaint.setPathEffect(dottedLineEffect);
    }

    public void update() {
        // Update any necessary logic for the Earth object
    }

    public void draw(Canvas canvas) {
        float solidLineY = yPosition;
        float dottedLineY = yPosition + 15f;

        canvas.drawLine(0f, solidLineY, canvas.getWidth(), solidLineY, solidLinePaint);
        canvas.drawLine(0f, dottedLineY, canvas.getWidth(), dottedLineY, dottedLinePaint);
    }
}
