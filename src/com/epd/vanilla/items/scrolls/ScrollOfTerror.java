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
import com.epd.vanilla.actors.buffs.Buff;
import com.epd.vanilla.actors.buffs.Invisibility;
import com.epd.vanilla.actors.buffs.Terror;
import com.epd.vanilla.actors.mobs.Mob;
import com.epd.vanilla.effects.Flare;
import com.epd.vanilla.levels.Level;
import com.epd.vanilla.utils.GLog;

public class ScrollOfTerror extends Scroll {

	{
		name = "Scroll of Terror";
	}
	
	@Override
	protected void doRead() {
		
		new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();
		
		int count = 0;
		Mob affected = null;
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Level.fieldOfView[mob.pos]) {
				Buff.affect( mob, Terror.class, Terror.DURATION ).object = curUser.id();
				
				count++;
				affected = mob;
			}
		}
		
		switch (count) {
		case 0:
			GLog.i( "The scroll emits a brilliant flash of red light" );
			break;
		case 1:
			GLog.i( "The scroll emits a brilliant flash of red light and the " + affected.name + " flees!" );
			break;
		default:
			GLog.i( "The scroll emits a brilliant flash of red light and the monsters flee!" );
		}
		setKnown();
		
		readAnimation();
	}
	
	@Override
	public String desc() {
		return
			"A flash of red light will overwhelm all creatures in your field of view with terror, " +
			"and they will turn and flee. Attacking a fleeing enemy will dispel the effect.";
	}
	
	@Override
	public int price() {
		return isKnown() ? 50 * quantity : super.price();
	}
}
