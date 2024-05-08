import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String username;
	
	public Client(Socket socket,String username) {
		try {
			this.socket=socket;
			this.username=username;
			this.reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch(IOException exception) {
			closeEverything(socket,reader,writer);
		}
	}
	
	public void sendMessage() {
		Scanner scanner=new Scanner(System.in);

		try {
			writer.write(username);
			writer.newLine();
			writer.flush();
			
			while(socket.isConnected()) {
				
				String messageToSend=scanner.nextLine();
				writer.write(username+": "+messageToSend);
				writer.newLine();
				writer.flush();
				
			}
		}
		catch(IOException exception) {
			
			closeEverything(socket,reader,writer);
			scanner.close();

		}
	}
	
	public void listenForMessage() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					String messageFromGroupChat=reader.readLine();
					System.out.print(messageFromGroupChat);
					
				}catch(IOException ex) {
					
					closeEverything(socket,reader,writer);

				}
				
			}
		}).start();
	}
	
	public void closeEverything(Socket socket,BufferedReader reader,BufferedWriter writer) {
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
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scaner=new Scanner(System.in);
		System.out.println("Enter yout username for the GroupChat");
		String username=scaner.nextLine();
		Socket socket=new Socket("localhost",54321);
		Client client=new Client(socket,username);

		
			
			while(true) {
				
				try {
				
				client.listenForMessage();
				client.sendMessage();
				
					}
				catch(Exception e) {
					scaner.close();
					break;
					
				}
				
			}
			
			
		
		
		
	}
		
}














