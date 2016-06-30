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
package com.epd.vanilla.items.rings;

import com.epd.vanilla.Badges;
import com.epd.vanilla.actors.hero.Hero;
import com.epd.vanilla.items.Item;

public class RingOfHaggler extends Ring {

	{
		name = "Ring of Haggler";
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Haggling();
	}
	
	@Override
	public Item random() {
		level( +1 );
		return this;
	}
	
	@Override
	public boolean doPickUp( Hero hero ) {
		identify();
		Badges.validateRingOfHaggler();
		Badges.validateItemLevelAquired( this );
		return super.doPickUp(hero);
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public void use() {
		// Do nothing (it can't degrade)
	}
	
	@Override
	public String desc() {
		return isKnown() ?
			"In fact this ring doesn't provide any magic effect, but it demonstrates " +
			"to shopkeepers and vendors, that the owner of the ring is a member of " +
			"The Thieves' Guild. Usually they are glad to give a discount in exchange " +
			"for temporary immunity guarantee. Upgrading this ring won't give any additional " +
			"bonuses." :
			super.desc();
	}
	
	public class Haggling extends RingBuff {	
	}
}
