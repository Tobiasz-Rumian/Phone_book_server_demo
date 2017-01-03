import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Created by Tobiasz Rumian on 03.01.2017.
 */
public class About extends JFrame implements Runnable {
    private boolean playStopped;
    private Clip audioClip;
    private Random random = new Random();
    private JPanel panel = new JPanel();
    private boolean kill = false;

    About() throws MalformedURLException {
        super("O Autorze");
        setContentPane(panel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        URL url = null;
        try {
            url = new URL("https://media.giphy.com/media/l0HlIKdi4DIEDk92g/giphy.gif");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        Icon icon = new ImageIcon(url);
        JLabel label = new JLabel(icon);
        panel.add(new JLabel("Autor:\t Tobiasz Rumian\t Indeks: 226131"), BorderLayout.NORTH);
        panel.add(label, BorderLayout.CENTER);
        JButton ok = new JButton("ok");
        panel.add(ok, BorderLayout.SOUTH);
        setSize(400, 400);
        setVisible(true);
        (new Thread(this)).start();
        (new Thread(new AudioPlayer())).start();
        ok.addActionListener(e -> {
            setVisible(false);
            kill = true;
            playStopped = true;
        });
    }

    public void run() {
        while (!kill) {
            Color color;
            float a = (random.nextInt(100) / (float) 100), b = (random.nextInt(100) / (float) 100), c = (random.nextInt(100) / (float) 100);
            color = new Color(a, b, c);
            panel.setBackground(color);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }

    class AudioPlayer implements Runnable {

        public void run() {
            playStopped = false;
            File audioFile = new File("abc.wav");

            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                audioClip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, audioStream.getFormat()));
                audioClip.open(audioStream);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }

            audioClip.start();

            while (!playStopped) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            audioClip.stop();
            audioClip.close();
        }
    }

}
