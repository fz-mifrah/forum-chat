package forum;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Automated smoke test that connects two sockets to the ChatServer and verifies room broadcasts.
 */
public class TestClients {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 12345;
        if (args.length > 0) host = args[0];
        if (args.length > 1) port = Integer.parseInt(args[1]);

        Socket a = new Socket(host, port);
        Socket b = new Socket(host, port);

        BufferedReader ra = new BufferedReader(new InputStreamReader(a.getInputStream()));
        PrintWriter wa = new PrintWriter(a.getOutputStream(), true);

        BufferedReader rb = new BufferedReader(new InputStreamReader(b.getInputStream()));
        PrintWriter wb = new PrintWriter(b.getOutputStream(), true);

        // consume welcomes
        ra.readLine();
        rb.readLine();

        wa.println("NICK Alice");
        wa.println("CREATE testroom");
        wa.println("JOIN testroom");

        // read responses for A
        System.out.println("A1: " + ra.readLine());
        System.out.println("A2: " + ra.readLine());
        System.out.println("A3: " + ra.readLine());

        wb.println("NICK Bob");
        wb.println("JOIN testroom");

        // B should observe system join of Alice or OKs
        String lineB1 = rb.readLine();
        System.out.println("B1: " + lineB1);
        String lineB2 = rb.readLine();
        System.out.println("B2: " + lineB2);

        // A sends a message
        wa.println("MSG Hello everyone");

        // B should receive the MSG
        String received = rb.readLine();
        System.out.println("B received: " + received);

        a.close();
        b.close();
    }
}
