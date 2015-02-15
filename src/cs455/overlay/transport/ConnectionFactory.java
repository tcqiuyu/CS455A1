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

    public synchronized void registerConnection(TCPConnection connection) {
        String key = getKey(connection);
        connectionMap.put(key, connection);
    }

    public TCPConnection getConnection(String host, int port, Node node) throws IOException {
        String key = host + ":" + port;
        TCPConnection connection;
        if (connectionMap.containsKey(key)) {
            connection = connectionMap.get(key);
        } else {
            Socket s = new Socket(host, port);
            connection = new TCPConnection(node, s);
            registerConnection(connection);
        }
        return connection;
    }

    private String getKey(TCPConnection connection) {
        Socket socket = connection.getSocket();
        return socket.getInetAddress() + ":" + socket.getPort();
    }


}

