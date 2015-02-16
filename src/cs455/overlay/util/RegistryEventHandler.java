package cs455.overlay.util;

import cs455.overlay.node.OverlayNode;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.ConnectionFactory;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;

import java.io.IOException;

/**
 * Created by Qiu on 2/15/2015.
 */
public class RegistryEventHandler {

    private Registry registry;

    public RegistryEventHandler(Registry registry) {
        this.registry = registry;
    }

    public void handleRegRequest(Event event) throws IOException {
        OverlayNodeSendsRegistration regRequest = (OverlayNodeSendsRegistration) event;

        String srcIP = regRequest.getSrcIP();
        int srcPort = regRequest.getSrcPort();

        System.out.println("Receive registration request from host: " + srcIP + " port: " + srcPort);
        OverlayNode overlayNode = new OverlayNode(srcIP, srcPort);

        int nodeID = registry.getNextID();
        System.out.println("Allocate ID " + nodeID + " to incoming messaging node");
        int regResult = registry.registerNode(nodeID, overlayNode);

        String info;
        int infoLength;
        if (regResult != -1) {//registration successful
            info = "Registration request successful. The numer of messaging nodes curretly constituting the overlay " +
                    "is (" + registry.getNodeAmount() + ")";
            infoLength = info.length();
        } else {//registration failed
            info = "Registration failed. The node already exists in overlay.";
            infoLength = info.length();
        }
        RegistryReportsRegistrationStatus regReport = new RegistryReportsRegistrationStatus(regResult, infoLength, info);

        TCPConnection connection = ConnectionFactory.getInstance().getConnection(srcIP, srcPort, registry);

        try {
            connection.sendData(regReport.getBytes());
        } catch (IOException ioe) {
            registry.deRegisterNode(overlayNode.getHost(), overlayNode.getPort(), overlayNode.getNodeID());
            System.out.println("Failed to send registration report: " + ioe.getMessage());
        }

    }

    public void handleDeregRequest(Event event) throws IOException {
        OverlayNodeSendsDeregistration deregRequest = (OverlayNodeSendsDeregistration) event;

        String srcIP = deregRequest.getIp();
        int srcPort = deregRequest.getPort();
        int nodeID = deregRequest.getNodeID();


        String info;
        int infoLength;
        RegistryReportsDeregistrationStatus deregReport = null;

        switch (registry.deRegisterNode(srcIP, srcPort, nodeID)) {
            case 1:
                info = "Deregistration successful. Node with ID " + nodeID + " is deregistered.";
                infoLength = info.length();
                deregReport = new RegistryReportsDeregistrationStatus(1, infoLength, info);
                break;
            case -1:
                info = "Deregistration failed. Node information provided is not consistent with the one stored in " +
                        "registry.";
                infoLength = info.length();
                deregReport = new RegistryReportsDeregistrationStatus(-1, infoLength, info);
                break;
            case -2:
                info = "Deregisration failed. Node is not in overlay.";
                infoLength = info.length();
                deregReport = new RegistryReportsDeregistrationStatus(-2, infoLength, info);
                break;
            default:
                break;
        }

        TCPConnection connection = ConnectionFactory.getInstance().getConnection(srcIP, srcPort, registry);

        try {
            if (deregReport != null) {
                connection.sendData(deregReport.getBytes());
            }
        } catch (IOException ioe) {
            OverlayNode node = new OverlayNode(srcIP, srcPort);
            node.setID(nodeID);
            registry.addToNodeMap(nodeID, node);
            System.out.println("Deregistration report failed to send: " + ioe.getMessage());
        }
    }

    public void handleTaskFinishReports(Event event) {

    }

    public void handleTrafficSummaryReports(Event event) {

    }

    public void handleSetupStatusReports(Event event) {

    }


}
