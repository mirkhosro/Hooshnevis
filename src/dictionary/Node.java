package dictionary;

public class Node {
	Node parent;
	
	int slotIndex;
	
	public Node(Node parent, int slotIndex) {
		this.parent = parent;
		this.slotIndex = slotIndex;
	}

}
