package inf.elte.parhalg.example;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.Responder;
import inf.elte.parhalg.packet.ClosePacket;
import inf.elte.parhalg.packet.FilesendPacket;
import inf.elte.parhalg.packet.HelloPacket;
import inf.elte.parhalg.packet.MessagePacket;

import java.io.IOException;
import java.net.Socket;

import java.nio.file.Paths;
public class ExampleClient {

	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length < 3){
			help();
			return;
		}	

	
		try (Responder responder = new Responder(new Socket(args[0], Integer.parseInt(args[1])), PacketProcessor.PRINT_PROCESSOR)) {
		/*	responder.send(new HelloPacket());
			Thread.sleep(1000);
			responder.send(new MessagePacket("This is a sample message..."));
			Thread.sleep(1000);
			responder.send(FilesendPacket.createFromPath("../testfiles","/asd.txt"));
			Thread.sleep(1000);
			responder.send(FilesendPacket.createFromPath("../testfiles","/bsd.txt"));
			responder.send(new ClosePacket());
			Thread.sleep(5000);*/
			responder.send(FilesendPacket.createFromPath(args[2],args[3],"test"));	

			Thread.sleep(2000);
		}
	}

	
	private static void help(){
		System.out.println("Usage:");
		System.out.println("ExampleClient [address] [port number] [backup folder] [backuped file from folder]");
	}
}
