/*
 *  Klasa Client
 *  Zajmuje się wysyłaniem żądań do serwera oraz wyświetlaniem wyników.
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
import java.net.Socket;


class Client extends JFrame implements Runnable {
    private JTextField message1 = new JTextField(10);
    private JTextField message2 = new JTextField(10);
    private JTextArea textArea = new JTextArea(15, 30);
    private JLabel labelTextArea = new JLabel("Dialog:");
    private JButton send = new JButton("Wyślij");
    private JMenuBar menuBar = new JMenuBar();
    private JComboBox<String> commands = new JComboBox<>();

    {
        commands.addItem("");
        commands.addItem("LOAD");
        commands.addItem("SAVE");
        commands.addItem("GET");
        commands.addItem("PUT");
        commands.addItem("REPLACE");
        commands.addItem("DELETE");
        commands.addItem("LIST");
        commands.addItem("CLOSE");
        commands.addItem("BYE");
    }

    private JPanel panel = new JPanel();
    private JLabel labelMessage1 = new JLabel("Napisz:");
    private JLabel labelMessage2 = new JLabel();

    {
        labelMessage2.setVisible(false);
        labelMessage1.setVisible(false);
        message1.setVisible(false);
        message2.setVisible(false);
        send.setEnabled(false);
    }

    private static final int SERVER_PORT = 15000;
    private String nick;
    private String host;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean kill = false;

    Client(String n) {
        super(n);
        setJMenuBar(menuBar);
        commands.addActionListener(e -> {
            switch (commands.getSelectedIndex()) {
                case 0:
                    send.setEnabled(false);
                    setMessages(false, false, "", "");
                    break;
                case 1://load
                    setMessages(true, false, "Adres pliku", "");
                    send.setEnabled(true);
                    break;
                case 2://save
                    setMessages(true, false, "Adres pliku", "");
                    send.setEnabled(true);
                    break;
                case 3://get
                    setMessages(true, false, "Imię", "");
                    send.setEnabled(true);
                    break;
                case 4://put
                    setMessages(true, true, "Imię", "Numer");
                    send.setEnabled(true);
                    break;
                case 5://replace
                    setMessages(true, true, "Imię", "Numer");
                    send.setEnabled(true);
                    break;
                case 6://delete
                    setMessages(true, false, "Imię", "");
                    send.setEnabled(true);
                    break;
                case 7://list
                    setMessages(false, false, "", "");
                    send.setEnabled(true);
                    break;
                case 8://close
                    setMessages(false, false, "", "");
                    send.setEnabled(true);
                    break;
                case 9://bye
                    setMessages(false, false, "", "");
                    send.setEnabled(true);
                    break;
            }
        });
        send.addActionListener(e -> {
            try {
                textArea.append("<" + commands.getSelectedItem() + ">\n");
                switch (commands.getSelectedIndex()) {
                    case 0:
                        return;
                    case 1://load
                        output.writeObject("LOAD$" + message1.getText());
                        break;
                    case 2://save
                        output.writeObject("SAVE$" + message1.getText());
                        break;
                    case 3://get
                        output.writeObject("GET$" + message1.getText());
                        break;
                    case 4://put
                        output.writeObject("PUT$" + message1.getText() + "%" + message2.getText());
                        break;
                    case 5://replace
                        output.writeObject("REP$" + message1.getText() + "%" + message2.getText());
                        break;
                    case 6://delete
                        output.writeObject("DEL$" + message1.getText());
                        break;
                    case 7://list
                        output.writeObject("LIST");
                        break;
                    case 8://close
                        output.writeObject("CLOSE");
                        input.close();
                        output.close();
                        socket.close();
                        setVisible(false);
                        dispose();
                        break;
                    case 9://bye
                        output.writeObject("BYE");
                        kill = true;
                        input.close();
                        output.close();
                        socket.close();
                        setVisible(false);
                        dispose();
                        break;
                }
            } catch (IOException err) {
                System.out.println("Wyjatek klienta " + err);
            }
            repaint();
        });
        nick = n;
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        menuBar.add(commands);
        menuBar.add(labelMessage1);
        menuBar.add(message1);
        menuBar.add(labelMessage2);
        menuBar.add(message2);
        menuBar.add(send);
        panel.add(labelTextArea);
        JScrollPane scroll_bars = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scroll_bars);
        setContentPane(panel);
        Thread t = new Thread(this);
        t.start();
        setVisible(true);
    }

    private void setMessages(boolean Bmessage1, boolean Bmessage2, String Smessage1, String Smessage2) {
        labelMessage1.setVisible(Bmessage1);
        labelMessage2.setVisible(Bmessage2);
        message1.setVisible(Bmessage1);
        message2.setVisible(Bmessage2);
        labelMessage1.setText(Smessage1);
        labelMessage2.setText(Smessage2);
    }

    public void run() {
        try {
            host = InetAddress.getLocalHost().getHostName();
            socket = new Socket(host, SERVER_PORT);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(nick);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Polaczenie sieciowe dla klienta nie moze byc utworzone");
            setVisible(false);
            dispose();
            return;
        }
        try {
            while (!kill) {
                textArea.append("<ODPOWIEDŹ SERWERA>\n" + input.readObject() + "\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Polaczenie sieciowe dla klienta zostalo przerwane");
            setVisible(false);
            dispose();
        }
    }

    public static void main(String[] args) {
        String nick;

        nick = JOptionPane.showInputDialog("Podaj nazwe klienta");
        if (nick != null && !nick.equals("")) {
            new Client(nick);
        }
    }

}
