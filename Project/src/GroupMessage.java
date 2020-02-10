import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GroupMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5997286769086097191L;

	public final long timestamp;
	public final String username;
	public final String message;
	public final String groupName;

	public GroupMessage(long timestamp, String username, String message, String groupName) {
		this.timestamp = timestamp;
		this.username = username;
		this.message = message;
		this.groupName = groupName;
	}

	public byte[] getBytes() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return null;
	}

	public static GroupMessage fromBytes(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			GroupMessage o = (GroupMessage) in.readObject();
			return o;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return null;
	}
}
