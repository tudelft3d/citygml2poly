import java.util.ArrayList;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.BuildingPartProperty;

/**
 * Generic type to be responsible as well for Building as for BuildingPart
 * analysis of geometries; it works recursively when BuildingPart 
 * exists;
 * @author kooijmanj1
 * @param <BuildingOrBuildingPart>
 */
public class VConstruct<BuildingOrBuildingPart extends AbstractBuilding>{
	private BuildingOrBuildingPart object;
	private VSolid solid;
	private VMultiSurface multiSurface;
	private ArrayList<String[]> shellDataArrays;
	private VShellDataStore shellDataStore;
	private VUnicNodes unicNodes = new VUnicNodes();
	
	public void store(BuildingOrBuildingPart object){
		this.object = object;
	}
		
	public void setShellDataArrays(ArrayList<String[]> shellDataArrays){
		this.shellDataArrays = shellDataArrays;
	}
	
	public void setShellDataStore(VShellDataStore shellDataStore){
		this.shellDataStore = shellDataStore;
	}
	
	public void setUnicNodes(VUnicNodes unicNodes){
		this.unicNodes = unicNodes;
	}
	
	/**
	 * For conditions that various geometries may occur at the various 
	 * LODs see CityGML Encoding standard (OGC 08-007r1 page 61).
	 * Only one of lod1MultiSurface and lod1Solid properties must be used.
	 * If building isSetBoundedBySurface than on that level MultiSurface 
	 * will appear in LOD2, LOD3 and/or LOD4.
	 * In case building isSetBoundedBySurface than additional 
	 * MultiSurface and Solid geometries on building level
	 * should refer to the MultiSurface implementation on BoundarySurface level.
	 * If building is not isSetBoundedBySurface than on all LODs Solid and 
	 * MultiSurface could have their own geometric representation.
	 */
	public void organize(){
		if(object.isSetLod1MultiSurface()){
			multiSurface = new VMultiSurface(object.getLod1MultiSurface(), unicNodes);
			multiSurface.organize();
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		if(object.isSetLod1Solid()){
			solid = new VSolid(object.getLod1Solid(), unicNodes);
			solid.organize();
			shellDataStore.store(solid.getShellDataArray());
		}
		if (object.isSetBoundedBySurface()){
			multiSurface = new VMultiSurface(unicNodes);
			multiSurface.organize(object.getBoundedBySurface());
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		else{
			if(object.isSetLod2MultiSurface()){
				multiSurface = new VMultiSurface(object.getLod2MultiSurface(), unicNodes);
				multiSurface.organize();
				shellDataStore.store(multiSurface.getShellDataArray());
			}
			if(object.isSetLod2Solid()){
				solid = new VSolid(object.getLod2Solid(), unicNodes);
				solid.organize();
				shellDataStore.store(solid.getShellDataArray());
			}
			if(object.isSetLod3MultiSurface()){
				multiSurface = new VMultiSurface(object.getLod3MultiSurface(), unicNodes);
				multiSurface.organize();
				shellDataStore.store(multiSurface.getShellDataArray());
			}
			if(object.isSetLod3Solid()){
				solid = new VSolid(object.getLod3Solid(), unicNodes);
				solid.organize();
				shellDataStore.store(solid.getShellDataArray());
			}
			if(object.isSetLod4MultiSurface()){
				multiSurface = new VMultiSurface(object.getLod4MultiSurface(), unicNodes);
				multiSurface.organize();
				shellDataStore.store(multiSurface.getShellDataArray());
			}
			if(object.isSetLod4Solid()){
				solid = new VSolid(object.getLod4Solid(), unicNodes);
				solid.organize();
				shellDataStore.store(solid.getShellDataArray());
			}
		}

		if(object.isSetConsistsOfBuildingPart()){
			for (BuildingPartProperty buildingPartProperty : object.getConsistsOfBuildingPart()){
				ArrayList<String[]> keepShellDataArrays = shellDataArrays;
				VShellDataStore keepShellDataStore = shellDataStore;
				BuildingPart buildingPart = buildingPartProperty.getBuildingPart();
				VConstruct<BuildingPart> construct = new VConstruct<BuildingPart>();
				construct.store(buildingPart);
				construct.setShellDataArrays(keepShellDataArrays);
				construct.setShellDataStore(keepShellDataStore);
				construct.organize();
			}
		}	
	}
}
