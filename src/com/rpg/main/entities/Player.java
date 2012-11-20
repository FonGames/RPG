package com.rpg.main.entities;

import com.rpg.main.InputHandler;
import com.rpg.main.InputHandler.Key;
import com.rpg.main.gfx.Colors;
import com.rpg.main.gfx.Font;
import com.rpg.main.gfx.Screen;
import com.rpg.main.level.Level;
import com.rpg.main.level.tiles.Tile;

@SuppressWarnings("unused")
public class Player extends Mob
{
  private InputHandler input;
  private int color = Colors.get(-1, 0, 510, 543);
  private int scale = 1;
  protected boolean isSwimming = false;
  protected boolean isSwimLava = false;
  private int tickCount = 0;
  private String username;

  public Player(Level level, int x, int y, InputHandler input, String username)
  {
    super(level, "Player", x, y, 1);
    this.input = input;
    this.username = username;
  }

  public void tick()
  {
    int xa = 0;
    int ya = 0;
    if (this.input.up.isPressed()) {
      ya--;
    }
    if (this.input.down.isPressed()) {
      ya++;
    }
    if (this.input.left.isPressed()) {
      xa--;
    }
    if (this.input.right.isPressed()) {
      xa++;
    }
    if ((xa != 0) || (ya != 0)) {
      move(xa, ya);
      this.isMoving = true;
    } else {
      this.isMoving = false;
    }
    if (this.level.getTile(this.x >> 3, this.y >> 3).getId() == 3) {
      this.isSwimming = true;
    }
    if (this.level.getTile(this.x >> 3, this.y >> 3).getId() == 5) {
        this.isSwimLava = true;
      }

    if ((this.isSwimming) && (this.level.getTile(this.x >> 3, this.y >> 3).getId() != 3)) {
      this.isSwimming = false;
    }
    if ((this.isSwimLava) && (this.level.getTile(this.x >> 3, this.y >> 3).getId() != 5)) {
        this.isSwimLava = false;
      }
    this.tickCount += 1;
  }

  public void render(Screen screen)
  {
    int xTile = 0;
    int yTile = 28;
    int walkingSpeed = 4;
    int flipTop = this.numSteps >> walkingSpeed & 0x1;
    int flipBottom = this.numSteps >> walkingSpeed & 0x1;

    if (this.movingDir == 1) {
      xTile += 2;
    } else if (this.movingDir > 1) {
      xTile += 4 + (this.numSteps >> walkingSpeed & 0x1) * 2;
      flipTop = (this.movingDir - 1) % 2;
    }

    int modifier = 8 * this.scale;
    int xOffset = this.x - modifier / 2;
    int yOffset = this.y - modifier / 2 - 4;

    if (this.isSwimming) {
      int waterColor = 0;
      yOffset += 4;
      if (this.tickCount % 60 < 15) {
        waterColor = Colors.get(-1, -1, 225, -1);
      } else if ((15 <= this.tickCount % 60) && (this.tickCount % 60 < 30)) {
        yOffset--;
        waterColor = Colors.get(-1, 225, 115, -1);
      } else if ((30 <= this.tickCount % 60) && (this.tickCount % 60 < 45)) {
        waterColor = Colors.get(-1, 115, -1, 225);
      } else {
        yOffset--;
        waterColor = Colors.get(-1, 225, 115, -1);
      }
      screen.render(xOffset, yOffset + 3, 864, waterColor, 0, 1);
      screen.render(xOffset + 8, yOffset + 3, 864, waterColor, 1, 1);
    }

    screen.render(xOffset + modifier * flipTop, yOffset, xTile + yTile * 32, this.color, flipTop, this.scale);
    screen.render(xOffset + modifier - modifier * flipTop, yOffset, xTile + 1 + yTile * 32, this.color, flipTop, this.scale);

    if (!this.isSwimming && !this.isSwimLava) {
      screen.render(xOffset + modifier * flipBottom, yOffset + modifier, xTile + (yTile + 1) * 32, this.color, flipBottom, this.scale);
      screen.render(xOffset + modifier - modifier * flipBottom, yOffset + modifier, xTile + 1 + (yTile + 1) * 32, this.color, flipBottom, this.scale);
    }

    if(username != null){
		if(username.startsWith("§1")){
			Font.render(username.substring(2), screen, xOffset - ((username.length()-3 )/ 2 * 8), yOffset - 10, Colors.get(-1,-1,-1,510),(1));
		} else {
			if(username.startsWith("§2")){
				color = Colors.get(-1, 000, 5, 543);
				Font.render(username.substring(2), screen, xOffset - ((username.length()-3 )/ 2 * 8), yOffset - 10, Colors.get(-1,-1,-1,19),(1));
			} else {
				if(username.startsWith("¿")){
					color = Colors.get(-1, 000, 001, 543);
					Font.render(username.substring(1), screen, xOffset - ((username.length()-2 )/ 2 * 8), yOffset - 10, Colors.get(000,-1,-1,-1),(1));
				}else if (username.length() <= 0){
					Font.render("Mr.null", screen, xOffset - ((username.length()+5 )/ 2 * 8), yOffset - 10, Colors.get(-1,-1,-1,555),(1));
				} 
				else {
					if(username.length() == 1){
						Font.render("Mr." + username, screen, xOffset - ((username.length()+2 )/ 2 * 8), yOffset - 10, Colors.get(-1,-1,-1,555),(1));
					} else {
		Font.render(username, screen, xOffset - ((username.length()-1 )/ 2 * 8), yOffset - 10, Colors.get(-1,-1,-1,555),(1));
	}
	}
	}
	}
	} 
	}

  public boolean hasCollided(int xa, int ya)
  {
    int xMin = 0;
    int xMax = 7;
    int yMin = 3;
    int yMax = 7;

    for (int x = xMin; x < xMax; x++) {
      if (isSolidTile(xa, ya, x, yMin)) {
        return true;
      }
    }
    for (int x = xMin; x < xMax; x++) {
      if (isSolidTile(xa, ya, x, yMax)) {
        return true;
      }
    }
    for (int y = yMin; y < yMax; y++) {
      if (isSolidTile(xa, ya, xMin, y)) {
        return true;
      }
    }
    for (int y = yMin; y < yMax; y++) {
      if (isSolidTile(xa, ya, xMax, y)) {
        return true;
      }
    }

    return false;
  }
}