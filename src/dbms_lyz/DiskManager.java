package dbms_lyz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(new File("/DB/Data_"+fileIdx+".rf"),"rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Cette méthode rajoute une page au fichier spécifié par fileIdx 
	 * (c’est à dire, elle rajoute pageSize octets à la fin du fichier) 
	 * et retourne un PageId correspondant à la page
	 * nouvellement rajoutée !
	 * @throws FileNotFoundException 
	 */
	public PageId addPage(int fileIdx) {
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(new File("/DB/Data_"+fileIdx+".rf"),"rw");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			rf.write(Constants.pageSize);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PageId p = new PageId("/DB/Data_"+fileIdx+".rf");
		return(p);
	}
	
	/**
	 * 
	 * @param pageId un identifiant de page
	 * @param buff un buffer
	 * @return Remplir l’argument buff avec le contenu disque de la page identifiée par
	 * l’argument pageId.
	 * c’est l’appelant de cette méthode qui crée et fournit le buffer à remplir!
	 */
	public ByteBuffer readPage(PageId pageId, ByteBuffer buff) {
		
		/**
		 * Creation d'un tableau a 5Mo
		 */
		byte data[] = new byte[40960];
		buff = ByteBuffer.wrap(data);
		/**
		 * Avance 4 par 4
		 */
		int i = buff.getInt();
		
		RandomAccessFile rf = null ;
		try {
			rf= new RandomAccessFile(new File("/DB/Data_"+pageId.getFileIdx()+".rf"),"rw");
			
			try {
				for(i=0 ; i<rf.length() ; i++) {
					buff.put(rf.readByte());
				}
			} catch (IOException e) {
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
		return(buff);
	}

	public void writePage(PageId pageId, ByteBuffer buff) {

	}
}
