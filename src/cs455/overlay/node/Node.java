package cs455.overlay.node;

import cs455.overlay.wireformats.Event;

import java.io.IOException;

public interface Node {

	void onEvent(Event e) throws IOException;
	
}
