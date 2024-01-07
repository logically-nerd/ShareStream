package Client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;

public class Client {

    public static void main(String[] args) {
        final File[] fileToSend = new File[1];
        JFrame jFrame = new JFrame("Sender");
        jFrame.setSize(750, 650);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);

        JLabel jTitle = new JLabel("ShareStream");
        jTitle.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 50));
        jTitle.setBorder(new EmptyBorder(70, 0, 20, 0));
        jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jFileName = new JLabel("Chose a file to send");
        jFileName.setFont(new Font("Arial", Font.BOLD, 25));
        jFileName.setBorder(new EmptyBorder(70, 0, 0, 0));
        jFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(80, 0, 10, 0));

        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(200, 100));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 25));

        JButton jbChooseFile = new JButton("Select File");
        jbChooseFile.setPreferredSize(new Dimension(200, 100));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 25));

        jpButton.add(jbSendFile);
        jpButton.add(jbChooseFile);

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose File to Share");

                jFileChooser.setPreferredSize(new Dimension(600, 600));

                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jFileName.setText("Sharing \"" + fileToSend[0].getName() + "\"");
                }
            }
        });

        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null) {
                    jFileName.setText("Choose a File First");
                } else {
                    // FileInputStream fileInputStream = new
                    // FileInputStream(fileToSend[0].getAbsolutePath());
                    send(fileToSend);
                }
            }
        });

        jFrame.add(jTitle);
        jFrame.add(jFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);
    }

    public static void send(File[] fileToSend) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
            Socket socket = new Socket("localhost", 8000);

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String fileName = fileToSend[0].getName();
            byte[] fileNameByte = fileName.getBytes();

            byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
            fileInputStream.read(fileContentBytes);// read from the file

            dataOutputStream.writeInt(fileNameByte.length);// send the size of the data it will be receiving
            dataOutputStream.write(fileNameByte);

            dataOutputStream.writeInt(fileContentBytes.length);
            dataOutputStream.write(fileContentBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}