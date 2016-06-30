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

import com.watabou.noosa.BitmapTextMultiline;
import com.epd.vanilla.Dungeon;
import com.epd.vanilla.items.Item;
import com.epd.vanilla.scenes.PixelScene;
import com.epd.vanilla.sprites.ItemSprite;
import com.epd.vanilla.ui.ItemSlot;
import com.epd.vanilla.ui.RedButton;
import com.epd.vanilla.ui.Window;
import com.epd.vanilla.utils.Utils;

public class WndItem extends Window {

	private static final float BUTTON_WIDTH		= 36;
	private static final float BUTTON_HEIGHT	= 16;
	
	private static final float GAP	= 2;
	
	private static final int WIDTH = 120;
	
	public WndItem( final WndBag owner, final Item item ) {	
		
		super();
		
		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite( item.image(), item.glowing() ) );
		titlebar.label( Utils.capitalize( item.toString() ) );
		if (item.isUpgradable() && item.levelKnown) {
			titlebar.health( (float)item.durability() / item.maxDurability() );
		}
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );
		
		if (item.levelKnown) {
			if (item.level() < 0) {
				titlebar.color( ItemSlot.DEGRADED );				
			} else if (item.level() > 0) {
				titlebar.color( item.isBroken() ? ItemSlot.WARNING : ItemSlot.UPGRADED );				
			}
		}
		
		BitmapTextMultiline info = PixelScene.createMultiline( item.info(), 6 );
		info.maxWidth = WIDTH;
		info.measure();
		info.x = titlebar.left();
		info.y = titlebar.bottom() + GAP;
		add( info );
	
		float y = info.y + info.height() + GAP;
		float x = 0;
		
		if (Dungeon.hero.isAlive() && owner != null) {
			for (final String action:item.actions( Dungeon.hero )) {
				
				RedButton btn = new RedButton( action ) {
					@Override
					protected void onClick() {
						item.execute( Dungeon.hero, action );
						hide();
						owner.hide();
					};
				};
				btn.setSize( Math.max( BUTTON_WIDTH, btn.reqWidth() ), BUTTON_HEIGHT );
				if (x + btn.width() > WIDTH) {
					x = 0;
					y += BUTTON_HEIGHT + GAP;
				}
				btn.setPos( x, y );
				add( btn );
				
				if (action == item.defaultAction) {
					btn.textColor( TITLE_COLOR );
				}
				
				x += btn.width() + GAP;
			}
		}
		
		resize( WIDTH, (int)(y + (x > 0 ? BUTTON_HEIGHT : 0)) );
	}
}
