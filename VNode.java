
/**
 * Responsible for a <b>node</b>, it holds its ordinates and offers an equals()-method
 * to decide on equality of two nodes and a toString()-method to deliver the ordinate 
 * values of this node concatenated in a String instance.
 * @author kooijmanj1
 */
public class VNode {
	private double[] node = new double[3];
	/**
	 * The snapMargin determines how close ordinate values of two nodes must be for 
	 * the nodes to considered as one single node.
	 */
	private double snapMargin = 0.001;
	
	/**
	 * constructs an instance of VNode with the ordinate values given
	 * @param o1 x ordinate
	 * @param o2 y ordinate
	 * @param o3 z ordinate
	 */
	public VNode(double o1, double o2, double o3){
		node[0] = o1;
		node[1] = o2;
		node[2] = o3;
	}
	
	public VNode(){}
	
	public void addOrdinate(int index, Double ordinate){
		node[index] = ordinate;
	}
	
	private Double getOrdinate(int index){
		return this.node[index];
	}
	
	/**
	 * Determines if this node equals the other node, meaning the ordinates of this node 
	 * are within the range of the other node plus or minus the snapMargin.
	 * @param node: the other node
	 * @return true if equal, otherwise false
	 */
	public boolean equals(VNode node){
	    if(    (this.node[0] > (node.getOrdinate(0) - snapMargin) && this.node[0] <= (node.getOrdinate(0) + snapMargin))
			&& (this.node[1] > (node.getOrdinate(1) - snapMargin) && this.node[1] <= (node.getOrdinate(1) + snapMargin))
			&& (this.node[2] > (node.getOrdinate(2) - snapMargin) && this.node[2] <= (node.getOrdinate(2) + snapMargin))  ){
			return true;}
		else{
			return false;
	    }
	}
	/**
	 * Delivers the ordinates concatenated in one string as component in the poly file content.
	 * @return String
	 */
	public String toString(){
		return "" + this.getOrdinate(0) +" "+ this.getOrdinate(1) +" "+ this.getOrdinate(2);
	}
}
