
import java.io.File;

class citygml2poly {
  public static void main(String[] args) {
    
    // if (args.length != 2) {
    //   System.out.println("\nUsage: java -jar citygml2poly.jar [infile] [outfolder]\n");
    // }
    // else {
      // VReaderWriter readerWriter = new VReaderWriter(new File(args[0]), new File(args[1]));
      VReaderWriter readerWriter = new VReaderWriter(new File("/Users/hugo/data/citygml/CityGML_British_Ordnance_Survey_v1.0.0.xml"), new File("/Users/hugo/temp/y/"));
      try { 
        readerWriter.organizeConversion();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    // }
  }
}