/*
 citygml2poly - Copyright (c) 2012, Jan Kooijman.  All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the authors nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL HUGO LEDOUX BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
*/

import java.io.File;
import java.util.ArrayList;

import org.citygml4j.model.citygml.building.Building;

/**
 * Responsible for reading building by building and delegating their organization to VConstruct;
 * Makes the output file names: each shell is converted into a poly file; the name of 
 * poly file is the gml:Id of the Solid representing the building or building part;
 * the Id is followed by "dot0" for the exterior shell and a "dot sequence number" for
 * interior shells; when solid has no gml:Id, the poly file gets a sequential number 
 * as first part of the file name followed by the dot0 or dot sequence number as before.
 * @author kooijmanj1
 */
public class VReaderWriter {
	private static final String NO_ID_INDICATOR = "-1";
	private String destinationName = null;
	private VInputFile input;
	private VOutputFile output;
	private File sourceFile;
	private File destinationFolder;
	private int shellNr = 1; // counter to generate number for file name when there is no id
	private int buildingNr = 1;
	private boolean is_OutSemantics = false;
	
	public VReaderWriter(File sourceFile, File destinationFolder){
		this.sourceFile = sourceFile;
		this.destinationFolder = destinationFolder;
	}

	public void organizeConversion() throws Exception{
		input = new VInputFile(sourceFile);
		VShellDataStore shellDataStore = new VShellDataStore();
		ArrayList<Building> buildingList = input.readAllBuildings();	
		for (Building building : buildingList){
			VConstruct<Building> construct = new VConstruct<Building>();
			construct.SetIsSemantics(this.is_OutSemantics);
			construct.store(building);
			construct.setShellDataStore(shellDataStore);
			construct.organize();
			String id = ""; // could be id from Solid or MultiSurface		
			for (String[] shellDataArray :shellDataStore.getShellDataArrays() ){	
				String fileName = "";
				id = shellDataArray[0];	
				if (id.substring(0,2).equals(NO_ID_INDICATOR)){
					fileName = fileName + shellNr;
				}
				destinationName = destinationFolder.getPath() + "/" + fileName + id + ".poly"; 
				System.out.println("filenaam = " + destinationName);
				output = new VOutputFile(new File(destinationName));
				output.writeBuilding(shellDataArray[1]);
				shellNr++;
			}
			buildingNr++;
			shellDataStore.clear();
		}
	}
	
	public void setOutputSemantics(boolean set){
		this.is_OutSemantics = set;
	}
	
	public int getNumberOfShells(){
		return shellNr-1;
	}
}
