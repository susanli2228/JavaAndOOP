import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

/** A treemap data structure. */
public class TreeMap {

    private static final double SPLIT_RATIO= 0.4;

    private Node root;        // Root of the hierarchy

    private int frameWidth;   // Size in pixels of display frame for which
    private int frameHeight;  // treemap was computed. May be invalidated (-1).

    private Node selection;   // Currently selected node, if any

    /** Create a treemap with no children, just a root node. */
    public TreeMap() {
        root= this.new Node();
    }

    /** Return the root node of the hierarchy. */
    public Node getRoot() {
        return root;
    }

    /** Return the node of the treemap that contains p (null if none) */
    public Node getNodeContaining(Vector2D v) {
        return getNodeContaining(getRoot(), v);
    }

    /** Return the leaf of tree n that contains v.
     * Precondition: n contains v */
    public Node getNodeContaining(Node n, Vector2D v) {
        for (Node nn : n.children) {
            if (nn.contains(v)) {
                return getNodeContaining(nn, v);
            }
        }

        return n;
    }

    /** Clear the treemap and mark it for lazy recomputation. */
    public void clear() {
        selectNone();
        root= new Node();
        invalidate();
    }

    /** Return true iff the treemap has width w and height h. */
    public boolean isValid(int w, int h) {
        return frameWidth == w  &&  frameHeight == h;
    }

    /** Invalidate the current treemap layout. */
    private void invalidate() {
        frameWidth= -1;
        frameHeight= -1;
    }

    /** Recompute the treemap for width w and height h, if necessary. */
    public void recompute(int w, int h) {
        if (!isValid(w, h)) {
            root.recompute(w, h, 0);

            frameWidth= w;
            frameHeight= h;
        }
    }

    /** Clear any current selection. */
    public void selectNone() {
        selection= null;
    }

    /** Display the treemap on a w*h pixel frame, using g.  */
    public void paint(Graphics g, int w, int h) {
        if (root == null)
            return;

        if (!isValid(w, h))
            recompute(w, h);

        root.paint(g, w, h, 0, false);

        // Redraw the outermost boundary with a little inset; otherwise
        // part of the thickness is cut off by the frame.
        Vector2D pixelSize= new Vector2D(w > 0 ? 1.0/w : 0, h > 0 ? 1.0/h : 0);
        Block outer= new Block(
                root.block.getBBox().lower.add(pixelSize),
                root.block.getBBox().upper.minus(pixelSize));
        outer.paint(g, w, h, false, 0, false);
    }

    /** Print the structure of the tree. */
    public void printTree() {
        if (root != null)
            root.printTree("");
    }

    /** Create and return a new Node with parent p, children c,
     *  weight w, and block b.*/
    public Node getNewNode(Node p, ArrayList<Node> c, double w, Block b) {
        Node n= new Node();
        n.parent= p;
        n.children= c;
        n.weight= w;
        n.block= b;
        return n;
    }

    /** A node of the hierarchy. Contains pointers to the parent and children,
     * as well as the weight and block representing this node. */
    public class Node implements Comparable<Node> {
        private Node parent;
        private ArrayList<Node> children;
        private double weight;
        private Block block;

        /** Constructor: a Node with no parent, no children, no block, and weight 0. */
        public Node() {
            parent= null;
            children= new ArrayList<Node>();
            weight= 0;
            block= null;
        }

        /** Constructor: a Node with parent p, no children, no block, and weight 0.  */
        public Node(Node p) {
            parent= p;
            children= new ArrayList<Node>();
            weight= 0;
            block= null;
        }

        /** Return a description of this Node. */
        @Override
        public String toString() {
            return "Node weight: " + weight + ", block: " + block;
        }

        /** Return the set of children of this tree node */
        public ArrayList<Node> getChildren() {
            return children;
        }

        /** Add an initially empty child with weight zero. */
        public Node addChild() {
            Node child= new Node(this);
            children.add(child);
            return child;
        }

        /** Return "this node's block contains v". */
        public boolean contains(Vector2D v) {
            return block.contains(v);
        }

