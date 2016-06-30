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
package com.epd.vanilla.items.weapon.melee;

import com.epd.vanilla.Dungeon;
import com.epd.vanilla.items.Item;
import com.epd.vanilla.items.weapon.Weapon;
import com.epd.vanilla.utils.Utils;
import com.watabou.utils.Random;

public class MeleeWeapon extends Weapon {
	
	private int tier;
	
	public MeleeWeapon( int tier, float acu, float dly ) {
		super();
		
		this.tier = tier;
		
		ACU = acu;
		DLY = dly;
		
		STR = typicalSTR();
	}
	
	protected int min0() {
		return tier;
	}
	
	protected int max0() {
		return (int)((tier * tier - tier + 10) / ACU * DLY);
	}
	
	@Override
	public int min() {
		return isBroken() ? min0() : min0() + level(); 
	}
	
	@Override
	public int max() {
		return isBroken() ? max0() : max0() + level() * tier;
	}
	
	@Override
	final public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean enchant ) {
		STR--;		
		return super.upgrade( enchant );
	}
	
	public Item safeUpgrade() {
		return upgrade( enchantment != null );
	}
	
	@Override
	public Item degrade() {		
		STR++;
		return super.degrade();
	}
	
	public int typicalSTR() {
		return 8 + tier * 2;
	}
	
	@Override
	public String info() {
		
		final String p = "\n\n";
		
		StringBuilder info = new StringBuilder( desc() );
		
		int lvl = visiblyUpgraded();
		String quality = lvl != 0 ? 
			(lvl > 0 ? 
				(isBroken() ? "broken" : "upgraded") : 
				"degraded") : 
			"";
		info.append( p );
		info.append( "This " + name + " is " + Utils.indefinite( quality ) );
		info.append( " tier-" + tier + " melee weapon. " );
		
		if (levelKnown) {
			int min = min();
			int max = max();
			info.append( "Its average damage is " + (min + (max - min) / 2) + " points per hit. " );
		} else {
			int min = min0();
			int max = max0();
			info.append( 
				"Its typical average damage is " + (min + (max - min) / 2) + " points per hit " +
				"and usually it requires " + typicalSTR() + " points of strength. " );
			if (typicalSTR() > Dungeon.hero.STR()) {
				info.append( "Probably this weapon is too heavy for you. " );
			}
		}
		
		if (DLY != 1f) {
			info.append( "This is a rather " + (DLY < 1f ? "fast" : "slow") );
			if (ACU != 1f) {
				if ((ACU > 1f) == (DLY < 1f)) {
					info.append( " and ");
				} else {
					info.append( " but ");
				}
				info.append( ACU > 1f ? "accurate" : "inaccurate" );
			}
			info.append( " weapon. ");
		} else if (ACU != 1f) {
			info.append( "This is a rather " + (ACU > 1f ? "accurate" : "inaccurate") + " weapon. " );
		}
		switch (imbue) {
		case SPEED:
			info.append( "It was balanced to make it faster. " );
			break;
		case ACCURACY:
			info.append( "It was balanced to make it more accurate. " );
			break;
		case NONE:
		}
		
		if (enchantment != null) {
			info.append( "It is enchanted." );
		}
		
		if (levelKnown && Dungeon.hero.belongings.backpack.items.contains( this )) {
			if (STR > Dungeon.hero.STR()) {
				info.append( p );
				info.append( 
					"Because of your inadequate strength the accuracy and speed " +
					"of your attack with this " + name + " is decreased." );
			}
			if (STR < Dungeon.hero.STR()) {
				info.append( p );
				info.append( 
					"Because of your excess strength the damage " +
					"of your attack with this " + name + " is increased." );
			}
		}
		
		if (isEquipped( Dungeon.hero )) {
			info.append( p );
			info.append( "You hold the " + name + " at the ready" + 
				(cursed ? ", and because it is cursed, you are powerless to let go." : ".") ); 
		} else {
			if (cursedKnown && cursed) {
				info.append( p );
				info.append( "You can feel a malevolent magic lurking within " + name +"." );
			}
		}
		
		return info.toString();
	}
	
	@Override
	public int price() {
		int price = 20 * (1 << (tier - 1));
		if (enchantment != null) {
			price *= 1.5;
		}
		return considerState( price );
	}
	
	@Override
	public Item random() {
		super.random();
		
		if (Random.Int( 10 + level() ) == 0) {
			enchant();
		}
		
		return this;
	}
}
