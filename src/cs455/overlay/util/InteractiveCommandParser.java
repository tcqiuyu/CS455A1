package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;

public class InteractiveCommandParser {

	private Node node;
	
	public InteractiveCommandParser(Node node){
		this.node = node;
	}
	
	public void listMessagingNodes(){
		Registry registry = (Registry) node;
	}
	
	public void setupOverlay(int size){
		Registry registry = (Registry) node;
	}
	
	public void listRoutingTable(){
		Registry registry = (Registry) node;
	}
	
	public void start(int msgNumber){
		Registry registry = (Registry) node;
	}
	
	public void printCountersAndDiagnostics(){
		MessagingNode msgNode = (MessagingNode) node;
	}
	
	public void exitOverlay(){
		MessagingNode msgNode = (MessagingNode) node;
	}
}
