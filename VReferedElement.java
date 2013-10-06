

import java.util.List;


import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.complexes.CompositeSurface;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.Solid;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;


/** 
 * THis class is not using presently, as now the Solid geometry is not checked directly with reference to the MultiSurface
 * values of the BoundedBySurface Feature. In stead the latter are checked directly and the depending Solid is not checked
 * anymore.
 * @author jan
 *
 */
public class VReferedElement {
	private SurfaceProperty surfaceMemberElement;
	Polygon polygon;
	List<BoundarySurfaceProperty> listSurfaceProperty;
	private int lod = -1;
	
	public VReferedElement(SurfaceProperty surfaceMemberElement, int lod){
		this.surfaceMemberElement = surfaceMemberElement;
		this.lod =  lod;
	}
	
	public void search(String polygonId){
		CompositeSurface compositeSurface = (CompositeSurface)surfaceMemberElement.getParent();// These four lines need to be

		SurfaceProperty surfaceProperty = (SurfaceProperty)compositeSurface.getParent();// made also for refered multisurface object

		Solid solid = (Solid)surfaceProperty.getParent();								// made also for refered multisurface object

		SolidProperty solidProperty = (SolidProperty)solid.getParent();					// made also for refered multisurface object
		
		
		// test if polygonId is searched within Building or BuildingPart instance
		if((solidProperty.getParent()) instanceof BuildingPart){
			BuildingPart buildingPart = (BuildingPart)solidProperty.getParent();
			listSurfaceProperty = buildingPart.getBoundedBySurface();
		}
		if((solidProperty.getParent()) instanceof Building){
			Building building = (Building)solidProperty.getParent();
			listSurfaceProperty = building.getBoundedBySurface();
		}
		for(BoundarySurfaceProperty boundarySurfaceProperty : listSurfaceProperty){
			AbstractBoundarySurface abstractBoundarySurface = boundarySurfaceProperty.getObject();
			MultiSurfaceProperty multiSurfaceProperty = null;
			if ( lod == 2){
				 multiSurfaceProperty = abstractBoundarySurface.getLod2MultiSurface();
			}
			if ( lod == 3){
				 multiSurfaceProperty = abstractBoundarySurface.getLod3MultiSurface();
			}
			if ( lod == 4){
				 multiSurfaceProperty = abstractBoundarySurface.getLod4MultiSurface();
			}
			MultiSurface multiSurface = multiSurfaceProperty.getMultiSurface();
			List<SurfaceProperty> surfaceMember = multiSurface.getSurfaceMember();
			for (SurfaceProperty surfaceMemberElement : surfaceMember){
				Polygon polygon = (Polygon)surfaceMemberElement.getSurface();
				if((polygon.getId()).equals(polygonId)){
					this.polygon = polygon;
				}
			}
		}
	}
	
	public Polygon getPolygon(){
		return polygon;
	}
}
