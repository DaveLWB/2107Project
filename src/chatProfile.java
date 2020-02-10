import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

public class chatProfile extends JFrame {

 private JPanel contentPane;
 private JTextField nameTextField;
 private JLabel PictureLabel = new JLabel("");

 /**
  * Launch the application.
  */
 public static void main(String[] args) {
  EventQueue.invokeLater(new Runnable() {
   public void run() {
    try {
     chatProfile frame = new chatProfile();
     frame.setVisible(true);
    } catch (Exception e) {
     e.printStackTrace();
    }
   }
  });
 }

 /**
  * Create the frame.
  */
 public chatProfile() {
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setBounds(100, 100, 760, 574);
  setMinimumSize(new Dimension(760, 470));
  setResizable(false);
  contentPane = new JPanel();
  contentPane.setBackground(Color.LIGHT_GRAY);
  contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
  setContentPane(contentPane);
  
  JButton logoutBtn = new JButton("LOGOUT");
  logoutBtn.setBounds(635, 19, 99, 27);
  logoutBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
  logoutBtn.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
   }
  });
  
  JButton backBtn = new JButton("< BACK");
  backBtn.setBounds(16, 19, 96, 27);
  backBtn.setFont(new Font("Tahoma", Font.PLAIN, 15));
  backBtn.addActionListener(new ActionListener() {
   public void actionPerformed(ActionEvent e) {
	   Project frame = new Project();
	   frame.setLocationRelativeTo(null);
	   frame.setVisible(true);
	   dispose();
   }
  });
  PictureLabel.setBounds(36, 65, 321, 285);
  
  PictureLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("profilepic.jpg")).getImage()));
  PictureLabel.setBorder(new LineBorder(Color.LIGHT_GRAY, 5));
  
  JLabel showNameLabel = new JLabel("");
  showNameLabel.setBounds(158, 387, 76, 19);
  showNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
  
  JLabel nameLabel = new JLabel("Full Name: ");
  nameLabel.setBounds(388, 126, 74, 19);
  nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
  
  JLabel noOfGroupLabel = new JLabel("Number Of Groups:");
  noOfGroupLabel.setBounds(388, 82, 129, 19);
  noOfGroupLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
  
  JButton btnUpdate = new JButton("UPDATE");
  btnUpdate.setBounds(534, 182, 115, 27);
  
  btnUpdate.setFont(new Font("Tahoma", Font.PLAIN, 15));
  
  nameTextField = new JTextField();
  nameTextField.setBounds(503, 122, 231, 27);
  nameTextField.setFont(new Font("Tahoma", Font.PLAIN, 15));
  nameTextField.setColumns(10);
  
  // Set Profile Info
  showNameLabel.setText("Johnny Sins");
  
  JLabel showNumberLabel = new JLabel("5");
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
  
  btnUpdate.addActionListener(new ActionListener() {
	  	public void actionPerformed(ActionEvent e) {
	  		showNameLabel.setText(nameTextField.getText());
	  		
	  	}
	  });
  contentPane.setLayout(null);
  contentPane.add(PictureLabel);
  contentPane.add(noOfGroupLabel);
  contentPane.add(showNumberLabel);
  contentPane.add(logoutBtn);
  contentPane.add(nameLabel);
  contentPane.add(nameTextField);
  contentPane.add(btnUpload);
  contentPane.add(btnUpdate);
  contentPane.add(backBtn);
  contentPane.add(showNameLabel);
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
    if (returnval == JFileChooser.APPROVE_OPTION)
    {
        File file = filechooser.getSelectedFile();
        System.out.println(file.length());
        if(file.length()>8192000)
        {
         int delete = JOptionPane.showConfirmDialog(null,"Too large", "Fail to upload",JOptionPane.CLOSED_OPTION);
   
        }
        else {
         BufferedImage newImage;
         try {
             // display the image in a Jlabel
          newImage = ImageIO.read(file);
          PictureLabel.setIcon(new ImageIcon(newImage));
 //            PictureLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/profilepic.jpg")).getImage()));
 //            PictureLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/profilepic.jpg")).getImage()));
         } catch(IOException e) {
            e.printStackTrace(); // Error handling
         }
         this.pack();
        }
    }
}
}