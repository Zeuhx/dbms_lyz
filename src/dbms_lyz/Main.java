package dbms_lyz;

import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBManager.init();
		Scanner scan = new Scanner(System.in);
		String commande = "";
		StringTokenizer stCommandaCouper ;
		String nomRelation = new String();
		int nbCol ;
		
		
		// Boucle de gestion de commande
		do {
			System.out.println("Quelles commandes voulez vous saisir ?");
			System.out.println("sous la forme :  create NomRelation NbCol TypeColl[1] TypeCol[2] … TypeCol[NbCol]");
			commande = scan.nextLine();
			stCommandaCouper = new StringTokenizer(commande, " ");
			
			/**
			 * On coupe le StringTokenizer en plusieurs partie :
			 * On compare le premier element avec create
			 * Le deuxieme element sera stocker pour etre passer en argument dans la fonction
			 * Le troisieme element sera convertit en int
			 * Et a partir du 4eme element on transfort sous une liste
			 */
			for(int i=0 ; stCommandaCouper.hasMoreElements() ; i++) {
				if(i==0) {
					if(!stCommandaCouper.nextElement().equals("create")) {
						System.exit(0);
					}
				}
				if(i==1) {
					
				}
				if(i==2) {
					
				}
				
			}
			
		}while(!commande.equals("exit"));
		
	}
}
