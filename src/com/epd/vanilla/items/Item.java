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
import java.util.Collections;
import java.util.Comparator;

import com.epd.vanilla.Challenges;
import com.watabou.noosa.audio.Sample;
import com.epd.vanilla.Assets;
import com.epd.vanilla.Badges;
import com.epd.vanilla.Dungeon;
import com.epd.vanilla.actors.Actor;
import com.epd.vanilla.actors.Char;
import com.epd.vanilla.actors.buffs.SnipersMark;
import com.epd.vanilla.actors.hero.Hero;
import com.epd.vanilla.effects.Degradation;
import com.epd.vanilla.effects.Speck;
import com.epd.vanilla.items.armor.Armor;
import com.epd.vanilla.items.bags.Bag;
import com.epd.vanilla.items.rings.Ring;
import com.epd.vanilla.items.wands.Wand;
import com.epd.vanilla.items.weapon.Weapon;
import com.epd.vanilla.items.weapon.missiles.MissileWeapon;
import com.epd.vanilla.mechanics.Ballistica;
import com.epd.vanilla.scenes.CellSelector;
import com.epd.vanilla.scenes.GameScene;
import com.epd.vanilla.sprites.CharSprite;
import com.epd.vanilla.sprites.ItemSprite;
import com.epd.vanilla.sprites.MissileSprite;
import com.epd.vanilla.ui.QuickSlot;
import com.epd.vanilla.utils.GLog;
import com.epd.vanilla.utils.Utils;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class Item implements Bundlable {

	private static final String TXT_PACK_FULL	= "Your pack is too full for the %s";
	
	private static final String TXT_BROKEN		= "Because of frequent use, your %s has broken.";
	private static final String TXT_GONNA_BREAK	= "Because of frequent use, your %s is going to break soon.";
	
	private static final String TXT_TO_STRING		= "%s";
	private static final String TXT_TO_STRING_X		= "%s x%d";
	private static final String TXT_TO_STRING_LVL	= "%s%+d";
	private static final String TXT_TO_STRING_LVL_X	= "%s%+d x%d";
	
	private static final float DURABILITY_WARNING_LEVEL	= 1/6f;
	
	protected static final float TIME_TO_THROW		= 1.0f;
	protected static final float TIME_TO_PICK_UP	= 1.0f;
	protected static final float TIME_TO_DROP		= 0.5f;
	
	public static final String AC_DROP		= "DROP";
	public static final String AC_THROW		= "THROW";
	
	public String defaultAction;
	
	protected String name = "smth";
	protected int image = 0;
	

	public boolean stackable = false;
	protected int quantity = 1;
	
	private int level = 0;
	private int durability = maxDurability();
	public boolean levelKnown = false;
	
	public boolean cursed;
	public boolean cursedKnown;
	
	public boolean unique = false;
	
	private static Comparator<Item> itemComparator = new Comparator<Item>() {	
		@Override
		public int compare( Item lhs, Item rhs ) {
			return Generator.Category.order( lhs ) - Generator.Category.order( rhs );
		}
	};
	
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add( AC_DROP );
		actions.add( AC_THROW );
		return actions;
	}
	
	public boolean doPickUp( Hero hero ) {
		if (collect( hero.belongings.backpack )) {
			
			GameScene.pickUp( this );
			Sample.INSTANCE.play( Assets.SND_ITEM );
			hero.spendAndNext( TIME_TO_PICK_UP );
			return true;
			
		} else {
			return false;
		}
	}
	
	public void doDrop( Hero hero ) {	
		hero.spendAndNext( TIME_TO_DROP );			
		Dungeon.level.drop( detachAll( hero.belongings.backpack ), hero.pos ).sprite.drop( hero.pos );	
	}
	
	public void doThrow( Hero hero ) {
		GameScene.selectCell( thrower );
	}
	
	public void execute( Hero hero, String action ) {
		
		curUser = hero;
		curItem = this;
		
		if (action.equals( AC_DROP )) {
			
			doDrop( hero );
			
		} else if (action.equals( AC_THROW )) {
			
			doThrow( hero );
			
		}
	}
	
	public void execute( Hero hero ) {
		execute( hero, defaultAction );
	}
	
	protected void onThrow( int cell ) {
		Heap heap = Dungeon.level.drop( this, cell );
		if (!heap.isEmpty()) {
			heap.sprite.drop( cell );
		}
	}
	
	public boolean collect( Bag container ) {
		
		ArrayList<Item> items = container.items;
		
		if (items.contains( this )) {
			return true;
		}
		
		for (Item item:items) {
			if (item instanceof Bag && ((Bag)item).grab( this )) {
				return collect( (Bag)item );
			}
		}
		
		if (stackable) {
			
			Class<?>c = getClass();
			for (Item item:items) {
				if (item.getClass() == c) {
					item.quantity += quantity;
					item.updateQuickslot();
					return true;
				}
			}	
		}
		
		if (items.size() < container.size) {
			
			if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
				Badges.validateItemLevelAquired( this );
			}
			
			items.add( this );	
			QuickSlot.refresh();
			Collections.sort( items, itemComparator );
			return true;
			
		} else {
			
			GLog.n( TXT_PACK_FULL, name() );
			return false;
			
		}
	}
	
	public boolean collect() {
		return collect( Dungeon.hero.belongings.backpack );
	}
	
	public final Item detach( Bag container ) {
		
		if (quantity <= 0) {
			
			return null;
			
		} else
		if (quantity == 1) {

			return detachAll( container );
			
		} else {
			
			quantity--;
			updateQuickslot();
			
			try { 
				Item detached = getClass().newInstance();
				detached.onDetach( );
				return detached;
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	public final Item detachAll( Bag container ) {
		
		for (Item item : container.items) {
			if (item == this) {
				container.items.remove( this );
				item.onDetach( );
				QuickSlot.refresh();
				return this;
			} else if (item instanceof Bag) {
				Bag bag = (Bag)item;
				if (bag.contains( this )) {
					return detachAll( bag );
				}
			}
		}
		
		return this;
	}
	
	protected void onDetach( ) {
	}
	
	public int level() {
		return level;
	}
	
	public void level( int value ) {
		level = value;
	}
	
	public int effectiveLevel() {
		return isBroken() ? 0 : level;
	}
	
	public Item upgrade() {
		
		cursed = false;
		cursedKnown = true;
		
		level++;if (Dungeon.isChallenged(Challenges.C1)){ level++;}
		fix();
		
		return this;
	}
	
	final public Item upgrade( int n ) {
		for (int i=0; i < n; i++) {
			upgrade();
		}
		
		return this;
	}
	
	public Item degrade() {
		{this.level--;}
		fix();
		
		return this;
	}
	
	final public Item degrade( int n ) {
		for (int i=0; i < n; i++) {
			degrade();
		}
		
		return this;
	}
	
	public void use() {
		if (level > 0 && !isBroken()) {
			int threshold = (int)(maxDurability() * DURABILITY_WARNING_LEVEL);
			if (durability-- >= threshold && threshold > durability && levelKnown) {
				GLog.w( TXT_GONNA_BREAK, name() );
			}
			if (isBroken()) {
				getBroken();
				if (levelKnown) {
					GLog.n( TXT_BROKEN, name() );
					Dungeon.hero.interrupt();
					
					CharSprite sprite = Dungeon.hero.sprite;
					PointF point = sprite.center().offset( 0, -16 );
					if (this instanceof Weapon) {
						sprite.parent.add( Degradation.weapon( point ) );
					} else if (this instanceof Armor) {
						sprite.parent.add( Degradation.armor( point ) );
					} else if (this instanceof Ring) {
						sprite.parent.add( Degradation.ring( point ) );
					} else if (this instanceof Wand) {
						sprite.parent.add( Degradation.wand( point ) );
					}
					Sample.INSTANCE.play( Assets.SND_DEGRADE );
				}
			}
		}
	}
	
	public boolean isBroken() {if (Dungeon.isChallenged(Challenges.NODEG)) {
		return false;
	}
		else {
		return durability <= 0;
	}
	}
	
	public void getBroken() {	
	}
	
	public void fix() {
		durability = maxDurability();
	}
	
	public void polish() {
		if (durability < maxDurability()) {
			durability++;
		}
	}
	
	public int durability() {
		return durability;
	}
	
	public int maxDurability( int lvl ) {
		return 1;
	}
	
	final public int maxDurability() {
		return maxDurability( level );
	}
	
	public int visiblyUpgraded() {
		return levelKnown ? level : 0;
	}
	
	public boolean visiblyCursed() {
		return cursed && cursedKnown;
	}
	
	public boolean visiblyBroken() {
		return levelKnown && isBroken();
	}
	
	public boolean isUpgradable() {
		return true;
	}
	
	public boolean isIdentified() {
		return levelKnown && cursedKnown;
	}
	
	public boolean isEquipped( Hero hero ) {
		return false;
	}
	
	public Item identify() {
		
		levelKnown = true;
		cursedKnown = true;
		
		return this;
	}
	
	public static void evoke( Hero hero ) {
		hero.sprite.emitter().burst( Speck.factory( Speck.EVOKE ), 5 );
	}
	
	@Override
	public String toString() {
		
		if (levelKnown && level != 0) {
			if (quantity > 1) {
				return Utils.format( TXT_TO_STRING_LVL_X, name(), level, quantity );
			} else {
				return Utils.format( TXT_TO_STRING_LVL, name(), level );
			}
		} else {
			if (quantity > 1) {
				return Utils.format( TXT_TO_STRING_X, name(), quantity );
			} else {
				return Utils.format( TXT_TO_STRING, name() );
			}
		}
	}
	
	public String name() {
		return name;
	}
	
	public final String trueName() {
		return name;
	}
	
	public int image() {
		return image;
	}
	
	public ItemSprite.Glowing glowing() {
		return null;
	}
	
	public String info() {
		return desc();
	}
	
	public String desc() {
		return "";
	}
	
	public int quantity() {
		return quantity;
	}
	
	public void quantity( int value ) {
		quantity = value;
	}
	
	public int price() {
		return 0;
	}
	
	public int considerState( int price ) {
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level > 0) {
				price *= (level + 1);
				if (isBroken()) {
					price /= 2;
				}
			} else if (level < 0) {
				price /= (1 - level);
			}
		}
		if (price < 1) {
			price = 1;
		}
		
		return price;
	}
	
	public static Item virtual( Class<? extends Item> cl ) {
		try {
			
			Item item = (Item)cl.newInstance();
			item.quantity = 0;
			return item;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	public Item random() {
		return this;
	}
	
	public String status() {
		return quantity != 1 ? Integer.toString( quantity ) : null;
	}
	
	public void updateQuickslot() {
		
		if (stackable) {
			Class<? extends Item> cl = getClass();
			if (QuickSlot.primaryValue == cl || QuickSlot.secondaryValue == cl) {
				QuickSlot.refresh();
			}
		} else if (QuickSlot.primaryValue == this || QuickSlot.secondaryValue == this) {
			QuickSlot.refresh();
		}
	}
	
	private static final String QUANTITY		= "quantity";
	private static final String LEVEL			= "level";
	private static final String LEVEL_KNOWN		= "levelKnown";
	private static final String CURSED			= "cursed";
	private static final String CURSED_KNOWN	= "cursedKnown";
	private static final String DURABILITY		= "durability";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( QUANTITY, quantity );
		bundle.put( LEVEL, level );
		bundle.put( LEVEL_KNOWN, levelKnown );
		bundle.put( CURSED, cursed );
		bundle.put( CURSED_KNOWN, cursedKnown );
		if (isUpgradable()) {
			bundle.put( DURABILITY, durability );
		}
		QuickSlot.save( bundle, this );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		quantity	= bundle.getInt( QUANTITY );
		levelKnown	= bundle.getBoolean( LEVEL_KNOWN );
		cursedKnown	= bundle.getBoolean( CURSED_KNOWN );
		
		int level = bundle.getInt( LEVEL );
		if (level > 0) {
			upgrade( level );
		} else if (level < 0) {
			degrade( -level );
		}
		
		cursed	= bundle.getBoolean( CURSED );
		
		if (isUpgradable()) {
			durability = bundle.getInt( DURABILITY );
		}
		
		QuickSlot.restore( bundle, this );
	}
	
	public void cast( final Hero user, int dst ) {
		
		final int cell = Ballistica.cast( user.pos, dst, false, true );
		user.sprite.zap( cell );
		user.busy();
		
		Sample.INSTANCE.play( Assets.SND_MISS, 0.6f, 0.6f, 1.5f );
		
		Char enemy = Actor.findChar( cell );
		QuickSlot.target( this, enemy );
		
		// FIXME!!!
		float delay = TIME_TO_THROW;
		if (this instanceof MissileWeapon) {
			delay *= ((MissileWeapon)this).speedFactor( user );
			if (enemy != null) {
				SnipersMark mark = user.buff( SnipersMark.class );
				if (mark != null) {
					if (mark.object == enemy.id()) {
						delay *= 0.5f;
					}
					user.remove( mark );
				}
			}
		}
		final float finalDelay = delay;
		
		((MissileSprite)user.sprite.parent.recycle( MissileSprite.class )).
			reset( user.pos, cell, this, new Callback() {			
				@Override
				public void call() {
					Item.this.detach( user.belongings.backpack ).onThrow( cell );
					user.spendAndNext( finalDelay );
				}
			} );
	}
	
	protected static Hero curUser = null;
	protected static Item curItem = null;
	protected static CellSelector.Listener thrower = new CellSelector.Listener() {	
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				curItem.cast( curUser, target );
			}
		}
		@Override
		public String prompt() {
			return "Choose direction of throw";
		}
	};
}
