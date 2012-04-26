package lezers6Domain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartConversionListener implements ActionListener{
	private VFrame frame;
	
	public StartConversionListener(VFrame frame){
		this.frame= frame;
	}
	
	public void actionPerformed(ActionEvent e){
		frame.startConversion();
	}

}
