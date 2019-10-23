package main.java.dbms_lyz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
	 * 
	 * @param fileIdx Cette méthode crée (dans le sous-dossier DB) un fichier
	 *                Data_fileIdx.rf initialement vide.
	 * 
	 */
	public static void createFile(int fileIdx) {
		/**
		 * f file already exists then it is opened else the file is created and then
		 * opened
		 */
		File f = new File(path + fileIdx + ".rf");

		try {
			if (f.createNewFile()) {
				System.out.println("Fichier id " + fileIdx + "creer");
			} else {
				System.out.println("Fichier id " + fileIdx + "  non creer");
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
		byte[] bt = new byte[Constants.getpageSize()];
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
	 * @param j    un identifiant de page
	 * @param buff un buffer
	 * @return Remplir l’argument buff avec le contenu disque de la page identifiée
	 *         par l’argument pageId. c’est l’appelant de cette méthode qui crée et
	 *         fournit le buffer à remplir!
	 * @throws IOException
	 */
	public static void readPage(PageId j, ByteBuffer buff) {
		RandomAccessFile rf = null;
		File f = new File(path + j.getFileIdx() + ".rf");
		// Verif : System.out.println(f.getAbsolutePath());
		// Obtention du flux de donnee du ficheir rf
		FileChannel channel = null;

		try {
			rf = new RandomAccessFile(f, "r");
			channel = rf.getChannel();
			channel.read(buff, 0);

		} catch (FileNotFoundException e1) {
			System.out.println("Le fichier " + rf + " n'a pas ete trouve !");
		} catch (IllegalArgumentException e2) {
			System.out.println("Le mode choisit n'est pas parmis les choix : \"r\", \"rw\", \"rws\", or \"rwd\"");
		} catch (IOException e) {
			System.out.println("Erreur d'I/O au niveau de la lecture du channel");
		}
		// Verif : System.out.println(nbr);
		try {
			channel.close();
			rf.close();
		} catch (IOException e) {
			System.out.println("Erreur au niveau de la fermeture du channel ou du fichier");
		}

	}

	public static void writePage(PageId pageId, ByteBuffer buff) {
		RandomAccessFile rf = null;
		File f = new File(path + pageId.getFileIdx() + ".rf");
		// Verif : System.out.println(f.getAbsolutePath());
		// Obtention du flux de donnee du ficheir rf
		FileChannel channel = null;

		try {
			rf = new RandomAccessFile(f, "rw");
		} catch (FileNotFoundException e1) {
			System.out.println("Le fichier " + rf + " n'a pas ete trouve !");
		} catch (IllegalArgumentException e2) {
			System.out.println("Le mode choisit n'est pas parmis les choix : \"r\", \"rw\", \"rws\", or \"rwd\"");
		}

		// Relier buff et fichier via le channel
		channel = rf.getChannel();
		buff.rewind();
		try {
			channel.write(buff, 0);
			// Verif : System.out.println(nbr);
			channel.close();
			rf.close();
		} catch (IOException e) {
			System.out.println("Erreur au niveau de la fermeture du channel ou du fichier");
		}

	}

	/*
	 * public void writePage2(PageId pageId, ByteBuffer buff) { byte data[] = new
	 * byte[40960]; buff = ByteBuffer.wrap(data); RandomAccessFile rf = null ; int i
	 * = buff.getInt(); try { rf= new RandomAccessFile(new
	 * File("DB/Data_"+pageId.getFileIdx()+".rf"),"rw"); try { for(i=0 ;
	 * i<rf.length() ; i++) { rf.write(buff.get()); } } catch (IOException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } }
	 * catch(FileNotFoundException e1) {
	 * 
	 * } try { rf.close(); } catch (IOException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * }
	 */
}
