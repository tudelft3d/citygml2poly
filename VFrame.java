package lezers6Domain;



import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;


public class VFrame extends JFrame{
	private JButton defaultButton;
	private JButton chooseSourceButton;
	private JButton chooseDestinationButton;
	private JButton inventoryButton;
	private JButton convertButton;
	private JLabel defaultLabel;
	private JLabel chosenSourceLabel;
	private JLabel chosenDestinationLabel;
	private JLabel inventoryLabel;
	private JLabel polyCountLabel;
	private JProgressBar progressBar;
	private JTextField chosenSourceField;
	private JTextField chosenDestinationField;
	private JTextField buildingCountField;
	private JTextField polyCountField;
	private JPanel panel;
	private JFileChooser sourceChooser;
	private JFileChooser destinationChooser;
	private File sourceFile;
	private File destinationFolder;
	private String sourceFolder = "c://CityGMLData/DenHaag/";
	private final int VERTOFF = 40;
	
	public VFrame(){
		super();
		initialize();
	}
	
	private void initialize(){
		setTitle("CityGML2PolyConvertor");
		setSize(225,520);
		setLocation(0,0);
		panel = (JPanel)getContentPane();
		panel.setLayout(null);
		
		defaultLabel = new JLabel("Select folders or use present ones ");
		defaultLabel.setBounds(10,20,380,60);
		panel.add(defaultLabel);
		
		chooseSourceButton = new JButton("Select source file...");
		chooseSourceButton.setBounds(10,20 + VERTOFF,200,30);
		chooseSourceButton.addActionListener(new ChooseSourceListener());
		chooseSourceButton.setToolTipText("Gives chooser screen to select file to be converted");
		panel.add(chooseSourceButton);
		
		chosenSourceLabel = new JLabel("Selected source file:");
		chosenSourceLabel.setBounds(10,50 + VERTOFF, 200, 30);
		chosenSourceLabel.setOpaque(true);
		chosenSourceLabel.setBackground(Color.GREEN);
		panel.add(chosenSourceLabel);
		
		chosenSourceField = new JTextField();
		chosenSourceField.setEnabled(false);
		chosenSourceField.setBounds(10, 80 + VERTOFF, 200, 30);
		panel.add(chosenSourceField);
		
		chooseDestinationButton = new JButton("Choose destination ...");
		chooseDestinationButton.setBounds(10,120 + VERTOFF,200,30);
		chooseDestinationButton.addActionListener(new ChooseDestinationListener());
		chooseDestinationButton.setToolTipText("Gives chooser screen to select directory for poly files; " +
				"requires one file in directory upfront that should be selected!!!");
		panel.add(chooseDestinationButton);
		
		chosenDestinationLabel = new JLabel("Selected destination folder: ");
		chosenDestinationLabel.setBounds(10, 150 + VERTOFF, 200, 30);
		chosenDestinationLabel.setOpaque(true);
		chosenDestinationLabel.setBackground(Color.GREEN);
		panel.add(chosenDestinationLabel);
		
		chosenDestinationField = new JTextField();
		chosenDestinationField.setEnabled(false);
		chosenDestinationField.setBounds(10,180 + VERTOFF,200,30);
		panel.add(chosenDestinationField);
		
		inventoryButton = new JButton("Start inventory");
		inventoryButton.setBounds(10,220 + VERTOFF,200,30);
		inventoryButton.setToolTipText("Gives the number of buildings and building parts");
		inventoryButton.addActionListener(new StartInventoryListener());
		panel.add(inventoryButton);
		
		inventoryLabel = new JLabel("Number of buildings / buildingParts:");
		inventoryLabel.setBounds(10,250 + VERTOFF,200,30);
		inventoryLabel.setOpaque(true);
		inventoryLabel.setBackground(Color.GREEN);
		panel.add(inventoryLabel);
		
		buildingCountField = new JTextField();
		buildingCountField.setEnabled(false);
		buildingCountField.setBounds(10,280+VERTOFF,200,30);
		panel.add(buildingCountField);
		
		convertButton = new JButton("Start conversion");
		convertButton.setBounds(10,320 + VERTOFF ,200,30);
		convertButton.setToolTipText("Starts conversion from CityGML to poly files");
		convertButton.addActionListener(new StartConversionListener());
		panel.add(convertButton);
		
		polyCountLabel = new JLabel("Resulting number of poly files:");
		polyCountLabel.setBounds(10,390,200,30);
		polyCountLabel.setOpaque(true);
		polyCountLabel.setBackground(Color.GREEN);
		panel.add(polyCountLabel);
		
		polyCountField = new JTextField();
		polyCountField.setEnabled(false);
		polyCountField.setBounds(10,420,200,30);
		panel.add(polyCountField);
		
		
		
	}
	
	private void chooseSource(){

		sourceChooser = new JFileChooser(sourceFolder);
		int chosen = sourceChooser.showOpenDialog(this);
		if ( chosen == JFileChooser.APPROVE_OPTION){
			sourceFile = sourceChooser.getSelectedFile();
			chosenSourceField.setText(sourceFolder + sourceFile.getName());
			buildingCountField.setText("");
			polyCountField.setText("");
		}
	}
	
	private void chooseDestination(){
		destinationChooser = new JFileChooser("c://PolyFiles");
		destinationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int chosen = destinationChooser.showOpenDialog(this);
		if (chosen == JFileChooser.APPROVE_OPTION){
			destinationFolder = destinationChooser.getSelectedFile();
			chosenDestinationField.setText(destinationFolder.getName());
			buildingCountField.setText("");
			polyCountField.setText("");
		}
	}
	
	private void startInventory(){
		if (!chosenSourceField.getText().isEmpty()){
			buildingCountField.setText("");// no effect yet
			disableButtons();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			VCount counter = new VCount(new File(sourceFolder + sourceFile.getName()));
			counter.countBuildings();
			buildingCountField.setText("" + counter.getNrOfBuildings() +" / "
					+ counter.getNrOfBuildingParts());
			enableButtons();
			setCursor(Cursor.getDefaultCursor());
		}
		else{
			buildingCountField.setText("No source file selected!");
		}
	}
	
	private void startConversion(){
		
		if(!(chosenDestinationField.getText().isEmpty() 
				|| chosenSourceField.getText().isEmpty())){
			polyCountField.setText(""); // no effect yet
			disableButtons();
		    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			VReaderWriter readerWriter = new VReaderWriter(sourceFile, destinationFolder);
			try {
				readerWriter.organizeConversion();
				polyCountField.setText("" + readerWriter.getNumberOfShells());
				enableButtons();
				setCursor(Cursor.getDefaultCursor());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			polyCountField.setText("No source or destination selected!");
		}
	}
	
	private void enableButtons(){
		chooseSourceButton.setEnabled(true);
		chooseDestinationButton.setEnabled(true);
		convertButton.setEnabled(true);
		inventoryButton.setEnabled(true);
	}
	
	private void disableButtons(){
		chooseSourceButton.setEnabled(false);
		chooseDestinationButton.setEnabled(false);
		convertButton.setEnabled(false);
		inventoryButton.setEnabled(false);
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VFrame frame = new VFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private class ChooseSourceListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			chooseSource();
		}
	}
	
	private class ChooseDestinationListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			chooseDestination();
		}
	}
	
	private class StartInventoryListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			startInventory();
		}
	}
	
	private class StartConversionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			startConversion();
		}
	}
}
