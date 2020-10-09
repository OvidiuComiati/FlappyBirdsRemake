package com.comiati.flappy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	//unintialized variables that have to be used in multiple methods
	SpriteBatch batch;
	Texture background;
	Texture gameOver;
	Texture topObstacle;
	Texture bottomObstacle;
	Texture[] positions;
	Circle birdCircle;
	BitmapFont font;
	float maxObstacleOffset;
	Random randomGenerator;

	//variables
	int flapState = 0;
	float birdYPosition = 0;
	float velocity = 0;
	int score = 0;
	int scoringTube = 0;
	int gameState = 0;// not started, 1 for started , 2 for

	float[] tubeX = new float[4];
	float[] tubeOffset = new float[4];
	float distanceBetweenTubes;

	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.png");
		gameOver = new Texture("gameover.png");
		topObstacle = new Texture("topobstacle.png");
		bottomObstacle = new Texture("bottomobstacle.png");

		//define the 2 positions for the bird
		positions = new Texture[2];
		positions[0] = new Texture("birdup.png");
		positions[1] = new Texture("birddown.png");

		birdCircle = new Circle();

		//scoring styles
		font = new BitmapFont();
		font.setColor(Color.valueOf("#FFFFFF"));
		font.getData().setScale(12);

		maxObstacleOffset = Gdx.graphics.getHeight() / 2 - 300;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;
		topTubeRectangles = new Rectangle[4];
		bottomTubeRectangles = new Rectangle[4];

		startGame();

	}
	public void startGame() {
		birdYPosition = Gdx.graphics.getHeight() / 2 - positions[0].getHeight() / 2;
		for (int i = 0; i < 4; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 600);
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topObstacle.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if (gameState == 1) {//
			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
				score++;
				Gdx.app.log("Score", String.valueOf(score));
				if (scoringTube < 4 - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}
			if (Gdx.input.justTouched()) {
				velocity = -30; //a shot upwards at first, since it will be subtracted bellow(thus added)
			}
			for (int i = 0; i < 4; i++) {
				if (tubeX[i] < - topObstacle.getWidth()) {
					tubeX[i] += 4 * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 600);
				} else {
					tubeX[i] = tubeX[i] - 4;
				}
				batch.draw(topObstacle, tubeX[i], Gdx.graphics.getHeight() / 2 + 200 + tubeOffset[i]);
				batch.draw(bottomObstacle, tubeX[i], Gdx.graphics.getHeight() / 2 - 200 - bottomObstacle.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + 200 / 2 + tubeOffset[i], topObstacle.getWidth(), topObstacle.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - 200 / 2 - bottomObstacle.getHeight() + tubeOffset[i], bottomObstacle.getWidth(), bottomObstacle.getHeight());
			}
			if (gameState != 0) {//!!!
				velocity = velocity + 2f; // gravity pulls faster down
				birdYPosition -= velocity;
			} else {
				gameState = 2;
			}
		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
			if (Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}

		batch.draw(positions[flapState], Gdx.graphics.getWidth() / 2 - positions[flapState].getWidth() / 2, birdYPosition);
		font.draw(batch, String.valueOf(score), 100, 200);
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdYPosition + positions[flapState].getHeight() / 2, positions[flapState].getWidth() / 2);

		for (int i = 0; i < 4; i++) {
			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
				gameState = 2;
			}
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
