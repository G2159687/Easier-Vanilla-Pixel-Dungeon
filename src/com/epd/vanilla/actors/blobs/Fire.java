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
package com.epd.vanilla.actors.blobs;

import com.epd.vanilla.Dungeon;
import com.epd.vanilla.actors.Actor;
import com.epd.vanilla.actors.Char;
import com.epd.vanilla.actors.buffs.Buff;
import com.epd.vanilla.actors.buffs.Burning;
import com.epd.vanilla.effects.BlobEmitter;
import com.epd.vanilla.effects.particles.FlameParticle;
import com.epd.vanilla.items.Heap;
import com.epd.vanilla.levels.Level;
import com.epd.vanilla.scenes.GameScene;

public class Fire extends Blob {
	
	@Override
	protected void evolve() {

		boolean[] flamable = Level.flamable;
		
		int from = WIDTH + 1;
		int to = Level.LENGTH - WIDTH - 1;
		
		boolean observe = false;
		
		for (int pos=from; pos < to; pos++) {
			
			int fire;
			
			if (cur[pos] > 0) {
				
				burn( pos );
				
				fire = cur[pos] - 1;
				if (fire <= 0 && flamable[pos]) {
					
					int oldTile = Dungeon.level.map[pos];
					Dungeon.level.destroy( pos );
					
					observe = true;
					GameScene.updateMap( pos );
					if (Dungeon.visible[pos]) {
						GameScene.discoverTile( pos, oldTile );
					}
				}
				
			} else {
				
				if (flamable[pos] && (cur[pos-1] > 0 || cur[pos+1] > 0 || cur[pos-WIDTH] > 0 || cur[pos+WIDTH] > 0)) {
					fire = 4;
					burn( pos );
				} else {
					fire = 0;
				}

			}
			
			volume += (off[pos] = fire);

		}
		
		if (observe) {
			Dungeon.observe();
		}
	}
	
	private void burn( int pos ) {
		Char ch = Actor.findChar( pos );
		if (ch != null) {
			Buff.affect( ch, Burning.class ).reignite( ch );
		}
		
		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null) {
			heap.burn();
		}
	}
	
	public void seed( int cell, int amount ) {
		if (cur[cell] == 0) {
			volume += amount;
			cur[cell] = amount;
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.start( FlameParticle.FACTORY, 0.03f, 0 );
	}
	
	@Override
	public String tileDesc() {
		return "A fire is raging here.";
	}
}
