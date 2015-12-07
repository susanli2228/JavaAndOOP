import java.io.File;

/** A treemap view specialized for displaying a filesystem. */
public class FileTreeMapView extends TreeMapView implements FileSelectionListener {

    private static final long serialVersionUID= 534448900;

    private GUI gui; // The GUI on which this is displayed.

    /** Constructor: a canvas of width w and height h to display treemap t on
     * GUI g. */
    public FileTreeMapView(GUI g, FileTreeMap t, int w, int h) {
        super(t, w, h);
        gui= g;
    }

    /** Walk the file tree rooted at path and recompute the wrapped treemap
     * with default maximum depth. */
    public void recomputeTreeMap(File path) {
        FileTreeMap ftm= (FileTreeMap)getTreeMap();
        ftm.init(path);
        repaint();
    }

    /** Walk the file tree rooted at path and recompute the wrapped treemap
     * with maximum depth maxDepth. */
    public void recomputeTreeMap(File path, int maxDepth) {
        FileTreeMap ftm= (FileTreeMap)getTreeMap();
        ftm.init(path, maxDepth);
        repaint();
    }

    /** Respond to an external file selection and highlight the treemap node
     * corresponding to path, if it exists. */
    @Override
    public void selectionChanged(File path) {
        System.out.println("Selection changed to: " + path);
        FileTreeMap treemap= (FileTreeMap)getTreeMap();
        if (treemap == null)
            return;

        if (path == null)  {
            gui.setHasTreeMap("Nothing selected in the file tree");
            treemap.selectNone();
        }
        else {
            TreeMap.Node n= treemap.getNode(path);
            if (n == null) {
                gui.setHasTreeMap("Selection has no corresponding treemap node");
                treemap.selectNone();
            }
            else {
                gui.setHasTreeMap("");
                gui.setSelectedLabels("" + path, (long)n.getWeight());

                if (n == treemap.getRoot())
                    treemap.selectNone();
                else
                    n.setSelected(true);
            }
        }

        repaint();
    }
}
