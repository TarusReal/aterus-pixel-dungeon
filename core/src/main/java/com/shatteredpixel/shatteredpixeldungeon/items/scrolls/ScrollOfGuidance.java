package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;
import com.watabou.noosa.Game;
import java.util.HashSet;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class ScrollOfGuidance extends Scroll {

    {
        icon = ItemSpriteSheet.Icons.SCROLL_GUIDANCE;
    }

    // Hilfsmethode zur Fallen-Erkennung
    private static boolean isTrap(int terr) {
        return terr == Terrain.TRAP;
    }

    @Override
    public void doRead() {
        detach(curUser.belongings.backpack);
        int start = curUser.pos;
        int exit = Dungeon.level.exit();
        int length = Dungeon.level.length();
        boolean[] passable = new boolean[length];
        GLog.i( Messages.get(this, "way") );
        for (int i = 0; i < length; i++) {
            int terr = Dungeon.level.map[i];
            passable[i] = ((Terrain.flags[terr] & Terrain.PASSABLE) != 0) && !isTrap(terr);
            if ((Terrain.flags[terr] & Terrain.SECRET) != 0) passable[i] = true;
        }
        PathFinder.setMapSize(Dungeon.level.width(), Dungeon.level.height());
        PathFinder.Path path = PathFinder.find(start, exit, passable);
        if (path == null || path.isEmpty()) {;
            identify();
            readAnimation();
            return;
        }

        // Create a larger area around the path
        HashSet<Integer> guidancePath = new HashSet<>();
        HashSet<Integer> expandedArea = new HashSet<>();

        // Add path cells and their neighbors to expanded area
        for (int cell : path) {
            guidancePath.add(cell);
            // Add all 8 surrounding cells
            for (int i : PathFinder.NEIGHBOURS8) {
                int n = cell + i;
                if (n >= 0 && n < length) {
                    expandedArea.add(n);
                }
            }

            // Discover secrets and traps
            int terr = Dungeon.level.map[cell];
            if ((Terrain.flags[terr] & Terrain.SECRET) != 0 || isTrap(terr)) {
                Dungeon.level.discover(cell);
            }
        }

        // Add the expanded area to the guidance path
        guidancePath.addAll(expandedArea);

        // Initial animation of the path
        for (int cell : path) {
            com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene.effectOverFog(
                    new com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell(cell, start, null, 0xFFFFFF00)
            );
        }

        // Convert path to HashSet<Integer>
        HashSet<Integer> pathSet = new HashSet<>();
        for (int cell : path) {
            pathSet.add(cell);
        }

        // Apply the buff with the full guidance path
        Buff.affect(hero, GuidanceBuff.class).setPath(guidancePath, pathSet);
        identify();
        readAnimation();
    }

    @Override
    public int value() {
        return isKnown() ? 40 * quantity : super.value();
    }

    // GuidanceBuff: Solange der Spieler auf dem Pfad ist, gibt es Haste
    public static class GuidanceBuff extends com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff {
        {
            type = buffType.POSITIVE;
            announced = false;
        }
        private HashSet<Integer> path;
        private HashSet<Integer> mainPath;
        private float timeSinceLastPulse = 0f;
        private static final float PULSE_INTERVAL = 3.0f; // seconds between pulses
        private int depth;

        @Override
        public boolean attachTo(com.shatteredpixel.shatteredpixeldungeon.actors.Char target) {
            depth = Dungeon.depth;
            return super.attachTo(target);
        }

        public void setPath(HashSet<Integer> path, HashSet<Integer> mainPath) {
            this.path = path;
            this.mainPath = mainPath;
            timeSinceLastPulse = 0f;
            pulseEffect(); // Initial pulse
        }

        private void pulseEffect() {
            for (int cell : path) {
                // Make the cell fully visible
                Dungeon.level.visited[cell] = true;
                Dungeon.level.mapped[cell] = true;

                boolean isMainPath = mainPath.contains(cell);

                // Only show effect for main path
                if (isMainPath) {
                    // Create a custom image for the effect
                    Image cellEffect = new Image(TextureCache.createSolid(0xFFFFFF00)); // Yellow color
                    cellEffect.origin.set(0.5f);
                    cellEffect.point(DungeonTilemap.tileToWorld(cell).offset(
                            DungeonTilemap.SIZE / 2f,
                            DungeonTilemap.SIZE / 2f));

                    // Make the base size larger (1.5x the original size)
                    cellEffect.scale.set(1.5f);
                    cellEffect.alpha(0.8f);
                    cellEffect.origin.set(cellEffect.width()/2, cellEffect.height()/2);

                    // Add pulsing animation with larger scale
                    cellEffect.scale.set(1.2f);
                    cellEffect.alpha(0.8f);
                    cellEffect.scale.set(1.8f);  // Increased from 1.2f to 1.8f for more visible pulse
                    cellEffect.alpha(0f);
                    cellEffect.origin.set(cellEffect.width()/2, cellEffect.height()/2);
                    cellEffect.origin.set(0.5f);
                    cellEffect.scale.set(5f);  // Keep the final size larger
                    cellEffect.alpha(0.8f);

                    // Add to effect layer
                    com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene.effect(cellEffect);
                }
            }
        }

        @Override
        public void detach() {
            if (hero != null) {
                Buff.detach(hero, Haste.class);
                if (hero.sprite != null) {
                    // Debug: Sprite-Status vor idle
                    System.out.println("[GuidanceBuff.detach] Sprite before idle: looping=" + hero.sprite.looping() + ", isMoving=" + getSpriteField(hero.sprite, "isMoving"));
                    hero.sprite.idle();
                    // Setze isMoving explizit auf false
                    try {
                        java.lang.reflect.Field isMovingField = hero.sprite.getClass().getDeclaredField("isMoving");
                        isMovingField.setAccessible(true);
                        isMovingField.setBoolean(hero.sprite, false);
                    } catch (Exception e) {}
                    // Setze curAnim auf idle und stoppe Animation
                    try {
                        java.lang.reflect.Field curAnimField = hero.sprite.getClass().getDeclaredField("curAnim");
                        curAnimField.setAccessible(true);
                        Object idleAnim = hero.sprite.getClass().getDeclaredField("idle").get(hero.sprite);
                        curAnimField.set(hero.sprite, idleAnim);
                        // Setze looping und finished auf false/true
                        java.lang.reflect.Field loopingField = idleAnim.getClass().getDeclaredField("looped");
                        loopingField.setAccessible(true);
                        loopingField.setBoolean(idleAnim, false);
                        java.lang.reflect.Field finishedField = idleAnim.getClass().getDeclaredField("finished");
                        finishedField.setAccessible(true);
                        finishedField.setBoolean(idleAnim, true);
                    } catch (Exception e) {}
                    // Debug: Sprite-Status nach idle
                    System.out.println("[GuidanceBuff.detach] Sprite after idle: looping=" + hero.sprite.looping() + ", isMoving=" + getSpriteField(hero.sprite, "isMoving"));
                }
                // Neu: interrupt() aufrufen, um alle laufenden Aktionen zu stoppen
                try {
                    java.lang.reflect.Method interruptMethod = hero.getClass().getDeclaredMethod("interrupt");
                    interruptMethod.setAccessible(true);
                    interruptMethod.invoke(hero);
                } catch (Exception e) {}
                // Debug: Log Status vor und nach ready()
                System.out.println("[GuidanceBuff.detach] Vor ready: ready=" + getField(hero, "ready") + ", curAction=" + getField(hero, "curAction"));
                try {
                    java.lang.reflect.Method readyMethod = hero.getClass().getDeclaredMethod("ready");
                    readyMethod.setAccessible(true);
                    readyMethod.invoke(hero);
                } catch (Exception e) {}
                System.out.println("[GuidanceBuff.detach] Nach ready: ready=" + getField(hero, "ready") + ", curAction=" + getField(hero, "curAction"));
            }
            super.detach();
        }

        @Override
        public boolean act() {
            if (Dungeon.depth != depth) {
                if (hero != null) {
                    Buff.detach(hero, Haste.class);
                    if (hero.sprite != null) {
                        // Debug: Sprite-Status vor idle
                        System.out.println("[GuidanceBuff.act] Sprite before idle: looping=" + hero.sprite.looping() + ", isMoving=" + getSpriteField(hero.sprite, "isMoving"));
                        hero.sprite.idle();
                        // Setze isMoving explizit auf false
                        try {
                            java.lang.reflect.Field isMovingField = hero.sprite.getClass().getDeclaredField("isMoving");
                            isMovingField.setAccessible(true);
                            isMovingField.setBoolean(hero.sprite, false);
                        } catch (Exception e) {}
                        // Setze curAnim auf idle und stoppe Animation
                        try {
                            java.lang.reflect.Field curAnimField = hero.sprite.getClass().getDeclaredField("curAnim");
                            curAnimField.setAccessible(true);
                            Object idleAnim = hero.sprite.getClass().getDeclaredField("idle").get(hero.sprite);
                            curAnimField.set(hero.sprite, idleAnim);
                            // Setze looping und finished auf false/true
                            java.lang.reflect.Field loopingField = idleAnim.getClass().getDeclaredField("looped");
                            loopingField.setAccessible(true);
                            loopingField.setBoolean(idleAnim, false);
                            java.lang.reflect.Field finishedField = idleAnim.getClass().getDeclaredField("finished");
                            finishedField.setAccessible(true);
                            finishedField.setBoolean(idleAnim, true);
                        } catch (Exception e) {}
                        // Debug: Sprite-Status nach idle
                        System.out.println("[GuidanceBuff.act] Sprite after idle: looping=" + hero.sprite.looping() + ", isMoving=" + getSpriteField(hero.sprite, "isMoving"));
                    }
                    // Neu: interrupt() aufrufen, um alle laufenden Aktionen zu stoppen
                    try {
                        java.lang.reflect.Method interruptMethod = hero.getClass().getDeclaredMethod("interrupt");
                        interruptMethod.setAccessible(true);
                        interruptMethod.invoke(hero);
                    } catch (Exception e) {}
                    // Debug: Log Status vor und nach ready()
                    System.out.println("[GuidanceBuff.act] Vor ready: ready=" + getField(hero, "ready") + ", curAction=" + getField(hero, "curAction"));
                    try {
                        java.lang.reflect.Method readyMethod = hero.getClass().getDeclaredMethod("ready");
                        readyMethod.setAccessible(true);
                        readyMethod.invoke(hero);
                    } catch (Exception e) {}
                    System.out.println("[GuidanceBuff.act] Nach ready: ready=" + getField(hero, "ready") + ", curAction=" + getField(hero, "curAction"));
                }
                detach();
                return false;
            }

            // If we somehow don't have a path or hero anymore, detach
            if (path == null || hero == null) {
                detach();
                return false;
            }

            timeSinceLastPulse += Game.elapsed;
            try {
                // Only update visibility and apply effects if we're on the correct depth
                if (Dungeon.depth == depth) {
                    // Update visibility of all path cells
                    for (int cell : path) {
                        // Make sure we don't go out of bounds
                        if (cell >= 0 && cell < Dungeon.level.length()) {
                            Dungeon.level.visited[cell] = true;
                            Dungeon.level.mapped[cell] = true;
                        }
                    }

                    // Trigger pulse effect at intervals for the main path
                    if (timeSinceLastPulse >= PULSE_INTERVAL) {
                        timeSinceLastPulse = 0f;
                        pulseEffect();
                    }

                    // Apply Haste if hero is on the main path and on the same depth
                    if (mainPath.contains(hero.pos)) {
                        if (Buff.find(hero, Haste.class) == null) {
                            Buff.prolong(hero, Haste.class, 1);
                            SpellSprite.show(hero, SpellSprite.HASTE, 1, 1, 0);
                        }
                    } else {
                        // Remove Haste if hero is not on the main path
                        Buff.detach(hero, Haste.class);
                    }
                }
            } catch (Exception e) {
                // If any error occurs, clean up and detach to prevent issues
                if (hero != null) {
                    Buff.detach(hero, Haste.class);
                }
                detach();
                return false;
            }
            spend(1f);
            return true;
        }

        // Hilfsmethode für Debug-Log
        private Object getField(Object obj, String fieldName) {
            try {
                java.lang.reflect.Field f = obj.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.get(obj);
            } catch (Exception e) { return "?"; }
        }

        // Hilfsmethode für Sprite-Debug-Log
        private Object getSpriteField(Object obj, String fieldName) {
            try {
                java.lang.reflect.Field f = obj.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                return f.get(obj);
            } catch (Exception e) { return "?"; }
        }
    }
}
