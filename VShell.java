

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.citygml4j.model.gml.geometry.complexes.CompositeSurface;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
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
	private VPolygon vpolygon;
	private VReferedElement element;
	private String vpolygonId = null;
	private String polygonCode = null;
	private String compositeSurfaceGmlId = null;
	private int lod = -1;
	
	public VShell(VUnicNodes unicNodes, int lod){
		this.unicNodes = unicNodes;
		this.lod = lod;
	}
	
	public void organize(SurfaceProperty surfaceProperty){
		CompositeSurface compositeSurface = (CompositeSurface)surfaceProperty.getObject();
		compositeSurfaceGmlId = compositeSurface.getId();
		if (compositeSurfaceGmlId==null){
			compositeSurfaceGmlId = NO_ID_INDICATOR;
		}
		List<SurfaceProperty> surfaceMember = compositeSurface.getSurfaceMember(); 
		for (SurfaceProperty surfaceMemberElement : surfaceMember){
			Polygon polygon = (Polygon)surfaceMemberElement.getSurface();
			if (polygon == null){
				vpolygonId  = (surfaceMemberElement.getHref()).substring(1);
				element = new VReferedElement(surfaceMemberElement, lod);
				element.search(vpolygonId);
				polygon = element.getPolygon();
			}
			else{
				vpolygonId = polygon.getId();
				if (vpolygonId == null){
					vpolygonId = NO_ID_INDICATOR;
				}
			}
			facet = new VFacet(vpolygonId, polygonCode);
			AbstractRingProperty ringPropertyExt = polygon.getExterior(); 
			LinearRing linearRing = (LinearRing)ringPropertyExt.getObject();
			
			List<PosOrPointPropertyOrPointRep> pppList = null;
			if(linearRing.isSetPosOrPointPropertyOrPointRep()){
				pppList = linearRing.getPosOrPointPropertyOrPointRep();
				vpolygon = new VPolygon(unicNodes, pppList);
				facet.addPolygon(vpolygon);
				vpolygon.clearNodes();
			}
			
			DirectPositionList posList= null;
			if(linearRing.isSetPosList()){
				posList = linearRing.getPosList();
				vpolygon = new VPolygon(unicNodes, posList);
				facet.addPolygon(vpolygon);
				vpolygon.clearNodes();
			}
			
			List<AbstractRingProperty> listAbstractRingProperty = polygon.getInterior();
			for (AbstractRingProperty ringPropertyInt : listAbstractRingProperty){
				if(ringPropertyInt != null){
					linearRing = (LinearRing)ringPropertyInt.getObject();
					if(linearRing.isSetPosOrPointPropertyOrPointRep()){
						pppList = linearRing.getPosOrPointPropertyOrPointRep();
						vpolygon = new VPolygon(unicNodes, pppList);
						facet.addPolygon(vpolygon);
					}
					if(linearRing.isSetPosList()){
						posList = linearRing.getPosList();
						vpolygon = new VPolygon(unicNodes, posList);
						facet.addPolygon(vpolygon);
					}
					facet.addHolePoint(vpolygon.getHolePoint());
					vpolygon.clearNodes();
					/*pppList = linearRing.getPosOrPointPropertyOrPointRep();
					vpolygon = new VPolygon(unicNodes, pppList);
					facet.addPolygon(vpolygon);
					makeHolePoint();*/
				}
			}	
			facets.add(facet);
		}
	}
	
	/**
	 * As long as we don't have a proper algorithm to calculate, we just give a fixed coordinate.
	 */
	/*private void makeHolePoint(){ 
		VNode holePoint = new VNode(0.5, 0.5, 0.5);
		facet.addHolePoint(holePoint);
	}*/
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
		/*for (VNode node : unicNodes.getUnicNodes()){
			str = str + i + "  " + node.toString() + lineSeparator;
			i++;
		}*/
		for (Entry<VNode, Integer> node : unicNodes.getUnicNodes().entrySet()){
			str = str + i + "  " + node.getKey().toString() + lineSeparator;
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
	
	/*
	 * union all the polygons of a solid with inner shells, the vertices are stored uniquely in unicNodes)
	 *
	public void add_facets(ArrayList<VFacet> input_facets){
		for (VFacet facet : input_facets){
			facets.add(facet);
		}
	}
	//export all the facets of this shell
	public ArrayList<VFacet> get_facets(){
		return facets;
	}
	*/
}







