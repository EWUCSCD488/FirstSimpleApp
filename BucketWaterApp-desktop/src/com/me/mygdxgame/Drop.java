package com.me.mygdxgame;



import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop implements ApplicationListener {
	
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	SpriteBatch batch;
	Rectangle bucket;
	Array<Rectangle> raindrops;
	long lastDropTime;
	
	@Override
	public void create() {
		
		//load the images for droplet and bucket, 64x64 pixels each
		dropImage =  new Texture(Gdx.files.internal(Constants.IMAGE_DROPLET));			// internal files is located in assets folder
		bucketImage = new Texture(Gdx.files.internal(Constants.IMAGE_BUCKET));
		
		// load the drop sound effect
		dropSound = Gdx.audio.newSound(Gdx.files.internal(Constants.SOUND_WATERDROP));		// Sound is stored in memory 
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.SOUND_RAIN));			// music is stored in internal and stream from it
		
		// start the backback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();
		
		// create camera and SpriteBatch class to draw objects to screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Constants.WIDTH_GAME, Constants.HEIGHT_GAME);
		
		batch = new SpriteBatch();
		
		//draw bucket
		bucket = new Rectangle();
		bucket.x = Constants.WIDTH_GAME /2 - 64/2;	// centered horizontally
		bucket.y = 20;								// 20 pixels from the bottom 		, 	be default, y is counting upward
		bucket.width = 64;
		bucket.height = 64;
		
		//initialize raindrop array
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
		
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);					// set color to blue
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);			// clear the screen
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);			// camera.combined is a matrix which is passed into SpriteBatch
															// from now on, SpriteBatch will render everything in coordinate system
		
		if(Gdx.input.isTouched()){				// as if screen is currently touch
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64/2;
		}
		
		// constantly update drops
		if(TimeUtils.nanoTime() - lastDropTime > 100000000)
			spawnRaindrop();
		
		
		
		//render raindrop
		batch.begin();										// start new batch , it will collect all drawings from begin to end and submit at once
		batch.draw(bucketImage,bucket.x,bucket.y);			// draw bucket to batch
		
		Iterator<Rectangle> iter = raindrops.iterator(); // what is this
		while(iter.hasNext()){
			Rectangle raindrop = iter.next();
			raindrop.y -= 600 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 <0) iter.remove();
			
			batch.draw(dropImage,raindrop.x,raindrop.y);
			if(raindrop.overlaps(bucket)){
				dropSound.play();
				iter.remove();
			}
		}
		
		
		//for(Rectangle raindrop: raindrops){
			
		//}
		batch.end();										// end batch, write all drawings to screen
		
		
		
	}
	
	private void spawnRaindrop() {
	      Rectangle raindrop = new Rectangle();
	      raindrop.x = MathUtils.random(0, 800-64);
	      raindrop.y = 480;
	      raindrop.width = 64;
	      raindrop.height = 64;
	      raindrops.add(raindrop);
	      lastDropTime = TimeUtils.nanoTime();
	   }
	
	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}

}
