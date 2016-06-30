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
package com.epd.vanilla.windows;

import java.util.Locale;

import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.epd.vanilla.Assets;
import com.epd.vanilla.Badges;
import com.epd.vanilla.Dungeon;
import com.epd.vanilla.Statistics;
import com.epd.vanilla.actors.hero.Belongings;
import com.epd.vanilla.items.Item;
import com.epd.vanilla.scenes.PixelScene;
import com.epd.vanilla.sprites.HeroSprite;
import com.epd.vanilla.ui.BadgesList;
import com.epd.vanilla.ui.Icons;
import com.epd.vanilla.ui.ItemSlot;
import com.epd.vanilla.ui.QuickSlot;
import com.epd.vanilla.ui.RedButton;
import com.epd.vanilla.ui.ScrollPane;
import com.epd.vanilla.utils.Utils;

public class WndRanking extends WndTabbed {
	
	private static final String TXT_ERROR		= "Unable to load additional information";
	
	private static final String TXT_STATS	= "Stats";
	private static final String TXT_ITEMS	= "Items";
	private static final String TXT_BADGES	= "Badges";
	
	private static final int WIDTH			= 112;
	private static final int HEIGHT			= 134;
	
	private static final int TAB_WIDTH	= 40;
	
	private Thread thread;
	private String error = null;
	
	private Image busy;
	
	public WndRanking( final String gameFile ) {
		
		super();
		resize( WIDTH, HEIGHT );
		
		thread = new Thread() {
			@Override
			public void run() {
				try {
					Badges.loadGlobal();
					Dungeon.loadGame( gameFile );
				} catch (Exception e ) {
					error = TXT_ERROR;
				}
			}
		};
		thread.start();
		
		busy = Icons.BUSY.get();	
		busy.origin.set( busy.width / 2, busy.height / 2 );
		busy.angularSpeed = 720;
		busy.x = (WIDTH - busy.width) / 2;
		busy.y = (HEIGHT - busy.height) / 2;
		add( busy );
	}
	
	@Override
	public void update() {
		super.update();
		
		if (thread != null && !thread.isAlive()) {
			thread = null;
			if (error == null) {
				remove( busy );
				createControls();
			} else {
				hide();
				Game.scene().add( new WndError( TXT_ERROR ) );
			}
		}
	}
	
	private void createControls() {
		
		String[] labels = 
			{TXT_STATS, TXT_ITEMS, TXT_BADGES};
		Group[] pages = 
			{new StatsTab(), new ItemsTab(), new BadgesTab()};
		
		for (int i=0; i < pages.length; i++) {
			
			add( pages[i] );
			
			Tab tab = new RankingTab( labels[i], pages[i] );
			tab.setSize( TAB_WIDTH, tabHeight() );
			add( tab );
		}
		
		select( 0 );
	}

	private class RankingTab extends LabeledTab {
		
		private Group page;
		
		public RankingTab( String label, Group page ) {
			super( label );
			this.page = page;
		}
		
		@Override
		protected void select( boolean value ) {
			super.select( value );
			if (page != null) {
				page.visible = page.active = selected;
			}
		}
	}
	
	private class StatsTab extends Group {
		
		private static final int GAP	= 4;
		
		private static final String TXT_TITLE	= "Level %d %s";
		
		private static final String TXT_CHALLENGES	= "Challenges";
		
		private static final String TXT_HEALTH	= "Health";
		private static final String TXT_STR		= "Strength";
		
		private static final String TXT_DURATION	= "Game Duration";
		
		private static final String TXT_DEPTH	= "Maximum Depth";
		private static final String TXT_ENEMIES	= "Mobs Killed";
		private static final String TXT_GOLD	= "Gold Collected";
		
		private static final String TXT_FOOD	= "Food Eaten";
		private static final String TXT_ALCHEMY	= "Potions Cooked";
		private static final String TXT_ANKHS	= "Ankhs Used";
		
