package inf.elte.parhalg.example;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.ServerThread;

import java.io.IOException;

public class ExampleServer {

	public static void main(String[] args) throws IOException {
		new ServerThread(PacketProcessor.PRINT_PROCESSOR, 2345).start();
	}

}
