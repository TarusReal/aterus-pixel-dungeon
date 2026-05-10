package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;

public class SurfaceRoom extends StandardRoom {

    @Override
    public int minWidth() { return Math.max(super.minWidth(), 6); }

    @Override
    public int minHeight() { return Math.max(super.minHeight(), 6); }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{1, 0, 0};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.GRASS);

        // small decorative patch of high grass in center
        Point c = center();
        Painter.fill(level, new com.watabou.utils.Rect(c.x-1, c.y-1, c.x+1, c.y+1), Terrain.HIGH_GRASS);

        for (Room.Door door : connected.values()) {
            door.set( Room.Door.Type.REGULAR );
        }
    }

}
