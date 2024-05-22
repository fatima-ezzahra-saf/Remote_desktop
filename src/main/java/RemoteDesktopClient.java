import javax.sound.sampled.*;
import java.rmi.Naming;

public class RemoteDesktopClient {
    public static void main(String args[]) {
        String serverURL = "rmi://100.70.37.144/remoteDesktopServer";

        try {
            RemoteDesktopServer server = (RemoteDesktopServer) Naming.lookup(serverURL);

            new Thread(() -> ImageReceiver.receiveAndDisplayImages(server)).start();
            new Thread(() -> sendAudioDataToServer(server)).start();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private static void sendAudioDataToServer(RemoteDesktopServer server) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            byte[] buffer = new byte[4096];
            while (true) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    byte[] audioData = new byte[bytesRead];
                    System.arraycopy(buffer, 0, audioData, 0, bytesRead);
                    server.sendAudioData(audioData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}