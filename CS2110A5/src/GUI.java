/* Time spent on a4: 7 hours and 34 minutes.
 * Name: Susan Li
 * Netid: SZL5
 * What I thought about this assignment:
 * Another interesting visualizable project. 
 * Sometimes, with so many classes, it's hard to search 
 * through all of them to find that one function you need
 * to reference.
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

/** The class for the main program. */
public class GUI extends JFrame implements ActionListener {
    // NOTE: You can adjust the values of TREEMAP_WIDTH and TREEMAP_HEIGHT to make the initial
    // window fit well on your computer screen.

    private static final long serialVersionUID= 7926807064448718426L;

    private static final int TREEMAP_WIDTH = 800;    // Preferred width of the treemap view
    private static final int TREEMAP_HEIGHT= 650;    // Preferred height of the treemap view
    private static final int FILE_PANEL_WIDTH= 200;  // Preferred width of file tree panel

    private JPanel container;          // Encloses everything in the window
    private JSplitPane treeContainer;  // Contains the filetree to the left and treeview to right
    private FileTreePanel fileTree;    // Contains the file tree as a JTree
    private FileTreeMapView treeView;  // Contains the treemap

    private Box north;              // Contains the buttons and the label with file/directory selected
    private Box buttons;            // The buttons in the North part of the GUI
    private JButton depthPlus;      // Click button to increase recursion depth
    private JButton depthMinus;     // Click button to decrease recursion depth
    private JLabel depthOfTreemap;  // Gives depth of treemap
    private Box selectedLine;       // Contains selected and horizontal glue
    private JLabel selected;        // Selected file
    private Box selectedSizeLine;   // contains selectedSize and horizontal glue
    private JLabel selectedSize;    // Size of the selected file or directory
    private JLabel hasTreeMap;      // Is blank or "Selection has no corresponding tree map node"

    MouseEvents me;  // Processes mouse clicks

    /** Constructor: an instance that has the GUI */
    public GUI(FileTreeMap tmap) {
        // Initialize JFrame settings
        setTitle("TreeMap: " + tmap.getRootPath());
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        container= new JPanel();
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(container, BorderLayout.CENTER);

        fileTree= new FileTreePanel(tmap.getRootPath().toString());
        fileTree.setPreferredSize(new Dimension(FILE_PANEL_WIDTH, TREEMAP_HEIGHT));

        treeView= new FileTreeMapView(this, tmap, TREEMAP_WIDTH, TREEMAP_HEIGHT);
        treeView.setPreferredSize(new Dimension(TREEMAP_WIDTH, TREEMAP_HEIGHT));
        fileTree.setSelectionListener(treeView);
        fixTreeContainer();

        north= new Box(BoxLayout.Y_AXIS);
        buttons= new Box(BoxLayout.X_AXIS);
        depthPlus= new JButton("Increase depth");
        depthMinus= new JButton("Decrease depth");
        FileTreeMap ftm= (FileTreeMap)treeView.getTreeMap();
        depthOfTreemap= new JLabel("Depth of treemap: " + ftm.getMaxDepth());
        addComponent(depthPlus, depthMinus, depthOfTreemap, buttons);
        north.add(buttons);

        selectedLine= new Box(BoxLayout.X_AXIS);
        selected= new JLabel("Selected file");
        addComponent(selected, null, null, selectedLine);
        north.add(selectedLine);

        selectedSizeLine= new  Box(BoxLayout.X_AXIS);
        selectedSize= new JLabel("Size of selected file");
        hasTreeMap= new JLabel("");
        addComponent(selectedSize, new JLabel("          "), hasTreeMap,
                     selectedSizeLine);
        north.add(selectedSizeLine);
        container.add(north, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        depthPlus.addActionListener(this);
        depthMinus.addActionListener(this);

        me= new MouseEvents();
        treeView.addMouseListener(me);
    }

    /** If field container is not null, remove it from this JPanel, fix container
     * with its components and add it back at the Center */
    public void fixTreeContainer() {
        if (treeContainer != null) {
            container.remove(treeContainer);
        }

        treeContainer= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                      fileTree, treeView);
        treeContainer.setOneTouchExpandable(true);
        treeContainer.setDividerLocation(FILE_PANEL_WIDTH);
        container.add(treeContainer, BorderLayout.CENTER);
    }

