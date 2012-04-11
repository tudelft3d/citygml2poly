package lezers6Domain;

import java.util.List;

import org.citygml4j.impl.citygml.building.BuildingPartImpl;
import org.citygml4j.impl.gml.geometry.primitives.LinearRingImpl;
import org.citygml4j.impl.gml.geometry.primitives.PolygonImpl;
import org.citygml4j.impl.gml.geometry.primitives.SolidImpl;
import org.citygml4j.impl.gml.geometry.primitives.SolidPropertyImpl;
import org.citygml4j.impl.gml.geometry.primitives.SurfacePropertyImpl;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.impl.gml.geometry.complexes.*;

public class VReferedElement {
	private SurfaceProperty surfaceMemberElement;
	PolygonImpl polygonImpl;
	
	public VReferedElement(SurfaceProperty surfaceMemberElement){
		this.surfaceMemberElement = surfaceMemberElement;
	}
	
	public void search(String polygonId){
		CompositeSurfaceImpl compositeSurfaceImpl = (CompositeSurfaceImpl)surfaceMemberElement.getParent();
		SurfacePropertyImpl surfacePropertyImpl = (SurfacePropertyImpl)compositeSurfaceImpl.getParent();
		SolidImpl solidImpl = (SolidImpl)surfacePropertyImpl.getParent();
		SolidPropertyImpl solidPropertyImpl = (SolidPropertyImpl)solidImpl.getParent();
		BuildingPartImpl buildingPartImpl = (BuildingPartImpl)solidPropertyImpl.getParent();
		List<BoundarySurfaceProperty> listSurfaceProperty = buildingPartImpl.getBoundedBySurface();
		for(BoundarySurfaceProperty boundarySurfaceProperty : listSurfaceProperty){
			AbstractBoundarySurface abstractBoundarySurface = boundarySurfaceProperty.getObject();
			MultiSurfaceProperty multiSurfaceProperty = abstractBoundarySurface.getLod2MultiSurface();
			MultiSurface multiSurface = multiSurfaceProperty.getMultiSurface();
			List<SurfaceProperty> surfaceMember = multiSurface.getSurfaceMember();
			int i=0;
			for (SurfaceProperty surfaceMemberElement : surfaceMember){
				PolygonImpl polygonImpl = (PolygonImpl)surfaceMemberElement.getSurface();
				if((polygonImpl.getId()).equals(polygonId)){
					this.polygonImpl = polygonImpl;
					System.out.println(polygonImpl.toString());
					i++;
				}
			}
			System.out.println("Zo vaak een id gelezen: " + i);
			System.out.println("klaar");
		}
	}
	
	public PolygonImpl getPolygonImpl(){
		return polygonImpl;
	}

}
