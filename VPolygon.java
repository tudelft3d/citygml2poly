

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.gml.geometry.primitives.DirectPosition;
import org.citygml4j.model.gml.geometry.primitives.DirectPositionList;
import org.citygml4j.model.gml.geometry.primitives.PosOrPointPropertyOrPointRep;

/**
 * VPolygon is responsible for a polygon in terms of the poly format
 * or exterior or interior LinearRing in GML terms. 
 * @author kooijmanj1
 *
 */
public class VPolygon {
	/**
	 * The node values derived from the List<PosOrPointPropertyOrPointRep> pppList or 
	 * from the DirectPositionList posList that carry this polygon.
	 */
	private ArrayList<VNode> nodes = new ArrayList<VNode>(); 
	/**
	 * The indices of the nodes in unicNodes that carry this VPolygon
	 */
	private ArrayList<Integer> indices = new ArrayList<Integer>();
	private VUnicNodes unicNodes;
	 
	public VPolygon(VUnicNodes unicNodes){
		this.unicNodes = unicNodes;
	}
	
	/**
	 * Case: LinearRing implemented as a PosOrPointPropertyOrPointRep,
	 * Forms nodes, adds them to polygons and to the array of unique nodes.
	 * @param unicNodes pppList
	 */
	public VPolygon(VUnicNodes unicNodes, List<PosOrPointPropertyOrPointRep> pppList){
		this.unicNodes = unicNodes;
		for (PosOrPointPropertyOrPointRep pppElement : pppList ){
			DirectPosition directPosition = pppElement.getPos();
			List<Double> ordinates = directPosition.getValue();
			VNode node = new VNode(ordinates.get(0), ordinates.get(1), ordinates.get(2));
			/*int index = 0;
			for(Double ordinate : ordinates){
				node.addOrdinate(index, ordinate);
				index++;			
			}*/
			this.addNode(node);
			unicNodes.addUnicNode(node);
		}
		convertNodesToIndices();
	}
	
	/**
	 * Case: LinearRing implemented as DirectPositionList,
	 * Forms nodes, adds them to polygons and to the array of unique nodes, when nodes 
	 * of this polygon are complete, nodes are converted to indices.
	 * @param unicNodes posList
	 */
	public VPolygon(VUnicNodes unicNodes, DirectPositionList posList){
		this.unicNodes = unicNodes;
		VNode node = new VNode();
		int index = 0;
		List<Double> ordinates = posList.getValue();
		for (Double ordinate : ordinates){
			node.addOrdinate(index, ordinate);
			index++;
			if ( index == 3){
				index = 0;
				this.addNode(node);
				unicNodes.addUnicNode(node);
				node = new VNode();
			}
		}
		convertNodesToIndices();
	}
	
	/**
	 * Adds a node only that is not present in nodes
	 * @param node
	 */
	public void addNode(VNode node){
		boolean present = false;
		for(VNode nd : nodes){
			if(nd.equals(node)){
				present = true;
				break;
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
	 * 	Derives index values from the polygon nodes by comparing coordinates 
	 *	between polygon nodes and nodes in the array of unique nodes
	 * @param polygon
	 */
	private void convertNodesToIndices(){
		for (VNode polygonNode: this.getNodes()){
			for (VNode unicNode: unicNodes.getUnicNodes()){
				if (polygonNode.equals(unicNode)){
					this.addIndex(unicNodes.getIndex(unicNode));
					break;
				}
			}
		}
	}
	
	/**
	 * Concatenates the number of indices and the indices themselves of this
	 * polygon to one String instance as part of the content of the poly file.
	 */
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
