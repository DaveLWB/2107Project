import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.acl.Group;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Project extends JFrame implements UnderlyingActivityListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5119421811793569103L;

	private static final int PORT = 6789;
	private JPanel contentPane;
	private JTextField tfName;
	private JTextField tfConversationInput;
	private JTextField tfGroupName;
	private JList<String> onlineUserList;
	private JList<String> groupsList;
	private JTextArea conversationTextArea;
	private Set<String> onlineUserSet = new HashSet<>();
	private JButton btnEdit;
	private JButton btnDelete;
	private JButton btnAddToGroup;
	private JButton btnSendMessage;
	private HashMap<String, GroupMessageThread> groupThreadsMap = new HashMap<>();


	public UnderlyingService mainBroadcastService;
	public String userName = null;
	public String activeGroup = null;
	public Integer activeGroupIndex;
	public DefaultListModel<String> onlineUsers = new DefaultListModel<>();
	public DefaultListModel<String> selectedGroups = new DefaultListModel<>();
	public final HashMap<String, String> selectedGroupIP = new HashMap<String, String>();
	public final HashMap<String, List<GroupMessage>> groupMessagesMap = new HashMap<>();
	private String selectedUser = null;


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
//		GroupMessage test = new GroupMessage(1, "abc", "abc");
//		System.out.println(test.getBytes().length);
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
	 * Create the frame.™
	 */
	public Project() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 719, 524);
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
		
		JButton btnCreateGroup = new JButton("Create");
		btnCreateGroup.setBounds(288, 69, 89, 23);
		contentPane.add(btnCreateGroup);
		btnCreateGroup.setEnabled(false);

		btnEdit = new JButton("Edit");
		btnEdit.setBounds(109, 101, 89, 23);
		contentPane.add(btnEdit);
		btnEdit.setEnabled(false);

		btnDelete = new JButton("Delete");
		btnDelete.setBounds(208, 101, 89, 23);
		contentPane.add(btnDelete);
		btnDelete.setEnabled(false);

		btnAddToGroup = new JButton("Add to Group");
		btnAddToGroup.setBounds(10, 101, 89, 23);
		contentPane.add(btnAddToGroup);
		btnAddToGroup.setEnabled(false);
		
		JLabel lblOnlineUser = new JLabel("Online Users");
		lblOnlineUser.setForeground(Color.WHITE);
		lblOnlineUser.setBounds(10, 147, 98, 14);
		contentPane.add(lblOnlineUser);
		
		conversationTextArea = new JTextArea();
		//conversationTextArea.setBounds(288, 180, 383, 258);
		contentPane.add(conversationTextArea);
		JScrollPane scr = new JScrollPane(conversationTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scr.setBounds(288, 172, 383, 258);
		contentPane.add(scr);
		
		JLabel lblNewLabel = new JLabel("Groups");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(145, 147, 133, 14);
		contentPane.add(lblNewLabel);
		
		groupsList = new JList<>(selectedGroups);
		groupsList.setBounds(145, 172, 133, 258);
		contentPane.add(groupsList);
		JScrollPane scr2 = new JScrollPane(groupsList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scr2.setBounds(145, 172, 133, 258);
		contentPane.add(scr2);
			
		JLabel lblConversation = new JLabel("Conversation");
		lblConversation.setForeground(Color.WHITE);
		lblConversation.setBounds(288, 147, 243, 14);
		contentPane.add(lblConversation);
		
		onlineUserList = new JList<>(onlineUsers);
		onlineUserList.setBounds(10, 172, 125, 258);
		contentPane.add(onlineUserList);
		JScrollPane scr1 = new JScrollPane(onlineUserList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scr1.setBounds(10, 172, 125, 258);
		contentPane.add(scr1);
		
		btnSendMessage = new JButton("Send Message");
		btnSendMessage.setBounds(438, 441, 121, 23);
		contentPane.add(btnSendMessage);
		btnSendMessage.setEnabled(false);
		
		tfConversationInput = new JTextField();
		tfConversationInput.setColumns(10);
		tfConversationInput.setBounds(141, 442, 287, 22);
		contentPane.add(tfConversationInput);
		
		tfGroupName = new JTextField();
		tfGroupName.setBounds(10, 70, 267, 20);
		contentPane.add(tfGroupName);
		tfGroupName.setColumns(10);



		mainBroadcastService = getInstance();


		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mainBroadcastService.broadcastExit();
			}
			
		});

		JButton btnProfile = new JButton("Profile");
		btnProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatProfile frame = new ChatProfile(userName, selectedGroups.size());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				dispose();

			}
		});
		btnProfile.setBounds(507, 41, 89, 51);
		btnProfile.setEnabled(false);
		contentPane.add(btnProfile);

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
							JOptionPane.showMessageDialog(new JFrame(), "Username taken, please try again", "Error", JOptionPane.ERROR_MESSAGE);
						}
						@Override
						public void onTimeout() {
							userName = tempName;
							mainBroadcastService.broadcastUsername(userName);
							mainBroadcastService.currentUsername = userName;
							System.out.println(userName);
							btnCreateGroup.setEnabled(true);
							btnProfile.setEnabled(true);
							btnRegisterUser.setEnabled(false);
						}
					});
				}

			}
		});


		btnCreateGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String groupName = tfGroupName.getText();

				if (groupName.isEmpty()){
					JOptionPane.showMessageDialog(new JFrame(), "Please enter group name", "Error", JOptionPane.ERROR_MESSAGE);
				}else {

					mainBroadcastService.checkExistingGroup(groupName, new UnderlyingReplyListener() {
						@Override
						public void onReply(List<String> args) {
							JOptionPane.showMessageDialog(new JFrame(), "Group Exist, please join another group", "Error", JOptionPane.ERROR_MESSAGE);
						}

						@Override
						public void onTimeout() {
							String ip = generateIP();
							mainBroadcastService.groupNameIpMap.put(groupName, ip);
							selectedGroups.addElement(groupName);
							selectedGroupIP.put(groupName, ip);
							ArrayList<String> newGroupMember = new ArrayList<String>();
							newGroupMember.add(userName);
							activeGroup = groupName;
							activeGroupIndex = selectedGroups.size();
							runGroupThreads(groupName, ip);
						}
					});

				}
			}
		});



		btnAddToGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (userName.equals(selectedUser)) {
					JOptionPane.showMessageDialog(new JFrame(), "You are already in the group", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (selectedUser == null && activeGroup == null) {
					JOptionPane.showMessageDialog(new JFrame(), "No Selection made", "Error", JOptionPane.ERROR_MESSAGE);
				}else {
					mainBroadcastService.addUserToGroup(selectedUser, activeGroup);
				}
			}
		});



		ListSelectionListener onlineUserSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {

				boolean adjust = e.getValueIsAdjusting();

				if (!adjust) {
					JList<String> list = (JList<String>) e.getSource();
					selectedUser = list.getSelectedValue();
				}

			}
		};
		onlineUserList.addListSelectionListener(onlineUserSelectionListener);




		ListSelectionListener groupSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {

				boolean adjust = e.getValueIsAdjusting();

				if (!adjust) {
					JList<String> list = (JList<String>) e.getSource();
					String selectedGroupName = list.getSelectedValue();
					System.out.println(selectedGroupName);
					activeGroup = selectedGroupName;
					btnSendMessage.setEnabled(true);
					btnEdit.setEnabled(true);
					btnDelete.setEnabled(true);
					tfGroupName.setText(selectedGroupName);

					List<GroupMessage> gmList = groupMessagesMap.get(selectedGroupName);
					if (gmList == null) {
						gmList = new ArrayList<>();
						groupMessagesMap.put(selectedGroupName, gmList);
					}
					for (GroupMessage msg: gmList){
						System.out.println(msg.message);
					}

					if (gmList != null) {
						updateConversation(gmList);
					}

				}



			}
		};
		groupsList.addListSelectionListener(groupSelectionListener);


		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Integer selectedGroupIndex = groupsList.getSelectedIndex();
				if (selectedGroupIndex != null) {

					String selectedGroupName = selectedGroups.get(selectedGroupIndex);
					selectedGroups.remove(selectedGroupIndex);
					selectedGroupIP.remove(selectedGroupName);
					GroupMessageThread t = groupThreadsMap.remove(selectedGroupName);
					t.dispose();
					mainBroadcastService.groupNameIpMap.remove(selectedGroupName);
				}

			}
		});


		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String editName = tfGroupName.getText();
				if (editName.equals(activeGroup)) {
					return;
				}
				
				mainBroadcastService.checkExistingGroup(editName, new UnderlyingReplyListener() {
					@Override
					public void onReply(List<String> args) {
						JOptionPane.showMessageDialog(new JFrame(), "Group Exist, please choose another group name", "Error", JOptionPane.ERROR_MESSAGE);
					}

					@Override
					public void onTimeout() {
						mainBroadcastService.changeGroupName(activeGroup, editName);
					}
				});
			}
		});





		btnSendMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String inputText = tfConversationInput.getText().trim();

				if (inputText != null) {

					MulticastSocket messagingGroupSocket = groupThreadsMap.get(activeGroup).groupSocket;
					inputText = userName + " : " + inputText;
					byte[] buf = inputText.getBytes();

					if (activeGroup == null) {
						System.out.println("no active group");
						return;
					}
					String ipAddress = selectedGroupIP.get(activeGroup);
					try {

						InetAddress address = InetAddress.getByName(ipAddress);
						DatagramPacket dgpConnected = new DatagramPacket(buf, buf.length, address, PORT);
						messagingGroupSocket.send(dgpConnected);
//
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}

			}
		});


	}

	private void runGroupThreads(String groupName, String ip){
		if(selectedGroups.size() > 0){
			btnAddToGroup.setEnabled(true);
		}

		InetAddress groupAddress;
		MulticastSocket groupSocket = null;
		try {
			groupAddress = InetAddress.getByName(ip);
			groupSocket = new MulticastSocket(PORT);
			groupSocket.joinGroup(groupAddress);
		} catch (IOException e) {
			System.out.println("Could not create multicast socket" + e.getMessage());
			e.printStackTrace();
		}
		
		GroupMessageThread t = new GroupMessageThread(groupName, groupSocket, new GroupMessageListener() {
			
			@Override
			public void onGroupMessage(GroupMessage gm) {
				List<GroupMessage> groupMessagesList = groupMessagesMap.get(groupName);
				if (groupMessagesList == null) {
					groupMessagesList = new ArrayList<>();
					groupMessagesMap.put(groupName, groupMessagesList);
				}
				groupMessagesList.add(gm);

				if (gm.groupName == activeGroup) {
					updateConversation(groupMessagesList);
				}
			}
		});
		t.start();
		groupThreadsMap.put(groupName, t);
	}



	private void updateOnlineList() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				onlineUsers.clear();
				onlineUserSet.forEach(onlineUsers::addElement);
			}
		});
	}




	public void updateConversation(List<GroupMessage> groupMessages) {
		final StringBuilder conversationsText = new StringBuilder();

		for (GroupMessage message : groupMessages){
			DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
			String strDate = dateFormat.format(message.timestamp);
			conversationsText.append(message.message + "@" + strDate + "\n");
		}

		EventQueue.invokeLater(() -> conversationTextArea.setText(conversationsText.toString()));
	}





	@Override
	public void onUserOnline(String username) {
		onlineUserSet.add(username);
		updateOnlineList();
	}



	@Override
	public void onUserOffline(String username) {
		onlineUserSet.remove(username);
		updateOnlineList();
	}



	@Override
	public void onJoinGroup(String groupName, String ip) {
		System.out.println("On Join Group" + groupName + ip);
			btnAddToGroup.setEnabled(true);
			selectedGroupIP.put(groupName, ip);
			EventQueue.invokeLater(() -> selectedGroups.addElement(groupName));
			mainBroadcastService.groupNameIpMap.put(groupName,ip);


			runGroupThreads(groupName, ip);
			mainBroadcastService.requestLatestMessages(groupName);

	}

	@Override
	public List<GroupMessage> onRequestLatestMessages(String groupName) {

		List<GroupMessage> latestMessage = groupMessagesMap.get(groupName);
		if (latestMessage == null){
			latestMessage = new ArrayList<>();
			return latestMessage;
		}
		return latestMessage;

	}

	@Override
	public void onRequestLatestMessageResult(String groupName, List<GroupMessage> allMessages) {



			Collections.sort(allMessages, new Comparator<GroupMessage>() {
				@Override
				public int compare(GroupMessage o1, GroupMessage o2) {
					return (int)(o1.timestamp - o2.timestamp);
				}
			});


			List<GroupMessage> filteredMessage = new ArrayList<GroupMessage>();


			for (GroupMessage msg : allMessages) {

				if (filteredMessage.isEmpty() || !filteredMessage.get(filteredMessage.size() -1 ).message.equals(msg.message)){
					filteredMessage.add(msg);
				}

			}

			for (GroupMessage msg : filteredMessage) {
				System.out.println(msg.message + "\n");
			}

			groupMessagesMap.put(groupName, filteredMessage);
			updateConversation(filteredMessage);
	}
	
	@Override
	public void onGroupNameChange(String oldName, String newName, String ip) {
		EventQueue.invokeLater(() -> {
			int oldIndex = selectedGroups.indexOf(oldName);
			selectedGroups.set(oldIndex, newName);
			if (oldName.equals(activeGroup)) {
				groupsList.setSelectedIndex(oldIndex);
				activeGroup = newName;
				tfGroupName.setText(newName);
			}
		});
		groupMessagesMap.put(newName, groupMessagesMap.remove(oldName));
		
		groupThreadsMap.get(oldName).groupName = newName;
		groupThreadsMap.put(newName, groupThreadsMap.remove(oldName));
		
		selectedGroupIP.remove(oldName);
		selectedGroupIP.put(newName, ip);
	}



	private String generateIP(){
		Random r  = new Random();
		String ip = "230.1." + r.nextInt(256) + "." + r.nextInt(256);
		return ip;
	}

}
