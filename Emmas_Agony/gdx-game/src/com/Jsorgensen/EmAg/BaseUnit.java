package com.Jsorgensen.EmAg;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.concurrent.*;
import java.util.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.glutils.*;
import android.os.*;
import android.renderscript.*;
import android.widget.*;
import android.view.*;
import android.transition.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;

public class BaseUnit
{
	protected Texture texture, drawtexture
		, texturedead, texturelow, texturemedium;
	float w, h;
	
	Fire fire;
	Dieng dieng;
	Animate animation;
	ActionItem action;
	
	List<Animate> animatelist;
	private float basex, basey, baser
		, centerx, centery, centerr;
	
	private double health = 100.
		, healthcap = health
		, healthpercent = 100.
		, damage = 10.
		, attackrange = 400.
		, damagerange = 3.
		, speed = 4/10.
		, rotatespeed = 720/1000.
		, rotationoffset = 90.;
	private int attackspeed = 750;
	private boolean alive = true
		, attackcycle = true
		, moving = false
		, rotating = false
		, engaged =  true
		, damageengaged = true
		, lifelisteneron = false
		, followuniton = false;
	private Timer attacktimer = new Timer();
	private Long dt, ti, to;
	public float m;
	protected enum Allegiance {NONE, PLAYER, ENEMY};
	protected enum HealthStatus {FULL, MEDIUM, LOW, DEAD};
	protected enum AnimateFocus {NULL, FIRE, DIENG, ANIMATION};
	private Allegiance allegiance =  Allegiance.NONE;
	protected HealthStatus healthstatus = HealthStatus.FULL;
	protected AnimateFocus animatefocus = AnimateFocus.NULL;
	protected Color linecolor = Color.BLACK;;
	ShapeRenderer shapeline = new ShapeRenderer();
	protected LifeListener lifelistener;
	protected BaseUnit followunit;

	public float endx, endy, endr, xi, yi
		, ri, dri;
	public double degrees;
	
	
	BaseUnit(Object object){
		MyGdxGame mygdxgame = (MyGdxGame) object;
		mygdxgame.addBaseUnit(this);
		
		texture = new Texture(Gdx.files.internal("default.PNG"));
		w = texture.getWidth();
		h = texture.getHeight();
		
		animatelist = new ArrayList<Animate>();
		
		setCenterR(270);
	}

	public void draw(Batch batch){

		TextureRegion tr = new TextureRegion();

		switch(healthstatus){
			case(DEAD):
				drawtexture = texturedead;
				break;
			
			case(LOW):
				drawtexture = texturelow;
				break;
				
			case(MEDIUM):
				drawtexture = texturemedium;
				break;
				
			case(FULL):
				drawtexture = texture;
				break;
		}
		
		/*switch(animatefocus){
			case(FIRE):
				drawtexture = fire.atexture;
				break;
			
			case(DIENG):
				drawtexture = dieng.atexture;
				break;
			
			case(ANIMATION):
				break;
		}*/
		for(Animate animate: animatelist){
			if(animate.running){
				drawtexture = animate.atexture;
			}
		}
		
		tr.setRegion(drawtexture);

		batch.begin();
		batch.draw(tr, basex, basey, w/2, h/2, w, h, 1f, 1f, baser);
		batch.end();
		
		if(action != null){
			action.draw(this, batch);
		}
	}
	
	public void rangeDetection(BaseUnit enemy){
		float dxy2 = (float)(Math.pow((centerx - enemy.centerx),2)
			+ Math.pow((centery - enemy.centery),2));
		float dp2 = (float)Math.pow(this.getAttackRange(),2);
		float de2 = (float)Math.pow(enemy.getAttackRange(),2);

		if(dxy2<dp2){
			if(engaged && enemy.alive){
				this.attack(enemy);
			}
		}else{
			if(!engaged) engaged = true;
			if(dxy2<de2){
				if(damageengaged && alive){
					damageengaged = false;
					initiateMovement(enemy.centerx, enemy.centery);
				}
			}
		}
		if(dxy2<de2){
			if(enemy.engaged && alive){
				enemy.attack(this);
			}
		}else{
			if(enemy.engaged) enemy.engaged = true;
			if(dxy2<dp2){
				if(enemy.damageengaged  && enemy.alive){
					enemy.damageengaged = false;
					enemy.initiateMovement(centerx, centery);
				}
			}
		}
	}
	
