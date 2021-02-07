package Brewmaster;

import java.io.File;

/**
 * <h2>BrewMaster</h2> The BrewMaster program reads the temperature of a probe
 * connected to a raspberry pi and uses it<br>
 * to control the temperature of a refrigerator. It does this by switching the
 * refrigerator on<br>
 * and off with a wireless relay (ex.tp-link hs100) based on a settings file you
 * can control various<br>
 * aspects of this process.
 * 
 * @author Anders Lunde
 * @version 0.1
 * @since 2020-23-9
 */
public class BrewMaster {
	/**
	 * This is the main method which sets up the program and controls the main loop.
	 * It takes the file given as input and tries to load it as a Settings object
	 * then it sets up TemperatureHandler and TempWorker it tries to control the
	 * temperature and log information the way it's specified in Settings
	 * 
	 * @param a Array of arguments passed to the program (should contain path to
	 *          settings file at positon 0).
	 */
	public static void main(String[] a) {
		String file = getInput(a);
		if (new File(file).exists()) {
			run(file);
		} else {
			System.out.println("File does not exsist");
			System.exit(-1);
		}

	}

	/**
	 * Gets the first argument of string array or exits with code -1 if it is empty
	 * 
	 * @param a Path to settings file.
	 * @return {@link String} containing the string in position 0 of a[] or nothing
	 *         if a is empty
	 */
	private static String getInput(String[] a) {
		if (a.length == 0) {
			System.out.println("Need settings file as input \"eks: java -jar BrewMaster.jar settings.brew\"");
			System.exit(-1);
		}
		return a[0];
	}

	/**
	 * Sets up the settings file, TemperatureHandler and a new thread running
	 * {@link ServerThread}.then starts the program loop while(true)
	 * {@link TemperatureHandler#run}
	 * 
	 * @param file {@link String} file with path of settings file
	 * @see Settings
	 * @see TemperatureHandler
	 */
	private static void run(String file) {
		Settings s = new Settings(file);
		TemperatureHandler th = new TemperatureHandler(s);
		ServerThread t = new ServerThread(th);
		new Thread(t).start();

		while (true) {
			th.run();
		}
	}

}