		public StatsTab() {
			super();
			
			String heroClass = Dungeon.hero.className();
			
			IconTitle title = new IconTitle();
			title.icon( HeroSprite.avatar( Dungeon.hero.heroClass, Dungeon.hero.tier() ) );
			title.label( Utils.format( TXT_TITLE, Dungeon.hero.lvl, heroClass ).toUpperCase( Locale.ENGLISH ) );
			title.color(TITLE_COLOR2);
			title.setRect( 0, 0, WIDTH, 0 );
			add( title );
			
			float pos = title.bottom();
			
			if (Dungeon.challenges > 0) {
				RedButton btnCatalogus = new RedButton( TXT_CHALLENGES ) {
					@Override
					protected void onClick() {
						Game.scene().add( new WndChallenges( Dungeon.challenges, false ) );
					}
				};
				btnCatalogus.setRect( 0, pos + GAP, btnCatalogus.reqWidth() + 2, btnCatalogus.reqHeight() + 2 );
				add( btnCatalogus );
				
				pos = btnCatalogus.bottom();
			}
			
			pos += GAP + GAP;
			
			pos = statSlot( this, TXT_STR, Integer.toString( Dungeon.hero.STR ), pos );
			pos = statSlot( this, TXT_HEALTH, Integer.toString( Dungeon.hero.HT ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, TXT_DURATION, Integer.toString( (int)Statistics.duration ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, TXT_DEPTH, Integer.toString( Statistics.deepestFloor ), pos );
			pos = statSlot( this, TXT_ENEMIES, Integer.toString( Statistics.enemiesSlain ), pos );
			pos = statSlot( this, TXT_GOLD, Integer.toString( Statistics.goldCollected ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, TXT_FOOD, Integer.toString( Statistics.foodEaten ), pos );
			pos = statSlot( this, TXT_ALCHEMY, Integer.toString( Statistics.potionsCooked ), pos );
			pos = statSlot( this, TXT_ANKHS, Integer.toString( Statistics.ankhsUsed ), pos );
		}
		
		private float statSlot( Group parent, String label, String value, float pos ) {
			
			BitmapText txt = PixelScene.createText( label, 7 );
			txt.y = pos;
			parent.add( txt );
			
			txt = PixelScene.createText( value, 7 );
			txt.measure();
			txt.x = PixelScene.align( WIDTH * 0.65f );
			txt.y = pos;
			parent.add( txt );
			
			return pos + GAP + txt.baseLine();
		}
	}
	
	private class ItemsTab extends Group {
		
		private int count;
		private float pos;
		
		public ItemsTab() {
			super();
			
			Belongings stuff = Dungeon.hero.belongings;
			if (stuff.weapon != null) {
				addItem( stuff.weapon );
			}
			if (stuff.armor != null) {
				addItem( stuff.armor );
			}
			if (stuff.ring1 != null) {
				addItem( stuff.ring1 );
			}
			if (stuff.ring2 != null) {
				addItem( stuff.ring2 );
			}
			
			Item primary = getQuickslot( QuickSlot.primaryValue );
			Item secondary = getQuickslot( QuickSlot.secondaryValue );
			
			if (count >= 4 && primary != null && secondary != null) {
				
				float size = ItemButton.SIZE;
				
				ItemButton slot = new ItemButton( primary );
				slot.setRect( 0, pos, size, size );
				add( slot );
				
				slot = new ItemButton( secondary );
				slot.setRect( size + 1, pos, size, size );
				add( slot );
			} else {
				if (primary != null) {
					addItem( primary );
				}
				if (secondary != null) {
					addItem( secondary );
				}
			}
		}
		
		private void addItem( Item item ) {
			LabelledItemButton slot = new LabelledItemButton( item );
			slot.setRect( 0, pos, width, LabelledItemButton.SIZE );
			add( slot );
			
			pos += slot.height() + 1;
			count++;
		}
		
		private Item getQuickslot( Object value ) {
			if (value instanceof Item && Dungeon.hero.belongings.backpack.contains( (Item)value )) {
					
					return (Item)value;
					
			} else if (value instanceof Class){
				
				@SuppressWarnings("unchecked")
				Item item = Dungeon.hero.belongings.getItem( (Class<? extends Item>)value );
				if (item != null) {
					return item;
				}
			}
			
			return null;
		}
	}
	
	private class BadgesTab extends Group {
		
		public BadgesTab() {
			super();
			
			camera = WndRanking.this.camera;
			
			ScrollPane list = new BadgesList( false );
			add( list );
			
			list.setSize( WIDTH, HEIGHT );
		}
	}
	
	private class ItemButton extends Button {
		
		public static final int SIZE	= 26;
		
		protected Item item;
		
		protected ItemSlot slot;
		private ColorBlock bg;
		
		public ItemButton( Item item ) {
			
			super();

			this.item = item;
			
			slot.item( item );
			if (item.cursed && item.cursedKnown) {
				bg.ra = +0.2f;
				bg.ga = -0.1f;
			} else if (!item.isIdentified()) {
				bg.ra = 0.1f;
				bg.ba = 0.1f;
			}
		}
		
		@Override
		protected void createChildren() {	
			
			bg = new ColorBlock( SIZE, SIZE, 0xFF4A4D44 );
			add( bg );
			
			slot = new ItemSlot();
			add( slot );
			
			super.createChildren();
		}
		
		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;
			
			slot.setRect( x, y, SIZE, SIZE );
			
			super.layout();
		}
		
		@Override
		protected void onTouchDown() {
			bg.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
		};
		
		protected void onTouchUp() {
			bg.brightness( 1.0f );
		};
		
		@Override
		protected void onClick() {
			Game.scene().add( new WndItem( null, item ) );
		}
	}

	private class LabelledItemButton extends ItemButton {
		private BitmapText name;
		
		public LabelledItemButton( Item item ) {
			super( item );
		}
		
		@Override
		protected void createChildren() {	
			super.createChildren();
			
			name = PixelScene.createText( "?", 7 );
			add( name );
		}
		
		@Override
		protected void layout() {
			
			super.layout();
			
			name.x = slot.right() + 2;
			name.y = y + (height - name.baseLine()) / 2;
			
			String str = Utils.capitalize( item.name() );
			name.text( str );
			name.measure();
			if (name.width() > width - name.x) {
				do {
					str = str.substring( 0, str.length() - 1 );
					name.text( str + "..." );
					name.measure();
				} while (name.width() > width - name.x);
			}
		}
	}
}
