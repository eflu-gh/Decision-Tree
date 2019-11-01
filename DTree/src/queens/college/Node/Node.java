package queens.college.Node;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private String data = null;
	private int key = 0;
	private int colToAsk = 0;
	private int weight = 0;
	private List<Node> children = new ArrayList<>();
	private Node parent = null;
	private ArrayList<Integer> targetColumns; // Each node has to know its parent (s)
	private ArrayList<Integer> targetKeys; // Each node has to know its parent (s)

	public Node(String data, int key) {
		this.data = data;// data = root
		this.key = key;// Just the root has a key equal to 0.
	}

	public Node(String data, int key, int colToAsk) {
		this.data = data;
		this.key = key;
		this.colToAsk = colToAsk;
	}

	public void addChild(Node child) {
		child.setParent(this);
		this.children.add(child);

		ArrayList<Integer> tempColumns = new ArrayList<Integer>();
		ArrayList<Integer> tempKeys = new ArrayList<Integer>();
		child.targetColumns = new ArrayList<Integer>();
		child.targetKeys = new ArrayList<Integer>();

		if (child.getParent().getKey() != 0) { // For those nodes that are not the root.

			tempColumns = new ArrayList<Integer>(child.getParent().targetColumns);
			tempKeys = new ArrayList<Integer>(child.getParent().targetKeys);

			child.targetColumns = tempColumns;
			child.targetColumns.add(child.colToAsk);

			child.targetKeys = tempKeys;
			child.targetKeys.add(child.key);
			
		} else {
			child.targetColumns.add(child.colToAsk);
			child.targetKeys.add(child.key);
		}
	}
	
	public void addChildType(Node child) {
		child.setParent(this);
		this.children.add(child);
	}

	public void addChildren(List<Node> children) {
		children.forEach(each -> each.setParent(this));
		this.children.addAll(children);
	}

	public List<Node> getChildren() {
		return children;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	private void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}

	public int getKey() {
		return key;
	}

	public int getColToAsk() {
		return colToAsk;
	}

	public int getWeight() {
		return weight;
	}

	public ArrayList<Integer> getTargetColumns() {
		return targetColumns;
	}

	public ArrayList<Integer> getTargetKeys() {
		return targetKeys;
	}
}