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

	/**
	 * A definir (pas encore arrive)
	 */
	public DBManager() {
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
	 * @param nomRelation
	 * @param nombreCol
	 * @param typeCol     @return, une relation RelDef conformement aux arguments et
	 *                    ajoute dans DBDef
	 */
	public RelDef createRelation(String nomRelation, int nombreCol, List<String> typeCol) {
		RelDef rd = new RelDef(nomRelation, typeCol);
		(DBDef.getInstance()).addRelation(rd);
		return (rd);
	}
}
