package inf.elte.parhalg.packet;

public class MessagePacket extends Packet {

	private static final long serialVersionUID = -455750702351313529L;

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