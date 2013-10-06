
import java.util.LinkedHashMap;
//import java.util.ArrayList;
import java.util.Map.Entry;



/**
 * Is responsible for list of unique nodes that carry a facet (=planar surface
 * with or without holes). 
 * @author kooijmanj1
 */
public class VUnicNodes {
	private LinkedHashMap<VNode, Integer> nodes = new LinkedHashMap<VNode, Integer>();
	//private ArrayList<VNode> nodes = new ArrayList<VNode>();
	
	/**
	 * Adds node only if not present yet in list of unique nodes
	 * @param node
	 */
	public void addUnicNode(VNode node){
		if (!this.nodes.containsKey(node)){
			this.nodes.put(node, this.nodes.size());
		}
		/*boolean notPresent = true;
		for( VNode n : nodes){
			if( n.equals(node)){
				notPresent = false;
				break;
			}
		}
		if (notPresent){
			nodes.add(node);
		}*/
	}
	
	/**
	 * @param node of which the index in unicNodes is requested
	 * @return the index of node in unicNodes
	 */
	public int getIndex(VNode node){
		return nodes.get(node);
		/*
		int indexValue = 999999; 
		int i = 0;
		for(VNode n : nodes){
			if (n.equals(node)){
				indexValue = i;
				break;
			}
			i++;
		}
		return indexValue;*/
	}
	
	public LinkedHashMap<VNode, Integer> getUnicNodes(){
		return nodes;
	}
	/*
	public ArrayList<VNode> getUnicNodes(){
		return nodes;
	}*/
	
	public int getSize(){
		return nodes.size();
	}
	
	/**
	 * Concatenates the index of a node in UnicNodes instance with the respective ordinates
	 * of the node as part of the contents of the poly file. 
	 */
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		String str = "";
		/*for (VNode node : nodes){
			str = str + " " + (nodes.indexOf(node)+1) + " " + node.toString() + lineSeparator;
		}*/
		for (Entry<VNode, Integer> node : nodes.entrySet()){
			str = str + " " + (node.getValue()+1) + " " + node.getKey().toString() + lineSeparator;
		}
		return str;
	}
}
