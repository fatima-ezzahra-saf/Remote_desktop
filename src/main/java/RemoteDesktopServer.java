import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteDesktopServer extends Remote {
    byte[] getScreenImage() throws RemoteException;
    void receiveMouseClick(double x, double y) throws RemoteException;
    void receiveMousePress(double x, double y) throws RemoteException;
    void receiveMouseRelease(double x, double y) throws RemoteException;
    void receiveMouseMove(double x, double y) throws RemoteException;
    void receiveKeyPress(int keyCode) throws RemoteException;
    void receiveKeyPress(int... keyCodes) throws RemoteException;
    void receiveKeyRelease(int keyCode) throws RemoteException;
    void sendAudioData(byte[] audioData) throws RemoteException;
    byte[] receiveAudioData() throws RemoteException;
}
