import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame implements ActionListener, KeyListener {

    // Declaração de variaveis
    private Socket socket;
    private JButton buttonSend;
    private JButton buttonExit;
    private JPanel chatPanel;
    private JTextArea chatHistory;
    private JLabel labelHistory;
    private JLabel labelMessage;
    private OutputStream outputStream;
    private Writer outputWriter;
    private BufferedWriter bufferedWriter;
    private JTextField message;
    private JTextField textIP;
    private JTextField textPort;
    private JTextField textName;
    private static final long serialVersionUID = 7807451284291881701L;
    // Fim da declaração de variaveis

    public Client() throws IOException {
        JLabel labelTitle = new JLabel("Informações bate-papo: ");
        textIP = new JTextField("127.0.0.1");
        textPort = new JTextField("8081");
        textName = new JTextField("Anônimo");
        Object[] texts = { labelTitle, textIP, textPort, textName };
        JOptionPane.showMessageDialog(null, texts);

        chatHistory = new JTextArea(20, 35);
        JScrollPane scroll = new JScrollPane(chatHistory);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        chatPanel = new JPanel();
        chatHistory.setEditable(false);
        message = new JTextField(20);
        labelHistory = new JLabel("Histórico");
        labelMessage = new JLabel("Mensagem");
        buttonSend = new JButton("Enviar");
        buttonExit = new JButton("Sair");

        // Buttons
        buttonSend.addActionListener(this);
        buttonExit.addActionListener(this);
        buttonSend.addKeyListener(this);
        message.addKeyListener(this);

        // Configurações
        chatHistory.setLineWrap(true);
        chatPanel.add(labelHistory);
        chatPanel.add(scroll);
        chatPanel.add(labelMessage);
        chatPanel.add(message);
        chatPanel.add(buttonExit);
        chatPanel.add(buttonSend);

        setTitle(textName.getText());
        setContentPane(chatPanel);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(470, 450);
        setVisible(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    public void connect() throws IOException {

        socket = new Socket(textIP.getText(), Integer.parseInt(textPort.getText()));
        outputStream = socket.getOutputStream();
        outputWriter = new OutputStreamWriter(outputStream);
        bufferedWriter = new BufferedWriter(outputWriter);
        bufferedWriter.write(textName.getText() + "\r\n");
        bufferedWriter.flush();
    }

    public void sendMessage(String msg) throws IOException {
        if (msg.equals("Sair")) {
            bufferedWriter.write(textName.getText() + " foi desconectado \r\n");
            chatHistory.append(textName.getText() + " foi desconectado \r\n");
        } else if (!msg.equals("")) {
            bufferedWriter.write(msg + "\r\n");
            chatHistory.append(textName.getText() + ": " + message.getText() + "\r\n");
        }
        bufferedWriter.flush();
        message.setText("");
    }

    public void listen() throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";

        while (!"Sair".equalsIgnoreCase(msg))
            if (bfr.ready()) {
                msg = bfr.readLine();
                if (msg.equals("Sair"))
                    chatHistory.append("Falha no servidor. \r\n");
                else
                    chatHistory.append(msg + "\r\n");
            }
    }

    public void exit() throws IOException {
        sendMessage("Sair");
        bufferedWriter.close();
        outputWriter.close();
        outputStream.close();
        socket.close();
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getActionCommand().equals(buttonSend.getActionCommand())) {
                sendMessage(message.getText());
            } else if (e.getActionCommand().equals(buttonExit.getActionCommand())) {
                exit();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void keyPressed(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            try {
                sendMessage(message.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void keyReleased(java.awt.event.KeyEvent arg0) {
    }

    public void keyTyped(java.awt.event.KeyEvent arg0) {
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.connect();
        client.listen();
    }
}
