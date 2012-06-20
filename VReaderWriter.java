

import java.io.File;
import java.util.ArrayList;

import org.citygml4j.model.citygml.building.Building;


/**
 * 7-3-2012
 * Vandaag packages 6 gemaakt. Ook interior shell werkt nu goed, vanaf versie 8 van de poly files
 * Responsible for the overall process; voorlopig want wanneer ik GUI klas erbij
 * maak komt daar mogelijk een deel van de regie te liggen.
 * @author kooijmanj1
 *
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
