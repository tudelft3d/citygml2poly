package lezers5Domain;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.primitives.SolidImpl;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;

/**
 * 12-02-2012
 * This class is responsible for the building data that are read
 * out of a CityGML file and have to be written to a Poly file
 * @author kooijmanj1
 *
 */
public class VBuilding {
	private String name;// name of the building in the CityGML file e.g. Building_1
	private Building building;
	private VShell shell;
	private ArrayList<String> shellStrings = new ArrayList<String>();
	
	
	public VBuilding(Building building){
		this.building = building;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Detects type of surface property (Solid, MultiSurface, etc)and
	 * puts the corresponding private organizeXxx to work.
	 */
	public void organize(){
		System.out.println("Begonnen");
		// boven dit niveau nog onderscheid maken tussen de building parts?

		if(building.isSetLod1Solid()){
			organizeSolid(building.getLod1Solid());
		}
		if(building.isSetLod2Solid()){
			organizeSolid(building.getLod2Solid());
		}
	}
	
	private void organizeSolid(SolidProperty solidProperty){
		SolidImpl solidImpl = (SolidImpl)solidProperty.getObject();		
		SurfaceProperty exteriorSurfaceProperty = solidImpl.getExterior(); //exterior van de solid
		shell = new VShell();
		shell.organisizeShell(exteriorSurfaceProperty);
		shellStrings.add(shell.toString());
		// now the inner shells
		List<SurfaceProperty> surfacePropertyList = solidImpl.getInterior();
		for (SurfaceProperty interiorSurfaceProperty : surfacePropertyList){
			shell = new VShell();
			shell.organisizeShell(interiorSurfaceProperty);
			shellStrings.add(shell.toString());
		}
	}

	public ArrayList<String> getShellStrings(){
		return shellStrings;
	}
}
