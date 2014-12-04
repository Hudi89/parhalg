package inf.elte.parhalg.packet;

import java.util.Arrays;

public class FilesendPacket extends Packet {

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