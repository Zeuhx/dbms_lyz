package main.java.dbms_lyz;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
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
//		do {
//
//			System.out.println("Quelles commandes voulez vous saisir ?");
//			System.out.println("choix : [exit] [commande]");
//			choix = scan.nextLine();
//
//			if(choix.equals("exit")){
//				DBManager.finish();
//			}
//			System.out.println("Saisir votre commande de processCommand");
//			System.out.println("Ex : create NomRelation NbCol TypeColl[1] TypeCol[2] … TypeCol[NbCol]");
//			commande = scan.nextLine();
//			manager.processCommand(commande);
//			
//		} while(!choix.equals("exit"));
//		/**
//		 * TD2
//		 */
		
		ByteBuffer buff = ByteBuffer.allocate(Constants.pageSize) ;
		/*
		 * try { DiskManager.getInstance().readPage(1, buff); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		/**
		 * Test pour la creation de page #1 #2, puis transfer de contenu #1 � #2 via ByteBuffer
		 */
		DiskManager.createFile(1);
		DiskManager.createFile(2);
		
		PageId pageId1 = null;
		PageId pageId2 = null;
		
		pageId1 = new PageId("Data_1.rf");
		pageId2 = new PageId("Data_2.rf");
		
		DiskManager.addPage(1);
		ByteBuffer bf = ByteBuffer.allocate(Constants.pageSize);
		
		DiskManager.readPage(pageId1, bf);
		DiskManager.writePage(pageId2, bf);
		

		
		
		
//		pageId1 = DiskManager.addPage(1);

//
//		
//		DiskManager.readPage(pageId2, buff);
	}


	
	
	//public File file 

}
