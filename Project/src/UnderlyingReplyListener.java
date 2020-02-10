import java.util.List;

public interface UnderlyingReplyListener {
	public void onReply(List<String> args);
	public void onTimeout();
}
