import java.util.Arrays;

public class GroupMessage {
	public final long timestamp;
	public final String message;
	public final String groupName;

	public GroupMessage(long timestamp, String message, String groupName) {
		this.timestamp = timestamp;
		this.message = message;
		this.groupName = groupName;
	}
	
	public GroupMessage(String serialized) {
		String[] parts = serialized.split("#");
		Integer[] lengths = Arrays.stream(parts[0].split(",")).map(Integer::parseInt).toArray(Integer[]::new);
		
		String data = parts[1];
		int i = 0;
		timestamp = Long.parseLong(data.substring(i, lengths[0]));
		i = lengths[0] + lengths[1];
		message = data.substring(lengths[0], i);
		groupName = data.substring(i, i + lengths[2]);
	}

	public String serialize() {
		StringBuilder sb = new StringBuilder();
		String timestamp = String.valueOf(this.timestamp);
		sb.append(timestamp.length());
		sb.append(",");
		sb.append(message.length());
		sb.append(",");
		sb.append(groupName.length());
		sb.append("#");
		sb.append(timestamp);
		sb.append(message);
		sb.append(groupName);
		String result = sb.toString();
		System.out.println("serialize: " + result);
		return result;
	}
}
