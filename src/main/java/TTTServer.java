import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class TTTServer extends JFrame{
    Game game;
    Socket socket;
    boolean isHost;
    int turn;
    Button[] button;

    public TTTServer(String title, boolean isHost, Socket socket) {
        super(title);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        button = new Button[9];
        game = new Game();

        this.socket = socket;
        this.isHost = isHost;

        MyActionListener actionListener = new MyActionListener(this, socket, isHost, game);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int)((screenSize.getWidth() - 715) / 2), (int)((screenSize.getHeight() - 715) / 2));
        this.setSize(715,765);

        Container contentPane = this.getContentPane();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new GridLayout(3,3));

        for (int i = 0; i < 9; i++) {
            button[i] = new Button(i);
            button[i].addActionListener(actionListener);
            contentPane.add(button[i]);
        }
        this.setVisible(true);

        if (isHost) setHost();
        else setClient();
    }

    public void setHost() {
        turn = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input;

            while ((input = reader.readLine()) != null) {
                turn++;
                int x = Integer.parseInt(input.split(",")[0]);
                int y = Integer.parseInt(input.split(",")[1]);
                game.round(x, y);
                button[x * 3 + y].setText(game.players.get((game.round + 1) % 2));

                if (game.status == 1) {
                    socket.close();
                    System.out.println("Player exit.");
                    JOptionPane.showMessageDialog(new JFrame(),game.players.get((game.round + 1) % 2) + " wins!");
                    System.exit(0);
                    this.dispose();
                    break;
                } else if (game.status == -1) {
                    socket.close();
                    System.out.println("Player exit.");
                    JOptionPane.showMessageDialog(new JFrame(),"Draw.");
                    System.exit(0);
                    this.dispose();
                    break;
                }
                System.out.println("Client: " + y + "," + x);
            }
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClient() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input;

            System.out.println("Waiting for host... ");
            while ((input = reader.readLine()) != null) {
                turn++;
                int x = Integer.parseInt(input.split(",")[0]);
                int y = Integer.parseInt(input.split(",")[1]);
                game.round(x, y);
                button[x * 3 + y].setText(game.players.get((game.round + 1) % 2));
                if (game.status == 1) {
                    socket.close();
                    System.out.println("Host close");
                    JOptionPane.showMessageDialog(new JFrame(),game.players.get((game.round + 1) % 2) + " wins!");
                    System.exit(0);
                    this.dispose();
                    break;
                } else if (game.status == -1) {
                    socket.close();
                    System.out.println("Host closed.");
                    JOptionPane.showMessageDialog(new JFrame(),"Draw.");
                    System.exit(0);
                    this.dispose();
                    break;
                }
                System.out.println("Host: " + y + "," + x);
            }
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 1069;                        // Please don't scold me for this port number www

        if (args.length == 1) ip = args[0];
        else if (args.length == 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 1000);
            TTTServer game = new TTTServer("TicTacToe - client（Ｘ）",false, socket);
        } catch(IOException e) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Waiting for client connection......");

                Socket socket = serverSocket.accept();
                System.out.println("A new client is connected : " + socket);
                TTTServer game = new TTTServer("TicTacToe - host（Ｏ）",true, socket);
            } catch(IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}

class MyActionListener implements ActionListener {
    Game game;
    TTTServer frame;
    Socket socket;
    boolean isHost;

    MyActionListener(TTTServer frame, Socket socket, boolean isHost, Game game) {
        this.game = game;
        this.frame = frame;
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.socket = socket;
        this.isHost = isHost;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource().getClass().getName().equals("Button")) {
            Button clickedButton = (Button)event.getSource();

            if (!clickedButton.getText().equals("")) game.status = -2;      // Detect Invalid choice

            if (game.status == 0) {
                int x;
                int y;

                try {
                    if ((frame.turn % 2 == 0 ^ !isHost)) {
                        x = clickedButton.index / 3;
                        y = clickedButton.index % 3;

                        OutputStream out = socket.getOutputStream();
                        out.write((x + "," + y + "," + game.round + "\n").getBytes());
                        out.flush();

                        System.out.println(x + "," + y + "," + game.round);
                        game.round(x, y);
                        clickedButton.setText(game.players.get((game.round + 1) % 2));
                        frame.turn++;
                    } else JOptionPane.showMessageDialog(new JFrame(),game.players.get((game.round) % 2) + "'s turn.\nPlease wait.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            if (game.status == 1) {
                JOptionPane.showMessageDialog(new JFrame(),game.players.get((game.round + 1) % 2) + " wins!");
                System.exit(0);
                frame.dispose();
            } else if (game.status == -1) {
                JOptionPane.showMessageDialog(new JFrame(),"Draw.");
                System.exit(0);
                frame.dispose();
            } else if (game.status == -2) {
                JOptionPane.showMessageDialog(new JFrame(),"Position invalid!");
                game.status = 0;
            }
        }
    }
}
