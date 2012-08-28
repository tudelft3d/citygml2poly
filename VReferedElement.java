

import java.util.List;

import org.citygml4j.impl.citygml.building.BuildingImpl;
import org.citygml4j.impl.citygml.building.BuildingPartImpl;
import org.citygml4j.impl.gml.geometry.primitives.LinearRingImpl;
import org.citygml4j.impl.gml.geometry.primitives.PolygonImpl;
import org.citygml4j.impl.gml.geometry.primitives.SolidImpl;
import org.citygml4j.impl.gml.geometry.primitives.SolidPropertyImpl;
import org.citygml4j.impl.gml.geometry.primitives.SurfacePropertyImpl;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.impl.gml.geometry.aggregates.MultiSurfaceImpl;
import org.citygml4j.impl.gml.geometry.complexes.*;

/** 
 * THis class is not using presently, as now the Solid geometry is not checked directly with reference to the MultiSurface
 * values of the BoundedBySurface Feature. In stead the latter are checked directly and the depending Solid is not checked
 * anymore.
 * @author jan
 *
 */
public class VReferedElement {
	private SurfaceProperty surfaceMemberElement;
	PolygonImpl polygonImpl;
	List<BoundarySurfaceProperty> listSurfaceProperty;
	private int lod;
	
	public VReferedElement(SurfaceProperty surfaceMemberElement, int lod){
		this.surfaceMemberElement = surfaceMemberElement;
		this.lod =  lod;
	}
	
	public void search(String polygonId){
		CompositeSurfaceImpl compositeSurfaceImpl = (CompositeSurfaceImpl)surfaceMemberElement.getParent();// These four lines need to be

		SurfacePropertyImpl surfacePropertyImpl = (SurfacePropertyImpl)compositeSurfaceImpl.getParent();// made also for refered multisurface object

		SolidImpl solidImpl = (SolidImpl)surfacePropertyImpl.getParent();								// made also for refered multisurface object

		SolidPropertyImpl solidPropertyImpl = (SolidPropertyImpl)solidImpl.getParent();					// made also for refered multisurface object
		
		
		// test if polygonId is searched within Building or BuildingPart instance
		if((solidPropertyImpl.getParent()) instanceof BuildingPartImpl){
			BuildingPartImpl buildingPartImpl = (BuildingPartImpl)solidPropertyImpl.getParent();
			listSurfaceProperty = buildingPartImpl.getBoundedBySurface();
		}
		if((solidPropertyImpl.getParent()) instanceof BuildingImpl){
			BuildingImpl buildingImpl = (BuildingImpl)solidPropertyImpl.getParent();
			listSurfaceProperty = buildingImpl.getBoundedBySurface();
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
			int i=0;
			for (SurfaceProperty surfaceMemberElement : surfaceMember){
				PolygonImpl polygonImpl = (PolygonImpl)surfaceMemberElement.getSurface();
				if((polygonImpl.getId()).equals(polygonId)){
					this.polygonImpl = polygonImpl;
					i++;
				}
			}
		}
	}
	
	public PolygonImpl getPolygonImpl(){
		return polygonImpl;
	}
}
