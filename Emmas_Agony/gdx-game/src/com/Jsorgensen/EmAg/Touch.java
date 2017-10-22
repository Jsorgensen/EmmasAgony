package com.Jsorgensen.EmAg;
import com.badlogic.gdx.*;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.*;
import java.util.*;

public class Touch implements InputProcessor, GestureListener
{
	
	Touch(){
        for(int i = 0; i < 5; i++){
            touches.put(i, new TouchInfo());
        }
	}

	public Map<Integer,TouchInfo> touches = new HashMap<Integer,TouchInfo>();
	
	class TouchInfo {
        public float touchX = 0;
        public float touchY = 0;
        public boolean touched = false;
    }
	
	@Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
}
