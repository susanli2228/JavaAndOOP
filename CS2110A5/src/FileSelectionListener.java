import java.io.File;

/** Handles file selection events. */
public interface FileSelectionListener {

    /** Called when the selection changes to path, which may be null. */
    public void selectionChanged(File path);
}
