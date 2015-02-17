package cs455.overlay.routing;

public class RoutingTable {

    private int size;

    private RoutingEntry[] routingTable;

	public RoutingTable(int size, RoutingEntry[] entries, int id) {
        routingTable = new RoutingEntry[size];
        this.size = size;
        initTable(entries, id);
    }

    private void initTable(RoutingEntry[] entries, int id) {
        for (int i = 0; i < size; i++) {
            routingTable[i] = entries[id+2^i];
        }
    }

	public RoutingEntry[] getTable() {
        return routingTable;
    }

    public int getSize() {
        return size;
    }

}
