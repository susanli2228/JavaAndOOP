import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.vecmath.Point2d;

/**
 * An instance is a square block/pixel primitive for representing image-based
 * rigid objects and resolving contacts. Each Block is wrapped by a disk and
 * used for contact generation.
 *
 * @author Doug James, March 2007.
 */
public class Block {
	// Random generator for random colors.
	static Random gen = new Random(System.currentTimeMillis());

	private int row; // Image row index.
	private int col; // Image column index.

	private Color color; // Pixel color.

	protected double halfwidth; // Halfwidth of block (the block radius is
								// sqrt(2)*h).

	/** Body-frame position --- needed for contact processing. */
	protected Point2d position; // Center of the block (in the image coordinate
								// system)

	/**
	 * Constructor: an instance at position (i, j), with color c, position block
	 * center (in image coordinate system) center, and halfwidth hf
	 */
	Block(int i, int j, Color c, Point2d center, double hf) {
		row = i;
		col = j;
		color = c;
		position = center;
		halfwidth = hf;
	}

	/**
	 * Return true iff block b displaced by u overlaps block c displaced by v.
	 */
	public static boolean overlaps(Block b, Vector2D u, Block c, Vector2D v) {
		Vector2D c1 = Vector2D.add(new Vector2D(b.position), u);
		Vector2D c2 = Vector2D.add(new Vector2D(c.position), v);

		return Vector2D.dist(c1, c2) < b.halfwidth + c.halfwidth;
	}

	/** Return true iff this block contains point p */
	public boolean contains(Vector2D p) {
		return getBBox().contains(p);
	}

	/**
	 * Draw this block using g, which must be a Graphics2D object. It is scaled
	 * using scale and is displaced using d. clicked indicates whether it has
	 * been clicked on, in which case it appears differently. overlaps indicates
	 * whether it overlaps with some other shape.
	 *
	 * @param g
	 *            A Graphics2D object.
	 * @param scale
	 *            Scale factor from image coordinate system to canvas coordinate
	 *            system.
	 * @param d
	 *            Displacement vector for the block.
	 * @param clicked
	 *            Indicates whether the shape containing the block has been
	 *            clicked on.
	 * @param overlaps
	 *            Indicates whether the shape containing the block overlaps
	 *            another shape.
	 */
	public void display(Graphics g, double scale, Vector2D d, boolean clicked,
			boolean overlaps) {
		Graphics2D g2d = (Graphics2D) g;

		Color prevColor = g2d.getColor();

		Color newColor;
		if (overlaps) {
			// Generate random color
			int red = gen.nextInt(256);
			int green = gen.nextInt(256);
			int blue = gen.nextInt(256);
			newColor = new Color(red, green, blue);
		} else {
			newColor = color;
		}

		if (clicked) {
			int red = newColor.getRed();
			int green = newColor.getGreen();
			int blue = newColor.getBlue();
			newColor = new Color(red, green, blue, 64); // semi-transparent
		}
		g2d.setColor(newColor);

		// Account for displacement.
		double newX = position.x + d.x;
		double newY = position.y + d.y;

		// Draw with int precision
		/*
		 * int topLeftX = (int) Math.floor(scale*(newX - h)); int topLeftY =
		 * (int) Math.floor(scale*(newY - h)); int width = (int)
		 * Math.ceil(scale*2*h); int height = width; g2d.fill(new
		 * Rectangle(topLeftX,topLeftY,width,height)); //g.fillRect(topLeftX,
		 * topLeftY, width, height);
		 */

		// Draw with double precision.
		double topLeftX = scale * (newX - halfwidth);
		double topLeftY = scale * (newY - halfwidth);
		double width = scale * 2 * halfwidth;
		double height = width;
		g2d.fill(new Rectangle2D.Double(topLeftX, topLeftY, width, height));

		g2d.setColor(prevColor); // restore previous color
	}

	/** Return the bounding box of this block. */
	public BoundingBox getBBox() {
		Vector2D tl = new Vector2D(position.x - halfwidth, position.y
				- halfwidth);
		Vector2D br = new Vector2D(position.x + halfwidth, position.y
				+ halfwidth);
		return new BoundingBox(tl, br);
	}

	/**
	 * Return the color-based mass on [0,1] with white having 0 mass and darker
	 * colors approaching 1 (feel free to modify).
	 */
	public double getColorMass() {
		// Scale from integer interval [0,255] to [0,1].
		double x = color.getBlue() / 255.0;
		double y = color.getGreen() / 255.0;
		double z = color.getRed() / 255.0;

		double m = 1 - (x + y + z) / 3.0; // on [0,1]
		if (m < 0) {
			m = 0;
		}
		if (m > 1) {
			m = 1;
		}
		return m;
	}

	/**
	 * Return the halfwidth of block. Note that the block radius is sqrt(2)*h.
	 */
	public double getHalfwidth() {
		return halfwidth;
	}

	/** Return the center position of this Block (in body coordinates). */
	public Point2d getPosition() {
		return position;
	}

	/** Return a representation of this Block. */
	public @Override String toString() {
		return "x = " + col + ", y = " + row + ", c = " + color.toString()
				+ ", pos = " + position.toString() + ", h = " + halfwidth;
	}

	/** Return the Image column. */
	public int getCol() {
		return col;
	}

	/** Return the Image row. */
	public int getRow() {
		return row;
	}
}
