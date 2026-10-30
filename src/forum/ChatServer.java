package forum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * ChatServer simple implementation supporting named rooms (salons).
 * Protocol (single-line commands from client):
 *   NICK <name>         - set your nickname
 *   CREATE <room>       - create a room
 *   JOIN <room>         - join a room (leaves previous)
 *   LEAVE               - leave current room
 *   LIST                - list existing rooms
 *   MSG <text...>       - send message to members of current room
 */
public class ChatServer {
    private final int port;
    private final Map<String, Set<ClientHandler>> rooms = new ConcurrentHashMap<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("ChatServer started on port " + port);
            while (true) {
                Socket sock = server.accept();
                new Thread(new ClientHandler(sock)).start();
            }
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private String nick = "Anonymous";
        private String currentRoom = null;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {
                this.out = pw;
                pw.println("WELCOME");
                String line;
                while ((line = in.readLine()) != null) {
                    handleLine(line.trim());
                }
            } catch (IOException e) {
                // connection closed or error
            } finally {
                leaveRoom();
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private void handleLine(String line) {
            if (line.isEmpty()) return;
            String[] parts = line.split(" ", 2);
            String cmd = parts[0].toUpperCase();
            String arg = parts.length > 1 ? parts[1].trim() : "";

            switch (cmd) {
                case "NICK":
                    if (!arg.isEmpty()) {
                        this.nick = arg;
                        send("OK NICK " + this.nick);
                    } else send("ERR NICK requires a name");
                    break;
                case "CREATE":
                    if (arg.isEmpty()) { send("ERR CREATE requires room name"); break; }
                    rooms.computeIfAbsent(arg, k -> new CopyOnWriteArraySet<>());
                    send("OK CREATE " + arg);
                    break;
                case "JOIN":
                    if (arg.isEmpty()) { send("ERR JOIN requires room name"); break; }
                    joinRoom(arg);
                    break;
                case "LEAVE":
                    leaveRoom();
                    break;
                case "LIST":
                    send("ROOMS " + String.join(",", rooms.keySet()));
                    break;
                case "MSG":
                    if (currentRoom == null) { send("ERR You are not in a room"); break; }
                    if (arg.isEmpty()) { send("ERR MSG requires text"); break; }
                    broadcastToRoom(currentRoom, String.format("[%s][%s]: %s", currentRoom, nick, arg));
                    break;
                default:
                    send("ERR Unknown command");
            }
        }

        private void send(String s) {
            if (out != null) out.println(s);
        }

        private void joinRoom(String room) {
            rooms.computeIfAbsent(room, k -> new CopyOnWriteArraySet<>());
            leaveRoom();
            Set<ClientHandler> members = rooms.get(room);
            members.add(this);
            currentRoom = room;
            send("OK JOIN " + room);
            broadcastToRoom(room, "SYSTEM: " + nick + " joined " + room);
        }

        private void leaveRoom() {
            if (currentRoom == null) return;
            Set<ClientHandler> members = rooms.get(currentRoom);
            if (members != null) {
                members.remove(this);
                broadcastToRoom(currentRoom, "SYSTEM: " + nick + " left " + currentRoom);
                if (members.isEmpty()) {
                    rooms.remove(currentRoom);
                }
            }
            currentRoom = null;
            send("OK LEAVE");
        }

        private void broadcastToRoom(String room, String message) {
            Set<ClientHandler> members = rooms.get(room);
            if (members == null) return;
            for (ClientHandler m : members) {
                m.send(message);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 12345;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        new ChatServer(port).start();
    }
}
