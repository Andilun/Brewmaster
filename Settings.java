package Brewmaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/**
*<h2>Settings</h2>
*The settings class stores settings information dictating how the program should operates.
*<h2>Ex. of settings file below</h2>
*"<br>
*#settings<br>
*LogFileName=brew.log<br>
*CoolerState=1<br>
*HeaterState=0<br>
*HasHeater=0<br>
*DiffPlus=0.5<br>
*DiffMinus=0.5<br>
*CoolerIp=192.168.2.52<br>
*HeaterIp=0<br>
*LogFreq=1<br>
*Onoffdelay=10<br>
*#days;temp<br>
*!state=Ferment<br>
*14;12<br>
*!state=Diactyl rest<br>
*3;16.66<br>
*!state=Lager cooldown<br>
*1;13.89<br>
*"
* @author  Anders Lunde
* @version 0.1
* @since   2020-23-9 
*/

public class Settings {
	

	/**
	* The {@link Integer} represents total number of days to control temperature.
	*/
	protected int totalDays;
	/**
	* The {@link Double} represents temperature margin in positive direction.
	*/
	protected double tempErrorMarginPlus;
	/**
	* The {@link Double} represents temperature margin in negative direction.
	*/
	protected double tempErrorMarginMinus;
	/**
	* The {@link Double} represents minutes to wait between switching on and off wireless relay.
	*/
	protected double minutesToWaitPowerSwitch;
	/**
	* The {@link Double} represents minutes to wait between logging.
	*/
	protected double minutesToWaitLog;
	/**
	* The {@link Boolean} represents if the program should support a heater connected to a wireless relay.
	*/
	protected boolean hasHeater = false;
	/**
	* The {@link Boolean} represents the state of the coolers wireless relay.
	*/
	protected boolean coolerState=true;
	/**
	* The {@link String} represents the state of the heaters wireless relay.
	*/
	protected boolean heaterState=false;
	/**
	* The {@link String} represents the ip of the coolers wireless relay.
	*/
	protected String coolerIp;
	/**
	* The {@link LinkedList} represents the ip heaters wireless relay.
	*/
	protected String heaterIp;
	/**
	* A {@link LinkedList} containing {@link TempDateTime} temperature, date and time positioned by day where 0 is day 1, x is day-1.
	*/
	protected LinkedList<Double> dailyTemp;
	/**
	* A {@link String} containing {@link String} state positioned by day where 0 is day 1, x is day-1.
	*/
	protected LinkedList<String> dailyState;
	/**
	* The {@link String} represents the path of the logfile.
	*/
	protected String logFileName;
	
	/**
	 * Sets up a Settings object by initializes LinkedList dailyTemp and dailyState. Setting totalDays to 0 and calling load()
	 * @see #load
	 * @param file a {@link String} with the log file path
	 */
	Settings(String file){
		dailyTemp = new LinkedList<Double>();
		dailyState = new LinkedList<String>();
		totalDays = 0;
		load(file);
	}
	
	/**
	*The load methode will load a settings file.<br>
	*It will check the file for LogFileName,CoolerState,HeaterState,HasHeater,DiffPlus,DiffMinus,CoolerIp,HeaterIp,LogFreq,Onoffdelay
	*,!state and day;temp.<br>
	*Day and temp are separated by ';' everything else is separated by "tag=" tags are case sensitive.<br>
	*"tag=" will try to load everything after the '=' in the same line.<br>
	*It does not matter in what order the settings are specified except for the states and day;temps.<br>
	*They are loaded by there position in the file, all days and temps after a !state are in that state until a new !state is listed.<br>
	*The file containing  "...(\n)x;y(\n)a;b(\n)..." will hold temperature y for x days then b for a days<br><br>
	*<br>LogFileName<br> needs to be a valid path to a file this is the programs log file, it does not have to exist before you start the program<br>
	*<br>CoolerState,HeaterState,HasHeater<br> needs to an integer between 0 and 1, 0 representing false and 1 representing true<br>
	*<br>DiffPlus,DiffMinus<br> can be an integer or a decimal number and represents the acceptable temperature differential.<br>
	*<br>CoolerIp,HeaterIp<br> needs to be the ip address of your wireless relay, if HasHeater is false HeaterIp is not needed.<br>
	*<br>LogFreq,Onoffdelay<br> can be an integer or a decimal number and defines the wait time in minutes.<br>
	*<br>!state<br> gives the state name, it will persist until given a new state name, if no !states are given it will be left blank ("")<br>
	*<br>Days and temperatures<br> needs to be listed as d;t where d is an integer of days and t is an integer or a decimal number representing the temperature(degrees Celsius) you want to keep.<br>
	*log file will ignore lines starting with '#' it is used for comments<br><br>
	*@param file {@link String} that stores the path of a settings file
	*@see #splitByEquals
	*/
	private void load(String file){
		File f = new File(file);
		Scanner s = null;
		try {
			s = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String lastState = "";
		while(s.hasNext()) {
			String line = s.nextLine();
			if(line.charAt(0) == '#') {
			}else if(line.contains("HasHeater")) {
				String ret = splitByEquals(line);
				int bool = Integer.parseInt(ret);
				if(bool == 0) {
					hasHeater = false;
				}else if(bool == 1) {
					hasHeater = true;
				}
			}else if(line.contains("HeaterIp")) {
				String ret = splitByEquals(line);
				heaterIp = ret;
			
			}else if(line.contains("CoolerState")) {
				String ret = splitByEquals(line);
				int bool = Integer.parseInt(ret);
				if(bool == 0) {
					coolerState = false;
				}else if(bool == 1) {
					coolerState = true;
				}
			
			}else if(line.contains("HeaterState")) {
				String ret = splitByEquals(line);
				int bool = Integer.parseInt(ret);
				if(bool == 0) {
					heaterState = false;
				}else if(bool == 1) {
					heaterState = true;
				}
			}else if(line.contains("CoolerIp")) {
				String ret = splitByEquals(line);
				coolerIp = ret;
			}else if(line.contains("DiffPlus")) {
				String ret = splitByEquals(line);
				tempErrorMarginPlus = Double.parseDouble(ret);
			}else if(line.contains("DiffMinus")) {
				String ret = splitByEquals(line);
				tempErrorMarginMinus = Double.parseDouble(ret);
			}else if(line.contains("LogFileName")) {
				String ret = splitByEquals(line);
				logFileName= ret;
			}else if(line.contains("Onoffdelay")) {
				String ret = splitByEquals(line);
				minutesToWaitPowerSwitch = Double.parseDouble(ret);
			}else if(line.contains("LogFreq")) {
				String ret = splitByEquals(line);
				minutesToWaitLog = Double.parseDouble(ret);
			}else if(line.contains("!state")) {
				String ret = splitByEquals(line);
				lastState = ret;
			}else if(line.contains(";")) {
				String[] split = line.split(";");
				int days = Integer.parseInt(split[0]);
				double temp = Double.parseDouble(split[1]);
				for(int i = 0; i < days; i++) {
					dailyTemp.add(temp);
					dailyState.add(lastState);
				}
				totalDays += days;
				
			}
		}
		
	}
	
	/**
	 * Splits string s around matches of the regular expression "="
	 * @return the {@link String} at index 1 in the array of strings computed by splitting around matches of the regular expression "=" or null
	 * @param s {@link String} to split by the regular expression "="
	 */
	
	private String splitByEquals(String s){
		return s.split("=")[1];
	}
	

}
