package main.java.dbms_lyz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
		String path = new String("src" + File.separator + "main" + 
				File.separator + "resources" + File.separator + "DB" + File.separator);
		FileInputStream catalogue = null ;
		ObjectInputStream ois = null ;
		try {
			catalogue = new FileInputStream(path + "catalogue.def");
			ois = new ObjectInputStream(catalogue);
			compteurRelation = ois.readInt() ;
			/**
			 * Pour chaque relDef : on va creer un relDef
			 */
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
			}
 		} catch (FileNotFoundException e) {
			System.err.println("Le fichier catalogue n'existe pas");
		} catch (IOException e) {
			System.err.println("Erreur de lecture de donnee (I/O) pour le catalogue.def");
		} catch (ClassNotFoundException e) {
			System.err.println("La classe n'a pas ete trouver pour le fichier");
		} finally {
			try {
				catalogue.close() ; 
				ois.close();
			} catch (IOException e) {
				System.out.println("Erreur d'I/O lors de la fermeture du fichier ");
			}
		}
	}
	
	/**
	 * Sauvegarde les infos de DBDef dans catalogue.def
	 */
	public void finish(){
		ObjectOutputStream oos = null ;
		FileOutputStream catalogue = null ;
		try {
			String path = new String("src" + File.separator + "main" + 
			File.separator + "resources" + File.separator + "DB" + File.separator );
			catalogue = new FileOutputStream (path + "catalogue.def");
			oos= new ObjectOutputStream(catalogue);
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
		} finally {
			try {
				catalogue.close() ; 
				oos.close();	// TODO Erreur lors de l'execution sur cette ligne
			} catch (IOException e) {
				System.err.println("Erreur d'I/O lors du fermeture du DBDef (2)");
			}
		}
		
		System.out.println("ici le finish");
	}

	/**
	 * Rajoute une relation dans la liste et actualise le compteur de relation
	 * @param rd la relation a ajoute
	 */
	public void addRelation(RelDef rd) {
		if(rd != null) {
			relDefTab.add(rd);
			compteurRelation++;
		} else 
			System.err.println("Erreur, le contenu du relDef saisie est vide");
	}
	
	/**
	 * Pour la commande clean
	 * Remet DBDef a 0 avec relDefTab
	 * Remet a 0 le compteur
	 */
	public void reset() {
		relDefTab = new ArrayList<>();
		compteurRelation = 0;
	}
	
	// Getters et Setters 
	public static int getListSize() { return relDefTab.size(); }
	public static List<RelDef> getRelDefTab(){ return relDefTab; }
	
	public void setList(List <RelDef> l) { relDefTab = l; }
	
}
