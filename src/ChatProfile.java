import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

public class ChatProfile extends JFrame {

	private JPanel contentPane;
	private JLabel PictureLabel = new JLabel("");

	/**
	 * Create the frame.
	 */
	public ChatProfile(String username, int groupSize) {
		setBounds(100, 100, 760, 574);
		setMinimumSize(new Dimension(760, 470));
		setResizable(false);
		contentPane = new JPanel();
		  contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton backBtn = new JButton("< BACK");
		backBtn.setBounds(16, 19, 96, 27);
		backBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		  PictureLabel.setBounds(12, 59, 364, 285);

		PictureLabel.setIcon(
				new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/profilePix.jpg")).getImage()));
		PictureLabel.setBorder(new LineBorder(Color.LIGHT_GRAY, 5));

		JLabel showNameLabel = new JLabel("");
		  showNameLabel.setForeground(Color.WHITE);
		  showNameLabel.setBounds(158, 387, 134, 19);
		  showNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		  
		  JLabel nameLabel = new JLabel("Upload Image");
		  nameLabel.setForeground(Color.WHITE);
		  nameLabel.setBounds(398, 149, 134, 19);
		  nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		  
		  JLabel noOfGroupLabel = new JLabel("Number Of Groups:");
		  noOfGroupLabel.setForeground(Color.WHITE);
		  noOfGroupLabel.setBounds(388, 82, 321, 19);
		  noOfGroupLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));

	
		// Set Profile Info
		showNameLabel.setText("");

		  JLabel showNumberLabel = new JLabel("");
		  showNumberLabel.setBounds(534, 80, 88, 22);
		  showNumberLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		  
		  JButton btnUpload = new JButton("UPLOAD");
		  btnUpload.setBounds(388, 182, 115, 27);
		  btnUpload.setFont(new Font("Tahoma", Font.PLAIN, 15));
		  btnUpload.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
		    uploadImage(e);
		   }
		  });

		
		contentPane.setLayout(null);
		contentPane.add(PictureLabel);
		contentPane.add(noOfGroupLabel);
		contentPane.add(showNumberLabel);
		contentPane.add(nameLabel);
		contentPane.add(btnUpload);
		contentPane.add(backBtn);
		contentPane.add(showNameLabel);
		
		JLabel nameLabel_1 = new JLabel("Full Name: ");
		nameLabel_1.setForeground(Color.WHITE);
		nameLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		nameLabel_1.setBounds(53, 387, 74, 19);
		contentPane.add(nameLabel_1);

		// update the no. of group and user name fields
		showNameLabel.setText(username);
		noOfGroupLabel.setText("Number of group(s): " + groupSize);
	}

	private void uploadImage(java.awt.event.ActionEvent evt) {
		JFileChooser filechooser = new JFileChooser();

		filechooser.setDialogTitle("Choose Your New Profile Picture");

		filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		filechooser.setAcceptAllFileFilterUsed(false);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp");
		filechooser.addChoosableFileFilter(filter);

		// below code selects the file
		int returnval = filechooser.showOpenDialog(this);
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			System.out.println(file.length());
			if (file.length() > 8192000) {
				int delete = JOptionPane.showConfirmDialog(null, "Too large", "Fail to upload",
						JOptionPane.CLOSED_OPTION);

			} else {
				BufferedImage newImage;
				try {
					// display the image in a Jlabel
					newImage = ImageIO.read(file);
					PictureLabel.setIcon(new ImageIcon(newImage));
					// PictureLabel.setIcon(new ImageIcon(new
					// javax.swing.ImageIcon(getClass().getResource("/Images/profilepic.jpg")).getImage()));
					// PictureLabel.setIcon(new ImageIcon(new
					// javax.swing.ImageIcon(getClass().getResource("/Images/profilepic.jpg")).getImage()));
				} catch (IOException e) {
					e.printStackTrace(); // Error handling
				}
				this.pack();
			}
		}
	}
}