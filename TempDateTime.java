package app;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
*<h1>TempDateTime</h1>
*The TempDateTime class is used to store and format temperature and date, used for ease of storage in lists
*@author  Anders Lunde
*@version 0.1
*@since   2020-23-9 
*/
public class TempDateTime {
	/**
	*a {@link LocalDateTime} that is used to store time of initialization
	*/
	LocalDateTime dateTime;
	/**
	*a {@link Double} that is used to store the temperature in degrees celsius
	*/
	double temp;
	/**
	*Sets up TempDateTime object, by adding current time and 
	*@param temp a {@link Double} that stores a temperature in degrees celsius
	*/
	TempDateTime(double temp){
		dateTime = LocalDateTime.now();
		this.temp = temp;
	}
	
	/**
	*Creates and returns a {@link String} containing formatted date and temperature
	*@return A {@link String} containing formatted date and temperature
	*/
	String getFormatedDateTimeTemp() {
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	    String formattedDate = dateTime.format(myFormatObj);
	    String FormatedDateTimeTemp = formattedDate + " t:"+temp;
		return FormatedDateTimeTemp;
	}


}
