
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author acer
 */
public class serverFrame extends javax.swing.JFrame {
    /** Set state */
    private static String Chat = "Chat", username = "Username", len = "Array Length", turn = "Player Turn",
            coordinate = "Send coordiante", timeOut = "Time Out", isWin = "Who Win", clearPaint = "Clear Painting";

    /**
     * Creates new form serverFrame
     */

    Server server;
    Random rand;
    Font fontTitle;
    ArrayList<String> ansLst, cluLst;
    private static String clueWord, ansWord;
    private int count = 0, xx, yy;
    private ArrayList<String> userArr;

    // Start ClientHandeler Part//
    public class ClientHandler implements Runnable {
        private Socket client;
        private BufferedReader input;
        private PrintWriter output;
        private ArrayList<ClientHandler> clients;
        String text, temp1[], temp2[];
        int lastIndex;
        

        public ClientHandler(Socket client, ArrayList<ClientHandler> clients) throws IOException {
            this.client = client;
            this.clients = clients;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
        }

        @Override
        public void run() {

            /** wait for client sent data and check state of data */

            try {
                while ((text = input.readLine()) != null) {
                    temp1 = text.split(",");
                    lastIndex = temp1.length - 1;
                    try {

                        if (temp1[lastIndex].equals(username)) {
                            userArr.add(temp1[0]);
                            outToAll(text);
                            serverArea.append(temp1[0] + " has joined\n");

                            if (userArr.size() > 1) {
                                outToAll(userArr.get(0) + "," + turn);
                                count++;
                            }
                        } else if (temp1[lastIndex].equals(Chat)) {
                            serverArea.append(temp1[0] + ": " + temp1[1] + "  (state = " + temp1[lastIndex] + ")\n");
                            outToAll(text);
                        }

                        else if (temp1[lastIndex].equals(coordinate)) {
                            outToAll(text);
                        }

                        else if (temp1[lastIndex].equals(timeOut)) {
                            if (count % 2 == 0) {
                                outToAll(userArr.get(0) + "," + turn);
                            } else {
                                outToAll(userArr.get(1) + "," + turn);
                            }
                            count++;
                        }

                        else if (temp1[lastIndex].equals(isWin)) {
                            outToAll(text);
                        }

                        else if (temp1[lastIndex].equals(clearPaint)) {
                            outToAll(text);
                        }

                    } catch (Exception e) {
                    }

                }
            } catch (IOException e) {
                System.err.println("Error reading");
                System.err.println(e.getStackTrace());
            } finally {
                output.close();
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /** Sent data to all users and set conditions before sent data */
        private void outToAll(String msg) {
            temp2 = msg.split(",");
            lastIndex = temp2.length - 1;

            if (temp2[lastIndex].equals(Chat)) {
                for (ClientHandler aClient : clients) {
                    aClient.output.println(temp2[0] + "," + temp2[1] + "," + temp2[lastIndex]);
                }
            } else if (temp2[lastIndex].equals(username)) {
                for (ClientHandler aClient : clients) {
                    aClient.output.println(msg);
                    aClient.output.println(String.valueOf(userArr.size()) + "," + len);
                }
            } else if (temp2[lastIndex].equals(turn)) {

                for (ClientHandler aClient : clients) {
                    aClient.output.println(temp2[0] + "," + ansWord + "," + temp2[lastIndex]);
                }
                randomWord(rand);
                serverArea.append("Random Word: " + ansWord + "\n");

            } else if (temp2[lastIndex].equals(coordinate)) {
                for (ClientHandler aClient : clients) {
                    aClient.output.println(msg);
                }
            }

            else if (temp2[lastIndex].equals(isWin)) {
                for (ClientHandler aClient : clients) {
                    aClient.output.println(temp2[0] + "," + " Win!!" + "," + isWin);
                }
            }

            else if (temp2[lastIndex].equals(clearPaint)) {
                for (ClientHandler aClient : clients) {
                    aClient.output.println(msg);
                }
            }

        }

    }
    // End ClientHandeler Part //

    // Start Server Part //
    public class Server implements Runnable {
        private static final int PORT = 9090;
        private ArrayList<ClientHandler> clients = new ArrayList<>();

        /** Creates a new Socket Server */

        @Override
        public void run() {
            try {
                ServerSocket listener = new ServerSocket(PORT);
                while (true) {
                    serverArea.append("Waiting for client connection....\n");

                    /** wait for client connection */

                    Socket client = listener.accept();
                    for (ClientHandler s : clients) {
                        serverArea.append(s.toString() + " Connected!!\n");
                    }
                    serverArea.append("Connect to client!\n");

                    /**
                     * while client is connected on socket and then will be create new thread for
                     * run new client
                     */
                    ClientHandler client_thread = new ClientHandler(client, clients);
                    clients.add(client_thread);
                    Thread starter = new Thread(client_thread);
                    starter.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(serverFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // End Server Part //
    public serverFrame() {
        initComponents();
        userArr = new ArrayList<>();

        randomWord(rand);
        manageWord();
        serverArea.append("Random Word: " + ansWord + "\n");

    }

    /** Manage String And Array **/
    public void manageWord() {
        ansLst = splitString(ansWord);
        clueWord = repeat(ansWord.length(), "_");
        cluLst = splitString(clueWord);
    }

    /** Random 1 Word In Array **/
    public static void randomWord(Random rand) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("nouns.txt")); // Read File From File Text
            String line = reader.readLine();
            List<String> words = new ArrayList<>();
            while (line != null) {
                String[] wordsLine = line.split(" ");
                words.addAll(Arrays.asList(wordsLine)); // Add all word to list (like foreach)
                line = reader.readLine();
            }
            rand = new Random(System.currentTimeMillis());
            ansWord = words.get(rand.nextInt(words.size()));

        } catch (IOException e) {
            // Handle this
        }
    }

    /** Convert Array To String **/
    public static String arrToString(ArrayList<String> arr) {
        String tempString = "";
        for (String s : arr) {
            tempString += s + " ";
        }
        return tempString;
    }

    /** Split String To ArrayLists **/
    public static ArrayList<String> splitString(String word) {
        String[] tempArr;
        tempArr = word.split("");
        List<String> fixedLenghtList = Arrays.asList(tempArr);
        ArrayList<String> arrLst = new ArrayList<>(fixedLenghtList);
        return arrLst;

    }

    /** Replace All String To Something **/
    public static String repeat(int count, String with) {
        return new String(new char[count]).replace("\0", with);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        serverArea = new javax.swing.JTextArea();
        titleBar = new javax.swing.JPanel();
        titleExit = new javax.swing.JLabel();
        titleName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        getContentPane().add(startButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 80, 30));

        serverArea.setColumns(20);
        serverArea.setRows(5);
        jScrollPane1.setViewportView(serverArea);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 462, 433));

        titleBar.setBackground(new java.awt.Color(12, 52, 132));
        titleBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                titleBarMouseDragged(evt);
            }
        });
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                titleBarMousePressed(evt);
            }
        });

        titleExit.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        titleExit.setForeground(new java.awt.Color(255, 102, 102));
        titleExit.setText("X");
        titleExit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        titleExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                titleExitMouseClicked(evt);
            }
        });

        titleName.setFont(new java.awt.Font("Tempus Sans ITC", 1, 36)); // NOI18N
        titleName.setForeground(new java.awt.Color(255, 255, 255));
        titleName.setText("sketch.io server");

        javax.swing.GroupLayout titleBarLayout = new javax.swing.GroupLayout(titleBar);
        titleBar.setLayout(titleBarLayout);
        titleBarLayout.setHorizontalGroup(titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(titleBarLayout.createSequentialGroup().addContainerGap(120, Short.MAX_VALUE)
                        .addComponent(titleName).addGap(103, 103, 103).addComponent(titleExit).addContainerGap()));
        titleBarLayout.setVerticalGroup(titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(titleBarLayout.createSequentialGroup()
                        .addGroup(titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(titleExit).addComponent(titleName))
                        .addGap(3, 3, 3)));

        getContentPane().add(titleBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 50));

        jPanel1.setBackground(new java.awt.Color(12, 52, 132));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 500, Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 30, Short.MAX_VALUE));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 590, 500, 30));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bg.jpg"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 50, 510, 540));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void titleExitMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_titleExitMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }// GEN-LAST:event_titleExitMouseClicked

    private void titleBarMouseDragged(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_titleBarMouseDragged

        // Drag Window Program
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - yy);
    }// GEN-LAST:event_titleBarMouseDragged

    private void titleBarMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_titleBarMousePressed
        // TODO add your handling code here:
        xx = evt.getX();
        yy = evt.getY();
    }// GEN-LAST:event_titleBarMousePressed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_startButtonActionPerformed
        // TODO add your handling code here:
        Thread starter = new Thread(new Server());
        starter.start();

        serverArea.append("Server Started!!\n");

    }// GEN-LAST:event_startButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(serverFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new serverFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea serverArea;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel titleBar;
    private javax.swing.JLabel titleExit;
    private javax.swing.JLabel titleName;
    // End of variables declaration//GEN-END:variables
}
