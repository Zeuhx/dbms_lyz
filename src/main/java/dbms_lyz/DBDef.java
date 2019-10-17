package main.java.dbms_lyz;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
		relDefTab = new ArrayList<>();
		compteurRelation = 0;
		/**
		 * TODO LECTURE
		 */
		
	}

	public static void finish(){
		/**
		 * TODO : ECRITURE
		 * creer un fichier Catalog.def qui contient
		 * la sauvegarde des infos de DBDef
		 * Fichier txt ou ObjectOutputStram via la serialisation
		 */
		
		ObjectOutputStream oos = null ;
		FileOutputStream catalogue = null ;
		try {
			catalogue = new FileOutputStream ("catalogue.txt");
			oos= new ObjectOutputStream(catalogue);
			oos.writeChars("Compteur relation "+compteurRelation+"\nListe de tab " + relDefTab);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				catalogue.close() ; 
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
}
