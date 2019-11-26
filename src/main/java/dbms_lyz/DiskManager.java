package main.java.dbms_lyz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * API : Important! (Cette classe comporte une unique instance)
 * @author LYZ
 */
public class DiskManager {
	// Chemin du fichier src/main/resources/DB/

	/* Constructeur prive */
	private DiskManager() {
	}

	/* Instance unique non preinitialisee */
	private static DiskManager INSTANCE = null;

	/* Point d'acces pour l'instance unique du singleton */
	public static DiskManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DiskManager();
		}
		return INSTANCE;
	}

	/**
	 * Avec fileIdx un entier correspondant � un identifiant / indice de fichier.
	 * Cette m�thode cr�e (dans le sous-dossier DB) un fichier Data_fileIdx.rf initialement vide.
	 * @param fileIdx un entier correspondant � un identifiant de fichier
	 */
	public void createFile(int fileIdx) {
		// Ouvre le fichier si ce dernier existe deja
		File f = new File(Constants.PATH + "Data_"+ fileIdx + ".rf");

		try {
			if (f.createNewFile()) {
				System.out.println("Le fichier dont l'id est " + fileIdx + " a ete cree");
			} else {
				System.err.println("Erreur X13 : Le fichier id " + fileIdx + " est non cree [peut etre qu'il existe deja]");
			}
		} catch (SecurityException e_s) {
			System.out.println("Security Exception : il n'y a pas les droits necessaires");
		} catch (IOException e) {
			System.out.println("Il y a une erreur d'I/O");
		}
		System.out.println("Affichage X14 : Voici le chemin du fichier : " + f.getAbsolutePath());
	}

	/**
	 * Cette methode rajoute une page au fichier specifie par fileIdx (c est a� dire,
	 * elle rajoute pageSize octets a� la fin du fichier) et retourne un PageId
	 * correspondant a la page nouvellement rajoutee !
	 * 
	 * @throws FileNotFoundException
	 * @param fileIdx un entier correspondant � un identifiant de fichier
	 * @return pageid un PageId
	 * 
	 */
	public PageId addPage(int fileIdx) {
		
		byte[] bt = new byte[Constants.PAGE_SIZE];
		try (RandomAccessFile rf = new RandomAccessFile(new File(Constants.PATH + "Data_" + fileIdx + ".rf"), "rw")){
			rf.write(bt);
		} catch (FileNotFoundException e1) {
			System.err.println("Le fichier saisie n'a pas ete trouve !");
		} catch (IllegalArgumentException e2) {
			System.err.println("Le mode choisit n'est pas parmis les choix : \"r\", \"rw\", \"rws\", or \"rwd\"");
		} catch (IOException e) {
			System.err.println("Erreur d'I/O pour addPage");
		}
		
		PageId p = new PageId("Data_" + fileIdx + ".rf");
		return (p);
	}

	/**
	 * @param pageId un identifiant de page
	 * @param buff un buffer : byte[], ByteBuffer...
	 * @return Remplir l argument buff avec le contenu disque de la page identifiée
	 *         par l argument pageId. c est l appelant de cette mehode qui cree et
	 *         fournit le buffer à remplir!
	 *         
	 * @Attention : c�est l�appelant de cette m�thode qui cr�e et fournit 
	 * 					le buffer � remplir!        
	 * @throws IOException
	 */
	public void readPage(PageId pageId, ByteBuffer buff) {
		File f = new File(Constants.PATH + "Data_" + pageId.getFileIdx() + ".rf");
		int numeroPage = pageId.getPageIdx(); 
		// Verif : System.out.println(f.getAbsolutePath());
			
		try (RandomAccessFile rf = new RandomAccessFile(f, "r")){
			rf.seek(numeroPage * Constants.PAGE_SIZE);
			System.out.println(rf.read(buff.array()));
		} catch (FileNotFoundException e1) {
			System.out.println("Le fichier saisie n'a pas ete trouve !");
		} catch (IllegalArgumentException e2) {
			System.out.println("Le mode choisit n'est pas parmis les choix : \"r\", \"rw\", \"rws\", or \"rwd\"");
		} catch (IOException e) {
			System.err.println("Erreur d'I/O au niveau de la position du RandomFileAccess");
		}
		// Verif : System.out.println(nbr);
		System.out.println("Affichage X15 - ByteBuffer pour la pageId "+pageId.getPageIdx()+" " + Arrays.toString(buff.array()));

	}
	
	/**
	 * Ecrire le contenu de l'argument du ByteBuffer dans le fichier et a la
	 * position indiques par l�argument pageId.
	 * @param pageId
	 * @param buff
	 */
	public void writePage(PageId pageId, ByteBuffer buff) {
		
		File f = new File(Constants.PATH + "Data_" + pageId.getFileIdx() + ".rf");
		System.out.println(f.toString());
		int positionPage = pageId.getPageIdx();
		try (RandomAccessFile rf = new RandomAccessFile(f, "rw")){
			
			//Position du RandomAccessFile
			System.out.println("Affichage X16 - ByteBuffer : " + Arrays.toString(buff.array()));
			rf.seek(positionPage * Constants.PAGE_SIZE);
			rf.write(buff.array());
			
		} catch (FileNotFoundException e1) {
			System.err.println("Le fichier saisie n'a pas ete trouve !");
		} catch (IllegalArgumentException e2) {
			System.err.println("Le mode choisit n'est pas parmis les choix : \"r\", \"rw\", \"rws\", or \"rwd\"");
		} catch (IOException e) {
			System.err.println("Erreur d'I/O du fichier au niveau de writePage");
		}
	}
	
	public String getPath() { return Constants.PATH + "Data_"; }

}
