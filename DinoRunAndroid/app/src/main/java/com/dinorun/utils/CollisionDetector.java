package com.dinorun.utils;

import android.graphics.Rect;
import com.dinorun.models.Dino;
import com.dinorun.models.Cactus;

public class CollisionDetector {

    public static boolean isCollision(Dino dino, Cactus cactus) {
        Rect dinoRect = dino.getBoundingBox();
        Rect cactusRect = cactus.getBoundingBox();

        return Rect.intersects(dinoRect, cactusRect);
    }
}
