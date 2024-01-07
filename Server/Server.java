package Server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

public class Server {
    static ArrayList<MyFile> files = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int fileId = 0;

        JFrame jFrame = new JFrame("Receiver");
        jFrame.setSize(750, 650);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(jScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jTitle = new JLabel("ShareStream");
        jTitle.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 50));
        jTitle.setBorder(new EmptyBorder(70, 0, 20, 0));
        jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        // receiving

        ServerSocket serverSocket = new ServerSocket(8000);
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();
                if (fileNameLength > 0) {
                    byte[] fileNameByte = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameByte, 0, fileNameLength);
                    String fileName = new String(fileNameByte);

                    int fileContentLength = dataInputStream.readInt();
                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentLength);

                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

                        JLabel jlFileName = new JLabel(fileName);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 25));
                        jlFileName.setBorder(new EmptyBorder(15, 0, 15, 0));
                        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            jPanel.validate();
                        } else {
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);

                            jFrame.validate();
                        }
                        files.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));

                        fileId++;
                    }
                }
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    public static MouseListener getMyMouseListener() {

        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel jPanel = (JPanel) e.getSource();
                int fileId = Integer.parseInt(jPanel.getName());
                for (MyFile file : files) {
                    if (file.getId() == fileId) {
                        JFrame jfPreview = createFrame(file.getName(), file.getData(), file.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            public JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
                JFrame jFrame = new JFrame("Download");
                jFrame.setSize(600, 600);

                JPanel jPanel = new JPanel();
                jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

                JLabel jlTitle = new JLabel("Download the file");
                jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
                jlTitle.setBorder(new EmptyBorder(50, 0, 30, 0));

                JLabel jlPrompt = new JLabel("Continue to download " + fileName + " ?");
                jlPrompt.setFont(new Font("Arial", Font.BOLD, 25));
                jlPrompt.setBorder(new EmptyBorder(50, 0, 30, 0));
                jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton jbYes = new JButton("Yes");
                jbYes.setPreferredSize(new Dimension(200, 80));
                jbYes.setFont(new Font("Arial", Font.BOLD, 25));

                JButton jbNo = new JButton("No");
                jbNo.setPreferredSize(new Dimension(180, 70));
                jbNo.setFont(new Font("Arial", Font.PLAIN, 25));

                JLabel jlFileContent = new JLabel();
                jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

                JPanel jpButtons = new JPanel();
                jpButtons.setBorder(new EmptyBorder(30, 0, 20, 0));
                jpButtons.add(jbYes);
                jpButtons.add(jbNo);

                if (fileExtension.equalsIgnoreCase("txt")) {
                    jlFileContent.setText("<html>" + new String(fileData) + "</html>");
                } else {
                    // jlFileContent.setIcon(new ImageIcon(fileData));
                    jlFileContent.setIcon(new ImageIcon(
                            new ImageIcon(fileData).getImage().getScaledInstance(250, 250, Image.SCALE_DEFAULT)));
                }

                jbYes.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        File fileToDownload = new File(fileName);

                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);

                            fileOutputStream.write(fileData);
                            fileOutputStream.close();
                            ;

                            jFrame.dispose();
                        } catch (IOException error) {
                            error.printStackTrace();
                        }
                    }
                });

                jbNo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jFrame.dispose();
                    }
                });

                jPanel.add(jlTitle);
                jPanel.add(jlPrompt);
                jPanel.add(jlFileContent);
                jPanel.add(jpButtons);

                jFrame.add(jPanel);
                return jFrame;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        };
    }

    public static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            // if file have extension
            return fileName.substring(i + 1);
        }
        return null;
    }
}
