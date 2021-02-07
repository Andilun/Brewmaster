package Brewmaster;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * <h2>TemperatureHandler</h2> The PowerSocket class is used to control a
 * wireless relay
 * 
 * @author Anders Lunde
 * @version 0.1
 * @since 2020-23-9
 */
public class TemperatureHandler {
	/**
	 * a {@link PowerSocket} representing a heater
	 * 
	 * @see PowerSocket
	 */
	private PowerSocket heater;
	/**
	 * a {@link PowerSocket} representing a cooler or refrigerator
	 * 
	 * @see PowerSocket
	 */
	private PowerSocket cooler;
	/**
	 * {@link Settings} object that stores program settings
	 * 
	 * @see Settings
	 */
	private Settings settings;
	/**
	 * a {@link Double} that stores the last temperature reading of the probe
	 * 
	 * @see #updateTemp
	 */
	private double currentTemp = 0;
	/**
	 * a {@link File} object representing the programs log file
	 */
	private File logFile;
	/**
	 * a {@link Integer} that stores then latest count of the number of days the
	 * program has been running, day 1 = 0 etc
	 * 
	 * @see #updateDay
	 */
	private int daysLastCheck = 0;

	/**
	 * a {@link Double} that stores the last temperature reading of the probe
	 * 
	 * @see #updateTemp
	 */
	private double minTemp = 0;

	/**
	 * a {@link Double} that stores the last temperature reading of the probe
	 * 
	 * @see #updateTemp
	 */
	private double maxTemp = 0;

	/**
	 * a {@link String} that stores state for the current day
	 * 
	 * @see #logDaily
	 */
	private String state = "";

	/**
	 * Sets up based on {@link Settings} the logFile, heater if hasHeater is true,
	 * cooler, runs {@link #logDaily}
	 * 
	 * @param settings {@link Settings}
	 * @see #cooler
	 * @see #heater
	 * @see #logFile
	 */
	TemperatureHandler(Settings settings) {
		this.settings = settings;
		logFile = new File("brew.log");

		if (settings.hasHeater) {
			heater = new PowerSocket(settings.heaterIp, settings, settings.heaterState);
		}
		cooler = new PowerSocket(settings.coolerIp, settings, settings.coolerState);

		try {
			System.out.println("Log file created: " + logFile.createNewFile());
		} catch (IOException e) {
			System.out.println("Failed to create log file");
			e.printStackTrace();
		}
		// startInstant = Instant.now();
		logDaily();

	}

	/**
	 * Runs {@link #updateTemp}, {@link #checkTemp}, {@link #updateDay} and
	 * {@link #logTemp}
	 * 
	 * @see #updateTemp
	 * @see #checkTemp
	 * @see #updateDay
	 * @see #logTemp()
	 */
	protected void run() {
		updateTemp();
		checkTemp();
		updateDay();
		logTemp();
	}

	/**
	 * Getter for maxTemp
	 * 
	 * @return {@link #maxTemp}
	 */
	protected double getMaxTemp() {
		return maxTemp;
	}

	/**
	 * Getter for minTemp
	 * 
	 * @return {@link #minTemp}
	 */
	protected double getMinTemp() {
		return minTemp;
	}

	/**
	 * Getter for currentTemp
	 * 
	 * @return {@link #currentTemp}
	 */
	protected double getCurrentTemp() {
		return currentTemp;
	}

	/**
	 * Getter for daysLastCheck
	 * 
	 * @return {@link #daysLastCheck}
	 */
	protected int getDaysLastCheck() {
		return daysLastCheck;
	}

	/**
	 * Getter for state
	 * 
	 * @return {@link #state}
	 */
	protected String getState() {
		return state;
	}

