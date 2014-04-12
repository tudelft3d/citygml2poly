
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.PosOrPointPropertyOrPointRep;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.model.citygml.building.OpeningProperty;
import org.citygml4j.model.citygml.building.AbstractOpening;
import org.citygml4j.model.gml.geometry.complexes.CompositeSurface;
import org.citygml4j.model.gml.geometry.complexes.CompositeSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractSurface;
import org.citygml4j.model.gml.GMLClass; 
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
	private VUnicNodes unicNodes = new VUnicNodes();//140412;
	private VFacet facet;
	private VPolygon vpolygon;
	private VReferedElement element;
	private String polygonId = null;
	private String polygonCode  = null;
	private String multiSurfaceGmlId  = null;
	private String[] shellData = new String[2];
	private ArrayList<String[]> shellDataArray = new ArrayList<String[]>();
	private int lod = -1;
	private boolean wrapup = false; // noodmaatregel om te voorkomen dat voor elk surface een poly wordt gemaakt
	private boolean boundedBy = false;
	private boolean is_Semantics = false;
	private boolean is_SolidBoundary = false;//the multisurface is the boundasry of a solid 
	
	public VMultiSurface(VUnicNodes unicNodes, int lod){
		this.unicNodes = unicNodes;
		this.lod = lod;
	}
	
	//140412
	public VMultiSurface(int lod){
		this.lod = lod;
	}
	
	public void SetIsSemantics (boolean set){
		is_Semantics = set;
	}
	
	public void SetID(String str){
		this.multiSurfaceGmlId = str;
	}
	
	public void SetIsSolidBoundary (boolean set){
		is_SolidBoundary = set;
	}
	
	public void organize(List<BoundarySurfaceProperty> listBoundarySurfaceProperty){
		boundedBy = true;
		MultiSurfaceProperty multiSurfaceProperty = null;
		List<OpeningProperty> listOpeningProperty;
		int content = listBoundarySurfaceProperty.size();
		int count = 0;
		for (BoundarySurfaceProperty boundarySurfaceProperty : listBoundarySurfaceProperty){
			count++;
			AbstractBoundarySurface abstractBoundarySurface = boundarySurfaceProperty.getObject();
			//get the type of the facet
			this.polygonCode = abstractBoundarySurface.getCityGMLClass().name();
			//
			//if (lod == 2){
				if (abstractBoundarySurface.isSetLod2MultiSurface()){
					this.lod = 2;
					multiSurfaceProperty = abstractBoundarySurface.getLod2MultiSurface();				
					this.organize(multiSurfaceProperty);
				}
			//}			
			//if (lod == 3){
				if (abstractBoundarySurface.isSetLod3MultiSurface()){
					this.lod = 3;
					multiSurfaceProperty = abstractBoundarySurface.getLod3MultiSurface();				
					this.organize(multiSurfaceProperty);
				}
			//}
			//if (lod == 4){
				if (abstractBoundarySurface.isSetLod4MultiSurface()){
					this.lod = 4;
					multiSurfaceProperty = abstractBoundarySurface.getLod4MultiSurface();				
					this.organize(multiSurfaceProperty);
				}
				
				if(abstractBoundarySurface.isSetOpening()){
					listOpeningProperty = abstractBoundarySurface.getOpening();
					for (OpeningProperty openingProperty : listOpeningProperty ){
						AbstractOpening abstractOpening = openingProperty.getOpening();
						if (abstractOpening.isSetLod3MultiSurface()){
							this.lod = 3;
							multiSurfaceProperty = abstractOpening.getLod3MultiSurface();				
							this.organize(multiSurfaceProperty);
						}
						if (abstractOpening.isSetLod4MultiSurface()){
							this.lod = 4;
							multiSurfaceProperty = abstractOpening.getLod4MultiSurface();				
							this.organize(multiSurfaceProperty);
						}
					}
				}
			//}
			wrapup = count == content - 1;
			
		}
	}
			
	public void organize(SurfaceProperty polygonSurfaceProperty){
		Polygon polygon = (Polygon)polygonSurfaceProperty.getSurface();
		if (polygon == null){ 
			polygonId  = (polygonSurfaceProperty.getHref()).substring(1);
			element = new VReferedElement(polygonSurfaceProperty, lod);
			element.search(polygonId);
			polygon = element.getPolygon();
		}
		else{
			polygonId = polygon.getId();
			if (polygonId == null){
				polygonId = NO_ID_INDICATOR;
			}
		}
		facet = new VFacet(polygonId, polygonCode);
		//exterior polygon
		AbstractRingProperty abstractRingProperty = polygon.getExterior();
		LinearRing linearRing = (LinearRing)abstractRingProperty.getRing();
		
		List<PosOrPointPropertyOrPointRep> pppList = null;
		if(linearRing.isSetPosOrPointPropertyOrPointRep()){
			pppList = linearRing.getPosOrPointPropertyOrPointRep();
			vpolygon = new VPolygon(unicNodes, pppList);
			facet.addPolygon(vpolygon);
		}
		
		DirectPositionList posList = null;
		if(linearRing.isSetPosList()){
			posList = linearRing.getPosList();
			vpolygon = new VPolygon(unicNodes, posList);
			facet.addPolygon(vpolygon);
			vpolygon.clearNodes();
		}
		//Interior polygons
		List<AbstractRingProperty> abstractIntRingProperties = polygon.getInterior();
		for (AbstractRingProperty abstractIntRingProperty : abstractIntRingProperties){
			LinearRing linearIntRing = (LinearRing)abstractIntRingProperty.getRing();
			List<PosOrPointPropertyOrPointRep> pppIntList = null;
			if(linearIntRing.isSetPosOrPointPropertyOrPointRep()){
				pppIntList = linearIntRing.getPosOrPointPropertyOrPointRep();
				vpolygon = new VPolygon(unicNodes, pppIntList);
				facet.addPolygon(vpolygon);
				facet.addHolePoint(vpolygon.getHolePoint());
				vpolygon.clearNodes();
			}
			DirectPositionList posIntList = null;
			if(linearIntRing.isSetPosList()){
				posIntList = linearIntRing.getPosList();
				vpolygon = new VPolygon(unicNodes, posIntList);
				facet.addPolygon(vpolygon);
				facet.addHolePoint(vpolygon.getHolePoint());
				vpolygon.clearNodes();
			}
		}
		
		facets.add(facet);
	}
	
	public void organize(MultiSurfaceProperty multiSurfaceProperty){
		if(multiSurfaceProperty.isSetMultiSurface()){
			MultiSurface multiSurface = (MultiSurface)multiSurfaceProperty.getObject();
			if (multiSurfaceGmlId == null){
				multiSurfaceGmlId = multiSurface.getId();
				if (multiSurfaceGmlId == null){
					multiSurfaceGmlId = NO_ID_INDICATOR;
				}
			}
		}
		MultiSurface multiSurface = multiSurfaceProperty.getMultiSurface();
		List<SurfaceProperty> surfaceMember = multiSurface.getSurfaceMember();
		for (SurfaceProperty surfaceMemberElement : surfaceMember){ 
			GMLClass Surface_type = surfaceMemberElement.getGeometry().getGMLClass();
			if (Surface_type.name() == "COMPOSITE_SURFACE")
			{
				CompositeSurface subSurface =  (CompositeSurface)surfaceMemberElement.getSurface();
				List<SurfaceProperty> subSurfaceMember = subSurface.getSurfaceMember();
				for (SurfaceProperty subSurfaceMemberElement : subSurfaceMember){ 
					this.organize(subSurfaceMemberElement);
				}
			}
			else
			{
				this.organize(surfaceMemberElement);
			}
		}
		if (wrapup || !boundedBy){
			String multiSurfaceId = multiSurfaceGmlId + EXTERIOR_INDICATOR;
			shellData[0] = multiSurfaceId;
			shellData[1] = this.toString();
			shellDataArray.add(shellData);
		}
		/*String multiSurfaceId = multiSurfaceGmlId + EXTERIOR_INDICATOR;
		shellData[0] = multiSurfaceId;
		shellData[1] = this.toString();
		if (wrapup) shellDataArray.add(shellData);
		if (!boundedBy) shellDataArray.add(shellData);*/
	}
	
	/**
	 * Concatenates all the parts of underlying objects to one poly file string.
	 */
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		//First line of poly file, but no alternative for compositSurfaceGmlId
		String str = "# " + multiSurfaceGmlId + lineSeparator;
		// Second line of poly indicates the origin of the shell data
		if (is_SolidBoundary)
		{
			str = str + "# " + "1" + lineSeparator;
		}
		else
		{
			str = str + "# " + GEOMETRY_INDICATOR + lineSeparator;
		}
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
			facet.setIsSemantics(this.is_Semantics);
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