        /** Return the total weight of the subtree rooted at this node. */
        public double getWeight() {
            return weight;
        }

        /** Set the weight of this leaf node to w.
         * Throw a RuntimeException if this node has a child. */
        public void setLeafWeight(double w) {
            if (!children.isEmpty()) {
                throw new RuntimeException(
                        "Cannot explicitly set the weight of a non-leaf node");
            }

            weight= w;
            invalidate();

            if (parent != null)
                parent.updateWeightsFromChildren();
        }

        /** Compute the weight of this node from the weights of its children
         * and update its parent's weights accordingly. */
        private void updateWeightsFromChildren() {
            weight= 0;
            for (Node c : children)
                weight= weight + c.getWeight();

            invalidate();

            if (parent != null)
                parent.updateWeightsFromChildren();
        }

        /** Return -1, 0, or 1 depending on whether subtree rhs has greater,
         * equal or less weight than this subtree, respectively. */
        @Override
        public int compareTo(Node rhs) {
            return weight > rhs.weight ? -1 : (weight < rhs.weight ? 1 : 0);
        }

        /** Set the selection state of the subtree to state. */
        public void setSelected(boolean state) {
            selection= this;
        }

        /** Get the selection state of the subtree. */
        public boolean isSelected() {
            return selection == this;
        }

        /** Display this subtree at depth d and with ancestral selection state
         * sel, on a w*h pixel frame, using g. */
        private void paint(Graphics g, int w, int h, int d, boolean sel) {
            // Is this node or an ancestor selected?
            sel= sel  ||  selection == this;

            for (Node c : children)
                c.paint(g, w, h, d + 1, sel);

            if (block != null)
                block.paint(g, w, h, children.isEmpty(), d, sel);
        }

        /** Recompute the treemap at depth d for width w and height h. */
        private void recompute(int w, int h, int d) {
            if (d == 0) {
                block= new Block(new Vector2D(0, 0), new Vector2D(1, 1));

                System.out.println("Recomputing treemap, width= " + w
                        + ", height= " + h);
            }

            assert block != null;  // either created above or set by parent

            ArrayList<Node> sorted= new ArrayList<Node>(children);
            Collections.sort(sorted);

            sliceAndDice(sorted, 0, sorted.size() - 1, block.getBBox(), w, h);

            for (Node c : children)
                c.recompute(w, h, d + 1);
        }




        /** Print the structure of the tree with a prefix for each line. */
        private void printTree(String prefix) {
            String childPrefix= prefix.isEmpty() ? "+- " : "   " + prefix;

            for (Node c : children) {
                System.out.println(prefix + "w= " + c.getWeight() + ", "
                        + c.block);
                c.printTree(childPrefix);
            }
        }
    }

    /** Select k such that the weights of b[m..k] sum to at least SPLIT_RATIO
     * times the total weight of b[m..n] but b[m..k-1] does not, with the
     * following caveat:
     * (1) If the weight of b[m..n] is <= 0, use k= (m+n)/2 and split-ratio 0.5.
     *
     * @return A pair of values: k described above and the actual
     * split ratio: (total weight of b[m..k]) / (total weight of b[m..n]).
     *
     * This is a helper function for sliceAndDice().
     * Precondition: m < n  */
    public static Wrapper2 getSplit(ArrayList<Node> b, int m, int n) {
        assert m < n;
        // TODO Replace the return below by your code

        double total = 0;
        double bundle = 0;
        for (int i=m; i<=n; i++) {
        	total += b.get(i).weight;
        }
        if (total==0) {
        	return new Wrapper2((int) (m+n)/2, (double) 0.5);
        }
        int index = 0;
        for (int i=m; i<=n; i++) {
        	bundle += b.get(i).weight;
        	if (bundle >= SPLIT_RATIO*total) {
        		index = i;
        		break;
        	}
        }
        return new Wrapper2(index, (bundle/total));
        
        
    }