    /** Add c, c1 (if not null), and c1 (if not null) to b followed by horizontal glue */
    public void addComponent(Component c, Component c1, Component c2, Box b) {
        b.add(c);
        if (c1 != null) b.add(c1);
        if (c2 != null) b.add(c2);
        b.add(Box.createHorizontalGlue());
    }

    /** Main program. Args are not used. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Get the treemap root directory from the user
                String directory= FileTreePanel.getDirectory();
                FileTreeMap ftmap= new FileTreeMap(new File(directory));
                new GUI(ftmap);
            }
        });
    }

    /** Set the label that gives the selected path to p and its size to n. */
    public void setSelectedLabels(String p, long n) {
        selected.setText(p);
        selectedSize.setText(n + " bytes, " + abbreviate(n));
    }

    /** Return n in the form xxkb or xxmb of xxgb.
     * Precondition: n is >= 0. */
    public static String abbreviate(long n) {
        if (n < 1000000 - 1000) {
            return Math.round(n/1000.0)  + "KB";
        }
        if (n < 1000000000 - 1000000) {
            return Math.round(n/1000000.0)  + "MB";
        }
        return Math.round(n/1000000000.0)  + "GB";
    }

    /** Set label hasTreeMap to s */
    public void setHasTreeMap(String s) {
        hasTreeMap.setText(s);
    }

    /** Recompute container of the two views of the file system using max depth
     * maxDepth. This requires recomputing treeView and recreating fileTree. */
    public void recomputeTreeMap(int maxDepth) {
        // Update the treeview
        FileTreeMap ftm= (FileTreeMap)treeView.getTreeMap();
        File path= ftm.getRootPath();
        treeView.recomputeTreeMap(path, maxDepth);

        // Create the new fileTree
        fileTree= new FileTreePanel(ftm.getRootPath().toString());
        fileTree.setPreferredSize(new Dimension(FILE_PANEL_WIDTH, TREEMAP_HEIGHT));
        fileTree.setSelectionListener(treeView);

        // Set the text that gives the depth of the tree
        ftm= (FileTreeMap)treeView.getTreeMap();
        depthOfTreemap.setText("Depth of treemap: " + ftm.getMaxDepth());

        fixTreeContainer();
        repaint();
    }

    /** Process a click of a button, and appropriately update the treemap. */
    @Override
    public void actionPerformed(ActionEvent e) {
        // (a) Figure out which button was clicked (increase depth or decrease
        //     depth?)
        // (b) Find the current treemap depth.
        // (c) Increment or decrement it according to which button was clicked.
        // (d) Recompute the treemap with the new depth.
        // TODO
    	
    	Object event = e.getSource();
    	FileTreeMap tree = (FileTreeMap) treeView.getTreeMap();
    	
    	if (event == depthPlus) {
    		
    		tree.setMaxDepth(tree.getMaxDepth()+1);
    		recomputeTreeMap(tree.getMaxDepth());
    	
    	}else if(event == depthMinus) {
    		if (tree.getMaxDepth()==1) { //cannot go below depth=1
    			return;
    		}else {
    			tree.setMaxDepth(tree.getMaxDepth()-1);
    			recomputeTreeMap(tree.getMaxDepth());
    		}
    		
    	}
    }
   
    
    

    /** An instance has a method mouseClicked that will respond to clicks in the
     * JPanel that is the treemap. */
    public class MouseEvents extends MouseInputAdapter {
        /** Process a mouse click in the treemap. It should figure out the block
         * which was clicked, and select the corresponding node in the fileTree. */
        @Override
        public void mouseClicked(MouseEvent e) {
            
            // There are helper functions in FileTreeMap, FileTreeMapView and
            // FileTreePanel that can help you do this. Use them!

            // TODO
        	
        	
        	// (a) Figure out the (image) coordinates of the clicked point. 
        	double mX= e.getX();
            double mY= e.getY();
            
            //rescale
            double XRatio = mX/((double)treeView.getWidth());
            double YRatio = mY/((double)treeView.getHeight());
            
            Vector2D point = new Vector2D(XRatio, YRatio);
            
            // (b) Find the treemap node containing this point.
            
            FileTreeMap tree = (FileTreeMap) treeView.getTreeMap();
            
            TreeMap.Node thisNode = tree.getNodeContaining(point);
            
            // (c) Find the File corresponding to this node.
            
            File f = tree.getFile(thisNode);
            
            // (d) Select this file in the fileTree.
            
            fileTree.select(f);
            treeView.selectionChanged(f);
            
            
            
        	
        	
        	
        	
        }
    }
}
