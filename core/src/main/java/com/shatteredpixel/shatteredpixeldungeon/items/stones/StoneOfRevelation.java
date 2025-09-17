package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;

public class StoneOfRevelation extends InventoryStone {

    private static final int REVEAL_RADIUS = 2; // 5x5 Bereich (Radius 2)

    {
        image = ItemSpriteSheet.STONE_REVELATION;
    }

    @Override
    protected void activate(final int cell) {
        // Effekt ausführen
        int exit = Dungeon.level.exit();
        int width = Dungeon.level.width();
        int height = Dungeon.level.height();
        int exity = exit / width;
        int exitx = exit % width;
        for (int dy = -REVEAL_RADIUS; dy <= REVEAL_RADIUS; dy++) {
            for (int dx = -REVEAL_RADIUS; dx <= REVEAL_RADIUS; dx++) {
                int x = exitx + dx;
                int y = exity + dy;
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    int pos = y * width + x;
                    Dungeon.level.visited[pos] = true;
                    Dungeon.level.mapped[pos] = true;
                    if (Dungeon.level.discoverable != null) {
                        Dungeon.level.discoverable[pos] = true;
                    }
                    GameScene.effectOverFog(new CheckedCell(pos, exit, null, 0x99FFFF00));
                }
            }
        }
        // Bereich für 30 Runden sichtbar machen
        RevealedArea area = Buff.affect(Dungeon.hero, RevealedArea.class, 0f);
        if (area != null) {
            area.pos = exit;
            area.depth = Dungeon.depth;
            area.branch = Dungeon.branch;
        }
        Dungeon.observe();
        useAnimation();
        // Jetzt quantity reduzieren und ggf. entfernen
        this.quantity(this.quantity() - 1);
        if (this.quantity() == 0) {
            detach(curUser.belongings.backpack);
        }
    }

    @Override
    public void onItemSelected(com.shatteredpixel.shatteredpixeldungeon.items.Item item) {
        // Effekt ausführen
        int exit = Dungeon.level.exit();
        int width = Dungeon.level.width();
        int height = Dungeon.level.height();
        int exity = exit / width;
        int exitx = exit % width;
        for (int dy = -REVEAL_RADIUS; dy <= REVEAL_RADIUS; dy++) {
            for (int dx = -REVEAL_RADIUS; dx <= REVEAL_RADIUS; dx++) {
                int x = exitx + dx;
                int y = exity + dy;
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    int pos = y * width + x;
                    Dungeon.level.visited[pos] = true;
                    Dungeon.level.mapped[pos] = true;
                    if (Dungeon.level.discoverable != null) {
                        Dungeon.level.discoverable[pos] = true;
                    }
                    GameScene.effectOverFog(new CheckedCell(pos, exit, null, 0x99FFFF00));
                }
            }
        }
        RevealedArea area = Buff.affect(Dungeon.hero, RevealedArea.class, 0f);
        if (area != null) {
            area.pos = exit;
            area.depth = Dungeon.depth;
            area.branch = Dungeon.branch;
        }

        Dungeon.observe();
        useAnimation();
        // Jetzt quantity reduzieren und ggf. entfernen
        this.quantity(this.quantity() - 1);
        if (this.quantity() == 0) {
            detach(curUser.belongings.backpack);
        }
    }
}
