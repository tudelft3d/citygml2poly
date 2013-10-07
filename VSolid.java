import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.gml.geometry.primitives.Solid;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
	/**
	 * Responsible for the organization of a solid. The organization of 
	 * the inner and outer shells is delegated to VShell
	 * Jan Kooijman
	 */
public class VSolid{
	private static final String EXTERIOR_INDICATOR = ".0";
	private static final String NO_ID_INDICATOR = "-1";
	private SolidProperty solidProperty;
	private VShell shell;
	private String[] shellData = new String[2];
	private ArrayList<String[]> shellDataArray = new ArrayList<String[]>();
	private VUnicNodes unicNodes;
	private int lod = -1;
	private String solidGmlId = null;

	public VSolid(SolidProperty solidProperty, VUnicNodes unicNodes, int lod){
		this.solidProperty = solidProperty;
		this.unicNodes = unicNodes;
		this.lod = lod;
	}
	
	public void SetId(String str){
		this.solidGmlId = str;
	}
	
	public void organize(){
		Solid solid = (Solid)solidProperty.getObject();	
		if (solidGmlId == null){
			solidGmlId = solid.getId();
			if (solidGmlId == null){
				solidGmlId = NO_ID_INDICATOR;
			}
		}
		//exterior shell
		SurfaceProperty exteriorSurfaceProperty = solid.getExterior(); 
		String solidId = solidGmlId + EXTERIOR_INDICATOR;
		shellData[0] = solidId; //first element of shellData is Id for file name
		shell = new VShell(unicNodes, lod);
		shell.organize(exteriorSurfaceProperty);
		shellData[1] = shell.toString();
		shellDataArray.add(shellData);			
		// now the inner shells
		int innerShellNr = 1;
		List<SurfaceProperty> surfacePropertyList = solid.getInterior();
		for (SurfaceProperty interiorSurfaceProperty : surfacePropertyList){
			solidId = solidGmlId + "." + innerShellNr; //sequential number >0 as INTERIOR_INDICATOR
			shellData[0] = solidId;
			shell = new VShell(unicNodes, lod);
			shell.organize(interiorSurfaceProperty);
			shellData[1] = shell.toString();
			shellDataArray.add(shellData);
			innerShellNr++;
		}
	}
	
	public ArrayList<String[]> getShellDataArray(){
		return shellDataArray;
	}
}
