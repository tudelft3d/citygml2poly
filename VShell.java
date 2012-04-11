package lezers6Domain;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.complexes.CompositeSurfaceImpl;
import org.citygml4j.impl.gml.geometry.primitives.LinearRingImpl;
import org.citygml4j.impl.gml.geometry.primitives.PolygonImpl;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.PosOrPointPropertyOrPointRep;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.util.xlink.XLinkResolver;

/**
 * 7-3-2012
 * Responsible for the organization of a shell ( exterior or interior) into a poly file
 * @author kooijmanj1
 */
public class VShell {
	private ArrayList<VFacet> facets = new ArrayList<VFacet>();
	private VUnicNodes unicNodes;
	private VFacet facet;
	private VPolygon polygon;
	private String polygonId;
	private String objectId;
	private String solidId;
	
	
	public VShell(String solidId, VUnicNodes unicNodes){
		this.solidId = solidId;
		this.unicNodes = unicNodes;
	}
	
	public void organize(SurfaceProperty surfaceProperty){
		CompositeSurfaceImpl compositeSurfaceImpl = (CompositeSurfaceImpl)surfaceProperty.getObject();
		List<SurfaceProperty> surfaceMember = compositeSurfaceImpl.getSurfaceMember(); 
		int facetNr = 0;
		for (SurfaceProperty surfaceMemberElement : surfaceMember){
			PolygonImpl polygonImpl = (PolygonImpl)surfaceMemberElement.getSurface();
			if (polygonImpl == null){ 
				polygonId  = (surfaceMemberElement.getHref()).substring(1);
				System.out.println("Refered polygonId: " + polygonId);
				VReferedElement element = new VReferedElement(surfaceMemberElement);
				element.search(polygonId);
				polygonImpl = element.getPolygonImpl();
			}
			else{
				polygonId = polygonImpl.getId();
				if (polygonId == null){
					polygonId = "-1";
				}
				System.out.println("polygonId: " + polygonId);
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
			facetNr++;
		}
	}
	
	/**
	 * As long as we don't have a proper algorithm to calculate, we just give a fixed coordinate.
	 */
	private void makeHolePoint(){ // fake as long as we don't know how to calculate
		VNode holePoint = new VNode(0.5, 0.5, 0.5);
		facet.addHolePoint(holePoint);
	}
	
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		String str = "# " + solidId + lineSeparator;
		//str = str + "# node count, dim, attribute count, boundary marker (0|1) " + lineSeparator;
		str = str + unicNodes.getSize() + "  " + "3" + " " + "0" + " " + "0" + lineSeparator;
		//str = str + "# Node index, node coordinates " + lineSeparator;
		int i = 0;
		for (VNode node : unicNodes.getUnicNodes()){
			str = str + i + "  " + node.toString() + lineSeparator;
			i++;
		}
		//str = str + lineSeparator;
		//str = str + "# Part 2 - facet list" + lineSeparator;
		//str = str + "# facet count, boundary marker (0|1)" + lineSeparator;
		str = str + facets.size() + " 0" + lineSeparator;// boundary marker mendatory
		//str = str + "# facets " + lineSeparator;
		//str = str + "# polygon count, hole count, [boundary marker (0|1)] " + lineSeparator;
		for (VFacet facet : facets){
			str = str + facet.toString();// spaties en lineSeparator verwijderd		
		}
		//str = str +lineSeparator;
		str = str + "0" + lineSeparator;
		str = str + "0" + lineSeparator;
	return str;
	}

}
