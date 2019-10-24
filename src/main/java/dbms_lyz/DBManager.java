package main.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author LYZ
 *
 */
public class DBManager {
	
	private static DBManager INSTANCE = null;
	
	public DBManager() {
	}
	
	public DBManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DBManager();
		}
		
		return INSTANCE;
	}

	/**
	 * Fait appel au init de DBDef seulement
	 */
	public static void init() {
		DBDef.init();
	}

	/**
	 * Fait appel au finish de DBDef seulement
	 */
	public static void finish() {
		DBDef.finish();
	}

	/**
	 * @param commande, la commande qu'on va saisir Methode qui permet d'executer
	 *                  une commande entrer en parametre sous forme d'un String
	 */
	public void processCommand(String commande) {
		StringTokenizer stCommandaCouper;
		String nomRelation = new String("");
		int nbCol = 0;
		List<String> typeCol = new ArrayList<String>();
		int j = 0;

		stCommandaCouper = new StringTokenizer(commande, " ");

		/**
		 * On coupe le StringTokenizer en plusieurs partie : On compare le premier
		 * element avec create Le deuxieme element sera stocker pour etre passer en
		 * argument dans la fonction Le troisieme element sera convertit en int Et a
		 * partir du 4eme element on transfort sous une liste
		 */

		if (!stCommandaCouper.nextElement().equals("create")) {
			System.exit(0);
		}

		for (int i = 1; stCommandaCouper.hasMoreElements(); i++) {
			if (i == 1) {
				nomRelation = stCommandaCouper.nextToken();
			}
			if (i == 2) {
				nbCol = Integer.parseInt(stCommandaCouper.nextToken());
			}
			if (i > 2) {
				while (j < nbCol) {
					typeCol.add(stCommandaCouper.nextToken());
					j++;
				}
			}
		}

		/**
		 * Verification
		 */
		for (int i = 0; i < typeCol.size(); i++) {
			System.out.println(typeCol.get(i) + ", ");
		}

		/**
		 * Appel de la fonction
		 */
		createRelation(nomRelation, nbCol, typeCol);
	}

	/**
	 * 
	 * Methode qui creer une relation de type RelDef avec son nom, le nb de col , et les types de col
	 *  
	 * @param nomRelation
	 * @param nombreCol
	 * @param typeCol     
	 * @return, une relation RelDef conformement aux arguments et ajoute dans DBDef
	 * 
	 *
	 */
	public RelDef createRelation(String nomRelation, int nombreCol, List<String> typeCol) {
		RelDef rd = null ;

		DBDef.getInstance().addRelation(rd);
		
		// Calcul de la taille du record
		int recordSize = recordSize(rd);
		int slotCount = slotCount(rd);
		
		// WARNING : TODO on connait pas le fileIdx -> il faudra recuperer le fileIdx
		rd = new RelDef(nomRelation, typeCol, 0, recordSize, slotCount); 
		
		(DBDef.getInstance()).addRelation(rd);

		return (rd);
		
	}
	
	
	/**
	 * 
	 * @return  : ici qu'on calcule recordSize 
	 */
	public int recordSize(RelDef rd) {
		int i = 0;
		int recordSize = 0;

		
		do {
			// Verifie si c'est bien un Integer
			if (rd.getTypeCol().get(i).getClass().toString().contains("Integer")) {
				System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+4");
				recordSize += 4;
			}
			// Float
			else if (rd.getTypeCol().get(i).getClass().toString().equals("Float")) {
				System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+4");
				recordSize += 4;
			}
			// String
			/**
			 * ATTENTION : Verifier si la boucle est correct
			 */
			else if (rd.getTypeCol().get(i).getClass().toString().equals("String")) {
				int longueurAtteint = 0 ;
				do {
					System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+2");
					longueurAtteint ++ ;
					recordSize += 2;
				} while(rd.getTypeCol().get(i).length() > longueurAtteint);
				
			} else
				recordSize += 0;
		} while (i> rd.getNbCol());
		return recordSize;
	}
	/**
	 * 
	 * @return  : ici qu'on calcule slotCount
	 */
	public int slotCount(RelDef rd) {
		// 264 octets correspond a la taille d une case fixe
		return (Constants.getpageSize()*8)/((264*8)+1);
	}
}
