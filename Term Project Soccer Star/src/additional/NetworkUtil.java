package additional;

import common.GameState;

import java.io.*;
import java.net.Socket;

public class NetworkUtil
{
	private Socket socket;
	private DataOutputStream oos;
	private DataInputStream ois;

	/*public NetworkUtil(String s, int port) {
		try {
			this.socket=new Socket(s,port);  
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			GameState.addErrorCount(100);
			System.out.println("In NetworkUtil : " + e.toString());
		}
	}*/
	public NetworkUtil(){

	}

	public NetworkUtil(Socket sock) throws Exception {

			setSocket(sock);

	}
	public NetworkUtil(Socket sock, String string) throws Exception{

		setSocket(sock,string);
	}
	public void setSocket(Socket sock) throws Exception{
		this.socket = sock;
		oos=new DataOutputStream(socket.getOutputStream());
		ois=new DataInputStream(socket.getInputStream());
	}

	public void setSocket(Socket sock, String string) throws Exception{
		this.socket = sock;
		if(string.equals("read")){
			ois=new DataInputStream(socket.getInputStream());
		}
		else if(string.equals("write")){
			oos=new DataOutputStream(socket.getOutputStream());
		}
		else throw new Exception("additional.InvalidConstructorException");
	}

	public Socket getSocket() {
		return socket;
	}

	public String read() throws Exception{
		String s = null;
		try {

			s = ois.readUTF();
		}catch (Exception e){
			GameState.addErrorCount();

			throw e;
		}

		return s;
	}
	
	public void write(String s) throws Exception {
		try {
			oos.writeUTF(s);
			oos.flush();
		}catch (Exception e){
			GameState.addErrorCount();
			throw e;
		}

	}

	public void closeConnection() {
		try {
			ois.close();
			oos.close();
		} catch (Exception e) {
			GameState.addErrorCount();
			System.out.println("Closing Error in network : "  + e.toString());
		}
	}
}

