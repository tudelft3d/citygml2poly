package lezers6Domain;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.gml.geometry.primitives.SolidImpl;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.BuildingPartProperty;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;


/**
 * 6-04-2012
 * Generic type to be responsible as well for Building as for BuildingPart
 * analysis of geometries; it should work recursively when BuildingPart 
 * exists;
 * @author kooijmanj1
 *
 * @param <B>
 */
public class VConstruct<B extends AbstractBuilding> {
	private B object;
	private VShell shell;
	private ArrayList<String> shellStrings = new ArrayList<String>();
	
//	public VConstruct(B object){
//		this.object = object;
//	}
	
	public void store(B object){
		this.object = object;
	}
	
	public void organize(){
		if(object.isSetLod1Solid()){
			organizeSolid(object.getLod1Solid());
		}
		if(object.isSetLod2Solid()){
			organizeSolid(object.getLod2Solid());
		}
	}
	
	public void organizeSolid(SolidProperty solidProperty){
		if (solidProperty.isSetSolid()){                 
			SolidImpl solidImpl = (SolidImpl)solidProperty.getObject();		
			SurfaceProperty exteriorSurfaceProperty = solidImpl.getExterior(); //exterior van de solid
			shell = new VShell();
			shell.organisizeShell(exteriorSurfaceProperty);
			shellStrings.add(shell.toString());
			
			// now the inner shells
			List<SurfaceProperty> surfacePropertyList = solidImpl.getInterior();
			for (SurfaceProperty interiorSurfaceProperty : surfacePropertyList){
				shell = new VShell();
				shell.organisizeShell(interiorSurfaceProperty);
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
