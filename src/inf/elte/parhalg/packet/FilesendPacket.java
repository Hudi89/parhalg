package inf.elte.parhalg.packet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FilesendPacket extends Packet {

	private static final long serialVersionUID = 4794923092502417425L;

	public static FilesendPacket createFromPath(File file) throws IOException {
		Path path = file.toPath();
		return new FilesendPacket(path.toString(), Files.readAllBytes(path));
	}

	private final String path;

	private final byte[] data;

	public FilesendPacket(String path, byte[] data) {
		super(PacketType.FILESEND);
		this.path = path;
		this.data = data;
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