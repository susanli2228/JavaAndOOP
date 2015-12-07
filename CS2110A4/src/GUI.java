/* Time spent on a4: 5 hours and 52 minutes.
 * Name: Susan Li
 * Netid: SZL5
 * What I thought about this assignment:
 * It certainly was a challenge with so many classes and new data 
 * structures and so much code to read.
 * However, it was also fun to have a visualization of the code working.
 */

import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** The class for the main program. */
public class GUI extends JFrame {
	// NOTE:
	// You can adjust the values of WIDTH and HEIGHT to make the canvas
	// fit well your computer screen.

	private static final long serialVersionUID = 7926807064448718426L;

	private static int WIDTH = 800; // Width of the canvas.
	private static int HEIGHT = 650; // Height of the canvas

	JPanel container;
	Canvas canvas;
	private String[] bmpFileNames; // array of image file names

	class BmpFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith(".bmp");
		}
	}

	/** Constructor: an instance that has the GUI */
	public GUI() {
		// Find all bmps that are in current directory.
		File imageDir = new File("images");
		bmpFileNames = imageDir.list(new BmpFilter());

		if (bmpFileNames.length == 0)
			throw new RuntimeException("No image files in images directory.");

		System.out.println("Bmp File Names:");
		for (String f : bmpFileNames)
			System.out.println(f);

		// Initialize JFrame settings.
		setTitle("Collision Detector");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		container = new JPanel();

		canvas = new Canvas(this, WIDTH, HEIGHT);
		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		container.add(canvas);

		ButtonPanel b = new ButtonPanel(canvas, bmpFileNames);
		container.add(b);

		add(container);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/** Main program. Args are not used. */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}
		});
	}

}
