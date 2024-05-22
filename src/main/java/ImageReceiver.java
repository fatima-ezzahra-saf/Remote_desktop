import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class ImageReceiver {
    private static int titleBarHeight;
    private static int contentPaneHeight;
    private static JFrame frame;
    private static JLabel label;
    private static boolean ctrlPressed = false;

    public static void receiveAndDisplayImages(RemoteDesktopServer server) {
        try {
            createFrame(800, 600);

            frame.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    sendMouseClick(e.getX(), e.getY(), server);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    sendMousePress(e.getX(), e.getY(), server);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    sendMouseRelease(e.getX(), e.getY(), server);
                }
            });

            frame.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    sendMouseMove(e.getX(), e.getY(), server);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    sendMouseMove(e.getX(), e.getY(), server);
                }
            });

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                        ctrlPressed = true;
                    }
                    sendKeyPress(e, server);
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                        ctrlPressed = false;
                    }
                    sendKeyRelease(e, server);
                }
            });

            while (true) {
                byte[] imageData = server.getScreenImage();
                if (imageData != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                    BufferedImage image = ImageIO.read(bais);
                    bais.close();

                    updateImage(image);
                }
            }
        } catch (Exception e) {
            System.out.println("Error receiving and displaying images: " + e);
        }
    }

    private static void createFrame(int width, int height) {
        frame = new JFrame("Remote Desktop Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        label = new JLabel();
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.setSize(width, height);
        frame.setVisible(true);
    }

    private static void updateImage(BufferedImage image) {
        if (label != null) {
            label.setIcon(new ImageIcon(image));
        }
    }

    // Method to send mouse click event
    private static void sendMouseClick(int x, int y, RemoteDesktopServer server) {
        try {
            int frameWidth = frame.getContentPane().getWidth();
            int frameHeight = frame.getContentPane().getHeight();
            int titleBarHeight = frame.getInsets().top;
            int contentPaneHeight = frame.getContentPane().getHeight();
            double relativeX = (double) x / frameWidth;
            double relativeY = (double) (y - titleBarHeight) / contentPaneHeight;
            server.receiveMouseClick(relativeX, relativeY);
        } catch (Exception e) {
            System.out.println("Error sending mouse click event: " + e);
        }
    }

    // Method to send mouse press event
    private static void sendMousePress(int x, int y, RemoteDesktopServer server) {
        try {
            int frameWidth = frame.getContentPane().getWidth();
            int frameHeight = frame.getContentPane().getHeight();
            int titleBarHeight = frame.getInsets().top;
            int contentPaneHeight = frame.getContentPane().getHeight();
            double relativeX = (double) x / frameWidth;
            double relativeY = (double) (y - titleBarHeight) / contentPaneHeight;
            server.receiveMousePress(relativeX, relativeY);
        } catch (Exception e) {
            System.out.println("Error sending mouse press event: " + e);
        }
    }

    // Method to send mouse release event
    private static void sendMouseRelease(int x, int y, RemoteDesktopServer server) {
        try {
            int frameWidth = frame.getContentPane().getWidth();
            int frameHeight = frame.getContentPane().getHeight();
            int titleBarHeight = frame.getInsets().top;
            int contentPaneHeight = frame.getContentPane().getHeight();
            double relativeX = (double) x / frameWidth;
            double relativeY = (double) (y - titleBarHeight) / contentPaneHeight;
            server.receiveMouseRelease(relativeX, relativeY);
        } catch (Exception e) {
            System.out.println("Error sending mouse release event: " + e);
        }
    }

    // Method to send mouse move event
    private static void sendMouseMove(int x, int y, RemoteDesktopServer server) {
        try {
            int frameWidth = frame.getContentPane().getWidth();
            int frameHeight = frame.getContentPane().getHeight();
            int titleBarHeight = frame.getInsets().top;
            int contentPaneHeight = frame.getContentPane().getHeight();
            double relativeX = (double) x / frameWidth;
            double relativeY = (double) (y - titleBarHeight) / contentPaneHeight;
            server.receiveMouseMove(relativeX, relativeY);
        } catch (Exception e) {
            System.out.println("Error sending mouse move event: " + e);
        }
    }

    // Method to send key press event
    private static void sendKeyPress(KeyEvent e, RemoteDesktopServer server) {
        try {
            if (ctrlPressed) {
                // Handle key combinations with Ctrl
                server.receiveKeyPress(KeyEvent.VK_CONTROL, e.getKeyCode());
            } else {
                server.receiveKeyPress(e.getKeyCode());
            }
        } catch (Exception ex) {
            System.out.println("Error sending key press event: " + ex);
        }
    }

    // Method to send key release event
    private static void sendKeyRelease(KeyEvent e, RemoteDesktopServer server) {
        try {
            server.receiveKeyRelease(e.getKeyCode());
        } catch (Exception ex) {
            System.out.println("Error sending key release event: " + ex);
        }
    }
}
