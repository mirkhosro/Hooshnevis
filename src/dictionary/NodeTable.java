package dictionary;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A table holding nodes, that behaves like a stack. That is, you can only
 * add/get nodes to/from the topmost row and rows are pushed/popped.
 * Always the table has at least one row.
 * @author Amir
 * 
 */
public class NodeTable {
	
	private Vector rows;
		
	public NodeTable() {
		rows = new Vector();
	}
	
	/**
	 * Pushes a new row of Nodes to the table
	 */
	public NodeTable pushRow() {
		rows.addElement(new Vector());
		return this;
	}

	/**
	 * Removes the topmost row.
	 */
	public void popRow() {
		rows.removeElementAt(rows.size() - 1);
	}
	
	/**
	 * Appends a Node to the topmost row
	 * 
	 * @param n
	 *            the node to be added
	 */
	public void appendToRow(int row, Node n) {
		getRow(row).addElement(n);
	}

	/**
	 * Appends an array of Nodes to the topmost row
	 * 
	 * @param ns
	 *            the node array to be appended
	 */
	public void appendToRow(int row, Vector nodes) {
		Vector v = getRow(row);
		for (Enumeration e = nodes.elements(); e.hasMoreElements();) {
			v.addElement(e.nextElement());
		}
	}
	
	/**
	 * Gets the Node with the given index in the topmost row
	 * 
	 * @param index
	 *            the index of the Node to be returned
	 * @return a Node
	 */
	public Node getNodeAt(int row, int col) {
		return (Node)(getRow(row).elementAt(col));
	}

	/**
	 * Returns the length of the topmost row
	 * 
	 * @return the length of the topmost row
	 */
	public int rowLength(int row) {
		return getRow(row).size();
	}
	
	/**
	 * Makes this table empty.
	 */
	public void clear() {
		rows.removeAllElements();
	}
	
	private Vector getRow(int i) {
		return (Vector)rows.elementAt(i);
	}
}
