package dbms_lyz;

import java.nio.ByteBuffer;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBManager manager = new DBManager();
		DBManager.init();
		Scanner scan = new Scanner(System.in);
		String choix = "";
		String commande = "";



		// Boucle de gestion de commande
		/**
		 * Menu : 
		 * Quand on entre exit, ca sort
		 * Sinon on rentre dans processCommand
		 * On doit rentrer la commande 
		 * Et c'est bon
		 */
		do {

			System.out.println("Quelles commandes voulez vous saisir ?");
			System.out.println("choix : [exit] [commande]");
			choix = scan.nextLine();

			if(choix.equals("exit")){
				DBManager.finish();
			}
			System.out.println("Saisir votre commande de processCommand");
			System.out.println("Ex : create NomRelation NbCol TypeColl[1] TypeCol[2] … TypeCol[NbCol]");
			commande = scan.nextLine();
			manager.processCommand(commande);
			
		} while(!choix.equals("exit"));
		/**
		 * TD2
		 */
		
		ByteBuffer buff = ByteBuffer.allocate(4096) ;
		
		DiskManager.createFile(1);
		/*
		 * try { DiskManager.getInstance().readPage(1, buff); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */


	}

	
	
	//public File file 

}
