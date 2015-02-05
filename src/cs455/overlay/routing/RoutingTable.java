package cs455.overlay.routing;

public class RoutingTable {

	private int size;
	private int[] nodeIDArray;
	private Exception TableSizeNotMatchException;

	public RoutingTable() {
		size = 3;
		nodeIDArray = new int[size];
	}

	public RoutingTable(int s) {
		this.size = s;
		nodeIDArray = new int[size];
	}

	public int[] getTable() {
		return nodeIDArray;
	}

	public void setTable(int[] array) throws Exception {
		if (array.length == size) {
			nodeIDArray = array;
		} else {
			throw TableSizeNotMatchException;
		}
	}
}
