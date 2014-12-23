package inf.elte.parhalg.packet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class BackupDonePacket extends Packet {

	private static final long serialVersionUID = 4794923092502417425L;
	
	private final String root;

	private final String relative;

	private final String backupName;

	private final Date date;

	public BackupDonePacket(String root, String relative, String backupName) {
		super(PacketType.BACKUP_DONE);
		this.root = root;
		this.relative = relative;
		this.backupName = backupName;
		this.date = new Date();
	}

	public String getRoot() {
		return root;
	}

	public String getRelative() {
		return relative;
	}

	public String getBackupName() {
		return backupName;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "BackupDonePacket [root=" + root + ", relative=" + relative + ", backupName=" + backupName + ", date=" + date + "]";
	}

}
