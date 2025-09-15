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

package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PotionOfMindVision extends Potion {

	{
		icon = ItemSpriteSheet.Icons.POTION_MINDVIS;
	}

	@Override
	public void apply( Hero hero ) {
		identify();
		Buff.prolong( hero, MindVision.class, MindVision.DURATION );
		SpellSprite.show(hero, SpellSprite.VISION, 1, 0.77f, 0.9f);
		Dungeon.observe();

		// Alle Mobs ausblenden und deren Tiles unsichtbar machen
		for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mob : Dungeon.level.mobs) {
			if (mob.sprite != null) mob.sprite.visible = false;
			Dungeon.level.mapped[mob.pos] = false;
		}

		// Schockwellen-Animation in rot, Gegner und deren Tiles werden synchron sichtbar
		int length = Dungeon.level.length();
		int centerCell = hero.pos;
		for (int i = 0; i < length; i++) {
			final int cell = i;
			com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene.effectOverFog(
				new com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell(cell, centerCell, () -> {
					for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mob : Dungeon.level.mobs) {
						if (mob.pos == cell && mob.sprite != null) {
							// Tile und Kreatur synchron sichtbar machen
							Dungeon.level.mapped[cell] = true;
							Dungeon.level.discover(cell);
							if (Dungeon.level.heroFOV[cell]) {
								com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene.discoverTile(cell, Dungeon.level.map[cell]);
							}
							mob.sprite.visible = true;
						}
					}
				}, 0xFFFF0000)
			);
		}

		if (Dungeon.level.mobs.size() > 0) {
			GLog.i( Messages.get(this, "see_mobs") );
		} else {
			GLog.i( Messages.get(this, "see_none") );
		}
	}
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
