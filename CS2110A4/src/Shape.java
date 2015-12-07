import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.ArrayList; //AM I ALLOWED TO DO THIS??

/** An instance is a collection of blocks. */
public class Shape {
	private HashSet<Block> blocks; //Collection of blocks

	private Vector2D d; //Displacement of the shape in the image coordinate system.

	private boolean clickedOn; // True if this shape has been clicked on

	boolean overlaps; // True iff this shape overlaps with some other shape

	BlockTree tree; //A hierarchical tree structure for the blocks of the shape
	
	
	/** Constructor: An instance containing blocks.
     * Precondition: blocks is not empty. */
	public Shape(HashSet<Block> blocks) {
		if (blocks == null)
			throw new IllegalArgumentException("null blocks");
		if (blocks.size() == 0)
			throw new IllegalArgumentException("empty blocks");

		this.blocks= blocks;
		d= new Vector2D(0, 0); // original displacement is the zero vector

		// TODO: Construct the hierachical tree structure.
		//
		// ...
		//
		// tree = new BlockTree(...);
		
		
		ArrayList<Block> make = new ArrayList<Block>();
		for (Block a: blocks){
			make.add(a);
		}
		
		tree = new BlockTree(make);
		
		
	}
	
	/** Return the center of the bounding box of the shape. */
	Vector2D getCenter() {
		// TODO:
		// Uncomment the following line when your tree structure works correctly
		// and delete the "return null;" line.
		return tree.getBox().getCenter();
	}

	/** Return the bounding box of the image when you account for the displacement. */
	BoundingBox getAbsBBox() {
		// TODO:
		// Uncomment the following line when your tree structure works correctly
		// and delete the "return null;" line.
		return tree.getBox().displaced(d); // Note: returns a new BBox
		
	}

	/** Return true iff the object's bounding box is partially offscreen, i.e.
     *         outside the [0,width] x [0,height] region */
	boolean overlapsOffscreen(double width, double height) {
		// TODO:
		// Un-comment the following two lines after you have implemented the
		// required methods and delete the "return false;" line.
		BoundingBox box = getAbsBBox();
		return (box.lower.x < 0) || (box.upper.x > width) || (box.lower.y <
		0) || (box.upper.y > height);
		
	}

	/** Return true iff this shape overlaps shape t.  */
	boolean overlaps(Shape t) {
		// TODO: Use betterOverlaps when you implement it.
		return betterOverlaps(t);
		//return naiveOverlaps(t);
	}

	/** Return true iff this shape overlaps shape t. */
	boolean naiveOverlaps(Shape t) {
		for (Block a : blocks) {
			for (Block b : t.blocks) {
				if (Block.overlaps(a, d, b, t.d))
					return true;
			}
		}

		return false;
	}

	/** Return true iff this shape overlaps shape t.
	 * This is a better implementation of overlap detection than naiveOverlaps.
	  */
	boolean betterOverlaps(Shape t) {
		// TODO: Implement me.
		return tree.overlaps(d, t.tree, t.d);
		
		
	}

	/** "Clear" the shape from displacement and turned on flags. */
	public void clear() {
		// bring to original position
		d.x= 0;
		d.y= 0;
		// reset clickedOn flag
		clickedOn= false;
		// reset overlaps flag
		overlaps= false;
	}

	/** (Further) displace this shape by displacement vector v. */
	public void displace(Vector2D v) {
		d.addOn(v);
	}

	/** Paint using g, using scale to scale the image.
	 * @param g   A Graphics2D object.
	 * @param scale The scale from image coordinates to canvas coordinates.
	 */
	public void paint(Graphics g, double scale) {
		// Draw the blocks of the shape.
		for (Block b : blocks)
			b.display(g, scale, d, clickedOn, overlaps);

		// Draw bounding rectangle
		// EXTRA HELP: This code might give you a helpful visualization of the
		// bounding box
		// of the shape. You can use it if you want while developing.
		
		 BoundingBox box = tree.getBox(); if (box == null) return; BoundingBox dBox =
		 box.displaced(d); int x = (int) Math.floor(scale*dBox.lower.x); int y
		 = (int) Math.floor(scale*dBox.lower.y); int width = (int)
		 Math.ceil(scale*(dBox.upper.x-dBox.lower.x)); int height = (int)
		 Math.ceil(scale*(dBox.upper.y-dBox.lower.y));
		 g.drawRect(x,y,width,height);
		 
	}

	/** Toggle the clickedOn flag. */
	public void click() {
		clickedOn= !clickedOn;
	}

	/** Return true iff this shape contains point p. */
	public boolean contains(Vector2D p) {
		// TODO: Use betterContains when you implement it.
		return betterContains(p);
		//return naiveContains(p);
	}

	/** Return true iff this shape contains point p. */
	@SuppressWarnings("unused") // When implemented, move this tag to naiveContains
	private boolean naiveContains(Vector2D p) {
		// Account for displacement of shape.
		Vector2D newP= p.minus(d);

		for (Block b : blocks) {
			if (b.contains(newP))
				return true;
		}
		return false;
	}

	/** Return true iff this shape contains point p. */
	
	private boolean betterContains(Vector2D p) {
		// TODO: Implement me

		// NOTE: Do not forget to account for the displacement of this shape!
		
		
		Vector2D newP = p.minus(d);
		
		return tree.contains(newP); 
		
	}

}
