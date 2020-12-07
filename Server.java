import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server extends Thread {

    private static ServerSocket server;
    private static ArrayList<BufferedWriter> clients;
    private String name;
    private Socket connection;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;

    public Server(Socket connection) {
        this.connection = connection;
        try {
            in = connection.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String msg;
            OutputStream ou = this.connection.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clients.add(bfw);
            name = msg = bfr.readLine();

            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
                msg = bfr.readLine();
                sendToAll(bfw, msg);
                System.out.println(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter bwS;

        for (BufferedWriter bw : clients) {
            bwS = (BufferedWriter) bw;
            if (!(bwSaida == bwS)) {
                bw.write(name + ": " + msg + "\r\n");
                bw.flush();
            }
        }
    }
    public static void main(String[] args) throws IOException {
        // Cria os objetos necessário para instânciar o servidor
        JLabel lblMessage = new JLabel("Porta do Servidor:");
        JTextField txtPorta = new JTextField("8081");
        Object[] texts = { lblMessage, txtPorta };
        JOptionPane.showMessageDialog(null, texts);
        server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
        clients = new ArrayList<BufferedWriter>();
        JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

        while (true) {
            System.out.println("Servidor rodando na porta: " + txtPorta.getText());
            // Aceitando a conexão com o Cliente
            Socket client = server.accept();
            // IP do Cliente
            String ip = client.getInetAddress().getHostAddress();
            // Aviso de conexão
            System.out.println("Nova conexão com o cliente: " + ip);
            new Server(client).start();
        }
    }
}
