package lezers6Domain;

import java.io.File;
import java.util.ArrayList;
import lezers4Data.VInputFile;
import lezers4Data.VOutputFile;

import org.citygml4j.model.citygml.building.AbstractBuilding;
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
	private String geometryName = "13_buildings";
	private String destinationName;
	private VInputFile input;
	private VOutputFile output;
	private File sourceFile;
	private File destinationFolder;
	private File resultFile;
	private int shellNr = 0;
	private int buildingNr = 0;
	
	public VReaderWriter(File sourceFile, File destinationFolder){
		this.sourceFile = sourceFile;
		this.destinationFolder = destinationFolder;
	}


	public void organizeConversion() throws Exception{
		input = new VInputFile(sourceFile);
		VStringStore stringStore = new VStringStore();
		ArrayList<Building> buildingList = input.readAllBuildings();	

		destinationName = destinationFolder.getPath() + geometryName + "-shell-" + shellNr + ".poly";
		for (Building building : buildingList){
			String buildingId = building.getId();
			System.out.println("BuildingId: " + buildingId);
			VConstruct<Building> construct = new VConstruct<Building>();
			construct.store(building);
			construct.setStringStore(stringStore);
			construct.organize();
			for (String str :stringStore.getShellStrings() ){	
				System.out.println(destinationName);
				System.out.println(str);
				output = new VOutputFile(new File(destinationName));
				output.writeBuilding(str);
				destinationName = destinationFolder.getPath() +"/"+ geometryName + "-shell-" + shellNr + ".poly";
				shellNr++;
			}
			buildingNr++;
			stringStore.clear();
			System.out.println("Number of buildings: " + buildingNr );
			System.out.println("Number of shells: " + shellNr);
		}
	}
	
	public int getNumberOfBuildings(){
		return buildingNr;
	}
	
	public int getNumberOfShells(){
		return shellNr;
	}
}
