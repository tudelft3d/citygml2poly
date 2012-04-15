package lezers6Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import lezers2Data.VInputOutputException;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.JAXBBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;

/**
 * 18-02-2012
 * Responsible for reading CityGML file and producing a building list
 * @author kooijmanj1
 *
 */
public class VInputFile {
	private File file = null;
	private CityGMLReader reader = null;
	
	public VInputFile(File file){
		this.file = file;
	}
	
	public ArrayList<Building> readAllBuildings() throws VInputOutputException {
		ArrayList<Building> buildings = new ArrayList<Building>();
		CityGMLContext ctx = new CityGMLContext();
		JAXBBuilder builder;
		try {
			builder = ctx.createJAXBBuilder();
		} 
		catch (JAXBException e) {
			throw new VInputOutputException("JAXB problem" + e.getMessage());
		}
		try {
			CityGMLInputFactory in = builder.createCityGMLInputFactory();
			CityGMLReader reader = in.createCityGMLReader(file);
			CityGML citygml = reader.nextFeature();
			CityModel cityModel = (CityModel)citygml;
			for(CityObjectMember cityObjectMember : cityModel.getCityObjectMember()){
				AbstractCityObject cityObject = cityObjectMember.getCityObject();
				if(cityObject.getCityGMLClass() == CityGMLClass.BUILDING){
					Building building = (Building)cityObject;
					buildings.add(building);
				}
			}		
		} 
		catch (CityGMLReadException e) {
			throw new VInputOutputException("CityGMLReadException" + e.getMessage());
		}
		finally {
			if(reader != null){
				try {reader.close();}
				catch (CityGMLReadException e){
					throw new VInputOutputException("Closing problem" + e.getMessage());
				}
			}
		}
		return buildings; 
	}
}
