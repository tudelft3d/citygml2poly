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
	private String sourceName = "c:/CityGMLData/DenHaag/" + geometryName + ".xml";
	private String destinationName;
	private VInputFile input = new VInputFile(new File(sourceName));
	private VOutputFile output;


	public static void main(String[] args) throws Exception{
		VStringStore stringStore = new VStringStore();
		VReaderWriter readerWriter = new VReaderWriter();	
		ArrayList<Building> buildingList = readerWriter.input.readAllBuildings();	
		int shellNr = 1;
		readerWriter.destinationName = "c:/PolyFilesDenHaag/" + readerWriter.geometryName + shellNr + ".poly";
		for (Building building : buildingList){
			String buildingId = building.getId();
			System.out.println("BuildingId: " + buildingId);
			VConstruct<Building> construct = new VConstruct<Building>();
			construct.store(building);
			construct.setStringStore(stringStore);
			construct.organize();
			for (String str :stringStore.getShellStrings() ){	
				System.out.println(readerWriter.destinationName);
				System.out.println(str);
				readerWriter.output = new VOutputFile(new File(readerWriter.destinationName));
				readerWriter.output.writeBuilding(str);
				readerWriter.destinationName = "c:/PolyFilesDenHaag/" + readerWriter.geometryName + "-shell-" + shellNr + ".poly";
				shellNr++;
			}
		}
	}
}
