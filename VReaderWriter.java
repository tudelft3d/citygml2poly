

import java.io.File;
import java.util.ArrayList;

import org.citygml4j.model.citygml.building.Building;


/**
 * Responsible for reading building by building and delegating their organization to VConstruct;
 * Makes the output file names: each shell is converted into a poly file; the name of 
 * poly file is the gml:Id of the Solid representing the building or building part;
 * the Id is followed by "dot0" for the exterior shell and a "dot sequence number" for
 * interior shells; when solid has no gml:Id, the poly file get a sequential number 
 * as first part of the file name followed by the dot 0 or dot sequence number as before.
 * @author kooijmanj1
 */
public class VReaderWriter {
	private String destinationName;
	private VInputFile input;
	private VOutputFile output;
	private File sourceFile;
	private File destinationFolder;
	private int shellNr;
	private int buildingNr = 1;
	
	public VReaderWriter(File sourceFile, File destinationFolder){
		this.sourceFile = sourceFile;
		this.destinationFolder = destinationFolder;
	}

	public void organizeConversion() throws Exception{
		input = new VInputFile(sourceFile);
		VStringStore stringStore = new VStringStore();
		ArrayList<Building> buildingList = input.readAllBuildings();	
		for (Building building : buildingList){
			VConstruct<Building> construct = new VConstruct<Building>();
			construct.store(building);
			construct.setStringStore(stringStore);
			construct.organize();
			shellNr = 0;
			String solidId = "";
			for (String shellString :stringStore.getShellStrings() ){	
				String fileName = "";
				if (shellNr == 0){	//position 0 of shellStrings holds solidId
					solidId = shellString;
					System.out.println(solidId);
				}
				else{
					if (solidId.substring(0,2).equals("-1")){
						fileName = fileName + shellNr;
					}
					destinationName = destinationFolder.getPath() + "/" + fileName + solidId + ".poly"; 
					output = new VOutputFile(new File(destinationName));
					output.writeBuilding(shellString);
				}
				shellNr++;
			}
			buildingNr++;
			stringStore.clear();
		}
	}
	
	public int getNumberOfBuildings(){
		return buildingNr - 1;
	}
	
	public int getNumberOfShells(){
		return shellNr;
	}
}