    /** Recursively lay out nodes in sorted array b[m..n] as a flat
     * (single-level) treemap within bounding box bbox, assuming the overall
     * output window has width w, height h.
     *
     * Each node is assigned a block within bbox -- the size of the block is
     * proportional to the weight of the node. The blocks do not overlap,
     * and together they perfectly cover bbox.
     * 
     * In reading below, note that SPLIT_RATIO is a static var of this class.
     *
     * The layout follows the "slice-and-dice" algorithm (described
     * step-by-step below and in the handout). In brief, the nodes are split
     * into two groups so that the total weights of the two groups are
     * roughly in the ratio SPLIT_RATIO : 1 - SPLIT_RATIO; bbox is split
     * into two parts according to this ratio and each part assigned the
     * corresponding group; and the process is repeated within each part.
     *
     * Precondition: b[m..n] is sorted in descending order by weight.  */
    private void sliceAndDice(ArrayList<Node> b, int m, int n,
            BoundingBox bbox, int w, int h) {
        // Slice-and-dice algorithm (exposition adapted from
        // https://visualign.wordpress.com/2011/11/09/implementation-of-treemap/):
        
       
        // TODO: Default implementation just assigns the same block to each
        // node. Overwrite this!
        
    	/*for (int i= m; i <= n; i= i+1) {
            b.get(i).block= new Block(bbox, new Color(0, 0, 127));
        }*/
    	
    	// (a) Assume b is in descending order. [precondition]
    	// (b) Base case. For b[m..n] of size 0, do nothing.
        //        For b[m..n] of size 1, store a new block in the block
        //        field of b[m] with color Color(0, 0, 127).
    	if (m>n) {
    		return;
    	}
    	if (m==n) {
    		b.get(m).block = new Block(bbox, new Color(0, 0, 127));
    		return;
    	}
    	
    	// (c) Select the smallest k such that the total weight of b[m..k]
        //     is at least SPLIT_RATIO times the total weight of b[m..n].
        //     (Use function getSplit() to compute this.)
    	// (e) If k = n, subtract 1 from k to ensure progress toward
        //     termination in step f
    	Wrapper2 wrap = getSplit(b, m, n);
		if (wrap.k==n) {
			wrap.k -= 1;
		}
    	
		// (d) Split the rectangle into two parts according to the actual
        //     split-ratio (as returned by getSplit()) along its longer side
        //     (to avoid very narrow shapes).
		// (f) Recursively allocate nodes b[m..k] to the split-off part,
        //     and nodes b[k+1..n] to the rest of the rectangle.
		if (w*bbox.getWidth() >= h*bbox.getHeight()) { // vertical split axis
    		
    		double newX = bbox.lower.x + (bbox.upper.x - bbox.lower.x)*wrap.d;
    		
    		Vector2D leftUpper = new Vector2D(newX, bbox.upper.y);
    		Vector2D rightLower = new Vector2D(newX, bbox.lower.y);
    		
    		BoundingBox leftBB = new BoundingBox(bbox.lower, leftUpper);
    		BoundingBox rightBB = new BoundingBox(rightLower, bbox.upper);
    		
    		
    		sliceAndDice(b, m, wrap.k ,leftBB, w, h );
    		sliceAndDice(b, (wrap.k+1), n, rightBB, w, h);
    	}else { // horizontal split axis
    		double newY = bbox.lower.y + (bbox.upper.y-bbox.lower.y)*wrap.d;
    		
    		Vector2D topUpper = new Vector2D(bbox.upper.x, newY);
    		Vector2D botLower = new Vector2D(bbox.lower.x, newY);
    		
    		BoundingBox topBB = new BoundingBox(bbox.lower, topUpper);
    		BoundingBox botBB = new BoundingBox(botLower, bbox.upper);
    		
    		
    		sliceAndDice(b, m, wrap.k, topBB, w, h);
    		sliceAndDice(b, (wrap.k+1), n, botBB, w, h);
    		
    	}
    	
        
    }

    /** An instance wraps an int and a double. */
    public static class Wrapper2 {
        public int k;
        public double d;

        /** Constructor: an instance with k and d. */
        public Wrapper2(int k, double d) {
            this.k= k;
            this.d= d;
        }
    }

    /** Main function, used to run tests for the treemap. */
    public static void main(String[] args) {
        // TODO
    }
}
