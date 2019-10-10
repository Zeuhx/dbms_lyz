package dbms_lyz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
/**
 * 
 * @author LYZ
 * API : Important!
 * Cette classe comporte une unique instance
 *
 */
public class DiskManager {

	/** Constructeur prive */
	private DiskManager(){}

	/** Instance unique non préinitialisée */
	private static DiskManager INSTANCE = null ;

	/** Point d'accès pour l'instance unique du singleton */
	public static DiskManager getInstance(){           
		if (INSTANCE == null){   
			INSTANCE = new DiskManager(); 
		}
		return INSTANCE;
	}

	/**
	 * 
	 * @param fileIdx
	 * Cette méthode crée (dans le sous-dossier DB) 
	 * un fichier Data_fileIdx.rf initialement vide.
	 * 
	 */
	public static void createFile(int fileIdx) {
		/**
		 * f file already exists then it is opened else the file is created and then opened
		 */

		File f = new File("DB/Data_"+fileIdx+".rf");

		try {
			if(f.createNewFile()) {
				System.out.println("Fichier creer");
			}
			else {
				System.out.println("Fichier non creer");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(f.getAbsolutePath());
	}

	/**
	 * Cette méthode rajoute une page au fichier spécifié par fileIdx 
	 * (c’est à dire, elle rajoute pageSize octets à la fin du fichier) 
	 * et retourne un PageId correspondant à la page
	 * nouvellement rajoutée !
	 * @throws FileNotFoundException 
	 */
	public static PageId addPage(int fileIdx) {
		RandomAccessFile rf = null;
		byte[] bt = new byte[Constants.getpageSize()];
		try {
			rf = new RandomAccessFile(new File("DB/Data_"+fileIdx+".rf"),"rw");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			rf.write(bt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PageId p = new PageId("DB/Data_"+fileIdx+".rf");
		return(p);
	}

	/**
	 * 
	 * @param j un identifiant de page
	 * @param buff un buffer
	 * @return Remplir l’argument buff avec le contenu disque de la page identifiée par
	 * l’argument pageId.
	 * c’est l’appelant de cette méthode qui crée et fournit le buffer à remplir!
	 * @throws IOException 
	 */
	public static void readPage(PageId j, ByteBuffer buff) throws IOException,FileNotFoundException {		
		RandomAccessFile rf = null ;
		File f = new File("DB"+ File.separator+ "Data_"+j.getFileIdx()+".rf");
		// Verif : System.out.println(f.getAbsolutePath());
		// Obtention du flux de donnee du ficheir rf
		FileChannel channel = null ;

		rf= new RandomAccessFile(f,"r");
		channel = rf.getChannel() ;
		channel.read(buff,0);

		// Verif : System.out.println(nbr);
		channel.close();
		rf.close();

	}
	
	public static void writePage(PageId pageId, ByteBuffer buff) throws IOException, FileNotFoundException {		
		RandomAccessFile rf = null ;
		File f = new File("DB"+ File.separator+ "Data_"+pageId.getFileIdx()+".rf");
		// Verif : System.out.println(f.getAbsolutePath());
		// Obtention du flux de donnee du ficheir rf
		FileChannel channel = null ;

		rf= new RandomAccessFile(f,"rw");
		
		// Relier buff et fichier via le channel
		channel = rf.getChannel() ;
		buff.rewind(); 
		channel.write(buff,0);

		// Verif : System.out.println(nbr);
		channel.close();
		rf.close();
	}
	
	/**
	public void writePage2(PageId pageId, ByteBuffer buff) {
		byte data[] = new byte[40960];
		buff = ByteBuffer.wrap(data);
		RandomAccessFile rf = null ;
		int i = buff.getInt();
		try {
			rf= new RandomAccessFile(new File("DB/Data_"+pageId.getFileIdx()+".rf"),"rw");
			try {
				for(i=0 ; i<rf.length() ; i++) {
					rf.write(buff.get());
				}
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch(FileNotFoundException e1) {

		}
		try {
			rf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
	*/
}
