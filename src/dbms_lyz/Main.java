package dbms_lyz;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBManager.init();
		Scanner scan = new Scanner(System.in);
		String choix = "";
		String commande = "";
		
		
		
		// Boucle de gestion de commande
		do {
			
			System.out.println("Quelles commandes voulez vous saisir ?");
			System.out.println("choix : [exit] [commande]");
			choix = scan.nextLine();
			if(choix.equals("exit")){
				DBManager.finish();
			}
			else {
				commande = scan.nextLine();
				processCommand(commande);
			}
			/**
			
			 */
			
			
			
		}while(!choix.equals("exit"));
		
	}

}
