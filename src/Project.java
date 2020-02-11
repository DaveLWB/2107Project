import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Project extends JFrame implements UnderlyingActivityListener {

	private JPanel contentPane;
	private JTextField tfName;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextArea onlineUserTextArea;
	private JTextArea groupsTextArea;
	private JTextArea conversationTextArea;


	public UnderlyingService mainBroadcastService;
	public String userName;
	public List<String> onlineUsers;
	public List<String> selectedGroups;

//	public UnderlyingReplyListener listener = new UnderlyingReplyListener() {
//		@Override
//		public void onReply(List<String> args) {
//
//		}
//
//		@Override
//		public void onTimeout() {
//
//		}
//	};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Project frame = new Project();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}



	public UnderlyingService getInstance() {
		UnderlyingService broadcastService = null;
		try {
			broadcastService = new UnderlyingService(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return broadcastService;
	}





	/**
	 * Create the frame.
	 */
	public Project() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 694, 503);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setForeground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnRegisterUser = new JButton("Register user");
		btnRegisterUser.setBounds(10, 11, 112, 23);
		contentPane.add(btnRegisterUser);
		
		tfName = new JTextField();
		tfName.setBounds(132, 12, 225, 22);
		contentPane.add(tfName);
		tfName.setColumns(10);
		
		JLabel lblGroupManagement = new JLabel("Group Management");
		lblGroupManagement.setForeground(Color.WHITE);
		lblGroupManagement.setBounds(20, 45, 133, 14);
		contentPane.add(lblGroupManagement);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.setBounds(10, 101, 89, 23);
		contentPane.add(btnCreate);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.setBounds(109, 101, 89, 23);
		contentPane.add(btnEdit);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setBounds(208, 101, 89, 23);
		contentPane.add(btnDelete);
		
		JLabel lblOnlineUser = new JLabel("Online Users");
		lblOnlineUser.setForeground(Color.WHITE);
		lblOnlineUser.setBounds(10, 135, 98, 14);
		contentPane.add(lblOnlineUser);
		
		onlineUserTextArea = new JTextArea();
		onlineUserTextArea.setBounds(288, 160, 383, 258);
		contentPane.add(onlineUserTextArea);
		
		JLabel lblNewLabel = new JLabel("Groups");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(145, 135, 133, 14);
		contentPane.add(lblNewLabel);
		
		groupsTextArea = new JTextArea();
		groupsTextArea.setBounds(145, 160, 133, 258);
		contentPane.add(groupsTextArea);
			
		JLabel lblConversation = new JLabel("Conversation");
		lblConversation.setForeground(Color.WHITE);
		lblConversation.setBounds(288, 135, 243, 14);
		contentPane.add(lblConversation);
		
		conversationTextArea = new JTextArea();
		conversationTextArea.setBounds(10, 160, 125, 258);
		contentPane.add(conversationTextArea);
		
		JButton btnSendMessage = new JButton("Send Message");
		btnSendMessage.setBounds(10, 429, 121, 23);
		contentPane.add(btnSendMessage);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(141, 430, 287, 22);
		contentPane.add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setBounds(10, 70, 267, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);



		mainBroadcastService = getInstance();




			//Get all online users

		btnRegisterUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String tempName = tfName.getText();

				if (tempName.equals("") || tempName.substring(0, 1).matches("[0-9]") || tempName.length() > 8) {

					JOptionPane.showMessageDialog(new JFrame(), "Username invalid", "Error", JOptionPane.ERROR_MESSAGE);
				}else {

					mainBroadcastService.checkExistingUser(tempName, new UnderlyingReplyListener() {
						@Override
						public void onReply(List<String> args) {

							JOptionPane.showMessageDialog(new JFrame(), "Username taken", "Error", JOptionPane.ERROR_MESSAGE);
						}
						@Override
						public void onTimeout() {
							userName = tempName;
							mainBroadcastService.broadcastUsername(userName);
							mainBroadcastService.currentUsername = userName;
							System.out.println(userName);
						}
					});
//					onUserOnline(userName);

				}

			}
		});



		JButton btnProfile = new JButton("Profile");
		btnProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chatProfile frame = new chatProfile();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				dispose();			
				
			}
		});
		btnProfile.setBounds(367, 11, 89, 23);
		contentPane.add(btnProfile);













	}





	public void clearChat(){
		conversationTextArea.setText("");
	}


	// set Chat button to be true
	public void enableChatButton(){

	}

	public void disableChat(){

	}


	public void renderOnlineUsers(List<String> onlineUsers){
		for (int i = 0 ; i < onlineUsers.size(); i++) {
			onlineUserTextArea.append(onlineUsers.get(i) + "\n");
		}
	}




	@Override
	public void onUserOnline(String username) {
		if (!onlineUsers.contains(username)){
			onlineUsers.add(username);
			onlineUserTextArea.append(username + "\n");
		}
	}



	@Override
	public void onUserOffline(String username) {

		if (onlineUsers.contains(username)){
			onlineUsers.remove(username);
			renderOnlineUsers(onlineUsers);
		}
	}


	@Override
	public void onJoinGroup(String groupName, String ip) {

	}

	@Override
	public List<GroupMessage> onRequestLatestMessages(String groupName) {
		return null;
	}

	@Override
	public void onRequestLatestMessageResult(List<GroupMessage> allMessages) {


	}

}
