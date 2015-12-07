/** An instance is a 2D bounding box. */
public class BoundingBox {
    /** The corner of the bounding box with the smaller x,y coordinates. */
    public Vector2D lower; // (minX, minY)

    /** The corner of the bounding box with the larger x,y coordinates. */
    public Vector2D upper; // (maxX, maxY)

    /** Constructor: an instance is a copy of bounding box b.*/
    public BoundingBox(BoundingBox b) {
        lower= new Vector2D(b.lower);
        upper= new Vector2D(b.upper);
    }

    /** Constructor: An instance with lower as smaller coordinates and
     * upper as larger coordinates. */
    public BoundingBox(Vector2D lower, Vector2D upper) {
        if (upper.x < lower.x)
            throw new IllegalArgumentException("invalid bbox");
        if (upper.y < lower.y)
            throw new IllegalArgumentException("invalid bbox");

        this.lower= lower;
        this.upper= upper;
    }

    /** Return the width of this bounding box (along x-dimension).  */
    public double getWidth() {
        return upper.x - lower.x;
    }

    /** Return the height of this bounding box (along y-dimension). */
    public double getHeight() {
        return upper.y - lower.y;
    }

    /** Return the larger of the width and height of this bounding box.  */
    public double getLength() {
        return Math.max(getHeight(),getWidth());
    }

    /** Return the center of this bounding box.  */
    public Vector2D getCenter() {
        return new Vector2D((upper.x + lower.x) / 2, (upper.y + lower.y) / 2);
    }

    /** Return the result of displacing this bounding box by d.  */
    public BoundingBox displaced(Vector2D d) {
        Vector2D l= Vector2D.add(lower, d);
        Vector2D u= Vector2D.add(upper, d);
        return new BoundingBox(l,u);
    }

    /** Return true iff this bounding box contains p.  */
    public boolean contains(Vector2D p) {
        boolean inX= lower.x <= p.x && p.x <= upper.x;
        boolean inY= lower.y <= p.y && p.y <= upper.y;
        return inX && inY;
    }

    /** Return the area of this bounding box.  */
    public double getArea() {
        return getWidth() * getHeight();
    }

    /** Return true iff this bounding box overlaps with box.  */
    public boolean overlaps(BoundingBox box) {
        if (upper.x < box.lower.x) return false;
        if (lower.x > box.upper.x) return false;
        if (upper.y < box.lower.y) return false;
        if (lower.y > box.upper.y) return false;
        return true;
    }

    /** Return a representation of this bounding box. */
    @Override
    public String toString() {
        return lower + " -- " + upper;
    }
}
