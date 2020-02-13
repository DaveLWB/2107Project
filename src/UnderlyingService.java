import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UnderlyingService {
	private static final int PORT = 6789;
	private static final String DELIMITER = "---";
	private static final String REPLY_SUFFIX = "_re";
	private static final int PREFIX_SIZE = 40;
	
	private static final String UNDERLYING_IP = "230.1.1.1";
	private static final String PREFIX_CHECK_EXISTING_USER = "___check_existing_user";
	private static final String REPLY_PREFIX_CHECK_EXISTING_USER = PREFIX_CHECK_EXISTING_USER + REPLY_SUFFIX;
	private static final String PREFIX_CHECK_EXISTING_GROUP = "___check_existing_group";
	private static final String REPLY_PREFIX_CHECK_EXISTING_GROUP = PREFIX_CHECK_EXISTING_GROUP + REPLY_SUFFIX;
	private static final String PREFIX_BROADCAST_USERNAME = "___check_broadcast_username";
	private static final String REPLY_PREFIX_BROADCAST_USERNAME = PREFIX_BROADCAST_USERNAME + REPLY_SUFFIX;
	private static final String PREFIX_ADD_USER_TO_GROUP = "___add_user_to_group";
	private static final String PREFIX_REQUEST_LATEST_MESSAGES = "___request_latest_messages";
	private static final String REPLY_PREFIX_REQUEST_LATEST_MESSAGES = PREFIX_REQUEST_LATEST_MESSAGES + REPLY_SUFFIX;
	private static final String PREFIX_USER_LEFT = "___user_left";

	private MulticastSocket underlyingSocket;
	private Map<String, UnderlyingReplyListener> listenerMap = new HashMap<>();
	private Map<String, ScheduledFuture<?>> timeoutMap = new HashMap<>();
	private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private UnderlyingActivityListener activityListener;
	private InetAddress address;
	private List<GroupMessage> latestMessagesBuffer = new ArrayList<GroupMessage>(100);


	public String currentUsername;
	public final HashMap<String, String> groupNameIpMap = new HashMap<String, String>();

	public UnderlyingService(UnderlyingActivityListener activityListener) throws IOException {
		this.activityListener = activityListener;
		address = InetAddress.getByName(UNDERLYING_IP);
		underlyingSocket = new MulticastSocket(PORT);
		underlyingSocket.joinGroup(address);
//		underlyingSocket.setLoopbackMode(true);
		handleMessages();
	}
	
	public void checkExistingUser(String username, UnderlyingReplyListener listener) {
		String replyId = createReplyId();
		listenerMap.put(replyId, listener);
		startTimeout(replyId);
		try {
			sendMessage(createMessage(PREFIX_CHECK_EXISTING_USER, replyId, username));
		} catch (IOException e) {
			System.out.println("Failed to send " + PREFIX_CHECK_EXISTING_USER);
			e.printStackTrace();
		}
	}
	
	public void checkExistingGroup(String groupName, UnderlyingReplyListener listener) {
		String replyId = createReplyId();
		listenerMap.put(replyId, listener);
		startTimeout(replyId);
		try {
			sendMessage(createMessage(PREFIX_CHECK_EXISTING_GROUP, replyId, groupName));
		} catch (IOException e) {
			System.out.println("Failed to send " + PREFIX_CHECK_EXISTING_GROUP);
			e.printStackTrace();
		}
	}
	
	public void broadcastUsername(String username) {
		String replyId = createReplyId();
		listenerMap.put(replyId, null);
		startTimeout(replyId);
		try {
			sendMessage(createMessage(PREFIX_BROADCAST_USERNAME, replyId, username));
		} catch (IOException e) {
			System.out.println("Failed to send " + PREFIX_BROADCAST_USERNAME);
			e.printStackTrace();
		}
	}
	
	public void addUserToGroup(String username, String groupName) {
		try {
			String groupIp = groupNameIpMap.get(groupName);
			if (groupIp == null) {
				throw new IllegalArgumentException("Invalid group name that is not in map");
			}
			sendMessage(createMessage(PREFIX_ADD_USER_TO_GROUP, username, groupName, groupIp));
		} catch (IOException e) {
			System.out.println("Failed to send " + PREFIX_ADD_USER_TO_GROUP);
			e.printStackTrace();
		}
	}
	
	public void requestLatestMessages(String groupName) {
		String replyId = createReplyId();
		listenerMap.put(replyId, null);
		startRequestLatestMessageTimeout(groupName, replyId);
		try {
			sendMessage(createMessage(PREFIX_REQUEST_LATEST_MESSAGES, replyId, groupName));
		} catch (IOException e) {
			System.out.println("Failed to send " + PREFIX_REQUEST_LATEST_MESSAGES);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void broadcastExit() {
		if (currentUsername == null) return;
		
		try {
			sendMessage(createMessage(PREFIX_USER_LEFT, currentUsername));
		} catch (IOException e) {
			System.out.println("Failed to send " + PREFIX_USER_LEFT);
			e.printStackTrace();
		}
	}
	
	private void handleMessages() throws IOException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				byte buf[] = new byte[1024];
                DatagramPacket dgpReceived = new DatagramPacket(buf, buf.length);
				while (true) {
					try {
//						System.out.println("Waiting for message");
						underlyingSocket.receive(dgpReceived);
					} catch (IOException e) {
						System.out.println("Error receiving packet from underlying socket");
						e.printStackTrace();
						continue;
					}
					
					byte[] data = dgpReceived.getData();
					try {
						String prefix = new String(data, 0, PREFIX_SIZE);
						String msg = new String(data, PREFIX_SIZE, data.length - PREFIX_SIZE);
						System.out.println("handlemessage prefix: " + prefix + ", msg: " + msg);

						List<String> args = getArguments(msg);
						if (prefix.startsWith(REPLY_PREFIX_CHECK_EXISTING_USER)) {
							String replyId = args.get(0);
							
							UnderlyingReplyListener listener = listenerMap.remove(replyId);
							if (listener != null) {
								listener.onReply(args);
								stopTimeout(replyId);
							}
						} else if (prefix.startsWith(PREFIX_CHECK_EXISTING_USER)) {
							String replyId = args.get(0);
							String username = args.get(1);
//							System.out.println("replyId: " + replyId + ", username: "+ username + ", currentUser: " + currentUsername);
							if (username.equals(currentUsername)) {
//								System.out.println("current user is equal");
								sendMessage(createMessage(REPLY_PREFIX_CHECK_EXISTING_USER, replyId));
							}
						} else if (prefix.startsWith(REPLY_PREFIX_CHECK_EXISTING_GROUP)) {
							String replyId = args.get(0);
							String groupName = args.get(1);
							String ip = args.get(2);
							groupNameIpMap.put(groupName, ip);

							UnderlyingReplyListener listener = listenerMap.remove(replyId);
							if (listener != null) {
								listener.onReply(args);
								stopTimeout(replyId);
							}
						} else if (prefix.startsWith(PREFIX_CHECK_EXISTING_GROUP)) {
							String replyId = args.get(0);
							String groupName = args.get(1);
							String groupIp = groupNameIpMap.get(groupName);
							if (groupIp != null) {
								sendMessage(createMessage(REPLY_PREFIX_CHECK_EXISTING_GROUP, replyId, groupName, groupIp));
							}
						} else if (prefix.startsWith(REPLY_PREFIX_BROADCAST_USERNAME)) {
							String replyId = args.get(0);
							String username = args.get(1);
							
							if (listenerMap.containsKey(replyId)) {
								if (activityListener != null) {
									activityListener.onUserOnline(username);
								}
							}
						} else if (prefix.startsWith(PREFIX_BROADCAST_USERNAME)) {
							String replyId = args.get(0);
							String username = args.get(1);
							
							if (activityListener != null) {
								activityListener.onUserOnline(username);
							}
							if (currentUsername != null) {							
								sendMessage(createMessage(REPLY_PREFIX_BROADCAST_USERNAME, replyId, currentUsername));
							}
						} else if (prefix.startsWith(PREFIX_ADD_USER_TO_GROUP)) {
							String username = args.get(0);
							if (!username.equals(currentUsername)) {
							    continue;
							}
							String groupName = args.get(1);
							String ip = args.get(2);
							groupNameIpMap.put(groupName, ip);
							if (activityListener != null) {
								activityListener.onJoinGroup(groupName, ip);
							}
						} else if (prefix.startsWith(REPLY_PREFIX_REQUEST_LATEST_MESSAGES)) {
							String replyId = args.get(0);
							String serializedMessage = args.get(1);
							if (listenerMap.containsKey(replyId)) {
								latestMessagesBuffer.add(new GroupMessage(serializedMessage));
							}
						} else if (prefix.startsWith(PREFIX_REQUEST_LATEST_MESSAGES)) {
							String replyId = args.get(0);
							String groupName = args.get(1);
							
							if (activityListener != null) {
								List<GroupMessage> messages = activityListener.onRequestLatestMessages(groupName);
								for (GroupMessage message : messages) {
									sendMessage(createMessage(REPLY_PREFIX_REQUEST_LATEST_MESSAGES, replyId, message.serialize()));
								}
							}
						} else if (prefix.startsWith(PREFIX_USER_LEFT)) {
							String username = args.get(0);
							
							if (activityListener != null) {
								activityListener.onUserOffline(username);
							}
						}
					} catch (IOException e) {
						System.out.println("Failed to send message");
						e.printStackTrace();
					}
					
				}
			}
		}).start();
	}
	
	private void sendMessage(byte[] buf) throws IOException {
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
		underlyingSocket.send(packet);
	}
	
	private void startTimeout(String replyId) {
		ScheduledFuture<?> future = executorService.schedule(new Runnable() {

			@Override
			public void run() {
				UnderlyingReplyListener listener = listenerMap.remove(replyId);
				if (listener != null) {
					listener.onTimeout();
				}
			}
		}, 3, TimeUnit.SECONDS);
		timeoutMap.put(replyId, future);
	}
	
	private void startRequestLatestMessageTimeout(String groupName, String replyId) {
		executorService.schedule(new Runnable() {

			@Override
			public void run() {
				UnderlyingReplyListener listener = listenerMap.remove(replyId);
				if (listener != null) {
					listener.onTimeout();
				}
				if (activityListener != null) {
					activityListener.onRequestLatestMessageResult(groupName, latestMessagesBuffer);
				}
				latestMessagesBuffer.clear();
			}
		}, 3, TimeUnit.SECONDS);
	}
	
	private void stopTimeout(String replyId) {
		ScheduledFuture<?> future = timeoutMap.remove(replyId);
		future.cancel(true);
	}
	
	private static List<String> getArguments(String message) {
		return Arrays.stream(message.split(DELIMITER)).map(p -> p.trim()).collect(Collectors.toList());
	}
	
	
	private static String createReplyId() {
		return RandomIdGenerator.getBase62(8);
	}
	
	private static byte[] createMessage(String prefix, String ...args) {
		if (prefix.length() > PREFIX_SIZE) {
			throw new IllegalArgumentException("invalid prefix");
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byteBuffer.put(prefix.getBytes());
		byteBuffer.position(PREFIX_SIZE);
		System.out.println("createMessage remaining: " + byteBuffer.remaining());
		byte[] data = Utility.trimZeros(String.join(DELIMITER, args)).getBytes();
		byteBuffer.put(data);
		
		
		return byteBuffer.array();
	}
	
	
}
