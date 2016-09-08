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
		System.out.println("Sunucu uygulamasý 2008 kapý adresinde gelen baðlantýlarý bekliyor.");
		while(true){
			Socket gelenBaglanti = dictionarySocket.accept();
			System.out.println(gelenBaglanti.getRemoteSocketAddress().toString()+ " adresinden yeni bir baðlantý alýndý.");
			runClient(gelenBaglanti);
		}
	}
	
	private void runClient(final Socket istemci) throws IOException{
		/* Ýstemci için yeni bir iþ parçacýðý yaratalým */
		new Thread(new Runnable(){
			public void run()
			{
				/* Buradaki method asenkronize olarak çalýþacak */
				try {
				/* Ýstemciden veri okumak ve yazmak için, 
				 * okuyucu ve yazýcý yaratalým.
				 */
				System.out.println("Ýstemci asenkronize olarak çalýþýyor.");
				InputStream gelenAkis = istemci.getInputStream();
				OutputStream gidenAkis = istemci.getOutputStream();
				BufferedReader okuyucu = new BufferedReader(new InputStreamReader(gelenAkis));
				BufferedWriter yazici = new BufferedWriter(new OutputStreamWriter(gidenAkis));
				String gelenIstek = null;
				
				/* Ýstemciden gelen istekleri oku */
				while((gelenIstek =okuyucu.readLine()) != null){
					System.out.println(istemci.getRemoteSocketAddress() + " istemcisinden gelen istek : " + gelenIstek);
					String cevap = null;
					/* Önce kelimeyi ingilizce varsayýp, türkçeye çevirmeye çalýþalým */
					cevap = eslesenTurkceKelimeyiBul(gelenIstek);
					/* Eðer eþleþen kelime bulunamadýysa, kelimeyi türkçe varsayýp ingilizceye çevirmeye çalýþalým */
					if(cevap == null)
						cevap = eslesenIngilizceKelimeyiBul(gelenIstek);
					/* Bulunan cevabý istemciye gönderelim */
					yazici.write(cevap+"\r\n");
					yazici.flush();
				}
				/* Döngü bittiðine göre istemci baðlantýyý kapatmýþ olmalý, 
				 * yazýcýmýzý ve okuyucumuzu kapatalým. */
				okuyucu.close();
				yazici.close();
				System.out.println("Ýstemci baðlantýsý koptu.");
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
