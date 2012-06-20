

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.primitives.SolidImpl;
import org.citygml4j.impl.gml.geometry.primitives.SurfacePropertyImpl;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
	/**
	 * 14-04-2012
	 * Responsible for the organization of a solid. The organization of 
	 * the inner and outer shells is delegated to VShell
	 * Jan Kooijman
	 */
public class VSolid{

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
			String solidId = solidGmlId + ".0";//EXTERIOR_INDICATOR
			shellStrings.add(solidId); // first element of shellStrings is solidId for the file name
			shell = new VShell(unicNodes);
			shell.organize(exteriorSurfaceProperty);
			shellStrings.add(shell.toString());
			
			
			// now the inner shells
			int shellNr = 1;
			List<SurfaceProperty> surfacePropertyList = solidImpl.getInterior();
			for (SurfaceProperty interiorSurfaceProperty : surfacePropertyList){
				solidId = solidGmlId + "." + shellNr;
				shellStrings.add(solidId);
				shell = new VShell(unicNodes);
				shell.organize(interiorSurfaceProperty);
				shellStrings.add(shell.toString());
				shellNr++;
			}
		}
		else{
			String href = solidProperty.getHref();
		}
	}
	
	public ArrayList<String> getShellStrings(){
		return shellStrings;
	}
	

}
