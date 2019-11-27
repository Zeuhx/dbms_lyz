package main.java.dbms_lyz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

/**
 * 	Garde les infos de schema sur l'ensemble de la database
 * @author LYZ : Le Marcel, Yu Willy, Zhang Cedric,
 *
 */
public class DBDef implements Serializable{

	private static final long serialVersionUID = 1L;
	private static List<RelDef> relDefTab = new ArrayList<>();
	private static int compteurRelation = 0;

	/** Singleton */
	private DBDef() {}
	private static DBDef INSTANCE = null;
	public static DBDef getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DBDef();
		}
		return INSTANCE;
	}

	/**
	 * Ouvre les infos deja stocker dans catalogue.def
	 */
	public void init() {
		// src/main/ressources/DB/catalogue.def
		//condition si le fichier n'exist pas on le cree
		boolean isFilee = false;
		File file = new File(Constants.PATH+"catalogue.def"); 
//		file.delete();
		
		if(!(file.isFile())) {
			System.out.println("Affichage Y1 - Verification du fichier s'il dois etre cree : Fichier est cree");
			File creatingCatalogue = new File (Constants.PATH + "catalogue.def");
			isFilee = true;
			try {
				creatingCatalogue.createNewFile();
			} catch (IOException e) {
				System.err.println("Attention pas de possibilite d'acceder au dossier");
			}
		}
		
		if(isFilee) {
			try{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Constants.PATH + "catalogue.def"));	
				compteurRelation = ois.readInt() ;
				/* Pour chaque relDef : on va creer un relDef */
				System.out.println("Re, reprise du programme avec un compteur de relation du catalogue.def a " + compteurRelation);
				for(int i = 0; i<compteurRelation ; i++) {
					String relname = (String) ois.readObject();
					int nbCol = ois.readInt();
					List <String> typeCol = new ArrayList<>();
					for(int j = 0; j<nbCol; j++) {
						typeCol.add((String) ois.readObject()) ;
					}
					int fileIdx = ois.readInt();
					int recordSize = ois.readInt();
					int slotCount = ois.readInt();

					RelDef relation = new RelDef(relname, typeCol, fileIdx, recordSize, slotCount);
					relDefTab.add(relation);
					FileManager.getInstance().createHeapFileWithRelation(relation);
					//TODO créer heapfile avec
				}
				ois.close();
			} catch (FileNotFoundException e) {
				System.err.println("Le fichier catalogue n'existe pas");
			} catch (IOException e) {
				System.out.println("Bienvenue sur une nouvelle session du SGBD LYZ");
			} catch (ClassNotFoundException e) {
				System.err.println("La classe n'a pas ete trouver pour le fichier");
			}



		}
	}
	
	/**
	 * Sauvegarde les infos de DBDef dans catalogue.def
	 */
	public void finish(){
		try(ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream (Constants.PATH + "catalogue.def"))) {
			oos.writeInt(compteurRelation);
			for(int i = 0; i<compteurRelation; i++) {
				oos.writeObject(relDefTab.get(i).getRelName());
				oos.writeInt(relDefTab.get(i).getNbCol());
				/**
				 * Compter le nb de col puis ajouter les types de col
				 * un par un
				 */
				for(String type : relDefTab.get(i).getTypeCol()) {
					oos.writeObject(type);
				}
				oos.writeInt(relDefTab.get(i).getFileIdx());
				oos.writeInt(relDefTab.get(i).getRecordSize());
				oos.writeInt(relDefTab.get(i).getSlotCount());
			}
		} catch (FileNotFoundException e) {
			System.err.println("Le fichier n'a pas ete trouver ");
		} catch (IOException e) {
			System.err.println("Erreur d'I/O lors du fermeture du DBDef (1)");
		}
		System.out.println("Le programme s'est arrete correctement ! Merci d'avoir utiliser le SGBD LYZ");
	}

	/**
	 * Rajoute une relation dans la liste et actualise le compteur de relation
	 * @param rd la relation a ajoute
	 */
	public void addRelationInRelDefTab(RelDef rd) {
//		System.err.println("Erreur X2 : "+rd.getTypeCol() + " Nb de colonne " + rd.getTypeCol().size());
		if(rd != null) {
			relDefTab.add(rd);
			compteurRelation++;
			System.out.println("Affichage : nombre de relation : " + compteurRelation);
		} else 
			System.err.println("Erreur : le contenu du relDef saisie est vide");
	}
	
	/**
	 * Pour la commande clean
	 * Remet DBDef a 0 avec relDefTab
	 * Remet a 0 le compteur
	 */
	public void reset() {
		relDefTab = new ArrayList<>();
		compteurRelation = 0;

		try {
			Files.delete(Paths.get(Constants.PATH+"catalogue.def"));
			try {
				new File(Constants.PATH+"catalogue.def").createNewFile();
			} catch (IOException e) {
				System.err.println("Erreur I/O Exception : le ou les fichier ne peuvent etre cree");
			}
		} catch(FileSystemException e) {
			System.err.println("Erreur le fichier catalogue.def est ouvert, il ne peux être supprimé");
			System.out.println("Solution finale : veuillez supprimer manuellement");
		} catch (IOException e) {
			System.err.println("Erreur I/O Exception : le ou les fichier ne peuvent etre supprimer");
		}
	}
	
	// Getters et Setters 
	public static int getListSize() 			{ return relDefTab.size(); }
	public static List<RelDef> getRelDefTab()	{ return relDefTab; }
	public static int getCompteurRelation()		{ return compteurRelation; }
	
	public void setList(List <RelDef> l) 		{ relDefTab = l; }
	
}
