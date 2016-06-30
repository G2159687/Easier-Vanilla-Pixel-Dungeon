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
package com.epd.vanilla.actors.buffs;

import com.epd.vanilla.Badges;
import com.epd.vanilla.actors.Char;
import com.epd.vanilla.ui.BuffIndicator;
import com.epd.vanilla.utils.GLog;

public class Combo extends Buff {
	
	private static String TXT_COMBO = "%d hit combo!";
	
	public int count = 0;
	
	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}
	
	@Override
	public String toString() {
		return "Combo";
	}
	
	public int hit( Char enemy, int damage ) {
		
		count++;
		
		if (count >= 3) {
			
			Badges.validateMasteryCombo( count );
			
			GLog.p( TXT_COMBO, count );
			postpone( 1.41f - count / 10f );
			return (int)(damage * (count - 2) / 5f);
			
		} else {
			
			postpone( 1.1f );
			return 0;
			
		}
	}
	
	@Override
	public boolean act() {
		detach();
		return true;
	}
	
}
