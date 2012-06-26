import java.util.ArrayList;

/**
 * VFacet is responsible for a facet, managing polygons and hole points.
 * @author kooijmanj1
 */
public class VFacet {
	private String polygonId;
	/**
	 * The first polygon in polygons is the exterior, the others are interior.
	 */
	private ArrayList<VPolygon> polygons = new ArrayList<VPolygon>(); 
	/**
	 * Hole points occur when a facet has one or more interior rings, i.e. holes,
	 * Hole point for the time being fixed on 0.5 0.5 0.5.
	 */
	private ArrayList<VNode> holePoints = new ArrayList<VNode>();
	
	public VFacet(String polygonId){
		this.polygonId = polygonId;
	}
	
	public void addPolygon(VPolygon polygon){
		polygons.add(polygon);
	}
	
	public void addHolePoint(VNode holePoint){
		holePoints.add(holePoint);
	}
	
	/** 
	 * Concatenates number of polygons in facet with number of holePoints and polygonId and
	 * subsequently all polygons and holepoints.
	 */
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		String str = "";
		str = str + polygons.size()+ " " + holePoints.size()+ " # " + polygonId + lineSeparator;
		
		for (VPolygon polygon : polygons){
			str = str + polygon.toString();
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
