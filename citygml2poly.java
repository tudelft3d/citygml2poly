
import java.io.File;

class citygml2poly {
  public static void main(String[] args) {
    if (args.length != 2 && args.length != 3) {
      System.out.println("\nUsage: citygml2poly [infile] [outfolder] (Option -s)\n");
    }
    else {
    	VReaderWriter readerWriter = new VReaderWriter(new File(args[0]), new File(args[1]));
    	if (args.length == 3 && args[2].equals("-s")){
    		readerWriter.setOutputSemantics(true);
    	}
    	else{
    		readerWriter.setOutputSemantics(false);
    	}
      try { 
        readerWriter.organizeConversion();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
