package lezers5Domain;

import java.util.ArrayList;

/**
 * 29-02-2012
 * VPolygon is responsible for a polygon in terms of .poly format
 * @author kooijmanj1
 *
 */
public class VPolygon {
	private String name;
	private ArrayList<VNode> nodes = new ArrayList<VNode>(); // the node values derived from my gml:pppList 
	private ArrayList<Integer> indices = new ArrayList<Integer>(); // the indices of the nodes in unicNodes that carry this VPolygon
	 
	//constructor
	public VPolygon(String name){
		this.name = name;
	}
	
	public VPolygon(){}
	
	/**
	 * Adds only nodes that are not present in nodes
	 * @param node
	 */
	public void addNode(VNode node){
		boolean present = false;
		for(VNode nd : nodes){
			if(nd.equals(node)){
				present = true;
			}
		}
		if(!present){
			nodes.add(node);
		}
	}
	
	public VNode getNode(int index){
		return this.nodes.get(index);
	}
	
	public ArrayList<VNode> getNodes(){
		return nodes;
	}
	
	public void addIndex(int indexValue){
		this.indices.add(indexValue);
	}
	
	public int getIndexValue(int index){
		return this.indices.get(index);
	}
	
	public ArrayList<Integer> getIndices(){
		return indices;
	}
	
	public String toString(){
		String lineSeparator = System.getProperty ( "line.separator" );
		String str = "";
		str = str + indices.size() + " ";
		for (int i : indices){
			str = str + " " + i;
		}
		str = str + lineSeparator;
		return str;
	}
}
