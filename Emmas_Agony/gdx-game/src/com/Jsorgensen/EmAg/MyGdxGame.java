package com.Jsorgensen.EmAg;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import android.text.*;
import android.content.*;
import java.util.*;
import android.view.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import org.apache.commons.codec.net.*;
import java.io.*;
import android.widget.*;

public class MyGdxGame implements ApplicationListener, InputProcessor, GestureListener
{
	private OrthographicCamera camera;
	SpriteBatch batch;
	BitmapFont font;
	ShapeRenderer shapeline;
	
	String message = "Touch the screen";
	
	public int w, h;
	
	boolean scale_tracker = true;
	
    private Stage stage;
	
	public Player player;
	public Enemy enemy;
	public BaseUnit helicopter;
	
	private  java.util.List<BaseUnit> baseunits = new ArrayList<>();
	
	public Touch touch;
	
	@Override
	public void create()
	{
		stage = new Stage();
        Gdx.input.setInputProcessor(stage);
		
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
		touch = new Touch();
		
		InputMultiplexer im = new InputMultiplexer();
		GestureDetector gd = new GestureDetector(this);
		im.addProcessor(gd);
		im.addProcessor(this);

		Gdx.input.setInputProcessor(im);

		enemy = new Enemy(this);
		
		player = new Player(this);
		player.setUnitSize(w/5);
		player.setVector(w/2, h/2);
		player.addFireAnimation("default0/default",".png");
		player.setAttackRange(600.);
		
		helicopter = new BaseUnit(this);
		helicopter.setUnitSize(w/5*3);
		helicopter.setVector(0, 0);
		helicopter.addActionItem("rotor", ".PNG");
		helicopter.action.rotationspeed = 450;
		helicopter.setTexture("heli.PNG");
		helicopter.setRotationOffset(180.+90.);
		helicopter.setRotateSpeed(180/1000.);

		enemy.setUnitSize(w/5);
		enemy.setVector(w*9/10, h*9/10);
		enemy.addDiengAnimation("enemydie/enemydie",".png");
		enemy.dieng.framespeed = 500;
		enemy.setLifeListener(
			new LifeListener(){
				public void onDeath(){
					helicopter.action.rotateBegin();
					helicopter.setFollow(player);
				}
			}
		);
		
		camera= new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);
	}

	@Override
	public void render()
	{
	
	    Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		int points = 1;
		message = "";
		String format = "%.0f";
        for(int i = 0; i < 5; i++){
            if(!touch.touches.get(i).touched) continue;

			message += "Finger:" + Integer.toString(i) + "touch at:" +
				String.format(format, touch.touches.get(i).touchX) +
				"," +
				String.format(format, touch.touches.get(i).touchY) +
				"\n";
				
			message += "Player: Percent = " + String.format(format, player.getHealthPercent())
				+ "  X = " + String.format(format, player.getCenterX())
				+ "  Y = " + String.format(format, player.getCenterY())
				+ "  BaseX = " + String.format(format, player.getBaseX())
				+ "  BaseY = " + String.format(format, player.getBaseY())
				+ "\n";

			message += "Enemy: Percent = " + String.format(format, enemy.getHealthPercent())
				+ "  X = " + String.format(format, enemy.getCenterX())
				+ "  Y = " + String.format(format, enemy.getCenterY())
				+ "  BaseX = " + String.format(format, enemy.getBaseX())
				+ "  BaseY = " + String.format(format, enemy.getBaseY())
				+ "\n";
				
			message += "Rotation: = " + String.format(format, player.getCenterR())
				+ "  Delta Rotation = " + String.format(format, player.dri)
				+ "  EndR = " + String.format(format, player.endr)
				+ "  ActionRot = " + String.format(format, helicopter.action.rotation);
				
			points = i+1;
		}

        TextBounds tb = font.getBounds(message);
        float x = w/2 - tb.width/2/points/4;
        float y = h/2 + tb.height/2;

		player.rangeDetection(enemy);
		
		for(BaseUnit baseunite: baseunits){
			baseunite.draw(batch);
			baseunite.moveLine(batch, camera);
		}

		batch.begin();
        font.drawMultiLine(batch, message, x, y);
		batch.end();
		
		for(BaseUnit baseunite: baseunits){
			baseunite.rotate();
			baseunite.move();
		}
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		for(BaseUnit baseunit: baseunits){
			baseunit.disposeTexture();
			baseunit.shapeline.dispose();
			for(Animate animate: baseunit.animatelist){
				for(Texture texture: animate.sprites){
					texture.dispose();
				}
			}
		}
		stage.dispose();
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
	
	@Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer < 5){
            touch.touches.get(pointer).touchX = screenX;
            touch.touches.get(pointer).touchY = screenY;
            touch.touches.get(pointer).touched = true;

			float x = screenX
				, y = h - screenY;

			player.initiateMovement(x, y);
        }
		for(int i=pointer+1; i<5; i++){
			touch.touches.get(i).touched = false;
		}
        return false;
    }

	@Override
	public boolean touchUp(int p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean mouseMoved(int p1, int p2)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean keyUp(int p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean keyTyped(char p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean scrolled(int p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean keyDown(int p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean touchDragged(int p1, int p2, int p3)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean touchDown(float p1, float p2, int p3, int p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean tap(float p1, float p2, int p3, int p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean panStop(float p1, float p2, int p3, int p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean fling(float p1, float p2, int p3)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean pan(float p1, float p2, float p3, float p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean longPress(float p1, float p2)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean pinch(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean zoom(float p1, float p2)
	{
		// TODO: Implement this method
		return false;
	}
	
	public void addBaseUnit(BaseUnit baseunit){
		baseunits.add(baseunit);
	}
}