	public void attack(final BaseUnit unit){
		if(attackcycle && alive && unit.alive){
			if(engaged){
				engaged = false;
				moving = false;
				initiateRotate(unit.centerx, unit.centery);
				if(!damageengaged) damageengaged = true;
			}
			
			Double attack =  this.getDamage();
			Double range =  this.getDamageRange();
			Double min = attack - range
				, max = attack + range;

			attack = ThreadLocalRandom.current().nextDouble(min, max);
			unit.setHealth(unit.getHealth()-attack);
			
			final BaseUnit unit0 = this;
			attackcycle = false;
			attacktimer.schedule(new TimerTask()
				{
					@Override
					public void run() {
						attackcycle = true;
						unit0.attack(unit);
					}
				}
				, attackspeed);
			
			if(fire!=null){
				fire.animate(0);
			}
		}else{
			if(!unit.alive){
				moving = true;
				initiateMovement(endx, endy);
			}
		}
	}

	public void rotate(){
		if(followuniton){
			followRotate();
		}
		else if(rotating){
			deltaTime();
			float dr;
			dr = (float)(rotatespeed*dt);
			if(dr>dri){
				rotating = false;
				setCenterR(endr);
				ri = endr;
				ti = SystemClock.elapsedRealtime();
			}else{
				if(endr<ri) dr *= -1;
				if(Math.abs(endr - ri)>180f) dr *= -1;

				setCenterR(ri + dr);
			}
		}
	}
	
	public void move(){
		if(followuniton){
			followMove();
		}
		else if(moving && !rotating){
			deltaTime();
			float dx, dy;
			try{
				dx = (float)(Math.cos(Math.atan(m))*speed*dt);
				dy = (float)(Math.sin(Math.atan(m))*speed*dt);
			}catch(Exception e){
				dx = 0;
				dy = (float)(speed*1f);
			}
			if(endy<yi) dy *= -1;
			if(endx<xi) dx *= -1;

			setCenterX(xi + dx);
			setCenterY(yi + dy);

			if(Math.pow(centery - yi, 2) + Math.pow(centerx - xi, 2)
					>Math.pow(xi - endx,2) + Math.pow(yi - endy, 2)) {
				moving = false;
				setCenterX(endx);
				setCenterY(endy);
				xi = endx;
				yi = endy;
				ti = SystemClock.elapsedRealtime();
			}
		}
	}

	public void follow(){
		if(endx != followunit.centerx || endy != followunit.centery){
			initiateMovement(followunit.centerx, followunit.centery);
		}
	}
	
	public void followRotate(){
		if(rotating){
			deltaTime();
			float dr;
			dr = (float)(rotatespeed*dt);
			if(dr>dri){
				rotating = false;
				setCenterR(endr);
				ri = endr;
			}else{
				if(endr<ri) dr *= -1;
				if(Math.abs(endr - ri)>180f) dr *= -1;

				setCenterR(ri + dr);
			}
		}
	}
	
	public void followMove(){
		if(moving){
			deltaTime();
			float dx, dy;

			try{
				dx = (float)(Math.cos(Math.atan(m))*speed*dt);
				dy = (float)(Math.sin(Math.atan(m))*speed*dt);
			}catch(Exception e){
				dx = 0;
				dy = (float)(speed*1f);
			}
			if(endy<yi) dy *= -1;
			if(endx<xi) dx *= -1;

			setCenterX(xi + dx);
			setCenterY(yi + dy);

			if(Math.pow(centery - yi, 2) + Math.pow(centerx - xi, 2)
			   >Math.pow(xi - endx,2) + Math.pow(yi - endy, 2)) {
				moving = false;
				setCenterX(endx);
				setCenterY(endy);
				xi = endx;
				yi = endy;
			}
		}
		follow();
	}
	
	public void deltaTime(){
		to = SystemClock.elapsedRealtime();
		dt = (to - ti);
	}
	
