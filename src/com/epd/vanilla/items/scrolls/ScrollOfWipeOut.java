/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Easier Vanilla Pixel Dungeon
 * Copyright (C) 2016 Ken Wang
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
package com.epd.vanilla.items.scrolls;

import java.util.ArrayList;

import com.watabou.noosa.audio.Sample;
import com.epd.vanilla.Assets;
import com.epd.vanilla.Dungeon;
import com.epd.vanilla.actors.buffs.Blindness;
import com.epd.vanilla.actors.buffs.Invisibility;
import com.epd.vanilla.actors.hero.Hero;
import com.epd.vanilla.actors.mobs.Bestiary;
import com.epd.vanilla.actors.mobs.Mob;
import com.epd.vanilla.effects.CellEmitter;
import com.epd.vanilla.effects.Speck;
import com.epd.vanilla.items.Heap;
import com.epd.vanilla.items.Heap.Type;
import com.epd.vanilla.items.Item;
import com.epd.vanilla.scenes.GameScene;
import com.epd.vanilla.sprites.HeroSprite;
import com.epd.vanilla.sprites.ItemSpriteSheet;
import com.epd.vanilla.utils.GLog;
import com.watabou.utils.Random;

public class ScrollOfWipeOut extends Item {

	private static final String TXT_BLINDED	= "You can't read a scroll while blinded";
	
	public static final String AC_READ	= "READ";
	
	protected static final float TIME_TO_READ	= 1f;
	
	{
		name = "Scroll of Wipe Out";
		image = ItemSpriteSheet.SCROLL_WIPE_OUT;
		
		stackable = true;		
		defaultAction = AC_READ;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_READ );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( AC_READ )) {
			
			if (hero.buff( Blindness.class ) != null) {
				GLog.w( TXT_BLINDED );
			} else {
				curUser = hero;
				curItem = detach( hero.belongings.backpack );
				doRead();
			}
			
		} else {
		
			super.execute( hero, action );
			
		}
	}
	
	private void doRead() {
		GameScene.flash( 0xFF6644 );
		
		Invisibility.dispel();
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (!Bestiary.isBoss( mob )) {
				Sample.INSTANCE.play( Assets.SND_CURSED, 0.3f, 0.3f, Random.Float( 0.6f, 0.9f ) );
				mob.die( this );
			}
		}
		
		for (Heap heap : Dungeon.level.heaps.values()) {
			switch (heap.type) {
			case FOR_SALE:
				heap.type = Type.HEAP;
				if (Dungeon.visible[heap.pos]) {
					CellEmitter.center( heap.pos ).burst( Speck.factory( Speck.COIN ), 2 );
				}
				break;
			case MIMIC:
				heap.type = Type.HEAP;
				heap.sprite.link();
				Sample.INSTANCE.play( Assets.SND_CURSED, 0.3f, 0.3f, Random.Float( 0.6f, 0.9f ) );
				break;
			default:
			}
		}
		
		curUser.spend( TIME_TO_READ );
		curUser.busy();
		((HeroSprite)curUser.sprite).read();
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public String desc() {
		return
			"Read this scroll to unleash the wrath of the dungeon spirits, killing everything on the current level. " +
			"Well, almost everything. Some of the more powerful creatures may be not affected.";
	}
	
	@Override
	public int price() {
		return 100 * quantity;
	}
}
