package lezers6Domain;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.gml.geometry.primitives.DirectPosition;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.PosOrPointPropertyOrPointRep;

/**
 * 29-02-2012
 * VPolygon is responsible for a polygon in terms of .poly format
 * @author kooijmanj1
 *
 */
public class VPolygon {
	private ArrayList<VNode> nodes = new ArrayList<VNode>(); // the node values derived from my gml:pppList 
	private ArrayList<Integer> indices = new ArrayList<Integer>(); // the indices of the nodes in unicNodes that carry this VPolygon
	private VUnicNodes unicNodes;
	 
	//constructor
	public VPolygon(VUnicNodes unicNodes){
		this.unicNodes = unicNodes;
	}
	
	/**
	 * Case: LinearRing implemented as a PosOrPointPropertyOrPointRep
	 * Forms nodes, adds them to polygons and to the array of unique nodes
	 * @param pppList
	 */
	public VPolygon(VUnicNodes unicNodes, List<PosOrPointPropertyOrPointRep> pppList){
		this.unicNodes = unicNodes;
		VNode node = new VNode();
		int nodeNr = 0;
		for (PosOrPointPropertyOrPointRep pppElement : pppList ){
			DirectPosition directPosition = pppElement.getPos();
			List<Double> ordinates = directPosition.getValue();
			int index = 0;
			for(Double ordinate : ordinates){
				node.addOrdinate(index, ordinate);
				index++;			
				System.out.println("" + nodeNr + " " + index + " " + ordinate + "  ");
			}
			nodeNr++;
			this.addNode(node);
			unicNodes.addUnicNode(node);
			node = new VNode();
		}
		convertNodesToIndices();
	}
	
	/**
	 * Case: LinearRing implemented as DirectPositionList
	 * Forms nodes, adds them to polygons and to the array of unique nodes:
	 * adds polygons to facet
	 * @param posList
	 */
	public VPolygon(VUnicNodes unicNodes, DirectPositionList posList){
		this.unicNodes = unicNodes;
		VNode node = new VNode();
		int index = 0;
		int nodeNr = 0;
		List<Double> ordinates = posList.getValue();
		for (Double ordinate : ordinates){
			node.addOrdinate(index, ordinate);
			index++;
			System.out.println("" + nodeNr + " " + index + " " + ordinate + "  ");
			if ( index == 3){
				index = 0;
				this.addNode(node);
				unicNodes.addUnicNode(node);
				node = new VNode();
			}
		}
		convertNodesToIndices();
		nodeNr++;
	}
	
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
	
	/**
	 * 	Derives index values from the facet nodes by comparing coordinates 
	 *	between facet nodes and nodes in the array of unique nodes
	 * @param polygon
	 */
	private void convertNodesToIndices(){
		for (VNode polygonNode: this.getNodes()){
			for (VNode unicNode: unicNodes.getUnicNodes()){
				if (polygonNode.equals(unicNode)){
					this.addIndex(unicNodes.getIndex(unicNode));
				}
			}
		}
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
