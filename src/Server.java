/*
 *  Klasa Server
 *  Zajmuje się przetwarzaniem żądań klientów oraz przechowywaniem książki telefonicznej
 *
 *  @author Tobiasz Rumian
 *  @version 1.1
 *   Data: 02 Styczeń 2017 r.
 *   Indeks: 226131
 *   Grupa: śr 13:15 TN
 */

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


class Server extends JFrame implements Runnable {
    private ArrayList<ClientThread> clients = new ArrayList<>();
    private JTextArea textArea = new JTextArea(15, 18);
    private JScrollPane scroll = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private PhoneBook phoneBook = new PhoneBook();
    private volatile boolean kill = false;
    private static final int SERVER_PORT = 15000;
    private String host;
    private ServerSocket server;


    Server() {
        super("Server");
        setSize(300, 340);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setAutoscrolls(true);
        textArea.setEditable(false);
        panel.add(scroll);
        setContentPane(panel);
        Thread t = new Thread(this);
        t.start();
        setVisible(true);
    }

    private synchronized void showMessage(ClientThread k, String s) {
        textArea.append(k.getNick() + " >>> " + s + "\n");
    }

    synchronized void addClient(ClientThread clientThread) {
        clients.add(clientThread);
    }

    synchronized void deleteClient(ClientThread clientThread) {
        clients.remove(clientThread);
    }


    public void run() {
        Socket s;

        try {
            host = InetAddress.getLocalHost().getHostName();
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Gniazdko dla Servera nie może być utworzone");
            System.exit(0);
        }
        System.out.println("Server zostal uruchomiony na hoscie " + host);

        while (!kill) {
            try {
                s = server.accept();
                if (s != null) new ClientThread(this, s);
            } catch (IOException e) {
                System.out.println("BLAD Servera: Nie mozna polaczyc sie z klientem ");
            }
        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Server();
    }

    void useCommand(ClientThread clientThread) {
        String s = "error", command = "error", message1 = "error", message2 = "error";
        try {
            s = (String) clientThread.getInput().readObject();
            if (s.contains("%")) {
                message2 = s.substring(s.indexOf("%") + 1, s.length());
                message1 = s.substring(s.indexOf("$") + 1, s.indexOf("%"));
                command = s.substring(0, s.indexOf("$"));
            } else if (s.contains("$")) {
                message1 = s.substring(s.indexOf("$") + 1, s.length());
                command = s.substring(0, s.indexOf("$"));
            } else {
                command = s;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            switch (command) {
                case "LOAD"://load
                    showMessage(clientThread, "<LOAD>\n" + s + "\n");
                    clientThread.getOutput().writeObject(phoneBook.load(message1));
                    break;
                case "SAVE"://save
                    showMessage(clientThread, "<SAVE>\n" + s + "\n");
                    clientThread.getOutput().writeObject(phoneBook.save(message1));
                    break;
                case "GET"://get
                    showMessage(clientThread, "<GET>\n" + s + "\n");
                    clientThread.getOutput().writeObject(phoneBook.get(message1));
                    break;
                case "PUT"://put
                    showMessage(clientThread, "<PUT>\n" + s + "\n");
                    clientThread.getOutput().writeObject(phoneBook.put(message1, message2));
                    break;
                case "REP"://replace
                    showMessage(clientThread, "<REPLACE>\n" + s + "\n");
                    clientThread.getOutput().writeObject(phoneBook.replace(message1, message2));
                    break;
                case "DEL"://delete
                    showMessage(clientThread, "<DELETE>\n" + s + "\n");
                    clientThread.getOutput().writeObject(phoneBook.delete(message1));
                    break;
                case "LIST"://list
                    showMessage(clientThread, "<LIST>\n" + s + "\n");
                    clientThread.getOutput().writeObject(phoneBook.list());
                    break;
                case "CLOSE"://close
                    kill = true;
                    server.close();
                    showMessage(clientThread, "<CLOSE>\n" + s + "\n");
                    deleteClient(clientThread);
                    clientThread.kill();
                    break;
                case "BYE":
                    deleteClient(clientThread);
                    clientThread.kill();
                    break;
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}


class ClientThread implements Runnable {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private String nick;
    private Server okno;
    private volatile boolean kill = false;

    ClientThread(Server os, Socket s) throws IOException {
        okno = os;
        socket = s;
        Thread t = new Thread(this);
        t.start();
    }

    String getNick() {
        return nick;
    }

    ObjectOutputStream getOutput() {
        return output;
    }

    ObjectInputStream getInput() {
        return input;
    }

    void kill() {
        kill = true;
    }

    public String toString() {
        return nick;
    }

    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            nick = (String) input.readObject();
            okno.addClient(this);
            while (!kill) okno.useCommand(this);
            okno.deleteClient(this);
            input.close();
            output.close();
            socket.close();
            socket = null;
        } catch (Exception ignored) {}
    }
}



