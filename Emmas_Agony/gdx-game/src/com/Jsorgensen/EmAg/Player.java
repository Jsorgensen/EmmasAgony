package com.Jsorgensen.EmAg;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.*;

public class Player extends  BaseUnit
{
	Player(Object object){
		super(object);
		
		this.setAllegiance(Allegiance.PLAYER);
		this.linecolor = Color.BLUE;
		this.setTexture("player.PNG");
	}
}
