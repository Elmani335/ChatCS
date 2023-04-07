import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler extends Thread {
    private Socket socket; // Le socket de communication avec le client
    private ChatServer chatServer; // Le serveur de chat auquel le client est connecté
    private BufferedReader reader; // Le lecteur pour lire les messages du client
    private PrintWriter writer; // envoyer des messages au client
    private String username; // Le nom d'utilisateur du client
    public ClientHandler(Socket socket, ChatServer chatServer) throws IOException {
        this.socket = socket;
        this.chatServer = chatServer;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        try {
            // Lire le nom d'utilisateur du client
            username = reader.readLine();
            System.out.println("Client connecté avec le nom d'utilisateur : " + username);
            chatServer.logIncomingConnection(socket, username);

            // Envoyer un message de bienvenue au client
            writer.println("Bienvenue dans la salle de discussion, " + username + "!");

            String message;
            while ((message = reader.readLine()) != null) {
                // Diffuser le message à tous les clients
                chatServer.broadcast(username + ": " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Retirer le client de la liste des clients du serveur de chat et fermer les ressources
            chatServer.removeClient(this);
            try {
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}