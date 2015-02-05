package spaceman;

import java.awt.geom.Rectangle2D.Float;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.*;
//import org.lwjgl.util.vector.Vector2f;

import static org.lwjgl.opengl.GL11.*;

public class Main {
	// position variables
	public static float camerax = 0;
	public static float cameray = 0;
	public static Float player = new Float(32, 32, 32, 32);
	public static float xvel = 0;
	public static float yvel = 0;
	public static boolean onGround = false;

	// keys
	public static boolean left = false;
	public static boolean right = false;
	public static boolean up = false;
	public static boolean down = false;

	// mouse
	public static int mousex = 0;
	public static int mousey = 0;
	public static int mousedx = 0;
	public static int mousedy = 0;
	public static boolean leftClick = false;
	public static boolean rightClick = false;

	// game states
	public static final int main_menu = 0;
	public static final int game = 1;
	public static int game_state = main_menu;

	// runtime variables
	public static boolean running = true;

	// temporary level
	public static String[] level = {
			"PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP",
			"P                                          P",
			"P                                          P",
			"P                                          P",
			"P                    PPPPPPPPPPP           P",
			"P                                          P",
			"P                                          P",
			"P                                          P",
			"P    PPPPPPPP                              P",
			"P                                          P",
			"P                          PPPPPPP         P",
			"P                 PPPPPP                   P",
			"P                                          P",
			"P         PPPPPPP                          P",
			"P                                          P",
			"P                     PPPPPP               P",
			"P                                          P",
			"P   PPPPPPPPPPP                            P",
			"P                                         EP",
			"P                 PPPPPPPPPPP              P",
			"P                                          P",
			"P                                          P",
			"P                                          P",
			"P                                          P",
			"PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP"};
	public static Float[] levelcoords = new Float[202];
	public static int[] blocktype = new int[202];

	public static void init() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setTitle("Spaceman");
			Display.create();
		} catch (LWJGLException e) {
			System.out.println("Failed to create display.");
			e.printStackTrace();
			System.exit(1);
		}

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 600, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void loadLevel() {
		int x = 0;
		int y = 0;
		int count = 0;
		for (String s : level) {
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == 'P') {
					levelcoords[count] = new Float(x, y, 32, 32);
					blocktype[count] = 0;
					count += 1;
				} else if (s.charAt(i) == 'E') {
					levelcoords[count] = new Float(x, y, 32, 32);
					blocktype[count] = 1;
					count += 1;
				}
				x += 32;
			}
			x = 0;
			y += 32;
		}
	}

	public static void DrawLevel() {
		int count = 0;
		for (Float v : levelcoords) {
			if (blocktype[count] == 0) {
				glColor4f(0.8f, 0.8f, 0.8f, 1);
				glBegin(GL_QUADS);
				glVertex2f(v.x - camerax, v.y - cameray);
				glVertex2f(v.x + 32 - camerax, v.y - cameray);
				glVertex2f(v.x + 32 - camerax, v.y + 32 - cameray);
				glVertex2f(v.x - camerax, v.y + 32 - cameray);
				glEnd();
			} else if (blocktype[count] == 1) {
				glColor4f(1, 0.3f, 0.3f, 1);
				glBegin(GL_QUADS);
				glVertex2f(v.x - camerax, v.y - cameray);
				glVertex2f(v.x + 32 - camerax, v.y - cameray);
				glVertex2f(v.x + 32 - camerax, v.y + 32 - cameray);
				glVertex2f(v.x - camerax, v.y + 32 - cameray);
				glEnd();
			}

			count += 1;
		}
	}

	public static void DrawMainMenu() {
		// background
		glColor4f(1, 0, 0, 1);
		glBegin(GL_QUADS);
			glVertex2f(0, 0);
			glVertex2f(800, 0);
			glVertex2f(800, 600);
			glVertex2f(0, 600);
		glEnd();

		// buttons
		glColor4f(0, 1, 1, 1);
		glBegin(GL_QUADS);
			glVertex2f(100, 200);
			glVertex2f(400, 200);
			glVertex2f(400, 300);
			glVertex2f(100, 300);
		glEnd();
		glColor4f(1, 1, 1, 1);

		if (leftClick && mousex > 100 && mousex < 400 && mousey > 200 && mousey < 300) {
			game_state = game;
		}
	}

	public static void Camera() {
		if (left) {
			xvel = -8;
		}

		if (right) {
			xvel = 8;
		}

		if ((!left && !right) || (left && right)) {
			xvel = 0;
		}

		if (up) {
			if (onGround) {
				yvel -= 10;
			}
		}

		if (!onGround) {
			yvel += 0.3f;
			if (yvel > 100) {
				yvel = 100;
			}
		}

		player.x += xvel;

		int count = 0;
		for (Float v : levelcoords) {
			if (player.intersects(v)) {
				if (blocktype[count] == 1) {
					running = false;
				}

				if (xvel > 0) {
					player.x = v.x - 32;
				}

				if (xvel < 0) {
					player.x = v.x + 32;
				}
			}
			count += 1;
		}

		player.y += yvel;
		onGround = false;

		count = 0;
		for (Float v : levelcoords) {
			if (player.intersects(v)) {
				if (blocktype[count] == 1) {
					running = false;
				}

				if (yvel > 0) {
					player.y = v.y - 32;
					onGround = true;
					yvel = 0;
				}
		
				if (yvel < 0) {
					player.y = v.y + 32;
				}
			}
			count += 1;
		}

		camerax = player.x - 384;
		cameray = player.y - 284;

		if (camerax < 0) {
			camerax = 0;
		}

		if (camerax > 608) {
			camerax = 608;
		}

		if (cameray < 0) {
			cameray = 0;
		}

		if (cameray > 200) {
			cameray = 200;
		}
	}

	public static void Render() {
		glClear(GL_COLOR_BUFFER_BIT);
		switch (game_state) {
		case main_menu:
			DrawMainMenu();
			break;
		case game:
			Camera();
			DrawLevel();
			glColor4f(0, 0, 1, 1);
			glBegin(GL_QUADS);
			glVertex2f(player.x - camerax, player.y - cameray);
			glVertex2f(player.x + 32 - camerax, player.y - cameray);
			glVertex2f(player.x + 32 - camerax, player.y + 32 - cameray);
			glVertex2f(player.x - camerax, player.y + 32 - cameray);
			glEnd();
			glColor4f(1, 1, 1, 1);
			break;
		}
	}

	public static void CheckEvents() {
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			// move player left
			left = true;
		} else {
			left = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			// move player right
			right = true;
		} else {
			right = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			// jump
			up = true;
		} else {
			up = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
			System.out.printf("%f, %f\n", player.x, player.y);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			running = false;
		}

		mousex = Mouse.getX();
		mousey = 600 - Mouse.getY() - 1;
		mousedx = Mouse.getDX();
		mousedy = -Mouse.getDY();

		if (Mouse.isButtonDown(0)) {
			leftClick = true;
		}

		if (Display.isCloseRequested()) {
			running = false;
		}
	}

	public static void main(String[] args) {
		init();
		loadLevel();

		while (running) {
			CheckEvents();
			Render();

			Display.update();
			Display.sync(60);
		}

		Display.destroy();
		System.exit(0);
	}
}
