import java.io.File;
import java.util.HashMap;
import java.util.Map;

/** An instance is a treemap specialized for displaying a filesystem. */
public class FileTreeMap extends TreeMap {

    // The default maximum number of levels of the treemap.
    private static final int DEFAULT_MAX_DEPTH= 1;

    // The maximum number of levels to recurse when estimating the size of a
    // directory. Increase this to get better estimates for deep directory
    // hierarchies, but slower initial startup
    private static final int GET_SIZE_MAX_DEPTH= 1;

    private File rootPath;  // the path for which the treemap is constructed
    private Map<File, Node> nodeLookup= new HashMap<File, Node>();
    private Map<Node, File> fileLookup= new HashMap<Node, File>();

    private int maxDepth; // The current maximum depth of this file treemap

    /** Walk the file tree rooted at a path and create a corresponding treemap,
     * with default maximum depth. */
    public FileTreeMap(File path) {
        init(path);
    }

    /** Walk the file tree rooted at path and create a corresponding treemap,
     * with maximum depth maxDepth. */
    public FileTreeMap(File path, int maxDepth) {
        init(path, maxDepth);
    }

    /** Return the root filesystem path of the treemap. */
    File getRootPath() {
        return rootPath;
    }

    /** Return the node with path f, or null if no such node exists. */
    Node getNode(File f) {
        return nodeLookup.get(f);
    }

    /** Return the file corresponding to node n, or null if no such file exists
     * (this is usually the case only when n doesn't belong to this treemap or
     * is null. */
    File getFile(Node n) {
        return fileLookup.get(n);
    }

    /** Return the current max depth of the treemap */
    public int getMaxDepth() {
        return maxDepth;
    }

    /** Change the max depth of the tree map to d */
    public void setMaxDepth(int d) {
        maxDepth= d;
    }

    @Override
    public void clear() {
        nodeLookup.clear();
        fileLookup.clear();
        super.clear();
    }

    /** Walk the file tree rooted at path and initialize a corresponding
     * treemap, with default maximum depth. */
    public void init(File path) {
        init(path, DEFAULT_MAX_DEPTH);
    }

    /** Walk the file tree rooted at path and initialize a corresponding
     * treemap, with maximum depth maxDepth. */
    public void init(File path, int maxDepth) {
        clear();
        this.rootPath= path;
        this.maxDepth= maxDepth;
        walk(getRoot(), path, maxDepth);
    }

    /** Walk the file tree rooted at a path and initialize a treemap rooted
     * at root, with maximum depth d (relative to root). */
    private void walk(Node root, File path, int d) {
        nodeLookup.put(path, root);
        fileLookup.put(root, path);

        // If this is a file or no more levels can be added after this, set the
        // weight of the node and quit
        if (d <= 0 || !path.isDirectory()) {
            root.setLeafWeight(size(path));
            return;
        }

        // Recurse if directory
        File[] files= path.listFiles();
        if (files != null) {
            for (File f : files) {
                Node n= root.addChild();
                walk(n, f, d - 1);
            }
        }
    }

    /** Return the size of file f.
     * If it is a directory, that means calculating its size, up to a maximum
     * recursion depth of GET_SIZE_MAX_DEPTH. */
    public static long size(File f) {
        return size(f, GET_SIZE_MAX_DEPTH);
    }

    /** Return the size of f to depth d, as follows, in this order:
     * If f is null, return 0.
     * If d is 0 and this is a directory, return 4K (a directory does take up a
     * little bit of space, but d is 0, so don't look at contents of directory).
     * If f is a file, use its size
     * If f is a directory, calculate total the size of its contents, up to
     * a maximum recursion depth of d.
     * Precondition: d >= 0 */
    public static long size(File f, int d) {
        // Remember this is a recursive function!

        // TODO Replace the following statement by your code
        assert d>= 0;
    	
    	if (f==null) {
        	return 0;
        }else if (d==0 && f.isDirectory()) {
        	return 4000;
        }else if (f.isFile() ) { //file
        	return f.length();
        }else { //is it implied that it is a directory at this point?
        	long total = 0;
        	File[] array = f.listFiles();
        	if (array != null) {
        		for (int i=0; i< array.length; i++) {
	        		total += size(array[i], d-1);
	        	}
        	}
        	
        	return total;
        }
    	
    }
}
