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
package com.epd.vanilla;

public class Challenges {

	public static final int MOREHP			= 1;
	public static final int NODEG			= 2;
	public static final int INSID			= 4;
	public static final int HIGHSKILL      = 8;
	public static final int CHSHOP          = 16;

	
	public static final String[] NAMES = {
		"More HP",
		"No degradation",
		"Instant identify",
		"Higher skills",
		"Shops changed"
		};
	
	public static final int[] MASKS = {
		MOREHP,NODEG,INSID,HIGHSKILL,CHSHOP
	};
	
}
