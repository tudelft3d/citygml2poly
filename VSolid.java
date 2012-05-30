

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.primitives.SolidImpl;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
	/**
	 * 14-04-2012
	 * Responsible for the organization of a solid. The organization of 
	 * the inner and outer shells is delegated to VShell
	 * Jan Kooijman
	 */
public class VSolid{
	private final String EXTERIOR_INDICATOR = "EX";
	private final String INTERIOR_INDICATOR = "IN";
	private String NO_ID_INDICATOR = "-1";
	private SolidProperty solidProperty;
	private VShell shell;
	private ArrayList<String> shellStrings = new ArrayList<String>();
	private VUnicNodes unicNodes;
	private String objectId;


	public VSolid(String objectId, SolidProperty solidProperty, VUnicNodes unicNodes){
		this.objectId = objectId;
		this.solidProperty = solidProperty;
		this.unicNodes = unicNodes;
	}
	
	public void organize(){
		if (solidProperty.isSetSolid()){    
			SolidImpl solidImpl = (SolidImpl)solidProperty.getObject();		
			String solidGmlId = solidImpl.getId();
			if (solidGmlId == null){
				solidGmlId = NO_ID_INDICATOR;
			}
			//exterior shell
			SurfaceProperty exteriorSurfaceProperty = solidImpl.getExterior(); 
			String solidId = solidGmlId + EXTERIOR_INDICATOR;
			shell = new VShell(solidId, unicNodes);
			shell.organize(exteriorSurfaceProperty);
			shellStrings.add(shell.toString());
			
			
			// now the inner shells
			List<SurfaceProperty> surfacePropertyList = solidImpl.getInterior();
			for (SurfaceProperty interiorSurfaceProperty : surfacePropertyList){
				solidId = solidGmlId + INTERIOR_INDICATOR;
				shell = new VShell(solidId, unicNodes);
				shell.organize(interiorSurfaceProperty);
				shellStrings.add(shell.toString());
			}
		}
		else{
			String href = solidProperty.getHref();
			// System.out.println("href: " + href);
		}
	}
	
	public ArrayList<String> getShellStrings(){
		return shellStrings;
	}
	

}
