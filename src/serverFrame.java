
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static String Chat = "Chat", Game = "Game", draw = "Draw";

    /**
     * Creates new form serverFrame
     */

    Server server;
    ArrayList<String> ansLst, cluLst;
    private static String clueWord, ansWord;
    Random rand;

    // Start ClientHandeler Part//
    public class ClientHandler implements Runnable {
        private Socket client;
        private BufferedReader input;
        private PrintWriter output;
        private ArrayList<ClientHandler> clients;

        public ClientHandler(Socket client, ArrayList<ClientHandler> clients) throws IOException {
            this.client = client;
            this.clients = clients;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
        }

        @Override
        public void run() {
            String text;

            try {
                while ((text = input.readLine()) != null) {
                    String temp1[] = text.split(",");
                    if (temp1[2].equals(Chat)) {
                        serverArea.append(temp1[0] + ": " + temp1[1] + "  (state = " + temp1[2] + ")\n");
                        outToAll(text);
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

        private void outToAll(String msg) {
            String temp2[] = msg.split(",");
            
            if (temp2[2].equals(Chat)) {
                for (ClientHandler aClient : clients) {
                    aClient.output.println(temp2[0] + "," + temp2[1] + "," + temp2[2]);
                }
            }

        }

    }
    // End ClientHandeler Part //

    // Start Server Part //
    public class Server implements Runnable {
        private static final int PORT = 9090;
        private ArrayList<ClientHandler> clients = new ArrayList<>();

        @Override
        public void run() {
            try {
                ServerSocket listener = new ServerSocket(PORT);
                while (true) {
                    serverArea.append("Waiting for client connection....\n");
                    Socket client = listener.accept();
                    for (ClientHandler s : clients) {
                        serverArea.append(s.toString()+" Connected!!\n");
                    }
                    serverArea.append("Connect to client!\n");
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
        randomWord(rand);
        manageWord();
        serverArea.append("Random Word: "+ ansWord + "\n");
        serverArea.append("Clue Word: "+ clueWord + "\n");
    }
    
    /** Manage String And Array **/
    public void manageWord() {
        ansLst = splitString(ansWord);    
        clueWord = repeat(ansWord.length(), "_");
        cluLst = splitString(clueWord);
    }
    
    /** Random 1 Word In Array **/
    public static void randomWord(Random rand) {
        try{
            BufferedReader reader = new BufferedReader(new FileReader("nouns.txt")); // Read File From File Text
            String line = reader.readLine();
            List<String> words = new ArrayList<>();
            while(line != null) {
                String[] wordsLine = line.split(" ");
                words.addAll(Arrays.asList(wordsLine)); // Add all word to list (like foreach)
                line = reader.readLine();
            }
            rand = new Random(System.currentTimeMillis());
            String word = words.get(rand.nextInt(words.size()));
            ansWord = word.substring(0, 1).toUpperCase() + word.substring(1); // Capitalize The First Letter Of Word
        
        }   catch (IOException e) {
         // Handle this
        }
    }
    
    /**  Convert Array To String **/
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
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        serverArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        serverArea.setColumns(20);
        serverArea.setRows(5);
        jScrollPane1.setViewportView(serverArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(startButton).addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                421, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(53, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                layout.createSequentialGroup().addGap(30, 30, 30).addComponent(startButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 433,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(46, Short.MAX_VALUE)));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(serverFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(serverFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(serverFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(serverFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new serverFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea serverArea;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
