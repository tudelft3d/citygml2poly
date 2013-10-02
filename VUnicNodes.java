

import java.util.ArrayList;

/**
 * Is responsible for list of unique nodes that carry a facet (=planar surface
 * with or without holes). 
 * @author kooijmanj1
 */
public class VUnicNodes {
	private ArrayList<VNode> nodes = new ArrayList<VNode>();
	
	/**
	 * Adds node only if not present yet in list of unique nodes
	 * @param node
	 */
	public void addUnicNode(VNode node){
		boolean notPresent = true;
		for( VNode n : nodes){
			if( n.equals(node)){
				notPresent = false;
				break;
			}
		}
		if (notPresent){
			nodes.add(node);
		}
	}
	
	/**
	 * @param node of which the index in unicNodes is requested
	 * @return the index of node in unicNodes
	 */
	public int getIndex(VNode node){
		int indexValue = 999999; 
		int i = 0;
		for(VNode n : nodes){
			if (n.equals(node)){
				indexValue = i;
				break;
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
	 * Concatenates the index of a node in UnicNodes instance with the respective ordinates
	 * of the node as part of the contents of the poly file. 
	 */
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		String str = "";
		for (VNode node : nodes){
			str = str + " " + (nodes.indexOf(node)+1) + " " + node.toString() + lineSeparator;
		}
		return str;
	}
}
