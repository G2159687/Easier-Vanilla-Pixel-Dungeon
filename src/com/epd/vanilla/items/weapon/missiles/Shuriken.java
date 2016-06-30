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
package com.epd.vanilla.items.weapon.missiles;

import com.epd.vanilla.items.Item;
import com.epd.vanilla.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Shuriken extends MissileWeapon {

	{
		name = "shuriken";
		image = ItemSpriteSheet.SHURIKEN;
		
		STR = 13;
		
		DLY = 0.5f;
	}
	
	public Shuriken() {
		this( 1 );
	}
	
	public Shuriken( int number ) {
		super();
		quantity = number;
	}
	
	@Override
	public int min() {
		return 2;
	}
	
	@Override
	public int max() {
		return 6;
	}
	
	@Override
	public String desc() {
		return 
			"Star-shaped pieces of metal with razor-sharp blades do significant damage " +
			"when they hit a target. They can be thrown at very high rate.";
	}
	
	@Override
	public Item random() {
		quantity = Random.Int( 5, 15 );
		return this;
	}
	
	@Override
	public int price() {
		return 15 * quantity;
	}
}
