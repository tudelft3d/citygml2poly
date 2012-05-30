

import java.util.ArrayList;

/**
 * 8-04-2012
 * Is responsible for list of unique nodes that carry the facets of a 3D shape. 
 * @author kooijmanj1
 *
 */
public class VUnicNodes {
	private ArrayList<VNode> nodes = new ArrayList<VNode>();
	
	/**
	 * Adds node only if not present yet in list of unic nodes
	 * @param node
	 */
	public void addUnicNode(VNode node){
		boolean notPresent = true;
		for( VNode n : nodes){ // kan misschien ook met contains()
			if( n.equals(node)){
				notPresent = false;
			}
		}
		if (notPresent){
			nodes.add(node);
		}
	}
	
	/**
	 * 
	 * @param node of what the index in unicNodes is requested
	 * @return the index of node in unicNodes
	 */
	public int getIndex(VNode node){
		int indexValue = 999999; 
		int i = 0;
		for(VNode n : nodes){
			if (n.equals(node)){
				indexValue = i;
			}
			i++;
		}
		return indexValue;
	}
	
	public ArrayList<VNode> getUnicNodes(){
		return nodes;
	}
	
	public int getSize(){
		return nodes.size();
	}
	
	/**
	 * Prints the nodes to the poly format
	 */
	public String toString(){
		String str = "";
		for (VNode node : nodes){
			str = str + " " + (nodes.indexOf(node)+1) + " " + node.toString() + "\n";
		}
		return str;
	}
}
