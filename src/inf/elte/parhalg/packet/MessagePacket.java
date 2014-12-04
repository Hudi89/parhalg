package inf.elte.parhalg.packet;

public class MessagePacket extends Packet {

	private final String message;

	public MessagePacket(String message) {
		super(PacketType.MESSAGE);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "MessagePacket [message=" + message + "]";
	}

}