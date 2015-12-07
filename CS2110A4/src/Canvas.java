import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JPanel;

/**  This JPanel represents the area on the main window where images/shapes are
 * drawn. It also handles the events for clicking on shapes and moving them
 * around. */
public class Canvas extends JPanel implements MouseListener,
MouseMotionListener {
    private static final long serialVersionUID = -5399964428517899376L;

    private GUI mainWindow; // JFrame of main window.

    private int width; // Width in canvas coordinates.
    private int height; // Height in canvas coordinates. 

    private double imgWidth;// Image width in image coordinates (in [0,1]) 
    private double imgHeight;// Image height in image coordinates ( in [0,1]) 

    private double scale; // Scale factor to translate from image to canvas coordinates.

    private ArrayList<Shape> shapes; // Collection of all shapes. 

    private Shape selectedShape; // Selected shape (to be dragged).

    private Vector2D selectedAt; // Last position at which shape was selected.

    /** Random generator for shuffling shapes. */
    Random gen = new Random(System.currentTimeMillis());

    /** Default image file name */
    final String defImgFileName = "Letters.bmp";

    /**Constructor: a canvas of width w and height h on mainWindow. */
    public Canvas(GUI mainWindow, int w, int h) {
        mainWindow= mainWindow;
        width= w;
        height= h;
        scale= Math.min(w, h);

        setBackground(Color.WHITE);
        setFocusable(true);
        setLayout(null);
        setDoubleBuffered(true);

        addMouseListener(this);
        addMouseMotionListener(this);

        // Load default image
        try {
            setNewImage(defImgFileName);
        } catch (IOException e) {
            System.out.println("Images not installed properly.");
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }

    /** Paint the canvas using g.  */
    public void paint(Graphics g) {
        super.paint(g);
        if (shapes == null)
            return;

        long timeOverlap= -System.currentTimeMillis();

        // reset overlaps
        for (Shape s : shapes)
            s.overlaps = false;

        // Calculate overlaps
        for (Shape s1 : shapes) {
            for (Shape s2 : shapes) {
                if (s1.hashCode() < s2.hashCode()) { // check distinct pairs
                    // once
                    if (s1.overlaps(s2)) {
                        s1.overlaps= true;
                        s2.overlaps= true;
                    }
                }
            }
        }

        // Calculate overlap with off-screen canvas: (Can't move objects
        // off-screen to win)
        for (Shape s1 : shapes) {
            if (s1.overlapsOffscreen(width / scale, height / scale)) // omit
                // "pixelWidth"
                s1.overlaps= true;
        }

        timeOverlap += System.currentTimeMillis();

        long timeDisplay= -System.currentTimeMillis();
        for (Shape s : shapes) {
            s.paint(g, scale);
        }
        timeDisplay += System.currentTimeMillis();
        System.out.println("Timing Breakdown: Overlap=" + timeOverlap
                + "ms,  Display=" + timeDisplay + "ms");
    }

    /** Change the current image to imgFileName.
     * @throws IOException */
    public void setNewImage(String imgFileName) throws IOException {
        System.out.println("**** New Image ****");
        ImageBlocker blocker= new ImageBlocker("images/" + imgFileName);
        System.out.println("New image loaded: " + imgFileName);

        ArrayList<HashSet<Block>> parsedShapes = blocker
                .getLargeConnectedComponents(0.10);
        int shapesLeft = parsedShapes.size();
        System.out.println("# shapes left = " + shapesLeft);
        shapes = new ArrayList<Shape>();
        for (HashSet<Block> s : parsedShapes) {
            shapes.add(new Shape(s));
        }

        // Default values.
        imgWidth = 1.0;
        imgHeight = 1.0;
        // TODO:
        // When you have correctly implemented the tree structure,
        // you can uncomment the following piece of code, to get better
        // values for imgWidth & imgHeight. These values will allow the
        // image to fit better in the canvas.
        // TODO:
        // When you have correctly implemented the tree structure,
        // you can uncomment the following piece of code, to get better
        // values for imgWidth & imgHeight. These values will allow the
        // image to fit better in the canvas.
        
           double minX= Double.POSITIVE_INFINITY;
           double minY = minX;
           double maxX= Double.NEGATIVE_INFINITY;
           double maxY= maxX;
           for (Shape s: shapes) {
               BoundingBox box= s.tree.getBox();
               if (box.lower.x < minX) minX= box.lower.x;
               if (box.lower.y < minY) minY= box.lower.y;
               if (box.upper.x > maxX) maxX= box.upper.x;
               if (box.upper.y > maxY) maxY= box.upper.y;
           }
           System.out.println("Area Covered: (" + minX + "," +
                minY + ") -- (" + maxX + "," + maxY + ")");
           imgWidth= maxX;
           imgHeight= maxY;
           

        // Calculate the scale.
        double canvasRatio= ((double) width) / ((double) height);
        double imgRatio= imgWidth / imgHeight;
        if (imgRatio <= canvasRatio) {
            // adjust according to height
            scale= ((double) height) / imgHeight;
        } else {
            // adjust according to width
            scale= ((double) width) / imgWidth;
        }

        repaint();
    }

    /**  Shuffle the objects.  */
    public void shuffleObjects() {
        if (shapes == null)
            return;

        for (Shape s : shapes) {
            // move shape randomly as long as it is inside the canvas
            s.clear(); // first clear object from displacement, etc...

            // TODO:
            // When you have implemented the tree structure correctly,
            // you can safely delete the two following "if" statements.
            

            BoundingBox box = s.tree.getBox();

            // random movement along x
            double rnd= gen.nextDouble(); // [0,1]
            double xLeft= box.lower.x;
            double xRight= imgWidth - box.upper.x;
            rnd *= xLeft + xRight; // [0,xL+xR]
            double dx= rnd - xLeft; // [-xL,xR]

            // random movement along x
            rnd= gen.nextDouble(); // [0,1]
            double yTop= box.lower.y;
            double yBtm= imgHeight - box.upper.y;
            rnd *= (yTop + yBtm); // [0,yT+yB]
            System.out.println(yTop + yBtm);
            double dy= rnd - yTop; // [-yT,yB]
            System.out.println(rnd + " " + yTop + " " + yBtm + " " + dy);

            Vector2D d= new Vector2D(dx, dy);
            s.displace(d);
            // System.out.println(s.getAbsBBox());
        }

        repaint();
    }

    /**  Reset the shapes to their original positions.  */
    public void resetObjects() {
        if (shapes == null)
            return;

        for (Shape s : shapes)
            s.clear();

        repaint();
    }

    /** Process e and the location of the mouse when the event occurred in image
     *  coordinates. */
    private Vector2D getCoords(MouseEvent e) {
        // Get position.
        int mX= e.getX();
        int mY= e.getY();

        // Transform into image coordinates. (center of pixel)
        double pixelWidth= 1.0 / ((double) scale);
        double mXImg= (mX + pixelWidth) / ((double) scale);
        double mYImg= (mY + pixelWidth) / ((double) scale);

        return new Vector2D(mXImg, mYImg);
    }

    /** Process click event e */
    public void mouseClicked(MouseEvent e) {
        // Get mouse position when clicked.
        Vector2D p = getCoords(e);

        // TODO: Find if a shape has been clicked on. Toggle the clickedOn flag
        // of the shape. Note: check ALL shapes; don't stop when first one found.

        // NOTE: Repaint only if necessary, and then only once!
    	
    	int numClicked = 0;
    	for (Shape a: shapes) {
    		if (a.contains(p) ) { 
    			a.click();
    			numClicked += 1;
    		}
    		
    	}
    	if (numClicked >0) {
    		repaint();
    	}
    	
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /**  Process the event of left mouse button being pressed. */
    public void mousePressed(MouseEvent e) {
        // Left button should be pressed.
        if (e.getButton() != MouseEvent.BUTTON1)
            return;

        // Get mouse position when pressed.
        Vector2D p= getCoords(e);

        // reset
        selectedShape= null;
        selectedAt= null;

        for (Shape s : shapes) {
            if (s.contains(p)) {
                selectedShape = s;
                selectedAt= p;
                break;
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    /**  Process the event of left mouse button being released. */
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1)
            return;

        // reset
        selectedShape = null;
        selectedAt = null;
    }

    /**Process the event of mouse being dragged. */
    public void mouseDragged(MouseEvent e) {
        if (selectedShape == null || selectedAt == null) {
            // nothing selected
            return;
        }

        Vector2D droppedAt= getCoords(e);
        Vector2D d= droppedAt.minus(selectedAt); // displacement due to dragging
        selectedShape.displace(d);

        // Update the selected location to this one.
        selectedAt= droppedAt;

        repaint();
    }

}
