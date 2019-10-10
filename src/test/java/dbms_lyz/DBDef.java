package test.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

/**
 * @author Le Marcel, Yu Willy, Zhang Cedric
 * Garde les infos de schema sur l'ensemble de la database
 *
 */
public class DBDef {
	private static List<RelDef> relDefTab ;
	private static int compteurRelation ;
	
	/** Constructeur privé */
	private DBDef(){}
	
	/** Instance unique non préinitialisée */
	private static DBDef INSTANCE = null ;
	
    /** Point d'accès pour l'instance unique du singleton */
    public static DBDef getInstance(){           
        if (INSTANCE == null){   
        	INSTANCE = new DBDef(); 
        }
        return INSTANCE;
    }
	
	/**
	 * Constructeur
	 */
	public static void init() {
		relDefTab = new ArrayList<>();
		compteurRelation = 0 ;
	}
	
	
	public static void finish() {
		System.out.println("ici le finish");
	}
	
	/**
	 * 
	 * @param rd
	 * rajoute rd dans la liste et actualise le compteur
	 */
	public void addRelation(RelDef rd) {
		if(rd!=null) {
			relDefTab.add(rd);
			compteurRelation++ ;
		}
		else
			System.out.println("Erreur, le contenu est vide");
	}
}
