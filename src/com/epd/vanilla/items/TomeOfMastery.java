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
package com.epd.vanilla.items;

import java.util.ArrayList;

import com.watabou.noosa.audio.Sample;
import com.epd.vanilla.Assets;
import com.epd.vanilla.Badges;
import com.epd.vanilla.Dungeon;
import com.epd.vanilla.actors.buffs.Blindness;
import com.epd.vanilla.actors.buffs.Buff;
import com.epd.vanilla.actors.buffs.Fury;
import com.epd.vanilla.actors.hero.Hero;
import com.epd.vanilla.actors.hero.HeroSubClass;
import com.epd.vanilla.effects.Speck;
import com.epd.vanilla.effects.SpellSprite;
import com.epd.vanilla.scenes.GameScene;
import com.epd.vanilla.sprites.ItemSpriteSheet;
import com.epd.vanilla.utils.GLog;
import com.epd.vanilla.utils.Utils;
import com.epd.vanilla.windows.WndChooseWay;

public class TomeOfMastery extends Item {

	private static final String TXT_BLINDED	= "You can't read while blinded";
	
	public static final float TIME_TO_READ = 10;
	
	public static final String AC_READ	= "READ";
	
	{
		stackable = false;
		name = Dungeon.hero != null && Dungeon.hero.subClass != HeroSubClass.NONE ? "Tome of Remastery" : "Tome of Mastery";
		image = ItemSpriteSheet.MASTERY;
		
		unique = true;
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
				return;
			}
			
			curUser = hero;
			
			switch (hero.heroClass) {
			case WARRIOR:
				read( hero, HeroSubClass.GLADIATOR, HeroSubClass.BERSERKER );
				break;
			case MAGE:
				read( hero, HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK );
				break;
			case ROGUE:
				read( hero, HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER );
				break;
			case HUNTRESS:
				read( hero, HeroSubClass.SNIPER, HeroSubClass.WARDEN );
				break;
			}
			
		} else {			
			super.execute( hero, action );		
		}
	}
	
	@Override
	public boolean doPickUp( Hero hero ) {
		Badges.validateMastery();
		return super.doPickUp( hero );
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
	public String info() {
		return 
			"This worn leather book is not that thick, but you feel somehow, " +
			"that you can gather a lot from it. Remember though that reading " +
			"this tome may require some time.";
	}
	
	private void read( Hero hero, HeroSubClass sc1, HeroSubClass sc2 ) {
		if (hero.subClass == sc1) {
			GameScene.show( new WndChooseWay( this, sc2 ) );
		} else if (hero.subClass == sc2) {
			GameScene.show( new WndChooseWay( this, sc1 ) );
		} else {
			GameScene.show( new WndChooseWay( this, sc1, sc2 ) );
		}
	}
	
	public void choose( HeroSubClass way ) {
		
		detach( curUser.belongings.backpack );
		
		curUser.spend( TomeOfMastery.TIME_TO_READ );
		curUser.busy();
		
		curUser.subClass = way;
		
		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.SND_MASTERY );
		
		SpellSprite.show( curUser, SpellSprite.MASTERY );
		curUser.sprite.emitter().burst( Speck.factory( Speck.MASTERY ), 12 );
		GLog.w( "You have chosen the way of the %s!", Utils.capitalize( way.title() ) );
		
		if (way == HeroSubClass.BERSERKER && curUser.HP <= curUser.HT * Fury.LEVEL) {
			Buff.affect( curUser, Fury.class );
		}
	}
}
