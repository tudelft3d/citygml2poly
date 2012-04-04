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

/**
 * 7-3-2012
 * Responsible for the organization of a shell ( exterior or interior) into a poly file
 * @author kooijmanj1
 */
public class VShell {
	private ArrayList<VFacet> facets = new ArrayList<VFacet>();
	private VUnicNodes unicNodes = new VUnicNodes();
	private VFacet facet;
	
	public void organisizeShell(SurfaceProperty surfaceProperty){
		CompositeSurfaceImpl compositeSurfaceImpl = (CompositeSurfaceImpl)surfaceProperty.getObject();
		List<SurfaceProperty> surfaceMember = compositeSurfaceImpl.getSurfaceMember(); 
		int facetNr = 0;
		for (SurfaceProperty surfaceMemberElement : surfaceMember){
			facet = new VFacet();
			PolygonImpl polygonImpl = (PolygonImpl)surfaceMemberElement.getSurface();
			AbstractRingProperty ringPropertyExt = polygonImpl.getExterior(); // exterior of the polygon
			LinearRingImpl linearRingImpl = (LinearRingImpl)ringPropertyExt.getObject();
			
			//Check how linearRingImpl has been implemented: PosOrPointPropertyOrPointRep or PosList
			//Do we have to add Coord and Coordinates implementation?
			
			List<PosOrPointPropertyOrPointRep> pppList = null;
			if(linearRingImpl.isSetPosOrPointPropertyOrPointRep()){
				pppList = linearRingImpl.getPosOrPointPropertyOrPointRep();
				makePolygonFromPppList(pppList);
			}
			DirectPositionList posList= null;
			if(linearRingImpl.isSetPosList()){
				posList = linearRingImpl.getPosList();
				makePolygonFromPosList(posList);
			}
			
			List<AbstractRingProperty> listAbstractRingProperty = polygonImpl.getInterior();
			for (AbstractRingProperty ringPropertyInt : listAbstractRingProperty){
				if(ringPropertyInt != null){
					linearRingImpl = (LinearRingImpl)ringPropertyInt.getObject();
					pppList = linearRingImpl.getPosOrPointPropertyOrPointRep();
					makePolygonFromPppList(pppList);
					makeHolePoint();
				}
			}	
			facets.add(facet);
			facetNr++;
		}
	}
	
	
	/**
	 * Case: LinearRing implemented as a PosOrPointPropertyOrPointRep
	 * Forms nodes, adds them to polygons and to the array of unique nodes: 
	 * adds polygons to facet
	 * @param pppList
	 */
	private void makePolygonFromPppList(List<PosOrPointPropertyOrPointRep> pppList){
		VPolygon polygon = new VPolygon();
		VNode node = new VNode();
		int nodeNr = 0;
		for (PosOrPointPropertyOrPointRep pppElement : pppList ){
			DirectPosition directPosition = pppElement.getPos();
			List<Double> ordinates = directPosition.getValue();
			int index = 0;
			for(Double ordinate : ordinates){
				node.addOrdinate(index, ordinate);
				index++;			
				System.out.println("" + nodeNr + " " + index + " " + ordinate + "  ");
			}
			nodeNr++;
			polygon.addNode(node);
			unicNodes.addUnicNode(node);
			node = new VNode();
		}
		convertNodesToIndices(polygon);
		facet.addPolygon(polygon);
	}
	
	/**
	 * Case: LinearRing implemented as DirectPositionList
	 * Forms nodes, adds them to polygons and to the array of unique nodes:
	 * adds polygons to facet
	 * @param posList
	 */
	private void makePolygonFromPosList(DirectPositionList posList){
		VPolygon polygon = new VPolygon();
		VNode node = new VNode();
		int index = 0;
		int nodeNr = 0;
		List<Double> ordinates = posList.getValue();
		for (Double ordinate : ordinates){
			node.addOrdinate(index, ordinate);
			index++;
			System.out.println("" + nodeNr + " " + index + " " + ordinate + "  ");
			if ( index == 3){
				index = 0;
				polygon.addNode(node);
				unicNodes.addUnicNode(node);
				node = new VNode();
			}
		}
		convertNodesToIndices(polygon);
		facet.addPolygon(polygon);
		nodeNr++;
	}
	/**
	 * 	Derives index values from the facet nodes by comparing coordinates 
	 *	between facet nodes and nodes in the array of unique nodes
	 * @param polygon
	 */
	private void convertNodesToIndices(VPolygon polygon){
		for (VNode polygonNode: polygon.getNodes()){
			for (VNode unicNode: unicNodes.getUnicNodes()){
				if (polygonNode.equals(unicNode)){
					polygon.addIndex(unicNodes.getIndex(unicNode));
				}
			}
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
		String str = "# Part 1 - node list" + lineSeparator;
		str = str + "# node count, dim, attribute count, boundary marker (0|1) " + lineSeparator;
		str = str + unicNodes.getSize() + "  " + "3" + " " + "0" + " " + "0" + " # all except node count hard coded" + lineSeparator;
		str = str + "# Node index, node coordinates " + lineSeparator;
		int i = 0;
		for (VNode node : unicNodes.getUnicNodes()){
			str = str + i + "  " + node.toString() + lineSeparator;
			i++;
		}
		str = str + lineSeparator;
		str = str + "# Part 2 - facet list" + lineSeparator;
		str = str + "# facet count, boundary marker (0|1)" + lineSeparator;
		str = str + facets.size() + "  0 # hard coded zero " + lineSeparator;// boundary marker mendatory
		str = str + "# facets " + lineSeparator;
		str = str + "# polygon count, hole count, [boundary marker (0|1)] " + lineSeparator;
		for (VFacet facet : facets){
			str = str + facet.toString();// spaties en lineSeparator verwijderd		
		}
		str = str +lineSeparator;
		str = str + "# Part 3 - hole list " + lineSeparator;
		str = str +"0           # no hole " + lineSeparator;
	return str;
	}

}
