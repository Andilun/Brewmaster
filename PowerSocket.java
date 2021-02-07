package app;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
/**
*<h1>PowerSocket</h1>
*The PowerSocket class is used to control a wireless relay
*@author  Anders Lunde
*@version 0.1
*@since   2020-23-9 
*/
public class PowerSocket {
	/**
	* The {@link String} represents the IP address of a wireless relay.  
	*/
	String ip;
	/**
	* The {@link Settings} stores the program settings.
	* @see Settings
	*/
	Settings settings;
	/**
	* The {@link Boolean} represents the state of the wireless relay, true if its power is on, else false.
	* 
	*/
	boolean on;
	
	/**
	* Sets up a {@link PowerSocket} by passing and setting the value of {@link String} IP, {@link Boolean} on and {@link Settings} settings.
	* @see Settings
	* @param ip {@link String} ip-address of wireless relay
	* @param settings {@link Settings}
	* @param state {@link Boolean} state of wireless relay
	*/
	PowerSocket(String ip,Settings settings, Boolean state){
		this.ip = ip;
		this.settings = settings;
		this.on = state;
	}
	
	/**
	*Turns off a wireless relay, requires python-kassa in system path.
	*@see Settings
	*/
	protected void turnOff() {
		
		System.out.println("Turn off");
		on = false;
		Process process = null;
		try{ 
	        process = Runtime.getRuntime().exec("kasa --host " +ip+" off");
	        process.waitFor();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line;
	    while ((line=reader.readLine())!=null){
	    	System.out.println(line);
	    }
	    }catch(Exception e){
	    	System.out.println(e);
	    }finally{
	    	process.destroy();
	    }
		
		
	}
	
	/**
	*Turns on a wireless relay, requires python-kassa in system path.
	*@see Settings
	*/
	protected void turnOn() {

		System.out.println("Turn on");
		on = true;
		Process process = null;
		 try{ 
         process = Runtime.getRuntime().exec("kasa --host " +ip+" on"); 
         
         process.waitFor();
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         String line;
         while ((line=reader.readLine())!=null){
             System.out.println(line);
             }
         }catch(Exception e){
        	 System.out.println(e);
        }finally{
        	process.destroy();
        }


	}
	
	
	/**
	*The {@link Instant} stores a point on the time-line. Before waitBetweenPowerSwitch() is called it is not initialized.
	*@see #waitBetweenPowerSwitch
	*/
	Instant lastInstant = null;
	/**
	*Returns a {@link Boolean} that is false if the time since you last called the method is greater than than specified by the settings 
	*@see #lastInstant
	*@see Settings#minutesToWaitPowerSwitch
	*@return a {@link Boolean} that is false if the time since you last called the method is greater than than specified by the settings
	*/
	protected Boolean waitBetweenPowerSwitch() {
		if(lastInstant == null){
			lastInstant = Instant.now();
			return false;
		}
		
		double msInMinute = 60000;
		long timeWait = (long)(settings.minutesToWaitPowerSwitch*msInMinute);
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(lastInstant, finish).toMillis();
		
		if(timeElapsed > timeWait) {
			lastInstant = Instant.now();
			return false;
		} 
		return true;
	}

}
