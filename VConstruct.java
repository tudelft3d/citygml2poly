import java.util.ArrayList;

import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.BuildingPartProperty;
//import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;

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
	private int  lod = -1;
	private boolean is_Semantics = false;
	
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
	
	public void SetIsSemantics(boolean set){
		this.is_Semantics = set;
	}
	
	/**
	 * For conditions that various geometries may occur at the various 
	 * LODs see CityGML Encoding standard (OGC 08-007r1 page 61).
	 * Only one of lod1MultiSurface and lod1Solid properties must be used.
	 * If building isSetBoundedBySurface than on that level MultiSurface 
	 * will appear in LOD2, LOD3 and/or LOD4.
	 * In that case additional MultiSurface and Solid geometries on building level
	 * should refer to the MultiSurface implementation on BoundarySurface level.
	 * If building is not isSetBoundedBySurface than on all LODs Solid and 
	 * MultiSurface could have their own geometric representation.
	 */
	public void organize(){
		String BuildingId = object.getId();
		//BuildingParts
		if(object.isSetConsistsOfBuildingPart()){
			for (BuildingPartProperty buildingPartProperty : object.getConsistsOfBuildingPart()){
				ArrayList<String[]> keepShellDataArrays = shellDataArrays;
				VShellDataStore keepShellDataStore = shellDataStore;
				BuildingPart buildingPart = buildingPartProperty.getBuildingPart();
				VConstruct<BuildingPart> construct = new VConstruct<BuildingPart>();
				construct.SetIsSemantics(is_Semantics);
				construct.store(buildingPart);
				construct.setShellDataArrays(keepShellDataArrays);
				construct.setShellDataStore(keepShellDataStore);
				construct.organize();
			}
		}	
		//LoD1
		if(object.isSetLod1MultiSurface()){
			lod = 1;
			multiSurface = new VMultiSurface(unicNodes, lod);
			multiSurface.SetIsSemantics(is_Semantics);
			multiSurface.SetID(BuildingId);
			multiSurface.organize(object.getLod1MultiSurface());
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		else if(object.isSetLod1Solid()){
			lod = 1;
			solid = new VSolid(object.getLod1Solid(), unicNodes, lod );
			solid.SetId(BuildingId);
			solid.organize();
			shellDataStore.store(solid.getShellDataArray());
		}		
		//LoD2
		if (object.isSetBoundedBySurface() &&
				(object.isSetLod2Solid() || object.isSetLod2MultiSurface())){
			lod = 2;
			multiSurface = new VMultiSurface(unicNodes, lod);
			multiSurface.SetIsSemantics(is_Semantics);
			multiSurface.SetID(BuildingId);
			if(object.isSetLod2Solid())
				multiSurface.SetIsSolidBoundary(true);
			multiSurface.organize(object.getBoundedBySurface());
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		else if(object.isSetLod2MultiSurface()){
			lod = 2;
			multiSurface = new VMultiSurface(unicNodes, lod);
			multiSurface.SetIsSemantics(is_Semantics);
			multiSurface.SetID(BuildingId);
			multiSurface.organize(object.getLod2MultiSurface());
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		else if(object.isSetLod2Solid()){
			lod = 2;
			solid = new VSolid(object.getLod2Solid(), unicNodes, lod );
			solid.SetId(BuildingId);
			solid.organize();
			shellDataStore.store(solid.getShellDataArray());
		}
		
		//LoD3
		if (object.isSetBoundedBySurface() &&
				(object.isSetLod3Solid() || object.isSetLod3MultiSurface() ) ){
			lod = 3;
			multiSurface = new VMultiSurface(unicNodes, lod);
			multiSurface.SetIsSemantics(is_Semantics);
			multiSurface.SetID(BuildingId);
			if(object.isSetLod3Solid())
				multiSurface.SetIsSolidBoundary(true);
			multiSurface.organize(object.getBoundedBySurface());
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		else if(object.isSetLod3MultiSurface()){
			lod = 3;
			multiSurface = new VMultiSurface(unicNodes, lod);
			multiSurface.SetIsSemantics(is_Semantics);
			multiSurface.SetID(BuildingId);
			multiSurface.organize(object.getLod3MultiSurface());
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		else if(object.isSetLod3Solid()){
			lod = 3;
			solid = new VSolid(object.getLod3Solid(), unicNodes, lod );
			solid.SetId(BuildingId);
			solid.organize();
			shellDataStore.store(solid.getShellDataArray());
		}
		
		//LoD4
		if (object.isSetBoundedBySurface() &&
				(object.isSetLod4Solid() || object.isSetLod4MultiSurface())){
			lod = 4;
			multiSurface = new VMultiSurface(unicNodes, lod);
			multiSurface.SetIsSemantics(is_Semantics);
			multiSurface.SetID(BuildingId);
			if(object.isSetLod4Solid())
				multiSurface.SetIsSolidBoundary(true);
			multiSurface.organize(object.getBoundedBySurface());
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		else if(object.isSetLod4MultiSurface()){
			lod = 4;
			multiSurface = new VMultiSurface(unicNodes, lod);
			multiSurface.SetIsSemantics(is_Semantics);
			multiSurface.SetID(BuildingId);
			multiSurface.organize(object.getLod4MultiSurface());
			shellDataStore.store(multiSurface.getShellDataArray());
		}
		else if(object.isSetLod4Solid()){
			lod = 4;
			solid = new VSolid(object.getLod4Solid(), unicNodes, lod );
			solid.SetId(BuildingId);
			solid.organize();
			shellDataStore.store(solid.getShellDataArray());
		}
	}
}
