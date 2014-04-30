package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetworkCoreUtils {
	
	private final static Map<String, NetworkEnvironment> ENVIRONMENTS = 
			new HashMap<String, NetworkEnvironment>();
	private final static Map<String, NetworkEnvironmentSettings> ENVIRONMENT_SETTINGS = 
			new HashMap<String, NetworkEnvironmentSettings>();

	public static NetworkEnvironment createNetworkEnvironment(NetworkEnvironmentSettings settings) throws IOException {
		
		String group = settings.getGroupName().toUpperCase();


		if(group.contains(":")) {
			throw new IllegalArgumentException("The group name contains the illegal char ':'.");
		}


		if(NetworkCoreUtils.getNetworkEnvironment(group) != null) {
			throw new IllegalArgumentException("An NetworkEnvironment with the given group already exists! " +
					"(Hint: use getNetworkEnvironment(...) to get the existing instance)");
		}

		NetworkCoreUtils.ENVIRONMENT_SETTINGS.put(group, settings);
		NetworkCoreUtils.ENVIRONMENTS.put(group, new NetworkEnvironment(settings));

		return NetworkCoreUtils.getNetworkEnvironment(group);
	}
	
	public static NetworkEnvironmentSettings getNetEnvironmentSettings(String group) {
		return NetworkCoreUtils.ENVIRONMENT_SETTINGS.get(group.toUpperCase());
	}

	public static NetworkEnvironment getNetworkEnvironment(String group) {
		return NetworkCoreUtils.ENVIRONMENTS.get(group.toUpperCase());
	}

	static void disposeNetworkEnvironment(String group) {
		
		group = group.toUpperCase();
		
		NetworkCoreUtils.ENVIRONMENT_SETTINGS.remove(group);
		NetworkCoreUtils.ENVIRONMENTS.remove(group);
	}

}
