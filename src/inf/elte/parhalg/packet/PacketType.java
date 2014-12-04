package inf.elte.parhalg.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public enum PacketType {

	HELLO {
		@Override
		void writeFields(DataOutputStream os, Packet packet) {
			// No extra fields
		}

		@Override
		Packet readFields(DataInputStream is) {
			return new HelloPacket();

		}
	},
	MESSAGE {
		@Override
		void writeFields(DataOutputStream os, Packet packet) throws IOException {
			MessagePacket messagePacket = (MessagePacket) packet;
			os.writeUTF(messagePacket.getMessage());
		}

		@Override
		Packet readFields(DataInputStream is) throws IOException {
			String msg = is.readUTF();
			return new MessagePacket(msg);
		}
	},
	FILESEND {
		@Override
		void writeFields(DataOutputStream os, Packet packet) throws IOException {
			FilesendPacket filesendPacket = (FilesendPacket) packet;
			os.writeUTF(filesendPacket.getPath());
			os.writeInt(filesendPacket.getData().length);
			os.write(filesendPacket.getData());
		}

		@Override
		Packet readFields(DataInputStream is) throws IOException {
			String path = is.readUTF();
			int dataLen = is.readInt();
			byte[] data = new byte[dataLen];
			is.readFully(data, 0, dataLen);
			return new FilesendPacket(path, data);
		}
	},
	CLOSE {
		@Override
		void writeFields(DataOutputStream os, Packet packet) {
			// No extra fields

		}

		@Override
		Packet readFields(DataInputStream is) {
			return new ClosePacket();
		}
	};

	abstract void writeFields(DataOutputStream os, Packet packet) throws IOException;

	abstract Packet readFields(DataInputStream is) throws IOException;

	public static void write(DataOutputStream os, Packet packet) throws IOException {
		os.writeInt(packet.getType().ordinal());
		packet.getType().writeFields(os, packet);
		os.flush();
	}

	public static Packet read(DataInputStream is) throws IOException, EOFException {
		return values()[is.readInt()].readFields(is);
	}
}