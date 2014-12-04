package inf.elte.parhalg.packet;

import java.io.Serializable;

public abstract class Packet implements Serializable {

	private static final long serialVersionUID = -991252256127373928L;

	protected final PacketType type;

	public Packet(PacketType type) {
		this.type = type;
	}

	public PacketType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Packet [type=" + type + "]";
	}

}
