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

import com.watabou.noosa.BitmapText;
import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.Image;
import com.epd.vanilla.Badges;
import com.epd.vanilla.effects.BadgeBanner;
import com.epd.vanilla.scenes.PixelScene;
import com.epd.vanilla.ui.Window;

public class WndBadge extends Window {
	
	private static final int WIDTH = 120;
	private static final int MARGIN = 4;
	
	public WndBadge( Badges.Badge badge ) {
		
		super();
		
		Image icon = BadgeBanner.image( badge.image );
		icon.scale.set( 2 );
		add( icon );
		
		BitmapTextMultiline info = PixelScene.createMultiline( badge.description, 8 );
		info.maxWidth = WIDTH - MARGIN * 2;
		info.measure();
		
		float w = Math.max( icon.width(), info.width() ) + MARGIN * 2;
		
		icon.x = (w - icon.width()) / 2;
		icon.y = MARGIN;
		
		float pos = icon.y + icon.height() + MARGIN;
		for (BitmapText line : info.new LineSplitter().split()) {
			line.measure();
			line.x = PixelScene.align( (w - line.width()) / 2 );
			line.y = PixelScene.align( pos );
			add( line );
			
			pos += line.height(); 
		}

		resize( (int)w, (int)(pos + MARGIN) );
		
		BadgeBanner.highlight( icon, badge.image );
	}
}
