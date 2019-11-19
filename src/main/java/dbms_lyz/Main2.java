package main.java.dbms_lyz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main2 {
	public static void main(String[] args) throws FileNotFoundException, IOException {
//		test();
//		testGetPagee();
//		testCommandeDBDefPourCreer();
//		testC();
//		creetest();
//		testCommandeDBDefPourCreere();
		
		// src/main/ressources/DB/catalogue.def
		DBManager.init();
	}
	public static void testCommandeDBDefPourCreere() {
		DBManager manager = new DBManager();
		DBManager.init();
		Scanner scan = new Scanner(System.in);
		String commande = "";
		System.out.println("----- BASE DE DONNEE -----");
		do {
			System.out.println("Saisir la commande" 
					+ "\n\tElle doit commencer par : create, clean, insert, select, insertAll, selectAll");
			commande = scan.nextLine();
			manager.processCommand(commande);	
		} while(!commande.equals("exit"));
		//scan.close();
		System.out.println();
	}


	public static void testLireFichierAvecLeurPage(RandomAccessFile f) {
		ByteBuffer bf = ByteBuffer.allocate(Constants.PAGE_SIZE);
		
		try {
			f.seek(0);
			f.read(bf.array());
			System.out.println("Bonjour");
		} catch (IOException e) {
			System.err.println("Erreur I/O");
		}
		System.out.println("ByteBuffer : " + Arrays.toString(bf.array()));
		
	}
	public static void testCommandeDBDefPourCreer() {
		DBManager manager = new DBManager();
//		DBManager.init();
		Scanner scan = new Scanner(System.in);
		String choix = "";
		String commande = "";
		do {
			System.out.println("Quelles commandes voulez vous saisir ?");
			System.out.println("choix : [exit] [commande]");
			choix = scan.nextLine();
			if(choix.equals("exit")){
				DBManager.finish();
			}
			else if (choix.equals("commande")){
				System.out.println("Saisir votre commande : ");
				System.out.println("Ex : create NomRelation NbCol TypeColl[1] TypeCol[2] … TypeCol[NbCol]");
				commande = scan.nextLine();
				manager.processCommand(commande);	
			}
		} while(!choix.equals("exit"));
		scan.close();
	}

	
	////////////////////////////////////////

	
	public static void testFreePage(PageId pageId, boolean valdirty, List<Frame> listFrame) {
		int indexFrame = testSearchFrame(pageId, listFrame);
		if (indexFrame == 2)
			System.out.println("Frame pas trouve");
		else {
			Frame frame = listFrame.get(indexFrame);
			frame.decrementePinCount();
		}
	}

	/////////////////////////////////////////////////////
	public static int testSearchFrame(PageId pageId, List<Frame> listFrame) {
		BufferManager.getInstance();
		int i = 0;
		System.out.println("id de pageId a chercher : "+pageId.getPageIdx());

		for (Frame f : listFrame) {
			System.out.println("id de la page dans frame : "+i+" est : "+f.getPageId().getPageIdx());
			if (f.getPageId().getPageIdx() == (pageId.getPageIdx())) {
				System.out.println("resultat incide du frame: "+i);
				return (i);
			}
			i++;
		}		
		return (2);
	}

	
}
