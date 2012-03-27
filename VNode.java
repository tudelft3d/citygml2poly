package lezers5Domain;
/**
 * 1-3-2012
 * @author kooijmanj1
 * Responsible for a node
 */
public class VNode {
	private double[] node = new double[3];
	
	/**
	 * construct an instance of VNode with the ordinate values given
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
	 * Determines if this node equals the other node
	 * @param vnode: the other node
	 * @return true if equal, otherwise false
	 */
	public boolean equals(VNode node){
	    if(this.node[0] == node.getOrdinate(0)
			&& this.node[1] == node.getOrdinate(1)
			&& this.node[2] == node.getOrdinate(2)){
			return true;}
		else{
			return false;
	    }
	}
	
	public String toString(){
		return "" + this.getOrdinate(0) +" "+ this.getOrdinate(1) +" "+ this.getOrdinate(2);
	}
}
