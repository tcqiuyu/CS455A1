package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qiu on 2/15/2015.
 */
public class ConnectionFactory {

    private static ConnectionFactory instance = new ConnectionFactory();
    private Map<String, TCPConnection> connectionMap = new HashMap<String, TCPConnection>();

    private ConnectionFactory() {

    }

    public static ConnectionFactory getInstance() {
        return instance;
    }

    private synchronized void registerConnection(String host, int port, TCPConnection connection) {
        String key = getKey(host, port);
        connectionMap.put(key, connection);
    }

    public TCPConnection getConnection(String host, int port, Node node) throws
            IOException {
        System.out.println("\nLooking for connection...");
        TCPConnection connection = null;
        String key = getKey(host, port);
        if (connectionMap.containsKey(key)) {
            connection = connectionMap.get(key);
            System.out.println("Found connection... Host: " + host + ", on port: " + port);
        } else {
            System.out.println("Cannot find connection.");
            System.out.println("Initiating connection to host: " + host + ", port: " + port);
            Socket socket = new Socket(host, port);
            connection = new TCPConnection(node, socket);
            registerConnection(host, port, connection);
            System.out.println("Initiate connection successful.");
        }
        System.out.println("    Current Number of connection: " + connectionMap.size());
        System.out.println();
        return connection;
    }


    private String getKey(String host, int port) {
        return host + ":" + port;
    }


}

