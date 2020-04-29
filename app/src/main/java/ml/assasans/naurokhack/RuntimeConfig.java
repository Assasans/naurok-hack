package ml.assasans.naurokhack;

public class RuntimeConfig {
	private static RuntimeConfig instance;

	public static RuntimeConfig getInstance() {
		if(instance == null) instance = new RuntimeConfig();
		return instance;
	}

	private RuntimeConfig() {
		firstRun = true;
	}

	private boolean firstRun;
	public boolean firstRun() {
		if(firstRun) {
			firstRun = false;
			return true;
		}
		return false;
	}
}
