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

import com.watabou.noosa.audio.Sample;
import com.epd.vanilla.Assets;
import com.epd.vanilla.Dungeon;
import com.epd.vanilla.actors.buffs.Invisibility;
import com.epd.vanilla.actors.hero.Hero;
import com.epd.vanilla.items.wands.WandOfBlink;
import com.epd.vanilla.utils.GLog;

public class ScrollOfTeleportation extends Scroll {

	public static final String TXT_TELEPORTED = 
		"In a blink of an eye you were teleported to another location of the level.";
	
	public static final String TXT_NO_TELEPORT = 
		"Strong magic aura of this place prevents you from teleporting!";
	
	{
		name = "Scroll of Teleportation";
	}
	
	@Override
	protected void doRead() {

		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();
		
		teleportHero( curUser );
		setKnown();
		
		readAnimation();
	}
	
	public static void teleportHero( Hero  hero ) {

		int count = 10;
		int pos;
		do {
			pos = Dungeon.level.randomRespawnCell();
			if (count-- <= 0) {
				break;
			}
		} while (pos == -1);
		
		if (pos == -1) {
			
			GLog.w( TXT_NO_TELEPORT );
			
		} else {

			WandOfBlink.appear( hero, pos );
			Dungeon.level.press( pos, hero );
			Dungeon.observe();
			
			GLog.i( TXT_TELEPORTED );
			
		}
	}
	
	@Override
	public String desc() {
		return
			"The spell on this parchment instantly transports the reader " +
			"to a random location on the dungeon level. It can be used " +
			"to escape a dangerous situation, but the unlucky reader might " +
			"find himself in an even more dangerous place.";
	}
	
	@Override
	public int price() {
		return isKnown() ? 40 * quantity : super.price();
	}
}
