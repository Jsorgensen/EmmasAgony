package com.Jsorgensen.EmAg;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.*;

public class Enemy extends BaseUnit
{
	Enemy(Object object){
		super(object);

		this.setAllegiance(Allegiance.ENEMY);
		this.setHealthCap(this.getHealthCap()*6/10);
		this.setHealth(this.getHealthCap());
		this.setDamage(this.getDamage()*6/10);
		this.setSpeed(this.getSpeed()*6/10);
		this.setTexture("enemy.png");
		linecolor = linecolor.RED;
		fullHealth();
		
		this.texturedead = new Texture(Gdx.files.internal("enemydead.png"));
		this.texturemedium = new Texture(Gdx.files.internal("enemymedium.png"));
		this.texturelow = new Texture(Gdx.files.internal("enemylow.png"));
	}

	@Override
	public void fullHealth()
	{
		this.healthstatus = HealthStatus.FULL;
	}
	
	@Override
	public void mediumHealth(){
		this.healthstatus = HealthStatus.MEDIUM;
	}
	
	@Override
	public void lowHealth(){
		this.healthstatus = HealthStatus.LOW;
	}
	
	@Override
	public void deadHealth(){
		this.healthstatus = HealthStatus.DEAD;
	}
}
