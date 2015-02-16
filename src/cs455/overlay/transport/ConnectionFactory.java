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

    public synchronized void registerConnection(String host, int port, TCPConnection connection) {
        String key = getKey(host, port);
        connectionMap.put(key, connection);
    }

    public TCPConnection getConnection(String host, int port, Node node) throws
            IOException {
        System.out.println("Looking for connection...");
        String key = host + ":" + port;
        TCPConnection connection;
        if (connectionMap.containsKey(key)) {
            connection = connectionMap.get(key);
            System.out.println("Find connection... Host: " + host + ", Port: " + port);
        } else {
            System.out.println("Cannot find connection. Create one...Host: " + host + ", Port: " + port);

            Socket s = new Socket(host, port);
            connection = new TCPConnection(node, s);

            registerConnection(host, port, connection);


        }
        return connection;
    }

    private String getKey(String host, int port) {
        return host + ":" + port;
    }


}

