

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
 * Generic type to be responsible as well for Building as for BuildingPart
 * analysis of geometries; it should work recursively when BuildingPart 
 * exists;
 * @author kooijmanj1
 * @param <B>
 */
public class VConstruct<BuildingOrBuildingPart extends AbstractBuilding>{
	private BuildingOrBuildingPart object;
	private VSolid solid;
	private VBoundedBySurface boundedBySurface;
	private ArrayList<String> shellStrings;
	private VStringStore stringStore;
	private VUnicNodes unicNodes = new VUnicNodes();
	private String objectId = "-1";
	
	
	public void store(BuildingOrBuildingPart object){
		this.object = object;
	}
	
	public void setShellStrings(ArrayList<String> shellStrings){
		this.shellStrings = shellStrings;
	}
	
	public void setStringStore(VStringStore stringStore){
		this.stringStore = stringStore;
	}
	
	public void setUnicNodes(VUnicNodes unicNodes){
		this.unicNodes = unicNodes;
	}
	
	public void organize(){
		if(object.isSetLod1Solid()){
			if (object.getId() != null){
				objectId = object.getId();
			}
			solid = new VSolid(objectId, object.getLod1Solid(), unicNodes);
			solid.organize();
			stringStore.store(solid.getShellStrings());
		}
		if(object.isSetLod2Solid()){
			String objectId = object.getId();
			solid = new VSolid(objectId, object.getLod2Solid(), unicNodes);
			solid.organize();
			stringStore.store(solid.getShellStrings());
		}
//		if (object.isSetBoundedBySurface()){
//			boundedBySurface = new VBoundedBySurface(object.getBoundedBySurface(), unicNodes);
//			boundedBySurface.organize();
//			shellStrings = boundedBySurface.getShellStrings();
//			stringStore.store(boundedBySurface.getShellStrings());
//		}
		
		if(object.isSetConsistsOfBuildingPart()){
			for (BuildingPartProperty buildingPartProperty : object.getConsistsOfBuildingPart()){
				ArrayList<String> keepShellStrings = shellStrings;
				VStringStore keepStringStore = stringStore;
				BuildingPart buildingPart = buildingPartProperty.getBuildingPart();
				VConstruct<BuildingPart> construct = new VConstruct<BuildingPart>();
				construct.store(buildingPart);
				construct.setShellStrings(keepShellStrings);
				construct.setStringStore(keepStringStore);
				construct.organize();
			}
		}	
	}
}
