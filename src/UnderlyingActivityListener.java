import java.util.List;

public interface UnderlyingActivityListener {
	// when a user comes online
	void onUserOnline(String username);
	// when a user goes offline
	void onUserOffline(String username);
	// when a some added u to a group
	void onJoinGroup(String groupName, String ip);
	// when u receive a request from other client for latest message
	List<GroupMessage> onRequestLatestMessages(String groupName);
	// when u receive have captured all the replies for your latest message request
	void onRequestLatestMessageResult(String groupName, List<GroupMessage> allMessages);
}
