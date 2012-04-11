package lezers6Domain;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.complexes.CompositeSurfaceImpl;
import org.citygml4j.impl.gml.geometry.primitives.LinearRingImpl;
import org.citygml4j.impl.gml.geometry.primitives.PolygonImpl;
import org.citygml4j.impl.gml.geometry.primitives.SurfacePropertyImpl;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;

/**
 * 8-04-2012
 * @author kooijmanj1
 *
 */
		
		
public class VBoundedBySurface{
	private ArrayList<VFacet> facets = new ArrayList<VFacet>();
	private VUnicNodes unicNodes;
	private List<BoundarySurfaceProperty> listSurfaceProperty;
	private VFacet facet;
	private VPolygon polygon;
	private ArrayList<String> shellStrings = new ArrayList<String>();
	
	public VBoundedBySurface(List<BoundarySurfaceProperty> listSurfaceProperty,
			VUnicNodes unicNodes){
		this.listSurfaceProperty = listSurfaceProperty;
		this.unicNodes = unicNodes;
	}
	
	public void organize(){	
		for (BoundarySurfaceProperty boundarySurfaceProperty : listSurfaceProperty){
			AbstractBoundarySurface abstractBoundarySurface = boundarySurfaceProperty.getObject();
			MultiSurfaceProperty multiSurfaceProperty = abstractBoundarySurface.getLod2MultiSurface();
			MultiSurface multiSurface = multiSurfaceProperty.getMultiSurface();
			List<SurfaceProperty> surfaceMember = multiSurface.getSurfaceMember();
			for (SurfaceProperty surfaceMemberElement : surfaceMember){
				
				
				PolygonImpl polygonImpl = (PolygonImpl)surfaceMemberElement.getSurface();
				String polygonId = polygonImpl.getId();
				if (polygonId == null){
					polygonId = "-1";
				}
				facet = new VFacet(polygonId);
				AbstractRingProperty abstractRingProperty = polygonImpl.getExterior();
				LinearRingImpl linearRingImpl = (LinearRingImpl)abstractRingProperty.getRing();
				
				DirectPositionList posList = null;
				if(linearRingImpl.isSetPosList()){
					posList = linearRingImpl.getPosList();
					polygon = new VPolygon(unicNodes, posList);
					facet.addPolygon(polygon);
				}
			}
			facets.add(facet);
		}
		shellStrings.add(this.toString());
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
	
	public ArrayList<String> getShellStrings(){
		return shellStrings;
	}
}
