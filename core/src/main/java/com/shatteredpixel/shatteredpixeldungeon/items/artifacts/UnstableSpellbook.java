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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.ArrayList;

public class UnstableSpellbook extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_SPELLBOOK;

		levelCap = 10;

		charge = (int)(level()*0.6f)+2;
		partialCharge = 0;
		chargeCap = (int)(level()*0.6f)+2;

		defaultAction = AC_READ;
	}
	private Scroll lastReadScroll = null;
	public static final String AC_READ = "READ";
	public static final String AC_ADD = "ADD";

	private final ArrayList<Class> scrolls = new ArrayList<>();

	public UnstableSpellbook() {
		super();

		setupScrolls();
	}

	private void setupScrolls(){
		scrolls.clear();
		Class<?>[] scrollClasses = Generator.Category.SCROLL.classes;
		Class<?> chosen = null;
		while (chosen == null) {
			Class<?> candidate = scrollClasses[Random.Int(scrollClasses.length)];
			if (candidate != ScrollOfTransmutation.class && candidate != ScrollOfRemoveCurse.class && candidate != ScrollOfUpgrade.class) {
				chosen = candidate;
			}
		}
		scrolls.add(chosen);
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && charge > 0 && !cursed && hero.buff(MagicImmune.class) == null) {
			actions.add(AC_READ);
		}
		if (isEquipped( hero ) && level() < levelCap && !cursed && hero.buff(MagicImmune.class) == null) {
			actions.add(AC_ADD);
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (hero.buff(MagicImmune.class) != null) return;

		if (action.equals( AC_READ )) {

			if (hero.buff( Blindness.class ) != null) GLog.w( Messages.get(this, "blinded") );
			else if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (charge <= 0)                     GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                          GLog.i( Messages.get(this, "cursed") );
			else if (scrolls.size() == 1) {
				// Nur eine Schriftrolle im Buch: direkt ausführen
				try {
					Scroll scroll = (Scroll) Reflection.newInstance(scrolls.get(0));
					curItem = scroll;
					curUser = hero;
					checkForArtifactProc(curUser, scroll);
					scroll.setKnown();
					scroll.doRead();
					Talent.onArtifactUsed(hero);
					updateQuickslot();
				} catch (Exception e) {
					GLog.w("Fehler beim Ausführen der Schriftrolle.");
				}
			} else {
				GameScene.show(new WndSpellbookSelect(hero));
			}

		} else if (action.equals( AC_ADD )) {
			GameScene.selectItem(itemSelector);
		}
	}

	// Menü mit Icons wie beim Stein der Intuition, aber mit RedButton und ItemSprite
	private class WndSpellbookSelect extends Window {
		private static final int WIDTH = 120;
		private static final int BTN_SIZE = 20;

		public WndSpellbookSelect(final Hero hero) {
			IconTitle titlebar = new IconTitle();
			titlebar.icon(new ItemSprite(image, null));
			titlebar.label(Messages.get(UnstableSpellbook.this, "choose_scroll"));
			titlebar.setRect(0, 0, WIDTH, 0);
			add(titlebar);

			float top = titlebar.bottom() + 5;
			int n = scrolls.size();
			int cols = Math.min(n, 5);
			int rows = (n + 4) / 5;
			float left = (WIDTH - BTN_SIZE * cols) / 2f;

			int i = 0;
			for (final Class scrollClass : scrolls) {
				try {
					final Scroll scroll = (Scroll) Reflection.newInstance(scrollClass);
					RedButton btn = new RedButton("") {
						@Override
						protected void onClick() {
							hide();
							if(scroll instanceof ScrollOfUpgrade)
							{
								if(charge<2)
								{
									GLog.w(Messages.get(UnstableSpellbook.this, "less_charge"));
									return;
								}
								else{charge--;}
							}
							if (charge > 0) {
								charge--;
								curItem = scroll;
								curUser = hero;
								checkForArtifactProc(curUser, scroll);
								scroll.setKnown();
								lastReadScroll=null;
								if(!(scroll instanceof ScrollOfUpgrade))
								{
									lastReadScroll=scroll;
								}

								scroll.doRead();
								Talent.onArtifactUsed(hero);
								updateQuickslot();
							}
						}
					};

					if(scroll.isKnown()) {
						Image im = new Image(Assets.Sprites.ITEM_ICONS);
						im.scale = new PointF(2f, 2f);
						im.frame(ItemSpriteSheet.Icons.film.get(scroll.icon));
						btn.icon(im);
					} else {
						btn.icon(new ItemSprite(scroll));
					}
					btn.setRect(left + (i % 5) * BTN_SIZE, top + (i / 5) * BTN_SIZE, BTN_SIZE, BTN_SIZE);
					add(btn);
					i++;
				} catch (Exception e) {
					// Fehler beim Erstellen der Schriftrolle ignorieren
				}
			}

			height = (int) (top + rows * BTN_SIZE + 5);
			resize(WIDTH, height);
		}
	}

	public void doReadEffect(Hero hero){
		charge--;
		if(lastReadScroll != null) {
			checkForArtifactProc(hero, lastReadScroll);
			lastReadScroll.doRead();
			Talent.onArtifactUsed(hero);
		} else {
			GLog.w(Messages.get(this, "no_scroll"));
		}
		/*Scroll scroll;
		do {
			scroll = (Scroll) Generator.randomUsingDefaults(Generator.Category.SCROLL);
		} while (scroll == null
				//reduce the frequency of these scrolls by half
				||((scroll instanceof ScrollOfIdentify ||
				scroll instanceof ScrollOfRemoveCurse ||
				scroll instanceof ScrollOfMagicMapping) && Random.Int(2) == 0)
				//cannot roll transmutation
				|| (scroll instanceof ScrollOfTransmutation));

		scroll.anonymize();
		curItem = scroll;
		curUser = hero;

		//if there are charges left and the scroll has been given to the book
		if (charge > 0 && !scrolls.contains(scroll.getClass())) {
			final Scroll fScroll = scroll;

			final ExploitHandler handler = Buff.affect(hero, ExploitHandler.class);
			handler.scroll = scroll;

			GameScene.show(new WndOptions(new ItemSprite(this),
					Messages.get(this, "prompt"),
					Messages.get(this, "read_empowered"),
					scroll.trueName(),
					Messages.get(ExoticScroll.regToExo.get(scroll.getClass()), "name")){
				@Override
				protected void onSelect(int index) {
					handler.detach();
					if (index == 1){
						Scroll scroll = Reflection.newInstance(ExoticScroll.regToExo.get(fScroll.getClass()));
						curItem = scroll;
						charge--;
						scroll.anonymize();
						checkForArtifactProc(curUser, scroll);
						scroll.doRead();
						Talent.onArtifactUsed(Dungeon.hero);
					} else {
						checkForArtifactProc(curUser, fScroll);
						fScroll.doRead();
						Talent.onArtifactUsed(Dungeon.hero);
					}
					updateQuickslot();
				}

				@Override
				public void onBackPressed() {
					//do nothing
				}
			});
		} else {
			checkForArtifactProc(curUser, scroll);
			scroll.doRead();
			Talent.onArtifactUsed(Dungeon.hero);
		}

		updateQuickslot();*/
	}

	private void checkForArtifactProc(Hero user, Scroll scroll){
		//if the base scroll (exotics all match) is an AOE effect, then also trigger illuminate
		if (scroll instanceof ScrollOfLullaby
				|| scroll instanceof ScrollOfRemoveCurse || scroll instanceof ScrollOfTerror) {
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (Dungeon.level.heroFOV[mob.pos]) {
					artifactProc(mob, visiblyUpgraded(), 1);
				}
			}
		//except rage, which affects everything even if it isn't visible
		} else if (scroll instanceof ScrollOfRage){
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				artifactProc(mob, visiblyUpgraded(), 1);
			}
		}
	}

	//forces the reading of a regular scroll if the player tried to exploit by quitting the game when the menu was up
	public static class ExploitHandler extends Buff {
		{ actPriority = VFX_PRIO; }

		public Scroll scroll;

		@Override
		public boolean act() {
			curUser = Dungeon.hero;
			curItem = scroll;
			scroll.anonymize();
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					scroll.doRead();
					Item.updateQuickslot();
				}
			});
			detach();
			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( "scroll", scroll );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			scroll = (Scroll)bundle.get("scroll");
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new bookRecharge();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null){
			partialCharge += 0.1f*amount;
			while (partialCharge >= 1){
				partialCharge--;
				charge++;
			}
			if (charge >= chargeCap){
				partialCharge = 0;
			}
			updateQuickslot();
		}
	}

	@Override
	public Item upgrade() {
		chargeCap = (int)((level()+1)*0.6f)+2;

		//for artifact transmutation.
		while (!scrolls.isEmpty() && scrolls.size() > (levelCap-1-level())) {
			scrolls.remove(0);
		}

		return super.upgrade();
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.resetForTrinity(visibleLevel);
		setupScrolls();
		while (!scrolls.isEmpty() && scrolls.size() > (levelCap-1-level())) {
			scrolls.remove(0);
		}
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if (isEquipped(Dungeon.hero)) {
			if (cursed) {
				desc += "\n\n" + Messages.get(this, "desc_cursed");
			}
			
			if (level() < levelCap && scrolls.size() > 0) {
				desc += "\n\n" + Messages.get(this, "desc_index");
				for(int n=0; n<scrolls.size(); n++) {

                    Scroll scroll = null;
                    try {
                        scroll = (Scroll)scrolls.get(n).newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    desc += "\n"+ "_" + scroll.name()+ "_";

				}
				//desc += "\n" + "_" + Messages.get(scrolls.get(0), "name") + "_";
				//if (scrolls.size() > 1)
				//	desc += "\n" + "_" + Messages.get(scrolls.get(1), "name") + "_";
			}
		}
		
		if (level() > 0) {
			desc += "\n\n" + Messages.get(this, "desc_empowered");
		}

		return desc;
	}

	private static final String SCROLLS =   "scrolls";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( SCROLLS, scrolls.toArray(new Class[scrolls.size()]) );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		scrolls.clear();
		if (bundle.contains(SCROLLS) && bundle.getClassArray(SCROLLS) != null) {
			for (Class<?> scroll : bundle.getClassArray(SCROLLS)) {
				if (scroll != null) scrolls.add(scroll);
			}
		}
	}

	public class bookRecharge extends ArtifactBuff{
		@Override
		public boolean act() {
			if (charge < chargeCap
					&& !cursed
					&& target.buff(MagicImmune.class) == null
					&& Regeneration.regenOn()) {
				//120 turns to charge at full, 80 turns to charge at 0/8
				float chargeGain = 1 / (120f - (chargeCap - charge)*5f);
				chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
				partialCharge += chargeGain;

				while (partialCharge >= 1) {
					partialCharge --;
					charge ++;

					if (charge == chargeCap){
						partialCharge = 0;
					}
				}
			}

			updateQuickslot();

			spend( TICK );

			return true;
		}
	}

	// ItemSelector für das Hinzufügen von Schriftrollen (auch unidentifizierte)
	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(UnstableSpellbook.class, "prompt");
		}
		@Override
		public Class<?extends Bag> preferredBag(){
			return ScrollHolder.class;
		}
		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Scroll && !scrolls.contains(item.getClass())&&!(item instanceof ExoticScroll);
		}
		@Override
		public void onSelect(Item item) {
			if (item != null && item instanceof Scroll&&!(item instanceof ExoticScroll)) {
				Hero hero = Dungeon.hero;
				scrolls.add(item.getClass());
				item.detach(hero.belongings.backpack);
				GLog.i(Messages.get(UnstableSpellbook.class, "infuse_scroll"));
				updateQuickslot();
			} else {
				//GLog.w(Messages.get(UnstableSpellbook.class, "unable_scroll"));
			}
		}
	};
}
