package lezers6Domain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartInventoryListener implements ActionListener{
	VFrame frame;
	
	public StartInventoryListener(VFrame frame){
		this.frame = frame;
	}
	public void actionPerformed(ActionEvent e){
		frame.startInventory();
	}
}
