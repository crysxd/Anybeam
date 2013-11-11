import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Server {

	public static void main(String[] args) throws IOException {
		DatagramSocket ss = new DatagramSocket();
		ss.setBroadcast(true);
		byte[] b = new byte[100];
		DatagramPacket p = new DatagramPacket(b, b.length);
		p.setAddress(InetAddress.getByAddress(new byte[] { (byte) 255,
				(byte) 255, (byte) 255, (byte) 255 }));
		p.setPort(4242);

		int i = 0;
		while (true) {
			String s = new Integer(i++).toString();

			b = s.getBytes();
			p.setData(b);
			ss.send(p);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
