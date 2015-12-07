import java.util.ArrayList;
import java.util.Iterator;

/**
 * An instance is a non-empty collection of points organized in a hierarchical
 * binary tree structure.
 */
public class BlockTree {
	private BoundingBox box; // bounding box of the blocks contained in this
								// tree.

	private int numBlocks; // Number of blocks in this tree.

	private BlockTree left; // left subtree --null iff this is a leaf

	private BlockTree right; // right subtree --null iff this is a leaf

	private Block block; // The block of a leaf node --null if not a leaf

	// REMARK:
	// Leaf node: left, right == null && block != null
	// Intermediate node: left, right != null && block == null

	/**
	 * Constructor: a binary tree containing blocks. Precondition: blocks is
	 * nonempty, i.e. it contain at least one block.
	 */
	public BlockTree(ArrayList<Block> blocks) {
		// Leave the following two "if" statements as they are.
		if (blocks == null)
			throw new IllegalArgumentException("blocks null");
		if (blocks.size() == 0)
			throw new IllegalArgumentException("no blocks");

		// TODO: implement me.

		// create bounding box
		Iterator<Block> iter = blocks.iterator();
		box = BoundingBox.findBBox(iter);

		if (blocks.size() == 1) {
			left = null;
			right = null;
			// need to make it store a block
			block = blocks.get(0);

		} else if (box.getWidth() >= box.getHeight()) { // vertical line to
														// split
			double newX = (box.lower.x + box.upper.x) / 2;
			Vector2D newLower = new Vector2D(newX, box.lower.y);
			Vector2D newUpper = new Vector2D(newX, box.upper.y);

			BoundingBox leftBBox = new BoundingBox(box.lower, newUpper);
			BoundingBox rightBBox = new BoundingBox(newLower, box.upper);

			ArrayList<Block> leftL = new ArrayList<Block>();
			ArrayList<Block> rightL = new ArrayList<Block>();

			for (int i = 0; i < blocks.size(); i++) {
				if (leftBBox
						.contains(new Vector2D(blocks.get(i).getPosition()))) {
					leftL.add(blocks.get(i));
				} else {
					rightL.add(blocks.get(i));
				}
			}

			left = new BlockTree(leftL);
			left.numBlocks = leftL.size();
			right = new BlockTree(rightL);
			right.numBlocks = rightL.size();
			block = null;

		} else { // horizontal line split
			double newY = (box.lower.y + box.upper.y) / 2;
			Vector2D newLower = new Vector2D(box.lower.x, newY);
			Vector2D newUpper = new Vector2D(box.upper.x, newY);

			BoundingBox highBBox = new BoundingBox(box.lower, newUpper);
			BoundingBox lowBBox = new BoundingBox(newLower, box.upper);

			ArrayList<Block> highL = new ArrayList<Block>();
			ArrayList<Block> lowL = new ArrayList<Block>();

			for (int i = 0; i < blocks.size(); i++) {
				if (highBBox
						.contains(new Vector2D(blocks.get(i).getPosition()))) {
					highL.add(blocks.get(i));
				} else {
					lowL.add(blocks.get(i));
				}
			}

			left = new BlockTree(highL);
			left.numBlocks = highL.size();
			right = new BlockTree(lowL);
			right.numBlocks = lowL.size();
			block = null;
		}

	}

	/** Return the bounding box of the collection of blocks. */
	public BoundingBox getBox() {
		return box;
	}

	/** Return true iff this is a leaf node. */
	public boolean isLeaf() {
		return block != null;
	}

	/** Return true iff this is an intermediate node. */
	public boolean isIntermediate() {
		return !isLeaf();
	}

	/** Return the number of blocks contained in this tree. */
	public int getNumBlocks() {
		return numBlocks;
	}

	/** Return true iff this collection of blocks contains point p. */
	public boolean contains(Vector2D p) {
		// Caution. By "contains" we do NOT mean that the bounding box
		// of this block tree contains p. That is not enough. We mean
		// that one of the blocks in this BlockTree contains p.
		// TODO: Implement me.

		if (!box.contains(p)) {
			return false;
		} else if (block != null && block.contains(p)) {
			return true;

		} else {
			return left.contains(p) || right.contains(p);
		}

	}

	/**
	 * Return true iff (this tree displaced by thisD) and (tree t displaced by
	 * d) overlap.
	 */
	public boolean overlaps(Vector2D thisD, BlockTree t, Vector2D d) {
		// TODO: Implement me

		if (!box.displaced(thisD).overlaps(t.box.displaced(d))) {
			return false;
		} else if (isLeaf() && t.isLeaf()) {
			return Block.overlaps(block, thisD, t.block, d);
		} else if (isLeaf()) {
			return this.overlaps(thisD, t.left, d)
					|| this.overlaps(thisD, t.right, d);
		} else if (t.isLeaf()) {
			return left.overlaps(thisD, t, d) || right.overlaps(thisD, t, d);
		} else if (box.getArea() <= t.box.getArea()) { // split the bigger box
			return this.overlaps(thisD, t.left, d)
					|| this.overlaps(thisD, t.right, d);
		} else {
			return left.overlaps(thisD, t, d) || right.overlaps(thisD, t, d);

		}

	}

	/** Return a representation of this instance. */
	public String toString() {
		return toString(new Vector2D(0, 0));
	}

	/** Return a representation of this tree displaced by d. */
	public String toString(Vector2D d) {
		return toStringAux(d, "");
	}

	/** Useful for creating appropriate indentation for function toString. */
	private static final String indentation = "   ";

	/**
	 * Return a representation of this instance displaced by d, with indentation
	 * indent.
	 * 
	 * @param d
	 *            Displacement vector.
	 * @param indent
	 *            Indentation.
	 * @return String representation of this tree (displaced by d).
	 */
	private String toStringAux(Vector2D d, String indent) {
		String str = indent + "Box: ";
		str += "(" + (box.lower.x + d.x) + "," + (box.lower.y + d.y) + ")";
		str += " -- ";
		str += "(" + (box.upper.x + d.x) + "," + (box.upper.y + d.y) + ")";
		str += "\n";

		if (isLeaf()) {
			String vStr = "(" + (block.position.x + d.x) + ","
					+ (block.position.y + d.y) + ")" + block.halfwidth;
			str += indent + "Leaf: " + vStr + "\n";
		} else {
			String newIndent = indent + indentation;
			str += left.toStringAux(d, newIndent);
			str += right.toStringAux(d, newIndent);
		}

		return str;
	}
}
