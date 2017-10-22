package com.Jsorgensen.EmAg;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import android.os.*;
import com.badlogic.gdx.*;

public class ActionItem
{
	
	ActionItem(String filename, String extension, BaseUnit unit){
		texture = new Texture(Gdx.files.internal(filename+extension));
		textureregion.setRegion(texture);
		
		width = unit.getWidth();
		height = unit.getHeight();
		centerx = unit.getCenterX();
		centery = unit.getCenterY();
		basex = centerx - width/2;
		basey = centery - height/2;
	}
	
	Texture texture;
	TextureRegion textureregion = new TextureRegion();
	float rotation = 0
		, rotationspeed = 360
		, width, height
		, basex, basey
		, centerx, centery
		, dr;
	Long ti, to, dt;
	boolean running = false;
		
	public void rotate(BaseUnit unit){

		centerx = unit.getCenterX();
		centery = unit.getCenterY();
		basex = centerx - width/2;
		basey = centery - height/2;
		
		if(running){
			deltaTime();

			rotation = dt * rotationspeed / 1000;

			if(rotation > rotationspeed*5){
				ti = SystemClock.elapsedRealtime();
			}
		}
	}
	
	public void draw(BaseUnit unit, Batch batch){
		rotate(unit);
		
		batch.begin();
		batch.draw(textureregion
				   , basex, basey
				   , width/2, height/2
				   , width, height
				   ,1f, 1f, rotation);
		batch.end();
	}
	
	public void deltaTime(){
		to = SystemClock.elapsedRealtime();
		dt = to - ti;
	}
	
	public void rotateBegin(){
		running = true;

		ti = SystemClock.elapsedRealtime();
	}
}
