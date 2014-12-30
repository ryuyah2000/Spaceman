package spaceman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.*;
import org.lwjgl.input.*;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import static org.lwjgl.opengl.GL11.*;

public class Main {
	// define some variables
	public static boolean running = true;
	public static int fps = 60;
	public static int mousex = 0;
	public static int mousey = 0;
	public static int mousedx = 0;
	public static int mousedy = 0;

	// player stats
	public static int lives = 3;

	// camera variables
	public static Vector2f position = new Vector2f(400, 100);
	public static Vector2f camera = new Vector2f(0, 0);
	public static int left = Keyboard.KEY_LEFT;
	public static int right = Keyboard.KEY_RIGHT;
	public static int up = Keyboard.KEY_UP;
	public static int down = Keyboard.KEY_DOWN;
	public static double jumpNum = 0;
	public static boolean jump = false;
	public static float currentHeight = 0;

	// textures
	public static int playerTex = 0;

	// game states
	public static final int main_menu = 0;
	public static final int game = 1;
	public static int game_state = main_menu;

	public static void init() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 600, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public static int GetTexture(String path) {
		Texture tex;
		int texID = 0;
		try {
			tex = TextureLoader.getTexture("PNG", new FileInputStream(new File(path)));
			texID = tex.getTextureID();
			tex.release();
		} catch (FileNotFoundException e) {
			System.out.println("Failed to load texture " + path + " (File not found).");
			Display.destroy();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Failed to load texture " + path + " (IO Error).");
			Display.destroy();
			System.exit(1);
		}
		return texID;
	}

	public static void DrawPlayer() {
		glColor4f(0, 1, 1, 1);
		glRectf(position.x - 12.5f, position.y - 25, position.x + 12.5f, position.y);
		glColor4f(1, 1, 1, 1);
	}

	public static void Jump() {
		position.y = (float) Math.sin(jumpNum) * 100;
		if (jumpNum < 180) {
			jumpNum += 5;
		} else {
			position.y = currentHeight;
			jumpNum = 0;
		}
	}

	public static void CheckEvents() {
		if (Keyboard.isKeyDown(left)) {
			// move player left
			position.x += 3;
		}

		if (Keyboard.isKeyDown(right)) {
			// move player right
			position.x -= 3;
		}

		if (Keyboard.isKeyDown(up)) {
			// jump
			jump = true;
		} else {
			jump = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			running = false;
		}

		if (jump) {
			Jump();
		} else if (!jump && jumpNum != 0) {
			Jump();
		} else if (!jump && jumpNum <= 0) {
			position.y = currentHeight;
			jumpNum = 0;
		}

		mousex = Mouse.getX();
		mousey = Display.getHeight() - Mouse.getY() - 1;
		mousedx = Mouse.getDX();
		mousedy = -Mouse.getDY();

		if (Display.isCloseRequested()) {
			running = false;
		}
	}

	public static void Render() {
		switch (game_state) {
		case main_menu:
			break;
		case game:
			DrawPlayer();
			break;
		}
	}

	public static void main(String[] args) {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setTitle("Spaceman");
			Display.create();
		} catch (LWJGLException e) {
			System.out.println("Failed to create display.");
			e.printStackTrace();
			System.exit(1);
		}

		init();

		while (running) {
			glClear(GL_COLOR_BUFFER_BIT);
			CheckEvents();
			Render();

			Display.update();
			Display.sync(60);
		}

		Display.destroy();
		System.exit(0);
	}
}
