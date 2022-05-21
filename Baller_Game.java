import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Baller_Game {

	JFrame frame;
	DrawPanel drawPanel;

	public static void main(String... args) {
		new Baller_Game().go();
	}

	private void go() {
		frame = new JFrame("Baller Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(width, height + uiHeight));
		frame.setMinimumSize(new Dimension(900, 400));
		frame.setBackground(new Color(0, 200, 210));
		frame.pack();
		drawPanel = new DrawPanel();

		frame.getContentPane().add(BorderLayout.CENTER, drawPanel);

		frame.setResizable(true);
		frame.setLocationByPlatform(true);

		frame.setVisible(true);
	}

	// 36 vars which control the game
	public boolean gameStarted = false;
	public int maxScore = 7;
	public int hueShift = 0;
	public double rot = 0; // just for fun
	public boolean motionBlur = true; // may or may not be laggy

	public int width = 900; // width and height of area
	public int height = 600;
	public int margin = 80; // distance between edge and playfield
	public int uiHeight = 40; // height of ui
	public int size = 60; // size of balls

	public double[] bluePos = { margin * 3 - size / 2, height / 2 - size / 2 };
	public double[] blueVel = { 0, 0 };
	public int[][] blueTrail = new int[5][2];
	{ // i literally have no idea what this syntax means
		for (int[] i : blueTrail) { // just loop through the elements of the array lol why use indices when you
									// don’t need the number
			i[0] = (int) bluePos[0];
			i[1] = (int) bluePos[1];
		}
	}
	public boolean blueAlive = true;
	public int blueDied = 100;
	public int bluePoints = 0;
	public double[] redPos = { width - margin * 3 - size / 2, height / 2 - size / 2 };
	public double[] redVel = { 0, 0 };
	public int[][] redTrail = new int[5][2];
	{
		for (int[] i : redTrail) {
			i[0] = (int) redPos[0];
			i[1] = (int) redPos[1];
		}
	}
	public boolean redAlive = true;
	public int redDied = 100;
	public int redPoints = 0;

	public double accel = 0.8;
	public double maxSpeed = 15;

	public Color blueColor1 = Color.BLUE;
	public Color blueColor2 = new Color(0, 0, 255, 100);
	public Color redColor1 = Color.RED;
	public Color redColor2 = new Color(255, 0, 0, 100);

	public boolean UpButton = false;
	public boolean DownButton = false;
	public boolean RightButton = false;
	public boolean LeftButton = false;

	public boolean UpArrow = false;
	public boolean DownArrow = false;
	public boolean RightArrow = false;
	public boolean LeftArrow = false;

	class DrawPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public DrawPanel() {
			KeyListener listener = new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					// System.out.println("keyPressed=" + KeyEvent.getKeyText(e.getKeyCode()));
					int key = e.getKeyCode();

					if (key == 48) {
						System.exit(0);
					}
					if (key == 105) {
						rot += 0.017;
					} else if (key == 104) {
						rot -= 0.017;
					}
					if (key == KeyEvent.VK_W) {
						UpButton = true;
						DownButton = false;
					} else if (key == KeyEvent.VK_S) {
						UpButton = false;
						DownButton = true;
					}
					if (key == KeyEvent.VK_D) {
						RightButton = true;
						LeftButton = false;
					} else if (key == KeyEvent.VK_A) {
						RightButton = false;
						LeftButton = true;
					}

					if (key == KeyEvent.VK_UP) {
						UpArrow = true;
						DownArrow = false;
					} else if (key == KeyEvent.VK_DOWN) {
						UpArrow = false;
						DownArrow = true;
					}
					if (key == KeyEvent.VK_RIGHT) {
						RightArrow = true;
						LeftArrow = false;
					} else if (key == KeyEvent.VK_LEFT) {
						RightArrow = false;
						LeftArrow = true;
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					int key = e.getKeyCode();
					if (key == KeyEvent.VK_SPACE && !gameStarted) {
						gameStarted = true;
						bluePoints = 0;
						redPoints = 0;
					}

					if (key == KeyEvent.VK_W) {
						UpButton = false;
					} else if (key == KeyEvent.VK_S) {
						DownButton = false;
					}
					if (key == KeyEvent.VK_D) {
						RightButton = false;
					} else if (key == KeyEvent.VK_A) {
						LeftButton = false;
					}

					if (key == KeyEvent.VK_UP) {
						UpArrow = false;
					} else if (key == KeyEvent.VK_DOWN) {
						DownArrow = false;
					}
					if (key == KeyEvent.VK_RIGHT) {
						RightArrow = false;
					} else if (key == KeyEvent.VK_LEFT) {
						LeftArrow = false;
					}
				}
			};
			addKeyListener(listener);
			setFocusable(true);
		};

		public void paintComponent(Graphics g) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// rescales the player when the screen size changes
			int newWidth = frame.getWidth();
			int newHeight = frame.getHeight();
			if (width != newWidth) {
				bluePos[0] *= (double) newWidth / width;
				redPos[0] *= (double) newWidth / width;
				width = newWidth;
			}
			if (height + uiHeight != newHeight) {
				bluePos[1] *= (double) newHeight / (height + uiHeight);
				redPos[1] *= (double) newHeight / (height + uiHeight);
				height = newHeight - uiHeight;
			}
			frame.repaint();
			drawUi(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(width / 2, (height + uiHeight) / 2);
			g2d.rotate(rot); // rotation, just for fun (to remove, maybe)
			g2d.translate(-width / 2, -((height + uiHeight) / 2));
			if (!gameStarted) {
				// here because rishi wanted a main menu
				g2d.setFont(new Font("Helvetica", Font.BOLD, 60));
				FontMetrics fm = g2d.getFontMetrics(); // i use fontmetrics a lot because it gives the width and height
														// of text
				String title;
				if (bluePoints == maxScore || redPoints == maxScore) {
					// end screen
					// ternary operator supremacy
					title = bluePoints == maxScore ? "Blue Wins" : "Red Wins";
				} else {
					// main menu
					title = "Baller Game";
				}
				g2d.setColor(Color.BLACK);
				g2d.drawString(title, (width - fm.stringWidth(title)) / 2 + 2, height / 2 + 2); // the drop shadow
				if (bluePoints == maxScore || redPoints == maxScore) {
					g2d.setColor(bluePoints == maxScore ? blueColor1 : redColor1);
				} else {
					// when the decimal point is floating
					int cols = 20;
					float[] fractions = new float[cols];
					Color[] colors = new Color[cols];
					for (int i = 0; i < colors.length; i++) {
						fractions[i] = (float) i / cols;
						float hue = fractions[i];
						colors[i] = Color.getHSBColor((hue + hueShift) * 100 % 101 / 100, 0.8f, 1);
					}
					// this line is too long
					g2d.setPaint(new LinearGradientPaint((width - fm.stringWidth(title)) / 2, height / 2,
							(width - fm.stringWidth(title)) / 2 + fm.stringWidth(title), height / 2 + fm.getHeight(),
							fractions, colors));
					hueShift++;
				}
				g2d.drawString(title, (width - fm.stringWidth(title)) / 2, height / 2);
				g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, 40));
				fm = g2d.getFontMetrics();
				g2d.setColor(Color.BLACK); // for some reason i cant make this text rainbow so its black now
				String start = "Press SPACE to start";
				g2d.drawString(start, (width - fm.stringWidth(start)) / 2, height / 2 + 60);
			} else {
				if (bluePoints == maxScore || redPoints == maxScore) {
					// resets all vars
					bluePos[0] = margin * 3 - size / 2;
					bluePos[1] = height / 2 - size / 2;
					blueVel = new double[2];
					blueAlive = true;
					blueDied = 100;

					redPos[0] = width - margin * 3 - size / 2;
					redPos[1] = height / 2 - size / 2;
					redVel = new double[2];
					redAlive = true;
					redDied = 100;

					if (motionBlur) {
						for (int[] i : blueTrail) {
							i[0] = (int) bluePos[0];
							i[1] = (int) bluePos[1];
						}
						for (int[] i : redTrail) {
							i[0] = (int) redPos[0];
							i[1] = (int) redPos[1];
						}
					}

					gameStarted = false;
					return; // removes the need for an extra indent; idk why i only used this once
				}
				getInputs();

				g.setColor(Color.WHITE);
				g.fillRoundRect(margin, margin + uiHeight, width - margin * 2, height - uiHeight - margin * 2, 40, 40);
				g.setColor(Color.BLACK);
				g.drawRoundRect(margin, margin + uiHeight, width - margin * 2, height - uiHeight - margin * 2, 40, 40);

				clamp(blueVel, maxSpeed, blueAlive);
				clamp(redVel, maxSpeed, redAlive);

				updatePos();
				if (!blueAlive) {
					blueDied--; // animates the scale of the ball
					if (blueDied == 0) { // end of death animation
						redPoints++;
						bluePos[0] = margin * 3 + size / 2;
						bluePos[1] = height / 2 - size / 2;
						blueVel = new double[2];
						blueAlive = true;
						blueDied = 100;
						for (int[] i : blueTrail) {
							i[0] = (int) bluePos[0];
							i[1] = (int) bluePos[1];
						}
					}
				}
				if (!redAlive) {
					redDied--;
					if (redDied == 0) {
						bluePoints++;
						redPos[0] = width - margin * 3 - size / 2;
						redPos[1] = height / 2 - size / 2;
						redVel = new double[2];
						redAlive = true;
						redDied = 100;
						for (int[] i : redTrail) {
							i[0] = (int) redPos[0];
							i[1] = (int) redPos[1];
						}
					}
				}
				drawPlayers(g);
			}
		}

		public void getInputs() {
			if (blueAlive) {
				if (UpButton) {
					blueVel[1] -= accel;
				}
				if (DownButton) {
					blueVel[1] += accel;
				}
				if (LeftButton) {
					blueVel[0] -= accel;
				}
				if (RightButton) {
					blueVel[0] += accel;
				}
			}
			if (redAlive) {
				if (UpArrow) {
					redVel[1] -= accel;
				}
				if (DownArrow) {
					redVel[1] += accel;
				}
				if (LeftArrow) {
					redVel[0] -= accel;
				}
				if (RightArrow) {
					redVel[0] += accel;
				}
			}
		}

		public void updatePos() {
			// moves balls
			bluePos[0] += blueVel[0];
			bluePos[1] += blueVel[1];
			redPos[0] += redVel[0];
			redPos[1] += redVel[1];
			// doWallCollision(); // in own method so easy to comment out

			double distance = Math.hypot(redPos[0] - bluePos[0], redPos[1] - bluePos[1]);
			if (distance <= size) { // circle-circle collision checks are the easiest
				// all of this code works via magic
				double redSpeed = Math.sqrt(redVel[0] * redVel[0] + redVel[1] * redVel[1]);
				double blueSpeed = Math.sqrt(blueVel[0] * blueVel[0] + blueVel[1] * blueVel[1]);
				// obeying conservation of momentum; swaps ints with some math
				redVel[0] += blueVel[0];
				blueVel[0] = redVel[0] - blueVel[0];
				redVel[0] -= blueVel[0];
				redVel[1] += blueVel[1];
				blueVel[1] = redVel[1] - blueVel[1];
				redVel[1] -= blueVel[1];
				// dont ask me what this does (you wanna move the balls away from each other
				// right)
				redVel[0] += (redPos[0] - bluePos[0]) * Math.sqrt(redSpeed) * 0.05;
				redVel[1] += (redPos[1] - bluePos[1]) * Math.sqrt(redSpeed) * 0.05;
				blueVel[0] += (bluePos[0] - redPos[0]) * Math.sqrt(blueSpeed) * 0.05;
				blueVel[1] += (bluePos[1] - redPos[1]) * Math.sqrt(blueSpeed) * 0.05;
			}
			// if i wasnt good at math i would be screwed rn
			if (!(bluePos[0] + size / 2 > margin
					&& bluePos[0] + size / 2 < width - margin
					&& bluePos[1] + size / 2 > margin
					&& bluePos[1] + size / 2 < height - margin - uiHeight)
					&& blueAlive) {
				blueAlive = false;
			}
			if (!(redPos[0] + size / 2 > margin
					&& redPos[0] + size / 2 < width - margin
					&& redPos[1] + size / 2 > margin
					&& redPos[1] + size / 2 < height - margin - uiHeight)
					&& redAlive) {
				redAlive = false;
			}
		}

		public void doWallCollision() {
			// probably not necessary because the "walls" are in the water anyways
			if (bluePos[0] <= 0) {
				blueVel[0] *= -1;
				bluePos[0] = 0;
			} else if (bluePos[0] >= width - size) {
				blueVel[0] *= -1;
				bluePos[0] = width - size;
			}
			if (bluePos[1] <= 0) {
				blueVel[1] *= -1;
				bluePos[1] = 0;
			} else if (bluePos[1] >= height - size) {
				blueVel[1] *= -1;
				bluePos[1] = height - size;
			}
			if (redPos[0] <= 0) {
				redVel[0] *= -1;
				redPos[0] = 0;
			} else if (redPos[0] >= width - size) {
				redVel[0] *= -1;
				redPos[0] = width - size;
			}
			if (redPos[1] <= 0) {
				redVel[1] *= -1;
				redPos[1] = 0;
			} else if (redPos[1] >= height - size) {
				redVel[1] *= -1;
				redPos[1] = height - size;
			}
		}

		public void drawUi(Graphics g) {
			// draws ui; theres too much thinking to make sure the stuff is actually
			// centered properly
			g.setColor(new Color(255, 100, 180));
			g.fillRect(0, 0, width, uiHeight); // top bar
			g.setColor(Color.BLACK);
			for (int i = 0; i < maxScore; i++) {
				g.drawOval(i * 40 + margin, uiHeight / 2 - 10, 20, 20);
				g.drawOval(width - (i * 40 + margin + 20), uiHeight / 2 - 10, 20, 20);
			}
			g.setColor(blueColor1);
			for (int i = 0; i < bluePoints; i++) {
				g.fillOval(i * 40 + margin, uiHeight / 2 - 10, 20, 20);
			}
			g.setColor(redColor1);
			for (int i = 0; i < redPoints; i++) {
				g.fillOval(width - (i * 40 + margin + 20), uiHeight / 2 - 10, 20, 20);
			}
			// WARNING: EVEN MORE MATH
			g.setColor(Color.BLACK);
			g.setFont(new Font("Helvetica", Font.BOLD, 25));
			FontMetrics fm = g.getFontMetrics();
			String str = Integer.toString(bluePoints);
			g.drawString(str, margin / 2 - fm.stringWidth(str) / 2, ((uiHeight - fm.getHeight()) / 2) + fm.getAscent());

			str = Integer.toString(redPoints);
			g.drawString(str, width - (margin / 2 + fm.stringWidth(str)),
					((uiHeight - fm.getHeight()) / 2) + fm.getAscent());

			g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
			fm = g.getFontMetrics();
			g.setColor(Color.BLACK);
			String cr = "© Daniel™ 2021";
			g.drawString(cr, (width - fm.stringWidth(cr)) / 2, ((uiHeight - fm.getHeight()) / 2) + fm.getAscent());
		}

		public void drawPlayers(Graphics g) {
			if (motionBlur) {
				drawTrail(g);
			}
			// more math yay - scales the player when they are dead
			g.setColor(blueColor1);
			g.drawOval((int) (bluePos[0] + size * (100 - blueDied) / 200.0),
					(int) (bluePos[1] + size * (100 - blueDied) / 200.0) + uiHeight, (int) (size * blueDied / 100.0),
					(int) (size * blueDied / 100.0));
			g.setColor(blueColor2);
			g.fillOval((int) (bluePos[0] + size * (100 - blueDied) / 200.0),
					(int) (bluePos[1] + size * (100 - blueDied) / 200.0) + uiHeight, (int) (size * blueDied / 100.0),
					(int) (size * blueDied / 100.0));
			g.setColor(redColor1);
			g.drawOval((int) (redPos[0] + size * (100 - redDied) / 200.0),
					(int) (redPos[1] + size * (100 - redDied) / 200.0) + uiHeight, (int) (size * redDied / 100.0),
					(int) (size * redDied / 100.0));
			g.setColor(redColor2);
			g.fillOval((int) (redPos[0] + size * (100 - redDied) / 200.0),
					(int) (redPos[1] + size * (100 - redDied) / 200.0) + uiHeight, (int) (size * redDied / 100.0),
					(int) (size * redDied / 100.0));
		}

		public void drawTrail(Graphics g) {
			// self-explanatory
			int i;
			if (blueAlive) {
				shift(blueTrail);
				g.setColor(new Color(0, 0, 200, 15));
				for (i = 0; i < blueTrail.length - 1; i++) {
					int[] pos = blueTrail[i];
					g.fillOval(pos[0], pos[1] + uiHeight, size, size);
				}
				blueTrail[i][0] = (int) bluePos[0];
				blueTrail[i][1] = (int) bluePos[1];
			}
			if (redAlive) {
				shift(redTrail);
				g.setColor(new Color(200, 0, 0, 15));
				for (i = 0; i < redTrail.length - 1; i++) {
					int[] pos = redTrail[i];
					g.fillOval(pos[0], pos[1] + uiHeight, size, size);
				}
				redTrail[i][0] = (int) redPos[0];
				redTrail[i][1] = (int) redPos[1];
			}
		}
	}

	public static void clamp(double[] arr, double num, boolean alive) {
		// makes sure the values dont exceed maxSpeed
		// idk why i made this static
		for (int i = 0; i < 2; i++) {
			double val = arr[i];
			if (val > num) {
				val = num;
			} else if (val < -num) {
				val = -num;
			}
			arr[i] = val * (alive ? 0.95 : 0.8); // friction wheee
		}
	}

	public static void shift(int[][] arr) {
		// moves all elements in the array left 1
		for (int i = 0; i < arr.length - 1; i++) {
			arr[i] = arr[i + 1];
		}
		arr[arr.length - 1] = new int[2];
	}
}
