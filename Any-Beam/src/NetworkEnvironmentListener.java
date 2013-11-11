import java.net.InetAddress;


public interface NetworkEnvironmentListener {
	
	public void clientAdded(InetAddress i);
	public void clientListCleared();

}
