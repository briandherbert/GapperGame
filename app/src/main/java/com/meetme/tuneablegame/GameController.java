package com.meetme.tuneablegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;

/**
 * Created by bherbert on 3/25/16.
 */
public abstract class GameController {
    public abstract void init(Context context);
    public abstract Visual[] getVisuals();
    public abstract void onTouch(int action, float x, float y);
    public abstract void onCollision(Visual visual1, int idx1, Visual visual2, int idx2);

    public void onTick(long deltaMs) {
        // Basic collision detection
        for (Visual visual :  getVisuals()) {
            if (visual != null) visual.onTick(deltaMs);
        }
    }

    public void doDraw(Canvas canvas) {
        for (Visual visual : getVisuals()) {
            if (visual != null) visual.draw(canvas);
        }
    }

    public void detectCollisions() {
        // Basic collision detection

        int numVisuals = getVisuals().length;

        if (numVisuals <= 1) return;

        Visual iVisual, jVisual;

        //check for collisions with items
        for (int i = 0; i < numVisuals; i++) {
            int iRegionIdx = -1;

            iVisual = getVisuals()[i];
            if (iVisual == null) continue;

            for (Region region : iVisual.getHitRegions()) {
                iRegionIdx++;
                if (region == null) continue;

                for (int j = i + 1; j < numVisuals; j++) {
                    int jRegionIdx = -1;

                    jVisual = getVisuals()[j];
                    if (jVisual == null) continue;

                    for (Region jRegion : jVisual.getHitRegions()) {
                        jRegionIdx++;
                        if (jRegion == null) continue;

                        if (!region.quickReject(jRegion) && region.op(jRegion, Region.Op.INTERSECT)) {
                            onCollision(iVisual, iRegionIdx, jVisual, jRegionIdx);
                        }
                    }
                }
            }
        }
    }
}
