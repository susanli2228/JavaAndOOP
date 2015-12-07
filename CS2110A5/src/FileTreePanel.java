import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/** An instance is a JPanel that displays a directory and its contents
 * Author: Kirill Grouchnikov. Revised and extended by Gries and Chaudhuri. */
public class FileTreePanel extends JPanel implements TreeSelectionListener {

    private static final long serialVersionUID= 947853617;

    /** File system view. */
    protected static FileSystemView fsv= FileSystemView.getFileSystemView();

    private JTree tree; // The file tree.
    private FileSelectionListener selectionListener;  // Handles selection events.
    private boolean dirsOnly;  // Display only directories, not files?

    /** Renderer for the file tree. */
    private static class FileTreeCellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID= 381492850;

        /** Icon cache to speed the rendering. */
        private Map<String, Icon> iconCache= new HashMap<String, Icon>();

        /** Root name cache to speed the rendering. */
        private Map<File, String> rootNameCache= new HashMap<File, String>();

        /** See javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent.
         *  (javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)  */
        public @Override Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            FileTreeNode ftn= (FileTreeNode) value;
            File file= ftn.file;
            String filename= "";
            if (file != null) {
                if (ftn.isFileSystemRoot) {
                    filename= rootNameCache.get(file);
                    if (filename == null) {
                        filename= fsv.getSystemDisplayName(file);
                        rootNameCache.put(file, filename);
                    }
                } else {
                    filename= file.getName();
                }
            }
            JLabel result= (JLabel) super.getTreeCellRendererComponent(tree,
                    filename, sel, expanded, leaf, row, hasFocus);
            if (file != null) {
                Icon icon= iconCache.get(filename);
                if (icon == null) {
                    icon= fsv.getSystemIcon(file);
                    iconCache.put(filename, icon);
                }
                result.setIcon(icon);
            }
            return result;
        }
    }

    /** A filter that accepts only directories. */
    private class DirectoryFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname != null && pathname.isDirectory();
        }
    }

    /** An instance is a node in the file tree. */
    private class FileTreeNode implements TreeNode {
        private boolean isFileSystemRoot; //= "this node is root of file system
        private File file; // Node file
        private File[] children; // Children of the node file.
        private TreeNode parent; // Parent node.

        /** Constructor: a new file tree node for file f with parent p
         * isRoot tells whether it is the root of the file system. */
        public FileTreeNode(File f, boolean isRoot, TreeNode p) {
            file= f;
            isFileSystemRoot= isRoot;
            parent= p;
            children= (dirsOnly ? file.listFiles(new DirectoryFilter())
                                : file.listFiles());
            if (children == null)
                children= new File[0];
        }

        /** Constructor: instance with no file, no parent, and children files c. */
        public FileTreeNode(File[] c) {
            file= null;
            parent= null;
            children= c;
        }

        /** Return the absolute path to this node */
        @Override
        public String toString() {
            if (file == null) return "null";
            return file.getAbsolutePath();
        }

        /** Return the file object corresponding to this node. */
        File getFile() {
            return file;
        }

        /** Return an enumeration that enumerates the children of this FileTreeNode. */
        public @Override Enumeration<?> children() {
            final int elementCount= children.length;
            return new Enumeration<File>() {
                int count= 0;

                /** Return "there is another element to enumerate". */
                @Override
                public boolean hasMoreElements() {
                    return count < elementCount;
                }

                /** Return the next element to enumerate.
                 * Throw a NoSuchElementException if there are no more. */
                @Override
                public File nextElement() {
                    if (count < elementCount) {
                        count= count + 1;
                        return FileTreeNode.this.children[count-1];
                    }
                    throw new NoSuchElementException("Vector Enumeration");
                }
            };

        }

        /** Return true (i.e. this allows children). */
        public @Override boolean getAllowsChildren() {
            return true;
        }

        /** Return child number i (the first is number 0). */
        public @Override TreeNode getChildAt(int i) {
            return new FileTreeNode(children[i], parent == null, this);
        }

        /** Return the number of children of this FileTreeNode. */
        public @Override int getChildCount() {
            return children.length;
        }

        /** Return the index of node in this node's children.
         * (-1 if node is not a child). */
        public @Override int getIndex(TreeNode node) {
            FileTreeNode ftn= (FileTreeNode) node;
            for (int i= 0; i < children.length; i= i+1) {
                if (ftn.file.equals(children[i]))
                    return i;
            }
            return -1;
        }

        /** Return the parent of this node. */
        public @Override TreeNode getParent() {
            return parent;
        }

        /** Return "this node is a leaf". */
        public @Override boolean isLeaf() {
            return getChildCount() == 0;
        }
    }

    /** Create a file tree panel for the root of the harddrive. */
    public FileTreePanel() {
        this(false);
    }

    /** Create a file tree panel for the root of the harddrive, with the option
     * to display only directories. */
    public FileTreePanel(boolean dirsOnly) {
        this.dirsOnly= dirsOnly;
        File[] roots= File.listRoots();
        helpConstructor(roots);
    }

    /** Create a file tree panel for directory d.
     * Precondition: d must be the absolute path of the directory. */
    public FileTreePanel(String d) {
        this(d, false);
    }

    /** Create a file tree panel for directory d, with the option to display
     * only directories.
     * Precondition: d must be the absolute path of the directory. */
    public FileTreePanel(String d, boolean dirsOnly) {
        this.dirsOnly= dirsOnly;
        File[] f= new File[]{new File(d)};
        helpConstructor(f);
    }

    /** Select the file f in the tree, if it exists. */
    public void select(File f) {
        // Recursively find the parent of the file that has the same root as
        // this file tree. Store the full set of ancestor paths, NOT including
        // the root path, in a list.
        FileTreeNode rootNode= (FileTreeNode)tree.getModel().getRoot();
        File p= f;
        LinkedList<File> genealogy= new LinkedList<File>();
        while (!p.equals(rootNode.getFile())) {
            genealogy.addFirst(p);
            File gp= p.getParentFile();
            if (gp == null || gp.equals(p))  // can't go up any further
                return;
            p= gp;
        }

        // If we've reached here, f is a valid descendant of the root

        // Traverse the genealogy "forwards", starting from the root, mapping
        // each descendant path to the corresponding tree node, if one exists.
        LinkedList<TreeNode> genealogyNodes= new LinkedList<TreeNode>();
        genealogyNodes.add(rootNode);
        TreeNode n= rootNode;
        for (File g : genealogy) {
            TreeNode c= null;  // matching child
            for (int i= 0; i < n.getChildCount(); ++i) {
                FileTreeNode t= (FileTreeNode)n.getChildAt(i);
                if (t.getFile().equals(g)) {
                    c= t;
                    break;
                }
            }

            if (c == null) {
                System.out.println("Could not find child matching " + g);
                return;
            }

            genealogyNodes.add(c);
            n= c;
        }

        // Get the tree path corresponding to the genealogy
        TreePath treePath= new TreePath(genealogyNodes.toArray());

        // ... and select it
        tree.setSelectionPath(treePath);
        tree.scrollPathToVisible(treePath);
    }

    /** Initialize for the directories in f */
    public void helpConstructor(File[] f) {
        setLayout(new BorderLayout());
        FileTreeNode rootTreeNode=
                (f.length == 1
                ? this.new FileTreeNode(f[0], false, null)
                : this.new FileTreeNode(f));
        tree= new JTree(rootTreeNode);
        tree.setCellRenderer(new FileTreeCellRenderer());
        tree.setRootVisible(false);

        tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);
        final JScrollPane jsp= new JScrollPane(tree);
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(jsp, BorderLayout.CENTER);

    }

    /** Set a listener that handles file selection events. */
    void setSelectionListener(FileSelectionListener listener) {
        selectionListener= listener;
    }

    /** Get the current listener that handles file selection events. */
    FileSelectionListener getSelectionListener() {
        return selectionListener;
    }

    /** Process a click on a file/directory that changes the selection.
     * Precondition: the selection model allows only a single selection. */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        FileTreeNode node= (FileTreeNode) tree.getLastSelectedPathComponent();

        if (selectionListener != null) {
            selectionListener.selectionChanged(node == null ? null
                                                            : node.getFile());
        }
        else {
            if (node == null)  // Nothing is selected.
                System.out.println("Nothing selected in the file tree");
            else
                System.out.println("Selected node: " + node);
        }
    }

    /** Return the absolute path of a directory chosen by the user
     * ---null if dialog canceled by the user. */
    public static String getDirectory() {
        JFileChooser jd= new JFileChooser();
        jd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal= jd.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        return jd.getSelectedFile().getAbsolutePath();

    }

    /** Code to test the file tree panel. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame= new JFrame("File tree");
                frame.setSize(500, 400);
                frame.setLocationRelativeTo(null);
                String directory= getDirectory();
                frame.add(new FileTreePanel(directory, false));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
