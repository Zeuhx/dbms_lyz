package dbms_lyz;

import java.io.File;

public class DiskManager {
	/** Constructeur priv�sSs */
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
	 * Cette méthode crée (dans le sous-dossier DB) un fichier Data_fileIdx.rf initialement vide.
	 * 
	 */
	public void createFile(int fileIdx) {
		File f = new File(" Data_"+fileIdx+".rf");
		System.out.println(" Data_"+fileIdx+".rf" + f.getPath( )); //chemin des répertoires
	}
	
	public void addFile(int fileIdx) {
		
	}
	
	public void readPage() {
		
	}
	
	public void writePage() {
		
	}
}
