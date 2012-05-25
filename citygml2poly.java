
package lezers6Domain;

import java.io.File;

class citygml2poly {
  public static void main(String[] args) {
    VCount counter = new VCount(new File("/Users/hugo/temp/CityGML_British_Ordnance_Survey_v1.0.0.xml"));
		counter.countBuildings();
    // System.out.println(Integer.toString(counter.nrBuildings));
    VReaderWriter readerWriter = new VReaderWriter(new File("/Users/hugo/temp/CityGML_British_Ordnance_Survey_v1.0.0.xml"), new File("/Users/hugo/temp/y/"));
    try { 
		  readerWriter.organizeConversion();
    }
    catch (Exception e) {
		  e.printStackTrace();
		}
  }
}
