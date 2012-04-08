package lezers6Domain;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.primitives.SolidImpl;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;

public class VSolid {
	private SolidProperty solidProperty;
	private VShell shell;
	private ArrayList<String> shellStrings = new ArrayList<String>();
	private VUnicNodes unicNodes;
	
	
	public VSolid(SolidProperty solidProperty, VUnicNodes unicNodes){
		this.solidProperty = solidProperty;
		this.unicNodes = unicNodes;
	}
	
	public void organize(){
		if (solidProperty.isSetSolid()){                 
			SolidImpl solidImpl = (SolidImpl)solidProperty.getObject();		
			SurfaceProperty exteriorSurfaceProperty = solidImpl.getExterior(); //exterior van de solid
			shell = new VShell(unicNodes);
			shell.organize(exteriorSurfaceProperty);
			shellStrings.add(shell.toString());
			
			// now the inner shells
			List<SurfaceProperty> surfacePropertyList = solidImpl.getInterior();
			for (SurfaceProperty interiorSurfaceProperty : surfacePropertyList){
				shell = new VShell(unicNodes);
				shell.organize(interiorSurfaceProperty);
				shellStrings.add(shell.toString());
			}
		}
		else{
			String href = solidProperty.getHref();
			System.out.println("href: " + href);
		}
	}
	
	public ArrayList<String> getShellStrings(){
		return shellStrings;
	}
	

}
