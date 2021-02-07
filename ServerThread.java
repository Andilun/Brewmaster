package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * <h1>ServerThread</h1> The ServerThread class is used to as a server for an
 * android monitor temprature monitor app.
 * 
 * @author Anders Lunde
 * @version 0.1
 * @since 2020-23-9
 */
class ServerThread implements Runnable {
	/**
	 * The {@link PrintWriter} is used to send formatted data from
	 * {@link TemperatureHandler} to android app.
	 */
	private PrintWriter output;
	/**
	 * The {@link Integer} is used to store the server port number.
	 */
	int SERVER_PORT = 4111;
	/**
	 * The {@link ServerSocket} used for connections.
	 */
	ServerSocket serverSocket;
	/**
	 * The {@link Integer} is used to store the server port number.
	 */

	/**
	 * The {@link TemperatureHandler} is the object that stores all relevant
	 * information.
	 */
	TemperatureHandler th;

	/**
	 * Constructor for {@link ServerThread}
	 * 
	 * @param TemperatureHandler
	 */
	ServerThread(TemperatureHandler th) {
		this.th = th;
	}

	/**
	 * The {@link TemperatureHandler} is the object that stores all relevant
	 * information to send.
	 */

	/**
	 * Sets up a {@link ServerSocket} and accepts connections. After connection it
	 * starts sending information in the following order minTemp, currentTemp,
	 * maxTemp,daysLastCheck and state. if at any point an {@link IOException}
	 * occurs it will start a new thread and try to run again.
	 * 
	 * @see TempratureHandeler#getMinTemp
	 * @see TempratureHandeler#getCurrentTemp
	 * @see TempratureHandeler#getMaxTemp
	 * @see TempratureHandeler#getDaysLastCheck
	 * @see TempratureHandeler#getState
	 * @error IOException
	 */
	@Override
	public void run() {
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			socket = serverSocket.accept();
			output = new PrintWriter(socket.getOutputStream());
			output.write(th.getMinTemp() + "," + th.getCurrentTemp() + "," + th.getMaxTemp() + ","
					+ (th.getDaysLastCheck() + 1) + "," + th.getState() + ",");
			LinkedList<String> logList = th.getLogList();
			for (int i = 0; i < logList.size(); i++) {
				if (i < logList.size() - 1)
					output.write(logList.get(i) + ",");
				else {
					output.write(logList.get(i));
				}
			}
			output.flush();
			socket.close();
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(new ServerThread(th)).start();
	}
}
