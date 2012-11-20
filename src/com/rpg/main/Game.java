package com.rpg.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.rpg.main.entities.Player;
import com.rpg.main.gfx.Colors;
import com.rpg.main.gfx.Font;
import com.rpg.main.gfx.Screen;
import com.rpg.main.gfx.SpriteSheet;
import com.rpg.main.level.Level;
import com.rpg.main.net.GameClient;
import com.rpg.main.net.GameServer;
import com.rpg.main.net.packets.Packet00Login;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "GAME";

	private JFrame frame;

	public boolean isRunning = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	private int[] colors = new int[6*6*6];
	
	//private SpriteSheet  spriteSheet = new SpriteSheet("/sprite_sheet.png");
	private  Screen screen;
	public InputHandler input;
	
	public Level level;
	public Player player;
	
	private GameClient socketClient;
	private GameServer socketServer;
	
	public Game() {
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		frame = new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.pack();
        
        frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}

	public void init(){
		int index = 0;
		for (int r = 0; r < 6; r++){
			for (int g = 0; g < 6; g++){
				for (int b = 0; b < 6; b++){
					int rr = (r * 255/5);
					int gg = (g * 255/5);
					int bb = (b * 255/5);
			
					colors[index++] = rr <<16 | gg << 8 | bb;
				}
			}
		}
		
		
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		level = new Level("/levels/water_test_level.png");
		
	//	player = new Player(level, 0, 0, input, JOptionPane.showInputDialog(this, "Please Enter A Username"));
	//	level.addEntity(player);
	//	socketClient.sendData("ping".getBytes());
		Packet00Login loginPacket = new Packet00Login(JOptionPane.showInputDialog(this, "Please enter a username"));
        loginPacket.writeData(socketClient);
	}
	
    public synchronized void start() {
		isRunning = true;
    	new Thread(this).start();
    	
    	if(JOptionPane.showConfirmDialog(this,"Do you want to run the server?") == 0){
    		socketServer = new GameServer(this);
    		socketServer.start();
    	}
    	
		socketClient = new GameClient(this, "localhost");
		socketClient.start();
	}
	
    public synchronized void stop() {
		isRunning = false;
		
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/60D;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		init();
		
		while(isRunning){
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			while (delta >= 1){
				ticks ++;
				tick();
				delta -=1;
				shouldRender = true;
			}
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			if(shouldRender){
			frames++;
			render();
			}
			if (System.currentTimeMillis() -lastTimer >= 1000){
				lastTimer += 1000;
				frame.setTitle(frames + "FPS" + "," + ticks + "Ticks");
				frames = 0;
				ticks = 0;
			}
		}

	}
	

	
	public void tick(){
	tickCount ++;
	level.tick();
	
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		
		int xOffset = player.x - (screen.width / 2);
		int yOffset = player.y - (screen.height / 2);
		
	level.renderTiles(screen, xOffset, yOffset);
		
	
	
	
	level.renderEntities(screen);
	//String msg = "TESTING!!";
	//Font.render(msg, screen, screen.xOffset + screen.width / 2 - ((msg.length()*8)/2) , screen.yOffset+screen.height / 2, Colors.get(-1, -1,-1,000), 0);
		
		
		for(int y =0; y < screen.height; y++){
			for(int x =0; x < screen.width; x++){
			   int colorCode = 	screen.pixels[x+y * screen.width];
			   if(colorCode < 255) pixels[x+y*WIDTH] = colors[colorCode];
			}
		}
		
		Graphics g = bs.getDrawGraphics();
		
		
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
				g.dispose();
		bs.show();
		
	}

	public static void main(String[] args){
		new Game().start();

	}

	

}