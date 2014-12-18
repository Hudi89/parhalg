package inf.elte.parhalg.packet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.nio.file.Paths;

public class FilesendPacket extends Packet {

	private static final long serialVersionUID = 4794923092502417425L;

	public static FilesendPacket createFromPath(String pathRoot, String path, String backupName) throws IOException {
		Path file = Paths.get(pathRoot+"/"+path);
		return new FilesendPacket(pathRoot,path,backupName, Files.readAllBytes(file));
	}

	private final String pathRoot;
	private final String backupName;
	private final String path;

	private final byte[] data;

	public FilesendPacket(String pathRoot, String path, String backupName, byte[] data) {
		super(PacketType.FILESEND);
		this.pathRoot = pathRoot;
		this.backupName = backupName;
		this.path = path;
		this.data = data;
	}

	public String getPathRoot() {
		return pathRoot;
	}
	public String getBackupName() {
		return backupName;
	}
	public String getPath() {
		return path;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {
		return "FilesendPacket [path=" + path + ", data=" + Arrays.toString(data) + "]";
	}

}
