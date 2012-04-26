package lezers6Domain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseSourceListener implements ActionListener{
	VFrame frame;
	
	public ChooseSourceListener(VFrame frame){
		this.frame=frame;
	}
	
	public void actionPerformed(ActionEvent e){
		frame.chooseSource();
	}
}
