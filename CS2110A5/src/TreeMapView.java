import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

/** An instance displays a treemap in a JPanel. */
public class TreeMapView extends JPanel implements ComponentListener {

    private static final long serialVersionUID= -595115981L;

    private int width;        // Width in canvas coordinates.
    private int height;       // Height in canvas coordinates.

    private TreeMap treemap;  // The treemap to display

    /** Constructor: a JPanel of width w and height h to display t. */
    public TreeMapView(TreeMap t, int w, int h) {
        treemap= t;
        width= w;
        height= h;

        setBackground(Color.WHITE);
        setFocusable(false);
        setLayout(null);
        setDoubleBuffered(true);

        addComponentListener(this);
    }

    /** Display the treemap using g.  */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (treemap != null)
            treemap.paint(g, width, height);
    }

    /** Return a pointer to the treemap being displayed. */
    public TreeMap getTreeMap() {
        return treemap;
    }

    @Override
    /** Reset the width and height from super class JPanel and repaint. */
    public void componentResized(ComponentEvent e) {
        width= getWidth();
        height= getHeight();
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }
}
