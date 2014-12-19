package inf.elte.parhalg.example;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.Responder;
import inf.elte.parhalg.packet.ClosePacket;
import inf.elte.parhalg.packet.FileSendPacket;
import inf.elte.parhalg.packet.HelloPacket;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;

public class ExampleClient {

	private static void printHelp() {
		System.out.println("Usage:");
		System.out.println("ExampleClient [address] [port number] [backup folder] [backuped file from folder]");
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length < 4) {
			printHelp();
			return;
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String root = args[2];
		String relative = args[3];

		try (Responder responder = new Responder(new Socket(host, port), PacketProcessor.PRINT_PROCESSOR)) {
			responder.send(new HelloPacket());
			responder.send(FileSendPacket.createFromPath(Paths.get(root), Paths.get(relative), "test"));
			responder.send(new ClosePacket());
		}
	}

}
