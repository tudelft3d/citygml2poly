import java.util.ArrayList;

/**
 * VFacet is responsible for a facet, managing polygons and hole points.
 * @author kooijmanj1
 */
public class VFacet {
	private String polygonId = null;
	private String polygonCode = null;//extension of the code name of the facet
	private boolean is_Semantics = false;
	/**
	 * The first polygon in polygons is the exterior, the others are interior.
	 */
	private ArrayList<VPolygon> vpolygons = new ArrayList<VPolygon>(); 
	/**
	 * Hole points occur when a facet has one or more interior rings, i.e. holes,
	 * Hole point for the time being fixed on 0.5 0.5 0.5.
	 */
	private ArrayList<VNode> holePoints = new ArrayList<VNode>();
	
	public VFacet(String polygonId, String polygonCode){
		this.polygonId = polygonId;
		this.polygonCode = polygonCode;
	}
	
	public void addPolygon(VPolygon polygon){
		vpolygons.add(polygon);
	}
	
	public void addHolePoint(VNode holePoint){
		holePoints.add(holePoint);
	}
	
	public void setIsSemantics(boolean set){
		this.is_Semantics = set;
	}
	/** 
	 * Concatenates number of polygons in facet with number of holePoints and polygonId and
	 * subsequently all polygons and holepoints.
	 */
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		String str = "";
		if (polygonCode == null || is_Semantics == false)
			str = str + vpolygons.size()+ " " + holePoints.size()+ " # " + polygonId + lineSeparator;
		else
			str = str + vpolygons.size()+ " " + holePoints.size()+ " # " + polygonId + " # "+ polygonCode + lineSeparator;
		
		/*if (holePoints.size() > 0){
			str = str + vpolygons.get(0).toString();
			for (int i = 1; i < vpolygons.size(); ++i){
				str = str + vpolygons.get(i).toString();
				
			}
			for (VPolygon vpolygon : vpolygons){
				str = str + vpolygon.toString();
				vpolygons.get(0).toString()
			}
			int holeNr = 0;
			for (VNode holePoint : holePoints){
				str = str + holeNr + " ";
				str = str + holePoint.toString() + lineSeparator;
				holeNr++;
			}
		}
		else{
			for (VPolygon vpolygon : vpolygons){
				str = str + vpolygon.toString();
			}
		}*/
		
		for (VPolygon vpolygon : vpolygons){
			str = str + vpolygon.toString();
		}
		
		int holeNr = 0;
		for (VNode holePoint : holePoints){
			str = str + holeNr + " ";
			str = str + holePoint.toString() + lineSeparator;
			holeNr++;
		}
		return str;
	}
	
}
