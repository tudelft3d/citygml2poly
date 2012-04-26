package lezers6Domain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseDestinationListener implements ActionListener{
	private VFrame frame;
	
	public ChooseDestinationListener(VFrame frame){
		this.frame = frame;
	}
	
	public void actionPerformed(ActionEvent e){
		frame.chooseDestination();
	}
}
