package cs455.overlay.util;

import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;

import java.util.ArrayList;
import java.util.List;

public class StatisticsCollectorAndDisplay {

    private static StatisticsCollectorAndDisplay statCollector = new StatisticsCollectorAndDisplay();
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;

    private long totalSentCount;

    private long totalReceiveCount;
    private long totalSumSent;
    private long totalSumReceived;
    private long totalRelayedCount;
    private List<OverlayNodeReportsTrafficSummary> trafficSummaryList = new ArrayList<OverlayNodeReportsTrafficSummary>();

    private StatisticsCollectorAndDisplay() {
    }

    public static StatisticsCollectorAndDisplay getInstance() {
        return statCollector;
    }

    public long getTotalSumSent() {
        return totalSumSent;
    }

    public long getTotalSumReceived() {
        return totalSumReceived;
    }

    public long getTotalRelayedCount() {
        return totalRelayedCount;
    }

    public long getTotalReceiveCount() {
        return totalReceiveCount;
    }

    public long getTotalSentCount() {
        return totalSentCount;
    }

    public int getTrafficSummarySize() {
        return trafficSummaryList.size();
    }

    public synchronized void addTrafficSummary(OverlayNodeReportsTrafficSummary trafficSummary) {
        totalReceiveCount = totalReceiveCount + trafficSummary.getPacketReceived();
        totalSentCount = totalSentCount + trafficSummary.getPacketSent();
        totalSumReceived = totalSumReceived + trafficSummary.getDataReceivedSum();
        totalSumSent = totalSumSent + trafficSummary.getDataSentSum();
        totalRelayedCount = totalRelayedCount + trafficSummary.getPacketRelayed();
        trafficSummaryList.add(trafficSummary);

    }


    public synchronized void increSendTracker() {
        sendTracker++;
    }

    public synchronized void increReceiveTracker() {
        receiveTracker++;
    }

    public synchronized void increSendSummationBy(long val) {
        sendSummation = sendSummation + val;
    }

    public synchronized void increReceiveSummation(long val) {
        receiveSummation = receiveSummation + val;
    }

    public synchronized void increRelayTracker() {
        relayTracker++;
    }

    public synchronized int getSendTracker() {
        return sendTracker;
    }

    public synchronized int getReceiveTracker() {
        return receiveTracker;
    }

    public synchronized int getRelayTracker() {
        return relayTracker;
    }

    public synchronized long getSendSummation() {
        return sendSummation;
    }

    public synchronized long getReceiveSummation() {
        return receiveSummation;
    }

    public void reset() {
        sendTracker = 0;
        receiveTracker = 0;
        relayTracker = 0;
        sendSummation = 0;
        receiveSummation = 0;
        totalSentCount = 0;
        totalReceiveCount = 0;
        totalSumSent = 0;
        totalSumReceived = 0;
        totalRelayedCount = 0;
        trafficSummaryList = new ArrayList<OverlayNodeReportsTrafficSummary>();
    }

    public void printCountersAndDiagnostics() {
        System.out.println("Packets sent: " + sendTracker);
        System.out.println("Packets received: " + receiveTracker);
        System.out.println("Packets relayed: " + relayTracker);
        System.out.println("Sum values sent: " + sendSummation);
        System.out.println("Sum values received: " + receiveSummation);
    }

    public void printTrafficSummary() {
        String format = "%-25s %-25s %-25s %-30s %-40s %-40s";
        String dataFormat = "%-25s %-25d %-25d %-30d %-40d %-40d%n";
        System.out.printf(format, "Node", "Packets Sent", "Packets Received", "Packets Relayed", "Sum Values Sent", "Sum Values Received");
        System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        int i = 1;
        for (OverlayNodeReportsTrafficSummary trafficSummary : trafficSummaryList) {
            System.out.printf(dataFormat, "Node " + i, trafficSummary.getPacketSent(), trafficSummary.getPacketReceived(), trafficSummary.getPacketRelayed(), trafficSummary.getDataSentSum(), trafficSummary.getDataReceivedSum());
            i++;
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(dataFormat, "Sum", getTotalSentCount(), getTotalReceiveCount(), getTotalRelayedCount(), getTotalSumSent(), getTotalSumReceived());
    }
}
