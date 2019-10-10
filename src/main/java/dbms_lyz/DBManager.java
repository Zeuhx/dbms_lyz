package main.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DBManager {
	
	public DBManager() {
		
	}

	public static void init() {
		DBDef.init();
	}

	public static void finish() {
		DBDef.finish();
	}

	public void processCommand(String commande) {
		StringTokenizer stCommandaCouper ;
		String nomRelation = new String("");
		int nbCol = 0;
		List<Object> typeCol = new ArrayList<Object>();
		int j = 0 ;

		stCommandaCouper = new StringTokenizer(commande, " ");
		

		/**
		 * On coupe le StringTokenizer en plusieurs partie :
		 * On compare le premier element avec create
		 * Le deuxieme element sera stocker pour etre passer en argument dans la fonction
		 * Le troisieme element sera convertit en int
		 * Et a partir du 4eme element on transfort sous une liste
		 */
		
		if(!stCommandaCouper.nextElement().equals("create")) {
			System.exit(0);
		}
		
		for(int i=1 ; stCommandaCouper.hasMoreElements() ; i++) {
			if(i==1) {
				nomRelation = stCommandaCouper.nextToken() ;
			}
			if(i==2) {
				nbCol = Integer.parseInt(stCommandaCouper.nextToken());
			}
			if(i>2) {
				while(j < nbCol) {
					typeCol.add(stCommandaCouper.nextToken());
					j++;
				}	
			}
		}
		
		/**
		 * Verification
		 */
		for(int i=0 ; i<typeCol.size(); i++) {
			System.out.println(typeCol.get(i) + ", ");
		}
		
		/**
		 * Appel de la fonction
		 */
		createRelation(nomRelation, nbCol, typeCol);

	}

	public RelDef createRelation(String nomRelation, int nombreCol, List<Object> typeCol) {
		RelDef rd = new RelDef(nomRelation,  typeCol) ;
		(DBDef.getInstance()).addRelation(rd);
		return(rd);
	}



}
