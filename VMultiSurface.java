
import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.aggregates.MultiSurfaceImpl;
import org.citygml4j.impl.gml.geometry.primitives.LinearRingImpl;
import org.citygml4j.impl.gml.geometry.primitives.PolygonImpl;
import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.PosOrPointPropertyOrPointRep;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;

/**
 * This class deals with MultiSurface valued geometry property,
 * either directly from Building or from BoundedBySurface
 * @author kooijmanj1
 *
 */
public class VMultiSurface{
	private static final String EXTERIOR_INDICATOR = ".0";
	private static final String NO_ID_INDICATOR = "-1";
	private static final String GEOMETRY_INDICATOR = "0"; // for MultiSurface geometry
	private ArrayList<VFacet> facets = new ArrayList<VFacet>();
	private VUnicNodes unicNodes;
	private VFacet facet;
	private VPolygon polygon;
	private VReferedElement element;
	private String polygonId;
	private String multiSurfaceGmlId;
	private String[] shellData = new String[2];
	private ArrayList<String[]> shellDataArray = new ArrayList<String[]>();
	private int lod;
	private boolean wrapup =false; // noodmaatregel om te voorkomen dat voor elk surface een poly wordt gemaakt
	private boolean boundedBy = false;
	
	public VMultiSurface(VUnicNodes unicNodes, int lod){
		this.unicNodes = unicNodes;
		this.lod = lod;
	}
	
	public void organize(List<BoundarySurfaceProperty> listBoundarySurfaceProperty){
		boundedBy = true;
		MultiSurfaceProperty multiSurfaceProperty = null;
		int content = listBoundarySurfaceProperty.size();
		int count = 0;
		for (BoundarySurfaceProperty boundarySurfaceProperty : listBoundarySurfaceProperty){
			count++;
			AbstractBoundarySurface abstractBoundarySurface = boundarySurfaceProperty.getObject();			
			if (lod == 2){
				if (abstractBoundarySurface.isSetLod2MultiSurface()){
					multiSurfaceProperty = abstractBoundarySurface.getLod2MultiSurface();				
					this.organize(multiSurfaceProperty);
				}
			}			
			if (lod == 3){
				if (abstractBoundarySurface.isSetLod3MultiSurface()){
					multiSurfaceProperty = abstractBoundarySurface.getLod3MultiSurface();				
					this.organize(multiSurfaceProperty);
				}
			}
			if (lod == 4){
				if (abstractBoundarySurface.isSetLod4MultiSurface()){
					multiSurfaceProperty = abstractBoundarySurface.getLod4MultiSurface();				
					this.organize(multiSurfaceProperty);
				}
			}
			wrapup = count == content - 1;
			
		}
	}
			
	public void organize(MultiSurfaceProperty multiSurfaceProperty){
		if(multiSurfaceProperty.isSetMultiSurface()){
			MultiSurfaceImpl multiSurfaceImpl = (MultiSurfaceImpl)multiSurfaceProperty.getObject();
			multiSurfaceGmlId = multiSurfaceImpl.getId();
			if (multiSurfaceGmlId == null){
				multiSurfaceGmlId = NO_ID_INDICATOR;
			}
		}
		MultiSurface multiSurface = multiSurfaceProperty.getMultiSurface();
		List<SurfaceProperty> surfaceMember = multiSurface.getSurfaceMember();
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
			AbstractRingProperty abstractRingProperty = polygonImpl.getExterior();
			LinearRingImpl linearRingImpl = (LinearRingImpl)abstractRingProperty.getRing();
			
			List<PosOrPointPropertyOrPointRep> pppList = null;
			if(linearRingImpl.isSetPosOrPointPropertyOrPointRep()){
				pppList = linearRingImpl.getPosOrPointPropertyOrPointRep();
				polygon = new VPolygon(unicNodes, pppList);
				facet.addPolygon(polygon);
			}
			
			DirectPositionList posList = null;
			if(linearRingImpl.isSetPosList()){
				posList = linearRingImpl.getPosList();
				polygon = new VPolygon(unicNodes, posList);
				facet.addPolygon(polygon);
			}
			facets.add(facet);
		}

		String multiSurfaceId = multiSurfaceGmlId + EXTERIOR_INDICATOR;
		shellData[0] = multiSurfaceId;
		shellData[1] = this.toString();
		if (wrapup) shellDataArray.add(shellData);
		if (!boundedBy) shellDataArray.add(shellData);
	}
	
	/**
	 * Concatenates all the parts of underlying objects to one poly file string.
	 */
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		//First line of poly file, but no alternative for compositSurfaceGmlId
		String str = "# " + multiSurfaceGmlId + lineSeparator;
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
	
	public ArrayList<String[]> getShellDataArray(){
		return shellDataArray;
	}
}


