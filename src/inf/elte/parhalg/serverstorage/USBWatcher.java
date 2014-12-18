package inf.elte.parhalg.serverstorage;

import java.io.IOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;

class ExampleListener implements FreeSpaceListener {
	@Override
	public void freeSpaceWarningCallback(long freeSpace) {
		System.out.println("Limit reached, available byes:" + Long.toString(freeSpace));
	}
}

public class USBWatcher {
	public static void main(String[] args) {
		Path g = FileSystems.getDefault().getPath("/media/brian/MYLINUXLIVE");
		new USBWatcher(g, new ExampleListener());
		try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	private boolean limitWasReached;
	private Path usbPath;
	private Timer runing;
	private long sizeLimit; // in bytes, 1GB
	private int interval; // in milliseconds
	private FreeSpaceListener listener;
	
	public USBWatcher(Path usbPath, FreeSpaceListener listener) {
		this(usbPath, listener, 5000 /*5sec*/, 1073741824L /*1GB*/);
	}
	
	public USBWatcher(Path usbPath, FreeSpaceListener listener, int interval, long sizeLimit) {
		this.listener = listener;
		this.interval = interval;
		this.sizeLimit = sizeLimit;
		this.usbPath = usbPath;
		this.runing = (new Timer(true)); // true => exiting will kill the thread
		
		this.limitWasReached = false;
		// initial test
		try {
			long freeSpace = getFreeSpace();
			if(freeSpace < this.sizeLimit) {
				this.limitWasReached = true;
				this.listener.freeSpaceWarningCallback(freeSpace);
	        }
		} catch (IOException e) {
	    	System.out.println("error querying space: " + e.toString());
	    }
		
		runing.schedule(new WatcherRunable(), 0, this.interval);
	}
	
	private long getFreeSpace() throws IOException {	
        FileStore store = Files.getFileStore(usbPath);
        return store.getUsableSpace();	    
	}
	
	private class WatcherRunable extends TimerTask {
		public void run(){
			try {
				long freeSpace = getFreeSpace();
				// only send signal if we previously were not below the limit
				if(!limitWasReached && freeSpace < sizeLimit) {
					limitWasReached = true;
		        	listener.freeSpaceWarningCallback(freeSpace);
		        } else if (limitWasReached && freeSpace >= sizeLimit) {
		        	limitWasReached = false;
		        }
			} catch (IOException e) {
		    	System.out.println("error querying space: " + e.toString());
		    }
	    }
	}	
}
