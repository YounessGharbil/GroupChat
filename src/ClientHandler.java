import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String clientUsername;
	
	public static ArrayList<ClientHandler> clientsHandler=new ArrayList<ClientHandler>();
	
	public ClientHandler(Socket socket) {
		try {
			
			this.socket=socket;
			this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.clientUsername=reader.readLine();
			clientsHandler.add(this);
			broadcastMessage("SERVER: "+ clientUsername +" has entered the group chat");
			
		}catch(IOException ex) {
			
			closeEveryThing(socket,reader,writer);
			
		}
		
		
		
	}

	@Override
	public void run() {

		String clientMessage;
		
		while(socket.isConnected()) {
			
			try {
				
				clientMessage=reader.readLine();
				broadcastMessage(clientMessage);
				
			}
			catch(IOException exception) {
				
				closeEveryThing(socket,reader,writer);
				break;

			}
			
		}
		
	}
	
	public void broadcastMessage(String message) {
		
		for(ClientHandler clientHandler:clientsHandler) {
			
			try {
				
				if(!clientHandler.clientUsername.equals(clientUsername)) {
					clientHandler.writer.write(message);
					clientHandler.writer.newLine();
					clientHandler.writer.flush();
				}
				
			}
			catch(IOException exception) {
				
				closeEveryThing(socket,reader,writer);

			}
			
		}
		
	}
	
	public void removeClientHandler() {
		clientsHandler.remove(this);
		broadcastMessage("SERVER: "+ clientUsername +" has left the group chat");

	}
	
	public void closeEveryThing(Socket socket,BufferedReader reader,BufferedWriter writer) {
		
		removeClientHandler();
		try {
			if(socket!=null) {
				socket.close();
			}
			if(reader!=null) {
				reader.close();			
			}
			if(writer!=null) {
				writer.close();
			}
		}
		catch(IOException exception) {
			exception.printStackTrace();
		}
		
	}


}







