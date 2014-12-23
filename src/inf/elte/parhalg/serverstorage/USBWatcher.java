package inf.elte.parhalg.serverstorage;

import java.io.IOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class USBWatcher extends TimerTask {

	private static final Logger LOG = Logger.getAnonymousLogger();

	private final FileStore store;

	private final FreeSpaceListener listener;
	
	private final int interval; // in milliseconds

	private final int sizeLimit; // in megabytes, 1GB
	
	private final Timer timer;

	private boolean limitWasReached;

	public USBWatcher(Path usbPath, FreeSpaceListener listener) throws IOException {
		this(usbPath, listener, 5000 /* 5sec */, 1024 /* 1GB */);
	}

	public USBWatcher(Path usbPath, FreeSpaceListener listener, int interval, int sizeLimit) throws IOException {
		this.store = Files.getFileStore(usbPath);
		this.listener = listener;
		this.interval = interval;
		this.sizeLimit = sizeLimit;
		this.timer = new Timer(true);
		this.limitWasReached = false;
		
		timer.schedule(this, 0, interval);
	}

	public int getInterval() {
		return interval;
	}

	public long getSizeLimit() {
		return sizeLimit;
	}

	public void run() {
		try {
			long freeSpace = store.getUsableSpace() / (1024 * 1024); // in megabytes

			// only send signal if we previously were not below the limit
			if (!limitWasReached && freeSpace < sizeLimit) {
				limitWasReached = true;
				listener.freeSpaceWarningCallback(freeSpace);
				LOG.log(Level.INFO, "Free space dropped below " + sizeLimit + " MB! " + freeSpace + " MB / " + sizeLimit + " MB");
			} else if (limitWasReached && freeSpace >= sizeLimit) {
				limitWasReached = false;
				LOG.log(Level.INFO, "Free space is over " + sizeLimit + " MB again! " + freeSpace + " MB / " + sizeLimit + " MB");
			} else {
				LOG.log(Level.FINEST, "Free space: " + freeSpace + " MB");
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Could not query free space...", ex);
		}
	}
}
