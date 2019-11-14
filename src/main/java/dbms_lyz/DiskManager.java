package main.java.dbms_lyz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * API : Important! (Cette classe comporte une unique instance)
 * @author LYZ
 */
public class DiskManager {
	// Chemin du fichier
	private static String path = new String("src" + File.separator + "main" + 
			File.separator + "resources" + File.separator + "DB" + File.separator + "Data_");

	/** Constructeur prive */
	private DiskManager() {
	}

	/** Instance unique non préinitialisée */
	private static DiskManager INSTANCE = null;

	/** Point d'accès pour l'instance unique du singleton */
	public static DiskManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DiskManager();
		}
		return INSTANCE;
	}

	/**
	 * @param fileIdx Cette méthode crée (dans le sous-dossier DB) un fichier
	 *                Data_fileIdx.rf initialement vide.
	 */
	public void createFile(int fileIdx) {
		// Ouvre le fichier si ce dernier existe deja
		File f = new File(path + fileIdx + ".rf");

		try {
			if (f.createNewFile()) {
				System.out.println("Le fichier dont l'id est " + fileIdx + " a ete cree");
			} else {
				System.out.println("Le fichier id " + fileIdx + " est non cree [peut etre qu'il existe deja]");
			}
		} catch (SecurityException e_s) {
			System.out.println("Security Exception : il n'y a pas les droits necessaires");
		} catch (IOException e) {
			System.out.println("Il y a une erreur d'I/O");
		}

		System.out.println("Voici le chemin du fichier : " + f.getAbsolutePath());
	}

	/**
	 * Cette méthode rajoute une page au fichier spécifié par fileIdx (c’est à dire,
	 * elle rajoute pageSize octets à la fin du fichier) et retourne un PageId
	 * correspondant à la page nouvellement rajoutée !
	 * 
	 * @throws FileNotFoundException
	 */
	public static PageId addPage(int fileIdx) {
		RandomAccessFile rf = null;
		byte[] bt = new byte[Constants.PAGE_SIZE];
		try {
			rf = new RandomAccessFile(new File(path + fileIdx + ".rf"), "rw");
		} catch (FileNotFoundException e1) {
			System.out.println("Le fichier " + rf + " n'a pas ete trouve !");
		} catch (IllegalArgumentException e2) {
			System.out.println("Le mode choisit n'est pas parmis les choix : \"r\", \"rw\", \"rws\", or \"rwd\"");
		}
		try {
			rf.write(bt);
		} catch (IOException e) {
			System.out.println("Il y a une erreur d'I/O");
		}
		PageId p = new PageId("Data_" + (fileIdx + 1) + ".rf");
		return (p);
	}

	/**
	 * 
	 * @param pageId    un identifiant de page
	 * @param buff un buffer
	 * @return Remplir l’argument buff avec le contenu disque de la page identifiée
	 *         par l’argument pageId. c’est l’appelant de cette méthode qui crée et
	 *         fournit le buffer à remplir!
	 * @throws IOException
	 */
	public static void readPage(PageId pageId, ByteBuffer buff) {
		RandomAccessFile rf = null;
		File f = new File(path + pageId.getFileIdx() + ".rf");
		// Verif : System.out.println(f.getAbsolutePath());
		
		try {
			rf = new RandomAccessFile(f, "r");
			rf.seek(0);
			rf.read(buff.array());
		} catch (FileNotFoundException e1) {
			System.out.println("Le fichier " + rf + " n'a pas ete trouve !");
		} catch (IllegalArgumentException e2) {
			System.out.println("Le mode choisit n'est pas parmis les choix : \"r\", \"rw\", \"rws\", or \"rwd\"");
		} catch (IOException e) {
			System.out.println("Erreur d'I/O au niveau de la position du RandomFileAccess");
		}
		// Verif : System.out.println(nbr);
		System.out.println("ByteBuffer pour la pageId "+pageId.getPageIdx()+" " + Arrays.toString(buff.array()));

	}
	
	/**
	 * Ecrire le contenu du ByteBuffer dans la page concerne
	 * @param pageId
	 * @param buff
	 */
	public static void writePage(PageId pageId, ByteBuffer buff) {
		RandomAccessFile rf = null;
		File f = new File(path + pageId.getFileIdx() + ".rf");
		int positionPage = pageId.getPageIdx();

		try {
			rf = new RandomAccessFile(f, "rw");
			/**
			 * Position du RandomAccessFile
			 */
			rf.seek(positionPage * Constants.PAGE_SIZE);
			rf.write(buff.array());
		} catch (FileNotFoundException e1) {
			System.out.println("Le fichier " + rf + " n'a pas ete trouve !");
		} catch (IllegalArgumentException e2) {
			System.out.println("Le mode choisit n'est pas parmis les choix : \"r\", \"rw\", \"rws\", or \"rwd\"");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String getPath() { return path; }

}
