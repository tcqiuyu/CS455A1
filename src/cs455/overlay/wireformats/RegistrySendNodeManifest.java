package cs455.overlay.wireformats;

import cs455.overlay.node.OverlayNode;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;

import java.io.*;
import java.util.ArrayList;

public class RegistrySendNodeManifest implements Event {

    private final int type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;

    private RoutingTable routingTable;
    private int tableSize;
    private ArrayList<Integer> idArray;

    public RegistrySendNodeManifest(RoutingTable routingTable, ArrayList<Integer> idArray) {
        this.routingTable = routingTable;
        this.idArray = idArray;
        tableSize = routingTable.getSize();
    }

    public RegistrySendNodeManifest(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if (this.type == type) {
            tableSize = din.readInt();
            RoutingEntry[] entries = new RoutingEntry[tableSize];
            System.out.println("Table size is " + tableSize);
            for (int i = 0; i < tableSize; i++) {
                int nodeID = din.readInt();
                System.out.println("ID: " + nodeID);
                int ipLength = din.readInt();
                byte[] ipBytes = new byte[ipLength];
                din.readFully(ipBytes);
                String ip = new String(ipBytes);
                int port = din.readInt();

                OverlayNode overlayNode = new OverlayNode(ip, port);
                overlayNode.setID(nodeID);
                RoutingEntry routingEntry = new RoutingEntry(overlayNode);
                entries[i] = routingEntry;
            }
            int idArrayLength = din.readInt();
            System.out.println("ID array length: " + idArrayLength);
            idArray = new ArrayList<Integer>();
            for (int i = 0; i < idArrayLength; i++) {
                idArray.add(i, din.readInt());
            }

            routingTable = new RoutingTable(entries);
        } else {
            System.out.println("Message type does not match!");
        }
        baInputStream.close();
        din.close();
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public ArrayList<Integer> getIdArray() {
        return idArray;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(type);
        dout.writeInt(routingTable.getSize());
        RoutingEntry[] entries = routingTable.getTable();
        for (int i = 0; i < tableSize; i++) {
            RoutingEntry entry = entries[i];
            if (entry != null) {
                dout.writeInt(entry.getNodeID());
                byte[] ipBytes = entry.getLocalhost().getBytes();
                int ipLength = ipBytes.length;
                dout.writeInt(ipLength);
                dout.write(ipBytes);
                dout.writeInt(entry.getPort());
            }
        }
        dout.writeInt(idArray.size());
        for (int i = 0; i < idArray.size(); i++) {
            dout.writeInt(idArray.get(i));
        }
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        return marshalledBytes;
    }

}
