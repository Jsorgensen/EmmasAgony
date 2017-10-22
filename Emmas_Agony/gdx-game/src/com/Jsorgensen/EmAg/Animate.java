package com.Jsorgensen.EmAg;
import com.badlogic.gdx.graphics.*;
import java.util.*;
import com.badlogic.gdx.*;

public class Animate
{
	Animate(String filename, String extension){
		sprites = new ArrayList<>();
		this.setsprites(filename, extension);

		atexture = sprites.get(0);
		aw = atexture.getWidth();
		ah = atexture.getHeight();
		ax = aw/2;
		ay = ah/2;
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		framespeed = 18;
	}

	List<Texture> sprites;
	Texture atexture;
	int i, framespeed, w, h;
	float ax, ay, aw, ah;
	Boolean running = false
		, repeat = false;

	public void setsprites(String filename, String extension){
		sprites.clear();
		try{
			int j = 0;
			while(j<100){
				Texture t = new Texture(Gdx.files.internal(filename+(j+1)+extension));
				sprites.add(t);
				j++;
			}
		}catch(Exception e){

		}
	}

	public void animate(int ai){
		if(!running){
			if(ai<sprites.size()){
				running = true;

				i = ai;
				atexture = sprites.get(ai);

				TimerTask timertask = new TimerTask(){
					@Override
					public void run(){
						running = false;
						animate(++i);
					}
				};
				Timer timer = new Timer();
				timer.schedule(timertask, framespeed);
			}else{
				if(repeat){
					animate(0);
				}
			}
		}
	}

	public void setsides(float sides){
		aw = sides;
		ah = sides;

		ax = aw/2;
		ay = h - ah/2;
	}

	public void setVector(float x, float y){
		ax = x - aw/2;
		ay = y - ah/2;
	}

	public Texture getanimatetexture(){
		return atexture;
	}
	
	public void setRepeat(boolean repeat){
		 this.repeat = repeat;
	}
}
