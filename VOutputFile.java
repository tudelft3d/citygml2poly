package lezers5Data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 18-02-2012
 * Responsible for writing a string to a Poly file
 * @author kooijmanj1
 *
 */
public class VOutputFile {
	
	private File file = null;
	
	public VOutputFile(File file){
		this.file = file;
	}
	
	public void writeBuilding(String output)throws VInputOutputException{
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(output);
		} catch (IOException e) {
			throw new VInputOutputException("Writing error");
		}
		finally {
			if (writer != null){
				try {writer.close();}
				catch (IOException ioe){}
			}
		}
	}
}
