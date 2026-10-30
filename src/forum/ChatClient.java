package forum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Simple console client for the ChatServer.
 * Commands (use slash-style in client):
 *   /nick NAME      -> NICK NAME
 *   /create ROOM    -> CREATE ROOM
 *   /join ROOM      -> JOIN ROOM
 *   /leave          -> LEAVE
 *   /list           -> LIST
 *   any other line  -> MSG line
 */
public class ChatClient {
    private final String host;
    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            reader.setDaemon(false);
            reader.start();

            System.out.println("Connected to " + host + ":" + port + ". Type /help for commands.");
            String input;
            while ((input = console.readLine()) != null) {
                input = input.trim();
                if (input.isEmpty()) continue;
                if (input.startsWith("/")) {
                    String[] parts = input.split(" ", 2);
                    String cmd = parts[0].toLowerCase();
                    String arg = parts.length > 1 ? parts[1] : "";
                    switch (cmd) {
                        case "/nick": serverOut.println("NICK " + arg); break;
                        case "/create": serverOut.println("CREATE " + arg); break;
                        case "/join": serverOut.println("JOIN " + arg); break;
                        case "/leave": serverOut.println("LEAVE"); break;
                        case "/list": serverOut.println("LIST"); break;
                        case "/help": printHelp(); break;
                        case "/quit": return;
                        default: System.out.println("Unknown client command. Use /help");
                    }
                } else {
                    serverOut.println("MSG " + input);
                }
            }
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  /nick NAME   - set your nickname");
        System.out.println("  /create ROOM - create a room");
        System.out.println("  /join ROOM   - join a room");
        System.out.println("  /leave       - leave current room");
        System.out.println("  /list        - list rooms");
        System.out.println("  /quit        - exit");
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 12345;
        if (args.length > 0) host = args[0];
        if (args.length > 1) port = Integer.parseInt(args[1]);
        new ChatClient(host, port).start();
    }
}
