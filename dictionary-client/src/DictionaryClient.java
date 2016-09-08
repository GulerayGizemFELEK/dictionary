import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class DictionaryClient extends JFrame implements ActionListener {
	
	private Socket _istemci;
	private JTextField _textField1 = new JTextField();
	private JTextField _textField2 = new JTextField();
	private JButton _buton1 = new JButton("Dönüþtür");
	public DictionaryClient(){
		this.setLayout(new FlowLayout());
		this.setSize(new Dimension(640,480));
		this.setTitle("Sözlük istemcisi");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		_textField1.setPreferredSize(new Dimension(125,25));
		_textField2.setPreferredSize(new Dimension(125,25));
		_buton1.setPreferredSize(new Dimension(125,25));
		this.add(_textField1);
		this.add(_textField2);
		this.add(_buton1);
		_buton1.addActionListener(this);
		baglan("127.0.0.1",2008);
	}
	
	public boolean baglan(String sunucuIp, int sunucuPort){
		try 
		{
			_istemci = new Socket(sunucuIp,sunucuPort);
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Baðlantý sunucu tarafýndan reddedildi.");
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Baðlantý sunucu tarafýndan reddedildi.");
			return false;
		}
	}

	public static void main(String[] args) 
	{
		DictionaryClient dc = new DictionaryClient();
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == _buton1){
			try 
			{
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(_istemci.getOutputStream()));
				BufferedReader br = new BufferedReader(new InputStreamReader(_istemci.getInputStream()));
				
				bw.write(_textField1.getText() +"\r\n");
				bw.flush();
				String result = br.readLine();
				
				_textField2.setText(result);
				if(result.compareTo("null") == 0)
					JOptionPane.showMessageDialog(this, "Eþleþme bulunamadý!");
				//bw.close();
				//br.close();
				
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
				
			
		}
		
	}

}
