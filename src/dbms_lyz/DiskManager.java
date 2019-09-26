package dbms_lyz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
		File f = new File("/DB/Data_"+fileIdx+".rf");
		System.out.println(" Data_"+fileIdx+".rf" + f.getPath()); //chemin des répertoires
	}

	/**
	 * Cette méthode rajoute une page au fichier spécifié par fileIdx 
	 * (c’est à dire, elle rajoute pageSize octets à la fin du fichier) 
	 * et retourne un PageId correspondant à la page
	 * nouvellement rajoutée !
	 */
//	public PageId addPage(int fileIdx) {
//		// FileInputStream fis = new FileInputStream;
//
//	}
	
	/**
	 * 
	 * @param fichier_source
	 * @param fichier_dest
	 * @throws IOException
	 * Ajoute le fichier_source au fichier_dest avec les exceptions 
	 */
	private static void copier(String fichier_source, String fichier_dest) throws IOException{
		FileInputStream src = new FileInputStream(fichier_source);
		FileOutputStream dest = new FileOutputStream(fichier_dest);
		
		FileChannel inChannel = src.getChannel();
		FileChannel outChannel = dest.getChannel();
		
		for (ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
				inChannel.read(buffer) != -1;
				buffer.clear()) {
			buffer.flip();
			while (buffer.hasRemaining()) outChannel.write(buffer);
		}
		
		inChannel.close();
		outChannel.close();
		src.close();
		dest.close();
	}

	public void readPage() {

	}

	public void writePage() {

	}
}
