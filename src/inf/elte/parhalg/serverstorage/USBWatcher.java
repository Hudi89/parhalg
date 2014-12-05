package inf.elte.parhalg.serverstorage;

import java.io.IOException;
import java.nio.file.*;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class USBWatcher {
	
	public static void main(String[] args) {
		Path g = FileSystems.getDefault().getPath("g:/");
		USBWatcher watcher = new USBWatcher(g);
		watcher.getFreeSpace();
		try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	private Path usbPath;
	private Timer runing;
	
	public USBWatcher(Path UsbPath) { 
		this.usbPath = UsbPath;
		this.runing = (new Timer(true));
		runing.schedule(new WatcherRunable(), 0, 1000);
		
	}
	
	private void getFreeSpace() {	
		NumberFormat nf = NumberFormat.getNumberInstance();
	    try {
	        FileStore store = Files.getFileStore(usbPath);
	        System.out.println("available=" + nf.format(store.getUsableSpace()) + ", total=" + nf.format(store.getTotalSpace()));
	    } catch (FileSystemException e) {
	        System.out.println("error querying space: " + e.toString());
	    } catch (IOException e) {
	    	System.out.println("error querying space: " + e.toString());
	    }
	}
	
	private class WatcherRunable extends TimerTask {
		public void run() {
			getFreeSpace();
	    }
	}	
}
