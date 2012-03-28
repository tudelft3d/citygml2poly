package lezers6Domain;

import java.io.File;
import java.util.ArrayList;
import lezers4Data.VInputFile;
import lezers4Data.VOutputFile;
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
	private String geometryName = "cubepy2";
	private String sourceName = "c:/CityGMLData/Testdata1/" + geometryName + ".xml";
	private String destinationName;
	private VInputFile input = new VInputFile(new File(sourceName));
	private VOutputFile output;


	public static void main(String[] args) throws Exception{
		VReaderWriter readerWriter = new VReaderWriter();	
		ArrayList<Building> buildingList = readerWriter.input.readAllBuildings();	
		Building building = buildingList.get(0);
		VBuilding vbuilding = new VBuilding(building);
		vbuilding.organize();
		
		int shellNr = 0;
		String seqNr = "-7";
		String stringShellNr = "Exterior-of-";
		String geometryCore = readerWriter.geometryName;
		readerWriter.geometryName = stringShellNr + readerWriter.geometryName;
		readerWriter.destinationName = "c:/PolyFiles/" + readerWriter.geometryName + seqNr + ".txt";
		for (String str :vbuilding.getShellStrings() ){
			readerWriter.output = new VOutputFile(new File(readerWriter.destinationName));
			readerWriter.output.writeBuilding(str);
			stringShellNr = "Interior-" + shellNr + "-of-";
			readerWriter.geometryName = stringShellNr + geometryCore;
			readerWriter.destinationName = "c:/PolyFiles/" + readerWriter.geometryName + seqNr + ".txt";
			shellNr++;
		}
	}
}
