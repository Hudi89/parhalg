package inf.elte.parhalg.example;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.ServerThread;
import inf.elte.parhalg.connection.Responder;

import java.io.IOException;

import inf.elte.parhalg.packet.*;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;

import inf.elte.parhalg.serverstorage.USBWatcher;
import inf.elte.parhalg.serverstorage.FreeSpaceListener;

public class ExampleServer implements PacketProcessor,FreeSpaceListener {
	private String mountPoint;

	public ExampleServer(String mountPoint, int port) throws IOException{
		this.mountPoint = mountPoint; 
		new USBWatcher(Paths.get(mountPoint), this);
		new ServerThread(this, port).start();
	}

	@Override
	public void freeSpaceWarningCallback(long freeSpace) {
		Responder.broadcast(new MessagePacket("Limit reached, available space:" + (freeSpace/1024) + " MB!"));
	}
	
	@Override
	public void process(Responder responder, Packet packet) {
		if(packet.getType() == PacketType.MESSAGE){
			System.out.println(packet);
		}else if(packet.getType() == PacketType.FILESEND){
			FilesendPacket p = (FilesendPacket)packet;
			try{
				System.out.println(mountPoint+p.getPath());


				String backupPath = mountPoint+p.getBackupName()+"/"+p.getPath();
				File backupPathFile = new File(backupPath);
				backupPathFile.getParentFile().mkdirs();

				Files.write(backupPathFile.toPath(),p.getData());
			}catch(IOException e){
				System.out.println("File write ... up" + p.getPath() + "!");
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) throws IOException {
		if(args.length < 2){
			help();
			return;
		}		
		String mountPoint = args[0];
		if(!mountPoint.endsWith("/")){
			mountPoint += "/";
		}
		if(!(new File(mountPoint).exists())){
			System.out.println("The mount point doesn't exists!");
			System.out.println("");
			return;
		}
		new ExampleServer(mountPoint,Integer.parseInt(args[1]));
	}

	private static void help(){
		System.out.println("Usage:");
		System.out.println("ExampleServer [backup mount point] [port to listen]");
	}

}
