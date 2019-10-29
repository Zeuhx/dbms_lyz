package main.java.dbms_lyz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	private static List<RelDef> relDefTab;
	private static int compteurRelation;

	/** Constructeur privé */
	private DBDef() {
	}

	/** Instance unique non préinitialisée */
	private static DBDef INSTANCE = null;

	/** Point d'accès pour l'instance unique du singleton */
	public static DBDef getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DBDef();
		}
		return INSTANCE;
	}

	/**
	 * Constructeur
	 */
	public static void init() {
		String path = new String("src" + File.separator + "main" + 
				File.separator + "resources" + File.separator + "DB" + File.separator );
		relDefTab = new ArrayList<>();
		compteurRelation = 0;
		/**
		 * TODO LECTURE
		 */
		FileInputStream catalogue = null ;
		ObjectInputStream ois = null ;
		try {
			catalogue = new FileInputStream(path + "catalogue.def");
			ois = new ObjectInputStream(catalogue);
			compteurRelation = ois.readInt() ;
			relDefTab = (List<RelDef>) ois.readObject(); 
 		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Le fichier catalogue n'existe pas");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				catalogue.close() ; 
				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sauvegarde les infos de DBDef dans catalogue.def
	 */
	public static void finish(){
		/**
		 * TODO : ECRITURE & Verifier si les infos sont bien sauvegarder dans la DBDef
		 * creer un fichier Catalog.def qui contient
		 * la sauvegarde des infos de DBDef
		 * Fichier txt ou ObjectOutputStram via la serialisation
		 */
		
		ObjectOutputStream oos = null ;
		FileOutputStream catalogue = null ;
		try {
			String path = new String("src" + File.separator + "main" + 
					File.separator + "resources" + File.separator + "DB" + File.separator );
			catalogue = new FileOutputStream (path + "catalogue.def");
			oos= new ObjectOutputStream(catalogue);
			oos.writeInt(compteurRelation);
			oos.writeObject(relDefTab);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				catalogue.close() ; 
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("ici le finish");
	}

	/**
	 * @param rd qui est une RelDef rajoute rd dans la liste et actualise le
	 *           compteur
	 */
	public void addRelation(RelDef rd) {
		System.out.println("Rentre dans addRelation");
		if (rd != null) {
			relDefTab.add(rd);
			compteurRelation++;
		} else
			System.out.println("Erreur, le contenu est vide");
	}
	
	public static List<RelDef> getList(){ return relDefTab; }
		
	public static int getListSize() { return relDefTab.size(); }
	
}
