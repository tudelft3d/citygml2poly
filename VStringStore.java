package lezers6Domain;

import java.util.ArrayList;

/**
 * 9-2-2012
 * Responsable for storing the generated strings from the various 
 * implementations like Lod1Solid, BoundedBySurface and Multisurface.
 * It stores a string for each shell in a ArrayList<String>
 * @author kooijmanj1
 *
 */
public class VStringStore {
	private ArrayList<String> shellStrings = new ArrayList<String>();
	
	public void store(ArrayList<String> shellStrings){
		this.shellStrings.addAll(shellStrings);
	}
	
	public ArrayList<String> getShellStrings(){
		return shellStrings;
	}
}