	/**
	 * Reads the temperature probe text file named w1_slave to get temperature and
	 * store in {@link #currentTemp}, updates {@link #maxTemp} and {@link #minTemp}
	 * 
	 * @see #findFolderNameOfProbe
	 * @see #currentTemp
	 * @see #maxTemp
	 * @see #minTemp
	 */
	private void updateTemp() {

		File f = new File("/sys/bus/w1/devices/");
		String fname = findFolderNameOfProbe(f.list());
		File found = new File(f.getAbsoluteFile() + "/" + fname + "/w1_slave");
		String temp = null;
		try {
			Scanner s = new Scanner(found);
			while (s.hasNext()) {
				temp = s.nextLine();
				if (temp.contains("t=")) {
					temp = temp.split("t=")[1];
				}
			}
			s.close();

			int intTempC = Integer.parseInt(temp);
			currentTemp = ((double) intTempC) / ((double) 1000);
			minTemp = settings.dailyTemp.get(daysLastCheck) - settings.tempErrorMarginMinus;
			maxTemp = settings.dailyTemp.get(daysLastCheck) + settings.tempErrorMarginPlus;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tries to find folder path in "/sys/bus/w1/devices/" starting with 28 and
	 * return it, if not found system will exit with status 1
	 * 
	 * @param str {@link String} array containing folder paths in
	 *            "/sys/bus/w1/devices/"
	 * @see #updateTemp
	 * @return {@link String} path of the folder that is starts with "28"
	 */
	private String findFolderNameOfProbe(String[] str) {
		String filename = null;
		String contains = "28";
		for (String s : str) {
			if (s.startsWith(contains)) {
				filename = s;
				break;
			}
		}
		if (filename == null) {
			System.exit(1);
		}
		return filename;
	}

	/**
	 * Checks if temperature {@link #currentTemp} is in the acceptable range of
	 * {@link Settings#tempErrorMarginPlus} and
	 * {@link Settings#tempErrorMarginMinus} if {@link Settings#hasHeater} is true
	 * and {@link PowerSocket#waitBetweenPowerSwitch} is false and it's to cold it
	 * will turn on {@link #heater} by calling {@link PowerSocket#turnOn} or turn
	 * off {@link #heater} by calling {@link PowerSocket#turnOff} if it's too hot.
	 * if {@link PowerSocket#waitBetweenPowerSwitch} is false and it's too cold it
	 * will turn off the {@link #cooler} by calling {@link PowerSocket#turnOff} or
	 * turn off the {@link #cooler} by calling {@link PowerSocket#turnOff} if it's
	 * too hot.
	 * 
	 * @see #currentTemp
	 * @see Settings
	 * @see PowerSocket#waitBetweenPowerSwitch
	 * @see #heater
	 * @see #cooler
	 * @see PowerSocket#turnOff
	 * @see PowerSocket#turnOn
	 * @see #daysLastCheck
	 * @see Settings#hasHeater
	 * @see Settings#tempErrorMarginMinus
	 * @see Settings#tempErrorMarginPlus
	 */
	private void checkTemp() {

		if (currentTemp <= (settings.dailyTemp.get(daysLastCheck) - settings.tempErrorMarginMinus)) {
			if (settings.hasHeater) {
				if (!heater.waitBetweenPowerSwitch()) {
					heater.turnOn();
				}
			}
			if (!cooler.waitBetweenPowerSwitch()) {
				cooler.turnOff();
			}

		} else if (currentTemp >= (settings.dailyTemp.get(daysLastCheck) + settings.tempErrorMarginPlus)) {
			if (settings.hasHeater) {
				if (!heater.waitBetweenPowerSwitch()) {
					heater.turnOff();
				}
			}
			if (!cooler.waitBetweenPowerSwitch()) {
				cooler.turnOn();
			}

		}
	}
	/**
	 * a {@link Instant} used to store a day after the current day
	 * @see #updateDay
	 */
	private Instant tomorrow = null;
	/**
	 * a {@link Integer} used to store total amount of seconds in a day.
	 * @see #updateDay
	 */
	private final int secondsInADay = 86400;

	/**
	 * When first run it initializes {@link #tomorrow} as the current time and adds
	 * {@link #secondsInADay} and a {@link Instant} now as the current time. the
	 * next times the method is ran it will check if the time past since
	 * {@link #tomorrow} has exceeded 24 hours. If it has it will set
	 * {@link #tomorrow} as the current time plus 24 hours and add 1 to
	 * {@link #daysLastCheck} to signify that a day has past. If
	 * {@link #daysLastCheck} is greater or equal than the size of
	 * {@link Settings#dailyTemp} the program will terminate with status 0, the
	 * program has finished. If it's not greater it will call {@link #logDaily}
	 * 
	 * @see #daysLastCheck
	 * @see Settings#dailyTemp
	 * @see #logDaily
	 */
	private void updateDay() {
		Instant now = Instant.now();

		if (tomorrow == null) {
			tomorrow = Instant.now();
			tomorrow = tomorrow.plusSeconds(secondsInADay);

		} else if (now.isAfter(tomorrow)) {
			tomorrow = Instant.now();
			tomorrow = tomorrow.plusSeconds(secondsInADay);
			daysLastCheck++;
			if (daysLastCheck >= settings.dailyTemp.size()) {
				System.out.println("out of days, done");
				System.exit(0);
			} else {
				logDaily();
			}

		}
	}

	/**
	 * The {@link Instant} stores a point on the time-line. Before
	 * waitBetweenLogging() is called it is not initialized.
	 * 
	 * @see #waitBetweenLogging
	 */
	Instant lastInstant = null;

	/**
	 * Returns a {@link Boolean} that is false if the time since you last called the
	 * method is greater than than specified by the settings
	 * 
	 * @see #lastInstant
	 * @see Settings#minutesToWaitLog
	 * @return a {@link Boolean} that is false if the time since you last called the
	 *         method is greater than than specified by the settings
	 */
	private Boolean waitBetweenLogging() {
		if (lastInstant == null) {
			lastInstant = Instant.now();
			return false;
		}

		double msInMinute = 60000;
		long timeWait = (long) (settings.minutesToWaitLog * msInMinute);
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(lastInstant, finish).toMillis();

		if (timeElapsed > timeWait) {
			lastInstant = Instant.now();
			return false;
		}
		return true;
	}

	/**
	 * Tries to add {@link String} log to {@link #logFile}
	 * 
	 * @see #lastInstant
	 * @see #logFile
	 * @param log {@link String} to add to log file
	 */
	private void log(String log) {
		try {
			FileWriter fw = new FileWriter(logFile, true);
			fw.write(log + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tries to add {@link #daysLastCheck}+1 (adding 1 to start with day 1 instead
	 * of day 0) and the {@link Settings#dailyState} and {@link Settings#dailyTemp}
	 * for index {@link #daysLastCheck} (the current day) to {@link #logFile}
	 * 
	 * @see #daysLastCheck
	 * @see Settings#dailyTemp
	 * @see Settings#dailyTemp
	 * @see #logFile
	 */
	private void logDaily() {
		this.state = settings.dailyState.get(daysLastCheck);
		log("Day:" + (daysLastCheck + 1) + ", State:" + state + ", Temp goal: "
				+ settings.dailyTemp.get(daysLastCheck));
	}

	/**
	 * A list containing {@link String} from logTemps
	 * 
	 * @see #logList
	 */
	private LinkedList<String> logList = new LinkedList<String>();

	/**
	 * @return #logList
	 */
	protected LinkedList<String> getLogList() {
		return logList;
	}

	/**
	 * Tries to add the current value from {@link PowerSocket#on} of the
	 * {@link #heater} if {@link Settings#hasHeater} and the current value from
	 * {@link PowerSocket#on} of the {@link #cooler} and
	 * {@link TempDateTime#getFormatedDateTimeTemp} to {@link #logFile} adds log to
	 * {@link #logList}, removes first if {@link #logList} is larger than 23
	 * 
	 * @see #currentTemp
	 * @see #cooler
	 * @see #heater
	 * @see #logFile
	 * @see #logList
	 * @see TempDateTime
	 * @see Settings#hasHeater
	 * @see PowerSocket#on
	 */
	protected void logTemp() {
		if (!waitBetweenLogging()) {
			String log = "";
			TempDateTime temp = new TempDateTime(currentTemp);
			if (settings.hasHeater) {
				log(temp.getFormatedDateTimeTemp() + " Cooler on: " + cooler.on + " Heater on: " + heater.on);
				System.out.println(
						temp.getFormatedDateTimeTemp() + " Cooler on: " + cooler.on + " Heater on: " + heater.on);
				log = temp.getFormatedDateTimeTemp() + " Cooler on: " + cooler.on + " Heater on: " + heater.on;
			} else {
				log((temp.getFormatedDateTimeTemp() + " Cooler on: " + cooler.on));
				System.out.println(temp.getFormatedDateTimeTemp() + " Cooler on: " + cooler.on);
				log = temp.getFormatedDateTimeTemp() + " Cooler on: " + cooler.on;
			}
			logList.add(log);
			if (logList.size() > 23) {
				logList.removeFirst();
			}
		}
	}
}
