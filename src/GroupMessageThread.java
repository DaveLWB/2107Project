import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

public class GroupMessageThread extends Thread {
	public String groupName;
	public final MulticastSocket groupSocket;
	private boolean running = true;
	private GroupMessageListener listener;
	
	public GroupMessageThread(String groupName, MulticastSocket groupSocket, GroupMessageListener listener) {
		this.groupName = groupName;
		this.groupSocket = groupSocket;
		this.listener = listener;
	}
	
	@Override
	public void run() {
		byte buf[] = new byte[1024];
		DatagramPacket dgpReceived = new DatagramPacket(buf, buf.length);
		while (running) {
			try {
				groupSocket.receive(dgpReceived);
				byte[] data = dgpReceived.getData();
				String message = new String(data, 0, data.length);
				GroupMessage gm = new GroupMessage(new java.util.Date().getTime(), Utility.trimZeros(message), groupName);

				listener.onGroupMessage(gm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void dispose() {
		running = false;
		groupSocket.close();
	}
}
