package inf.elte.parhalg.clientgui;

import java.io.File;
import java.util.Date;

import java.nio.file.Path;
import java.io.IOException;
import java.net.Socket;

import javax.swing.SwingUtilities;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.Responder;
import inf.elte.parhalg.packet.ClosePacket;
import inf.elte.parhalg.packet.FilesendPacket;
import inf.elte.parhalg.packet.HelloPacket;
import inf.elte.parhalg.packet.MessagePacket;
import inf.elte.parhalg.watcher.DirFigyelo;
import inf.elte.parhalg.packet.FilesendPacket;

public class Gui implements GuiEventListener {
	private ConnectionFrame connectionFrame;
	private FolderFrame folderFrame;
  private Responder responder;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Gui().start();
			}
		});
	}
	
	public Gui() {
		connectionFrame = new ConnectionFrame(this);
		folderFrame = new FolderFrame(this);
	}

	public void start() {
		connectionFrame.setVisible(true);
	}
 
  @Override
  public void closeGUI(){
   try {
     responder.close();
   }
   catch(Exception e) {}
  }

 	@Override
	public void connectionRequest(String address, int port) {
    try {
      responder = new Responder(new Socket(address, port), PacketProcessor.PRINT_PROCESSOR);
		  responder.send(new HelloPacket());
    }
    catch(Exception e) {}
		connectionFrame.setVisible(false);
		folderFrame.setVisible(true);
	}

	@Override
	public void addDirectoryRequest(final File path) {
		final Runnable r = new Runnable(){
			public void run(){
	  	  BlockingQueue<Path> changes = DirFigyelo.changedFiles(path.toPath());
        while(true) {
          try {
            Path changedFile = changes.take();    
            FilesendPacket packet = FilesendPacket.createFromPath(changedFile);            
            responder.send(packet);
          } catch (Exception e) {}
        }
      }
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
		folderFrame.addFolder(path, new Date(), 0, "new");
		folderFrame.updateFolder(path, null, 1, null);
	}

}
