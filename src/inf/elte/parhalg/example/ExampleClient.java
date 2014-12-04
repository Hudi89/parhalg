package inf.elte.parhalg.example;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.Responder;
import inf.elte.parhalg.packet.ClosePacket;
import inf.elte.parhalg.packet.FilesendPacket;
import inf.elte.parhalg.packet.HelloPacket;
import inf.elte.parhalg.packet.MessagePacket;

import java.io.IOException;
import java.net.Socket;

public class ExampleClient {

	public static void main(String[] args) throws IOException, InterruptedException {
		try (Responder responder = new Responder(new Socket("localhost", 2345), PacketProcessor.PRINT_PROCESSOR)) {
			responder.send(new HelloPacket());
			responder.send(new MessagePacket("This is a sample message..."));
			responder.send(new FilesendPacket("/this/is/a/path.txt", new byte[] { 0, 1, 2, 3 }));
			responder.send(new ClosePacket());
			Thread.sleep(5000);
		}
	}
}
