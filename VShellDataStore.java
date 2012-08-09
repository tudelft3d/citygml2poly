import java.util.ArrayList;
/**
 * 8-8-2012
 * Responsible for storing the generated strings form the various ...
 * @author jan
 *
 */
public class VShellDataStore {
	private ArrayList<String[]> shellDataArrays = new ArrayList<String[]>();
	
	public void store(ArrayList<String[]> shellDataArray){
		(this.shellDataArrays).addAll(shellDataArray);
	}
	
	public void clear(){
		shellDataArrays.clear();
	}
	
	public ArrayList<String[]> getShellDataArrays(){
		return shellDataArrays;
	}
}
