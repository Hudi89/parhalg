package inf.elte.parhalg.packet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FilesendPacket extends Packet {

	private static final long serialVersionUID = 4794923092502417425L;

	public static FilesendPacket createFromPath(Path root, Path relative, String backupName) throws IOException {
		return new FilesendPacket(root.toString(), relative.toString(), backupName, Files.readAllBytes(root.resolve(relative)));
	}

	private final String root;

	private final String relative;

	private final String backupName;

	private final byte[] data;

	public FilesendPacket(String root, String relative, String backupName, byte[] data) {
		super(PacketType.FILESEND);
		this.root = root;
		this.relative = relative;
		this.backupName = backupName;
		this.data = data;
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

	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {
		return "FilesendPacket [root=" + root + ", relative=" + relative + ", backupName=" + backupName + ", data=" + Arrays.toString(data) + "]";
	}

}
