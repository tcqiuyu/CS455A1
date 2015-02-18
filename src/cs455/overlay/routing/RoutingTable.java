package cs455.overlay.routing;

import java.util.ArrayList;

public class RoutingTable {

    private int size;

    private RoutingEntry[] routingTable;

    public RoutingTable(RoutingEntry[] routingTable) {
        this.routingTable = routingTable;
        this.size = routingTable.length;
    }

    public RoutingTable(int size, RoutingEntry[] entries, ArrayList<Integer> idArray, int id) {
        routingTable = new RoutingEntry[size];
        this.size = size;
        initTable(entries, idArray, id);
    }

    private void initTable(RoutingEntry[] entries, ArrayList<Integer> idArray, int id) {
        for (int i = 0; i < size; i++) {
            int index = idArray.indexOf(id);
            try {
                int temp = idArray.get((index + (int) Math.pow(2, i)) % idArray.size());
                routingTable[i] = entries[temp];
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }

    public RoutingEntry[] getTable() {
        return routingTable;
    }

    public int getSize() {
        return size;
    }

}
