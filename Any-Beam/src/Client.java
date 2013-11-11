import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Client {

	public static void main(String[] args) throws IOException {
		byte[] receiveData = new byte[100];
		DatagramSocket clientSocket = new DatagramSocket(4242);
		DatagramPacket receivePacket =
				new DatagramPacket(receiveData,
						receiveData.length);

		clientSocket.receive(receivePacket);
		System.out.println(receivePacket.getAddress());
	}
}
