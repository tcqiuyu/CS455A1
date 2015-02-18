package cs455.overlay.util;

public class StatisticsCollectorAndDisplay {

    private static StatisticsCollectorAndDisplay statCollector = new StatisticsCollectorAndDisplay();
    private int sendTracker = 0;
    private int receiveTracker = 0;
    private int relayTracker = 0;
    private long sendSummation = 0;
    private long receiveSummation = 0;

    private StatisticsCollectorAndDisplay() {
    }

    public static StatisticsCollectorAndDisplay getInstance() {
        return statCollector;
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

    public void printCountersAndDiagnostics() {
        System.out.println("Packets sent: " + sendTracker);
        System.out.println("Packets received: " + receiveTracker);
        System.out.println("Packets relayed: " + relayTracker);
        System.out.println("Sum values sent: " + sendSummation);
        System.out.println("Sum values received: " + receiveSummation);
    }

}
