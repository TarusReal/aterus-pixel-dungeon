package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.noosa.Game;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
=======
>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
=======
>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
=======
>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
=======
>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfForecast extends Scroll {

    {
        icon = ItemSpriteSheet.Icons.SCROLL_FORECAST;
    }

    /*@Override
    public boolean collect(Bag container) {
        GLog.i("Attempting to collect ScrollOfForecast");
        boolean collected = super.collect(container);
        GLog.i("ScrollOfForecast collection " + (collected ? "succeeded" : "failed"));
        return collected;
    }*/

    @Override
    public void doRead() {
        // Save the hero's current state
        Bundle bundle = new Bundle();
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD

        // Save hero state
        Dungeon.hero.storeInBundle(bundle);

        // Store hero's position separately
        bundle.put("pos", Dungeon.hero.pos);

        // Store the level's state
        Bundle levelBundle = new Bundle();
        Dungeon.level.storeInBundle(levelBundle);
        bundle.put("level_state", levelBundle);

        // Store buffs that shouldn't be duplicated
        float hungerLvl = 0;
        Hunger hunger = Dungeon.hero.buff(Hunger.class);
        if (hunger != null) {
            hungerLvl = hunger.hunger();
        }
        bundle.put("hunger", hungerLvl);

=======
        Dungeon.hero.storeInBundle(bundle);

>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
=======
        Dungeon.hero.storeInBundle(bundle);

>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
=======
        Dungeon.hero.storeInBundle(bundle);

>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
=======
        Dungeon.hero.storeInBundle(bundle);

>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
        // Apply the rewind buff
        RewindBuff rewind = Buff.affect(Dungeon.hero, RewindBuff.class);
        rewind.setRewindData(bundle);

        identify();
        Sample.INSTANCE.play(Assets.Sounds.READ);
        GLog.i(Messages.get(this, "forewarned"));

        readAnimation();
    }

    public static class RewindBuff extends Buff {

        private static final String BUNDLE = "bundle";
        private static final String TURNS_LEFT = "turns_left";

        private Bundle rewindData;
        private int turnsLeft = 10;

        public void setRewindData(Bundle bundle) {
            this.rewindData = bundle;
            turnsLeft = 10; // Reset turns when new data is set
        }

        @Override
        public boolean act() {
            turnsLeft--;

            if (turnsLeft <= 0) {
                // Time to rewind!
<<<<<<< HEAD
                if (rewindData != null && Dungeon.hero != null) {
                    try {
                        // Clear existing buffs to prevent duplication
                        for (Buff buff : Dungeon.hero.buffs().toArray(new Buff[0])) {
                            if (!(buff instanceof RewindBuff)) {
                                buff.detach();
                            }
                        }

                        // Store current level and depth in case we need to restore
                        Level oldLevel = Dungeon.level;
                        int oldDepth = Dungeon.depth;

                        try {
                            // Validate rewind data
                            if (rewindData == null) {
                                throw new Exception("No rewind data available");
                            }
                            
                            // Restore the level state
                            Bundle levelState = rewindData.getBundle("level_state");
                            if (levelState == null) {
                                throw new Exception("No level state found in rewind data");
                            }
                            
                            // Check if the saved depth matches current depth
                            if (rewindData.contains("depth") && rewindData.getInt("depth") != Dungeon.depth) {
                                throw new Exception("Cannot rewind: Save is from a different depth");
                            }
                            
                            // Check version compatibility
                            if (!levelState.contains("version")) {
                                GLog.w("Warning: No version found in save data, assuming old format");
                                throw new Exception("Cannot rewind: Save data is from an older version and cannot be loaded");
                            }
                            
                            int savedVersion = levelState.getInt("version");
                            int currentVersion = Game.versionCode;
                            
                            if (savedVersion != currentVersion) {
                                GLog.w("Warning: Save version mismatch (saved: " + savedVersion + ", current: " + currentVersion + ")");
                                
                                // If the version is too old, we can't safely restore
                                if (savedVersion < 200) { // Example version threshold
                                    throw new Exception("Cannot rewind: Save data is from an older version and cannot be loaded");
                                }
                                
                                // For minor version differences, try to proceed with a warning
                                GLog.w("Attempting to load save from different version, this might cause issues");
                                levelState.put("version", currentVersion);
                                
                                // Clear potentially problematic data
                                levelState.remove("mobs");
                                levelState.remove("blobs");
                                levelState.remove("mobs_to_spawn");
                                levelState.remove("traps");
                                levelState.remove("plants");
                            }

                            // Restore the level from its state
                            try {
                                // Log the level state for debugging
                                GLog.i("Attempting to restore level state...");
                                GLog.i("Level state keys: " + levelState.getKeys().toString());
                                
                                // Save current level state for comparison
                                Bundle currentState = new Bundle();
                                Dungeon.level.storeInBundle(currentState);
                                
                                // Attempt to restore
                                Dungeon.level.restoreFromBundle(levelState);
                                GLog.i("Level state restored successfully");
                                
                            } catch (Exception e) {
                                GLog.w("Level state restoration failed with error: " + e.toString());
                                GLog.w("Error details: " + e.getMessage());
                                if (e.getCause() != null) {
                                    GLog.w("Caused by: " + e.getCause().toString());
                                }
                                throw new Exception("Failed to restore level state: " + e.getMessage() + " (see log for details)");
                            }

                            // Restore hero state
                            try {
                                Dungeon.hero.restoreFromBundle(rewindData);
                            } catch (Exception e) {
                                throw new Exception("Failed to restore hero state: " + e.getMessage());
                            }
                            
                            // Set hero position with validation
                            int respawnPos;
                            if (rewindData.contains("pos")) {
                                respawnPos = rewindData.getInt("pos");
                                // Validate position is within bounds
                                if (respawnPos < 0 || respawnPos >= Dungeon.level.length()) {
                                    GLog.w("Warning: Saved position out of bounds, using random position");
                                    respawnPos = Dungeon.level.randomRespawnCell(Dungeon.hero);
                                }
                            } else {
                                respawnPos = Dungeon.level.randomRespawnCell(Dungeon.hero);
                            }
                            Dungeon.hero.pos = respawnPos;

                            // Make sure hero has a valid position
                            if (!Dungeon.level.passable[Dungeon.hero.pos]) {
                                // Find a safe spot for the hero
                                int newPos = Dungeon.level.randomRespawnCell(Dungeon.hero);
                                if (newPos != -1) {
                                    Dungeon.hero.pos = newPos;
                                } else {
                                    // Last resort, find any passable cell
                                    for (int i = 0; i < Dungeon.level.length(); i++) {
                                        if (Dungeon.level.passable[i]) {
                                            Dungeon.hero.pos = i;
                                            break;
                                        }
                                    }
                                }
                            }

                            // Restore hunger state if it existed
                            if (rewindData.contains("hunger")) {
                                float hungerLvl = rewindData.getFloat("hunger");
                                if (hungerLvl > 0) {
                                    Hunger hunger = Buff.affect(Dungeon.hero, Hunger.class);
                                    hunger.affectHunger(hungerLvl - hunger.hunger(), true);
                                }
                            }

                            // Update the game scene
                            Dungeon.observe();
                            GameScene.updateMap();

                            GLog.w(Messages.get(this, "rewind"));

                        } catch (Exception e) {
                            // If anything goes wrong, try to restore the previous state
                            Dungeon.level = oldLevel;
                            Dungeon.depth = oldDepth;
                            GLog.w("Something went wrong during rewind!");
                            throw e;
                        }
                    } catch (Exception e) {
                        // If we get here, something went very wrong
                        GLog.w("Failed to rewind time: " + e.getMessage());
                        // Try to at least keep the game running
                        Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                    }
=======
                if (rewindData != null) {
                    Dungeon.hero.restoreFromBundle(rewindData);
                    GLog.w(Messages.get(this, "rewind"));
>>>>>>> parent of c6fc9221b (Merge remote-tracking branch 'origin/master')
                }
                detach();
            } else {
                spend(TICK);
            }

            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            if (rewindData != null) {
                bundle.put(BUNDLE, rewindData);
            }
            bundle.put(TURNS_LEFT, turnsLeft);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (bundle.contains(BUNDLE)) {
                rewindData = bundle.getBundle(BUNDLE);
            }
            turnsLeft = bundle.getInt(TURNS_LEFT);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", turnsLeft);
        }
    }
}
