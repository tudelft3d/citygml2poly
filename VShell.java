

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.complexes.CompositeSurfaceImpl;
import org.citygml4j.impl.gml.geometry.primitives.LinearRingImpl;
import org.citygml4j.impl.gml.geometry.primitives.PolygonImpl;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.PosOrPointPropertyOrPointRep;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;

/**
 * Responsible for the organization of a shell ( exterior or interior) into a poly file.
 * @author kooijmanj1
 */
public class VShell {
	private final String NO_ID_INDICATOR = "-1";
	private final int GEOMETRY_INDICATOR = 1; // for Solid geometry
	private ArrayList<VFacet> facets = new ArrayList<VFacet>();
	private VUnicNodes unicNodes;
	private VFacet facet;
	private VPolygon polygon;
	private VReferedElement element;
	private String polygonId;
	private String compositeSurfaceGmlId;
	private int lod;
	
	public VShell(VUnicNodes unicNodes, int lod){
		this.unicNodes = unicNodes;
		this.lod = lod;
	}
	
	public void organize(SurfaceProperty surfaceProperty){
		CompositeSurfaceImpl compositeSurfaceImpl = (CompositeSurfaceImpl)surfaceProperty.getObject();
		compositeSurfaceGmlId = compositeSurfaceImpl.getId();
		if (compositeSurfaceGmlId==null){
			compositeSurfaceGmlId = NO_ID_INDICATOR;
		}
		List<SurfaceProperty> surfaceMember = compositeSurfaceImpl.getSurfaceMember(); 
		for (SurfaceProperty surfaceMemberElement : surfaceMember){
			PolygonImpl polygonImpl = (PolygonImpl)surfaceMemberElement.getSurface();
			if (polygonImpl == null){ 
				polygonId  = (surfaceMemberElement.getHref()).substring(1);
				element = new VReferedElement(surfaceMemberElement, lod);
				element.search(polygonId);
				polygonImpl = element.getPolygonImpl();
			}
			else{
				polygonId = polygonImpl.getId();
				if (polygonId == null){
					polygonId = NO_ID_INDICATOR;
				}
			}
			facet = new VFacet(polygonId);		
			AbstractRingProperty ringPropertyExt = polygonImpl.getExterior(); 
			LinearRingImpl linearRingImpl = (LinearRingImpl)ringPropertyExt.getObject();
			
			List<PosOrPointPropertyOrPointRep> pppList = null;
			if(linearRingImpl.isSetPosOrPointPropertyOrPointRep()){
				pppList = linearRingImpl.getPosOrPointPropertyOrPointRep();
				polygon = new VPolygon(unicNodes, pppList);
				facet.addPolygon(polygon);
			}
			
			DirectPositionList posList= null;
			if(linearRingImpl.isSetPosList()){
				posList = linearRingImpl.getPosList();
				polygon = new VPolygon(unicNodes, posList);
				facet.addPolygon(polygon);
			}
			
			List<AbstractRingProperty> listAbstractRingProperty = polygonImpl.getInterior();
			for (AbstractRingProperty ringPropertyInt : listAbstractRingProperty){
				if(ringPropertyInt != null){
					linearRingImpl = (LinearRingImpl)ringPropertyInt.getObject();
					pppList = linearRingImpl.getPosOrPointPropertyOrPointRep();
					polygon = new VPolygon(unicNodes, pppList);
					facet.addPolygon(polygon);
					makeHolePoint();
				}
			}	
			facets.add(facet);
		}
	}
	
	/**
	 * As long as we don't have a proper algorithm to calculate, we just give a fixed coordinate.
	 */
	private void makeHolePoint(){ 
		VNode holePoint = new VNode(0.5, 0.5, 0.5);
		facet.addHolePoint(holePoint);
	}
	/**
	 * Concatenates all the parts of underlying objects to one poly file string.
	 */
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		// First line of poly file
		String str = "# " + compositeSurfaceGmlId + lineSeparator;
		// Second line of poly indicates the origin of the shell data
		str = str + "# " + GEOMETRY_INDICATOR + lineSeparator;
		//# Part 1 - node list
		str = str + unicNodes.getSize() + "  " + "3" + " " + "0" + " " + "0" + lineSeparator;
		//# Node index, node ordinates
		int i = 0;
		for (VNode node : unicNodes.getUnicNodes()){
			str = str + i + "  " + node.toString() + lineSeparator;
			i++;
		}
		//# Part 2 - facet list
		str = str + facets.size() + " 0" + lineSeparator;
		for (VFacet facet : facets){
			str = str + facet.toString();	
		}
		str = str + "0" + lineSeparator;
		str = str + "0" + lineSeparator;
	return str;
	}
}







