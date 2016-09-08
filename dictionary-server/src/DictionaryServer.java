import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;


public class DictionaryServer {
	
	private HashMap<String,String> _dictionary = new HashMap<String,String>();
	public DictionaryServer(){
		try 
		{
			readDictionary();
			runServer();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void runServer() throws IOException{
		ServerSocket dictionarySocket = new ServerSocket(2008);
		System.out.println("Sunucu uygulamas� 2008 kap� adresinde gelen ba�lant�lar� bekliyor.");
		while(true){
			Socket gelenBaglanti = dictionarySocket.accept();
			System.out.println(gelenBaglanti.getRemoteSocketAddress().toString()+ " adresinden yeni bir ba�lant� al�nd�.");
			runClient(gelenBaglanti);
		}
	}
	
	private void runClient(final Socket istemci) throws IOException{
		/* �stemci i�in yeni bir i� par�ac��� yaratal�m */
		new Thread(new Runnable(){
			public void run()
			{
				/* Buradaki method asenkronize olarak �al��acak */
				try {
				/* �stemciden veri okumak ve yazmak i�in, 
				 * okuyucu ve yaz�c� yaratal�m.
				 */
				System.out.println("�stemci asenkronize olarak �al���yor.");
				InputStream gelenAkis = istemci.getInputStream();
				OutputStream gidenAkis = istemci.getOutputStream();
				BufferedReader okuyucu = new BufferedReader(new InputStreamReader(gelenAkis));
				BufferedWriter yazici = new BufferedWriter(new OutputStreamWriter(gidenAkis));
				String gelenIstek = null;
				
				/* �stemciden gelen istekleri oku */
				while((gelenIstek =okuyucu.readLine()) != null){
					System.out.println(istemci.getRemoteSocketAddress() + " istemcisinden gelen istek : " + gelenIstek);
					String cevap = null;
					/* �nce kelimeyi ingilizce varsay�p, t�rk�eye �evirmeye �al��al�m */
					cevap = eslesenTurkceKelimeyiBul(gelenIstek);
					/* E�er e�le�en kelime bulunamad�ysa, kelimeyi t�rk�e varsay�p ingilizceye �evirmeye �al��al�m */
					if(cevap == null)
						cevap = eslesenIngilizceKelimeyiBul(gelenIstek);
					/* Bulunan cevab� istemciye g�nderelim */
					yazici.write(cevap+"\r\n");
					yazici.flush();
				}
				/* D�ng� bitti�ine g�re istemci ba�lant�y� kapatm�� olmal�, 
				 * yaz�c�m�z� ve okuyucumuzu kapatal�m. */
				okuyucu.close();
				yazici.close();
				System.out.println("�stemci ba�lant�s� koptu.");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void readDictionary() throws IOException{
		RandomAccessFile raf = new RandomAccessFile("dictionary.txt","r");
		String line = "";
		while((line = raf.readLine()) != null){
			String[] ayrilan = line.split(" ");
			String en = ayrilan[0];
			String tr = ayrilan[1];
			_dictionary.put(en, tr);
		}
	}
	
	private String eslesenTurkceKelimeyiBul(String ingilizceKelime){
		for(Entry<String, String> entry : _dictionary.entrySet()) {
			   String enDeger = entry.getKey();
			   if(enDeger.compareTo(ingilizceKelime) == 0)
				   return entry.getValue();
			}
			return null;
	}
	
	private String eslesenIngilizceKelimeyiBul(String turkceKelime){
		for(Entry<String, String> entry : _dictionary.entrySet()) {
		   String trDeger = entry.getValue();
		   if(trDeger.compareTo(turkceKelime) == 0)
			   return entry.getKey();
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DictionaryServer abc = new DictionaryServer();

	}

}
