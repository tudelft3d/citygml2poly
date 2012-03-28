package lezers6Domain;

import java.awt.List;
import java.util.ArrayList;



/**
 * 1-3-2012
 * VFacet is responsible for a facet, managing polygons and hole points
 * Hole point for the time being fixed on 0.5 0.5 0.5
 * @author kooijmanj1
 *
 */
public class VFacet {

	private String name; 
	// The first polygon is the exterior, the others are interior(holes in poly)
	private ArrayList<VPolygon> polygons = new ArrayList<VPolygon>(); 
	// only  the interior rings have holepoint
	private ArrayList<VNode> holePoints = new ArrayList<VNode>(); // only  the interior rings have holepoint
	
	public VFacet(String name){
		this.name = name;
	}
	
	public VFacet(){}
	
	public void addPolygon(VPolygon polygon){
		polygons.add(polygon);
	}
	
	public void addHolePoint(VNode holePoint){
		holePoints.add(holePoint);
	}
	
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		String str = "";
		str = str + polygons.size()+ " " + holePoints.size()+ lineSeparator;
		
		for (VPolygon polygon : polygons){
			str = str + polygon.toString();
		}
		int holeNr = 0;
		for (VNode holePoint : holePoints){
			str = str + holeNr + " ";
			str = str + holePoint.toString() + " # hard coded" + lineSeparator;
			holeNr++;
		}
		return str;
	}
	
}
