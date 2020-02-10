import java.util.ArrayList;
import java.util.List;

public class UnderlyingData {
	public final String messageType;
	public final List<String> arguments = new ArrayList<>(5);
	
	public UnderlyingData(String messageType) {
		this.messageType = messageType;
	}
}
