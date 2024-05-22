import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.sampled.*;

public class RemoteDesktopServerImpl extends UnicastRemoteObject implements RemoteDesktopServer {
    private BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();

    protected RemoteDesktopServerImpl() throws RemoteException {
        super();
    }

    @Override
    public byte[] getScreenImage() throws RemoteException {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenImage = robot.createScreenCapture(screenRect);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return imageBytes;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error capturing screen image: " + e.getMessage());
        }
    }

    @Override
    public void receiveMouseClick(double x, double y) throws RemoteException {
        try {
            Robot robot = new Robot();
            robot.mouseMove((int) (x * Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int) (y * Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(100);  // Attendre une petite période pour simuler un clic unique
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void receiveMousePress(double x, double y) throws RemoteException {
        try {
            Robot robot = new Robot();
            robot.mouseMove((int) (x * Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int) (y * Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMouseRelease(double x, double y) throws RemoteException {
        try {
            Robot robot = new Robot();
            robot.mouseMove((int) (x * Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int) (y * Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMouseMove(double x, double y) throws RemoteException {
        try {
            Robot robot = new Robot();
            robot.mouseMove((int) (x * Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int) (y * Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveKeyPress(int keyCode) throws RemoteException {
        try {
            Robot robot = new Robot();
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveKeyPress(int... keyCodes) throws RemoteException {
        try {
            Robot robot = new Robot();
            for (int keyCode : keyCodes) {
                robot.keyPress(keyCode);
            }
            for (int keyCode : keyCodes) {
                robot.keyRelease(keyCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveKeyRelease(int keyCode) throws RemoteException {
        try {
            Robot robot = new Robot();
            robot.keyRelease(keyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAudioData(byte[] audioData) throws RemoteException {
        try {
            audioQueue.put(audioData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] receiveAudioData() throws RemoteException {
        try {
            return audioQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "100.70.37.144");
            RemoteDesktopServerImpl server = new RemoteDesktopServerImpl();

            LocateRegistry.createRegistry(1099);

            String bindURL = "rmi://100.70.37.144/remoteDesktopServer";
            Naming.rebind(bindURL, server);

            System.out.println("Server is running on 100.70.37.144...");

            // Audio playback setup
            AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(info);
            speakers.open(format);
            speakers.start();

            byte[] buffer = new byte[1024];
            while (true) {
                buffer = server.audioQueue.take();
                speakers.write(buffer, 0, buffer.length);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
