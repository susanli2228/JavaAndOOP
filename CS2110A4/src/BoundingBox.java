import java.util.Iterator;

/** An instance is a 2D bounding box. */
public class BoundingBox {
	/** The corner of the bounding box with the smaller x,y coordinates. */
	public Vector2D lower; // (minX, minY)

	/** The corner of the bounding box with the larger x,y coordinates. */
	public Vector2D upper; // (maxX, maxY)

	/** Constructor: an instance is a copy of bounding box b. */
	public BoundingBox(BoundingBox b) {
		lower = new Vector2D(b.lower);
		upper = new Vector2D(b.upper);
	}

	/**
	 * Constructor: An instance with lower as smaller coordinates and upper as
	 * larger coordinates.
	 */
	public BoundingBox(Vector2D lower, Vector2D upper) {
		if (upper.x < lower.x)
			throw new IllegalArgumentException("invalid bbox");
		if (upper.y < lower.y)
			throw new IllegalArgumentException("invalid bbox");

		this.lower = lower;
		this.upper = upper;
	}

	/** Return the width of this bounding box (along x-dimension). */
	public double getWidth() {
		return upper.x - lower.x;
	}

	/** Return the height of this bounding box (along y-dimension). */
	public double getHeight() {
		return upper.y - lower.y;
	}

	/** Return the larger of the width and height of this bounding box. */
	public double getLength() {
		// TODO: Implement me.

		return Math.max(this.getWidth(), this.getHeight());

	}

	/** Return the center of this bounding box. */
	public Vector2D getCenter() {
		// TODO: Implement me.

		double centerX = (upper.x + lower.x) / 2; 
		double centerY = (upper.y + lower.y) / 2;

		Vector2D center = new Vector2D(centerX, centerY);

		return center;

	}

	/** Return the result of displacing this bounding box by d. */
	public BoundingBox displaced(Vector2D d) {
		// TODO: Implement me.

		Vector2D hypLower = Vector2D.add(lower, d);
		Vector2D hypUpper = Vector2D.add(upper, d);
		

		return new BoundingBox(hypLower, hypUpper);

	}

	/** Return true iff this bounding box contains p. */
	public boolean contains(Vector2D p) {
		boolean inX = lower.x <= p.x && p.x <= upper.x;
		boolean inY = lower.y <= p.y && p.y <= upper.y;
		return inX && inY;
	}

	/** Return the area of this bounding box. */
	public double getArea() {
		// TODO: Implement me.

		double area = this.getWidth() * this.getHeight();

		return area;
	}

	/** Return true iff this bounding box overlaps with box. */
	public boolean overlaps(BoundingBox box) {
		// TODO: Implement me.

		return (this.lower.x <= box.upper.x && this.upper.x >= box.lower.x
				&& this.lower.y <= box.upper.y && this.upper.y >= box.lower.y); 

	}

	/** Return the bounding box of blocks given by iter. */
	public static BoundingBox findBBox(Iterator<Block> iter) {
		// Do not modify the following "if" statement.
		if (!iter.hasNext())
			throw new IllegalArgumentException("empty iterator");

		// TODO: Implement me.

		BoundingBox totalBBox = iter.next().getBBox(); 
		
		while (iter.hasNext()) {
			BoundingBox tempBBox = iter.next().getBBox();
			totalBBox.lower.x = Math.min(tempBBox.lower.x, totalBBox.lower.x);
			totalBBox.lower.y = Math.min(tempBBox.lower.y, totalBBox.lower.y);
			totalBBox.upper.x = Math.max(tempBBox.upper.x, totalBBox.upper.x);
			totalBBox.upper.y = Math.max(tempBBox.upper.y, totalBBox.upper.y);
		
		}
		
		
		return totalBBox;
	}

	/** Return a representation of this bounding box. */
	public String toString() {
		return lower + " -- " + upper;
	}
}
