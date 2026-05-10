/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

public abstract class DungeonTilemap extends Tilemap {

    public static final int SIZE = 16;

    protected int[] map;

    public DungeonTilemap(String tex) {
        super(tex, new TextureFilm(tex, SIZE, SIZE));
    }

    @Override
    // we need to retain two arrays, map is the dungeon tilemap which we can reference.
    // Data is our own internal image representation of the tiles, which may differ.
    public void map(int[] data, int cols) {
        map = data;
        super.map(new int[data.length], cols);
    }

    @Override
    public synchronized void updateMap() {
        if (map == null || data == null) return;
        for (int i = 0; i < data.length; i++)
            data[i] = getTileVisual(i, map[i], false);
        super.updateMap();
    }

    @Override
    public synchronized void updateMapCell(int cell) {
        // Safely update a 3x3 area around the cell; guard against out-of-range indices
        if (map == null || data == null) return;
        int max = data.length;
        for (int i : PathFinder.NEIGHBOURS9) {
            int idx = cell + i;
            if (idx >= 0 && idx < max) {
                data[idx] = getTileVisual(idx, map[idx], false);
                super.updateMapCell(idx);
            }
        }
    }

    protected abstract int getTileVisual(int pos, int tile, boolean flat);

    public int screenToTile(int x, int y) {
        return screenToTile(x, y, false);
    }

    public int screenToTile(int x, int y, boolean wallAssist) {
        PointF p = camera().screenToCamera(x, y).
                offset(this.point().negate()).
                invScale(SIZE);

        // snap to the edges of the tilemap (guard against null level)
        int lw = Dungeon.level != null ? Dungeon.level.width() : 1;
        int lh = Dungeon.level != null ? Dungeon.level.height() : 1;
        p.x = GameMath.gate(0, p.x, lw - 0.001f);
        p.y = GameMath.gate(0, p.y, lh - 0.001f);

        int cell = (int) p.x + (int) p.y * lw;

        // wall assist is used to make raised perspective tapping a bit easier.
        // If the pressed tile is a wall tile, the tap can be 'bumped' down into a none-wall tile.
        // currently this happens if the bottom 1/4 of the wall tile is pressed.
        if (wallAssist && map != null && isWallAssistable(cell)) {
            int maxIndex = (data != null) ? data.length : lw * lh;

            if (cell + mapWidth < maxIndex
                    && p.y % 1 >= 0.75f
                    && !isWallAssistable(cell + mapWidth)) {
                cell += mapWidth;
            }
        }

        return cell;
    }

    private boolean isWallAssistable(int cell) {
        if (map == null || cell < 0 || cell >= map.length) {
            return false;
        }

        if (DungeonTileSheet.wallStitcheable(map[cell])) {
            return true;
        }

        // caves region deco is very wall-like, so it counts
        if (Dungeon.depth >= 10 && Dungeon.depth <= 15
                && (map[cell] == Terrain.REGION_DECO || map[cell] == Terrain.REGION_DECO_ALT)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean overlapsPoint(float x, float y) {
        return true;
    }

    public void discover(int pos, int oldValue) {
        int visual = getTileVisual(pos, oldValue, false);
        if (visual < 0) return;

        final Image tile = new Image(texture);
        tile.frame(tileset.get(getTileVisual(pos, oldValue, false)));
        tile.point(tileToWorld(pos));

        parent.add(tile);

        parent.add(new AlphaTweener(tile, 0, 0.6f) {
            protected void onComplete() {
                tile.killAndErase();
                killAndErase();
            }
        });
    }

    public static PointF tileToWorld(int pos) {
        return new PointF(pos % Dungeon.level.width(), pos / Dungeon.level.width()).scale(SIZE);
    }

    public static PointF tileCenterToWorld(int pos) {
        return new PointF(
                (pos % Dungeon.level.width() + 0.5f) * SIZE,
                (pos / Dungeon.level.width() + 0.5f) * SIZE);
    }

    public static PointF raisedTileCenterToWorld(int pos) {
        return new PointF(
                (pos % Dungeon.level.width() + 0.5f) * SIZE,
                (pos / Dungeon.level.width() + 0.1f) * SIZE);
    }

    @Override
    public boolean overlapsScreenPoint(int x, int y) {
        return true;
    }

}
