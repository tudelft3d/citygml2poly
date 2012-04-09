package lezers6Domain;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.primitives.SolidImpl;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.BuildingPartProperty;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;


/**
 * 6-04-2012
 * Generic type to be responsible as well for Building as for BuildingPart
 * analysis of geometries; it should work recursively when BuildingPart 
 * exists;
 * @author kooijmanj1
 *
 * @param <B>
 */
public class VConstruct<B extends AbstractBuilding> {
	private B object;
	private VSolid solid;
	private VBoundedBySurface boundedBySurface;
	private ArrayList<String> shellStrings;
	private VUnicNodes unicNodes = new VUnicNodes();
	
	
	public void store(B object){
		this.object = object;
	}
	
	public void organize(){
		if(object.isSetLod1Solid()){
			solid = new VSolid(object.getLod1Solid(), unicNodes);
			solid.organize();
			shellStrings = solid.getShellStrings();
		}
		if(object.isSetLod2Solid()){
			solid = new VSolid(object.getLod2Solid(), unicNodes);
			solid.organize();
			shellStrings = solid.getShellStrings();
		}
		if (object.isSetBoundedBySurface()){
			System.out.println("Found: BoundedBySurface" );
			boundedBySurface = new VBoundedBySurface(object.getBoundedBySurface(), unicNodes);
			boundedBySurface.organize();
			shellStrings = boundedBySurface.getShellStrings();
			System.out.println("Finalized: BoundedBySurface");
		}
		
		if(object.isSetConsistsOfBuildingPart()){
			for (BuildingPartProperty buildingPartProperty : object.getConsistsOfBuildingPart()){
				BuildingPart buildingPart = buildingPartProperty.getBuildingPart();
				VConstruct<BuildingPart> construct = new VConstruct<BuildingPart>();
				construct.store(buildingPart);
				construct.organize();
				shellStrings.addAll(construct.getShellStrings());
			}
		}	
	}
	
	public ArrayList<String> getShellStrings(){
		return shellStrings;
	}	
}
