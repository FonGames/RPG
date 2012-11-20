package com.rpg.main.level.tiles;

import com.rpg.main.gfx.Colors;
import com.rpg.main.gfx.Screen;
import com.rpg.main.level.Level;

public abstract class Tile {

	public static final Tile[] tiles = new Tile[256];
	public static final Tile VOID = new BasicSolidTile(0,0,0, Colors.get(000, -1,-1,-1), 0xFF000000);
	public static final Tile STONE = new BasicSolidTile(1,1,0, Colors.get(-1, 333,-1,-1), 0xFF555555);
	public static final Tile GRASS = new BasicTile(2,2,0, Colors.get(-1, 131,141,-1), 0xFF00FF00);
	public static final Tile SAND = new BasicTile(4,4,0, Colors.get(-1, 552,554,-1), 0xFFFFFF00);
	public static final Tile LAVA = new AnimatedTile(5, new int[][] {{0, 6}, {1,6}, {2,6}, {3,6}, {1,6}}, Colors.get(-1,510,530,-1), 0xFFFF0000, 250);
	public static final Tile WATER = new AnimatedTile(3, new int[][] {{0, 5}, {1,5}, {2,5}, {1,5}}, Colors.get(-1,004,115,-1), 0xFF0000FF, 500);

	protected byte id;
	protected boolean solid;
	protected boolean emitter;
	private int levelColor;
	
	
	public Tile(int id, boolean isSolid, boolean isEmitter, int levelColor){
		this.id = (byte) id;
		if(tiles[id] != null) throw new RuntimeException("Duplicate file id on" + id);
	    this.solid = isSolid;
	    this.emitter = isEmitter;
	    this.levelColor = levelColor;
		tiles[id] = this;
	    
	
	}
	
	public byte getId(){
		return id;
	}
	
	public boolean isSolid(){
		return solid;
	}
	
	public boolean isEmitter(){
		return emitter;
	}
	
	public int getlevelColor(){
		return levelColor;
	}
	
	public abstract void tick();
	
	public abstract void render(Screen screen, Level level, int x, int y);

}