	public void initiateMove(){
		moving = true;
	}
	
	public void initiateRotate(){
		degrees = Math.atan(m)*180/Math.PI;
		if(endx<xi&&endy>yi||endx>xi&&endy<yi) degrees = 90. - degrees;
		if(endy<centery) {
			degrees += 180.;
			if(endx>centerx) degrees += 90.;
		}else{
			if(endx<centerx) degrees += 90.;
		}
		while(degrees>360) degrees -= 360;

		endr = (float)degrees;
		
		dri = Math.abs(endr - ri);
		if(dri>180f) dri = 360f - dri;

		rotating = true;
	}
	
	 public void initiateMovement(float endx, float endy){
		 setEndXY(endx, endy);
		 
		 ri = centerr;

		 xi = centerx;
		 yi = centery;
		 
		 slope(endx, endy);
		 
		 ti = SystemClock.elapsedRealtime();
		 
		 initiateRotate();
		 initiateMove();
	 }

	 public void initiateMove(float endx, float endy){
		 setEndXY(endx, endy);

		 xi = centerx;
		 yi = centery;
		 
		 slope(endx, endy);

		 ti = SystemClock.elapsedRealtime();

		 moving = true;
	 }

	 public void initiateRotate(float endx, float endy){
		 slope(endx, endy);
		 
		 degrees = Math.atan(m)*180/Math.PI;
		 if(endx<xi&&endy>yi||endx>xi&&endy<yi) degrees = 90. - degrees;
		 if(endy<centery) {
			 degrees += 180.;
			 if(endx>centerx) degrees += 90.;
		 }else{
			 if(endx<centerx) degrees += 90.;
		 }
		 while(degrees>360) degrees -= 360;

		 endr = (float)degrees;

		 ti = SystemClock.elapsedRealtime();
		 
		 ri = centerr;

		 dri = Math.abs(endr - ri);
		 if(dri>180f) dri = 360f - dri;

		 rotating = true;
	 }
	
	public void setFollow(BaseUnit unit){
		followuniton = true;
		followunit = unit;
	}
	
	public void slope (float x, float y){
		try{
			m = Math.abs((y - centery)/(x - centerx));
		}catch(Exception e){
			m = 1f;
		}
	}
	
	public void setRotationOffset(double rotationoffset){
		 this.rotationoffset = rotationoffset;
	}
	
	public Vector2 getCenterMove(){
		Vector2 v = new Vector2();
		v.x = centerx;
		v.y = centery;
		return v;
	}
	
	public void moveLine(Batch batch, Camera camera){
		Gdx.gl20.glLineWidth(5);
		shapeline.setProjectionMatrix(camera.combined);
		shapeline.begin(ShapeRenderer.ShapeType.Line);
		shapeline.setColor(linecolor);
		shapeline.line(centerx, centery
					   , endx, endy);
		shapeline.end();
	}

	public void setTexture(String name){
		texture = new Texture(Gdx.files.internal(name));
	}
	
