package chat_application;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;;

public class Client extends JFrame {
	
	Socket socket;
	BufferedReader br;
	PrintWriter out;
	
	//declare components 
	private JLabel heading = new JLabel("Client Area");
	private JTextArea messageArea = new JTextArea();
	private JTextField messageInput = new JTextField();
	private Font font = new Font("Roboto",Font.PLAIN,20);
	
	
	public Client() {
		try {
			
				System.out.println("sending request to server ...");
				socket = new Socket("127.0.01",7777);
				if(socket.isConnected())
				{
					System.out.println("Connection successful.");
				}
				
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
			
			
			createGUI();
			handleEvents();
			startReading();
//			startWriting();
		
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void handleEvents() {
		
		messageInput.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println("key released "+e.getKeyCode());
				if(e.getKeyCode()==10)
				{
					String contentToSend = messageInput.getText();
					messageArea.append("Me : "+contentToSend+"\n");
					out.println(contentToSend);
					out.flush();
					messageInput.setText("");
					messageInput.requestFocus();
				
				}
			}
			
		});
		
	}

	private void createGUI() {
		
		//gui code 
		this.setTitle("Client Side");
		this.setSize(500,700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		// components 
		
		heading.setFont(font);
		messageArea.setFont(font);
		messageInput.setFont(font);
		
		heading.setIcon(new ImageIcon("images/logo.png"));
		heading.setHorizontalTextPosition(SwingConstants.CENTER);
		heading.setVerticalTextPosition(SwingConstants.BOTTOM);
		heading.setHorizontalAlignment(SwingConstants.CENTER);
		heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		messageArea.setEditable(false);
		
		this.setLayout(new BorderLayout() );
		
		//adding components
		this.add(heading, BorderLayout.NORTH);
		JScrollPane jScrollPane = new JScrollPane(messageArea);
		this.add(jScrollPane, BorderLayout.CENTER);
		this.add(messageInput, BorderLayout.SOUTH);
		
		
		
		this.setVisible(true);
		

		
		
	}
	
	public void startReading() {
		
		// thread 1 for reading 
		Runnable r1 = ()->{
			
			System.out.println("reader started ....");
			
			
			try {
				
			while(true) {
				String msg = br.readLine();
				if(msg.equals("exit"))
				{
					System.out.println("Server terminated the chat ");
					JOptionPane.showMessageDialog(this, "Server terminated the chat ");
					messageInput.setEnabled(false);
					socket.close();
					break;
				}
				
				//System.out.println("Server : "+msg);
				messageArea.append("Server : "+msg+"\n");
			}
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		};
		
		new Thread(r1).start();
		
	}
	
	public void startWriting() {
		
		//thread 2 for writing 
		Runnable r2 = ()-> {
			
			System.out.println("writer has started ...");
			
			try {
				
			while(true && !socket.isClosed()) {

				BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
				String content = br1.readLine();
				out.println(content);
				out.flush();
				if(content.equals("exit"))
				{
					socket.close();
					break;
				}
			}
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		};
		
		new Thread(r2).start();
	}
	
	public static void main(String[] args) {
		
		new Client();
	}

}
