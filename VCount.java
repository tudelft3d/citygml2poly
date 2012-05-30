

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.BuildingPartProperty;


/**
 * Responsible count of buildings, buildingparts, solids, faces, polygons
 * @author kooijmanj
 * 12-04-2012
 *
 */
public class VCount {
	int nrBuildings;
	int nrBuildingParts;
	Building building;
	File sourceFile;
	
	
	public VCount(File sourceFile){
		this.sourceFile = sourceFile;
	}
	
	public void countBuildings(){
		System.out.println("countBuildings begonnen");
		VInputFile input = new VInputFile(sourceFile);
		ArrayList<Building> buildingList = null;
		try {
			buildingList = input.readAllBuildings();
		} catch (VInputOutputException e) {
			System.out.println("VInputOutputException");
			e.printStackTrace();
		}
		for(Building building : buildingList){
			nrBuildings++;
			nrBuildingParts = nrBuildingParts + countBuildingParts(building);
			System.out.println("nrBuilding: " + nrBuildings + " " + countBuildingParts(building) + " " + nrBuildingParts);
		}
	}
	
	private int countBuildingParts(Building building){
		int nrBuildingPartsInBuilding = 0;
		
		if (building.isSetConsistsOfBuildingPart()  ){
			List<BuildingPartProperty> listBuildingPartProperty = building.getConsistsOfBuildingPart();
			for (BuildingPartProperty buildingPartProperty : listBuildingPartProperty){
				nrBuildingPartsInBuilding++;
			}
		}
		return nrBuildingPartsInBuilding;
	}
	
	public int getNrOfBuildings(){
		return nrBuildings;
	}
	
	public int getNrOfBuildingParts(){
		return nrBuildingParts;
	}
}