	public void setTexture(Texture t){
		texture = t;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public void disposeTexture(){
		texture.dispose();
	}
	
	public void setUnitSize(int width, int height){
		w = width;
		h = height;
	}
	
	public void setUnitSize(int sides){
		w = sides;
		h = sides;
	}
	
	public void setVector(float x, float y){
		setCenterXY(x, y);
		
		xi = x;
		yi = y;
		
		endx = x;
		endy = y;
	}
	
	public void setVector(Vector2 v){
		setCenterXY(v.x, v.y);
		
		xi = centerx;
		yi = centery;
		
		endx = centerx;
		endy = centery;
	}

	public Vector2 getVector(){
		Vector2 v = new Vector2();
		v.add(centerx, centery);
		return v;
	}

	public void setBaseX(float x){
		basex = x;
		centerx = x + w/2;
	}

	public void setBaseY(float y){
		basey = y;
		centery = y + w/2;
	}

	public void setBaseXY(float x, float y){
		basex = x;
		centerx = x + w/2;

		basey = y;
		centery = y + w/2;
	}

	public void setCenterX(float x){
		centerx = x;
		basex = x - w/2;
	}

	public void setCenterY(float y){
		centery = y;
		basey = y - w/2;
	}

	public void setCenterXY(float x, float y){
		centerx = x;
		basex = x - w/2;

		centery = y;
		basey = y - w/2;
	}
	
	public void setEndXY(float endx, float endy){
		 this.endx = endx;
		 this.endy = endy;
	}

	public void setBaseR(float rotation){
		baser = rotation;
		centerr = rotation - (float)rotationoffset;
	}

	public void setCenterR(float rotation){
		centerr = rotation;
		baser = rotation + (float) rotationoffset;
	}

	public float getCenterX(){
		return centerx;
	}

	public float getCenterY(){
		return centery;
	}
	
	public float getBaseX(){
		return basex;
	}
	
	public float getBaseY(){
		return basey;
	}
	
	public float getCenterR(){
		return centerr;
	}
	
	public void setRotateSpeed(double duration){
		 rotatespeed = duration;
	}
	
	public double getRotateSpeed(){
		return rotatespeed;
	}
	
	public float getWidth(){
		return w;
	}
	
	public float getHeight(){
		return h;
	}
		
	public void setHealth(Double h){
		health = h;
		
		this.setHealthPercent(health / healthcap * 100.);
	}
	
	public Double getHealth(){
		return health;
	}

	public void setHealthCap(Double h){
		healthcap = h;
	}

	public Double getHealthCap(){
		return healthcap;
	}

	public void setHealthPercent(Double h){
		healthpercent = h;
		
		if(h <= 0.){
			deadHealth();
			setAlive(false);
		}else if(h < 1./3.*100.){
			lowHealth();
		}else if(h < 2./3.*100.){
			mediumHealth();
		}else{
			fullHealth();
		}
	}

	public Double getHealthPercent(){
		return healthpercent;
	}

	public void setDamage(Double a){
		 damage = a;
	}
	
	public Double getDamage(){
		return  damage;
	}

	public void setAttackSpeed(int a){
		 attackspeed = a;
	}
	
	public int getAttackSpeed(){
		 return attackspeed;
	}

	public void setAttackRange(Double r){
		attackrange = r;
	}
	
	public Double getAttackRange(){
		return attackrange;
	}

	public void setDamageRange(Double d){
		 damagerange = d;
	}
	
	public Double getDamageRange(){
		 return damagerange;
	}
	
	public void setSpeed(Double s){
		 speed = s;
	}
	
	public Double getSpeed(){
		return speed;
	}
	
	public void setAllegiance(Allegiance a){
		 allegiance = a;
	}
	
	public Allegiance getAllegiance(){
		return allegiance;
	}
	
	public void setAlive(Boolean b){
		 alive = b;
		 if(!b){
			 health = 0.;
			 healthpercent = 0.;
			 
			 if(dieng!=null){
				 dieng.animate(0);
				 texture = dieng.sprites.get(dieng.sprites.size()-1);
			 }
			 
			 setEndXY(centerx, centery);
			 
			 if(lifelisteneron){
				 lifelistener.onDeath();
			 }
		 }
	}
	
	public Boolean getAlive(){
		return alive;
	}
	
	public void setAttackCycle(Boolean a){
		attackcycle = a;
	}
	
	public Boolean getAttackCycle(){
		return attackcycle;
	}
	
	public void fullHealth(){
		
	}
	
	public void mediumHealth(){
		
	}
	
	public void lowHealth(){
		
	}
	
	public void deadHealth(){
		
	}

	public void setLifeListener(LifeListener listener){
		lifelisteneron = true;
		this.lifelistener = listener;
	}

	public void addFireAnimation(String filename, String extension){
		fire = new Fire(filename, extension);
		animatelist.add(fire);
	}

	public void addDiengAnimation(String filename, String extension){
		dieng = new Dieng(filename, extension);
		animatelist.add(dieng);
	}
	
	public void addAnimation(String filename, String extension){
		animation = new Animate(filename, extension);
		animatelist.add(animation);
	}

	public void addActionItem(String filename, String extension){
		action = new ActionItem(filename, extension, this);
	}
}
