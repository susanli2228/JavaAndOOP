import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/** JPanel for the image drop down menu and the buttons. */
public class ButtonPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 7426820497722133935L;
	private JComboBox<String> imageList;
	private String[] bmpFileNames;

	private Canvas canvas;
	private JButton reset, shuffle;

	/** Constructor: an instance using canvas c and names bmpFileNames*/
	public ButtonPanel(Canvas c, String[] bmpFileNames) {
		this.canvas= c;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// JComboBox for available images.
		this.bmpFileNames = bmpFileNames;
		// Strip the extension from the filenames.
		String[] namesNoExt = new String[bmpFileNames.length];
		for (int i= 0; i < bmpFileNames.length; i++) {
			int end= bmpFileNames[i].length();
			namesNoExt[i]= bmpFileNames[i].substring(0, end - 4);
		}
		imageList= new JComboBox<String>(namesNoExt);
		imageList.setAlignmentX(Component.CENTER_ALIGNMENT);
		imageList.addActionListener(this);
		add(imageList);

		// Button to shuffle shapes.
		shuffle= new JButton("Shuffle");
		shuffle.setAlignmentX(Component.CENTER_ALIGNMENT);
		shuffle.addActionListener(this);
		add(shuffle);

		// Button to reset shapes.
		reset= new JButton("Reset");
		reset.setAlignmentX(Component.CENTER_ALIGNMENT);
		reset.addActionListener(this);
		add(reset);
	}

	/** Handle the events from the drop down menu and the buttons. */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reset) {
			canvas.resetObjects();
		}
		if (e.getSource() == shuffle) {
			canvas.shuffleObjects();
		} else if (e.getSource() == imageList) {
			updateImage(e);
		}
	}

	/** Change the current image based on e. */
	private void updateImage(ActionEvent e) {
		@SuppressWarnings("unchecked")
		JComboBox<String> box = (JComboBox<String>) e.getSource();
		String fileName= (String) box.getSelectedItem() + ".bmp";
		try {
			canvas.setNewImage(fileName);
		} catch (IOException exc) {
			System.out.println(exc.getMessage());
			return;
		}
	}

}
