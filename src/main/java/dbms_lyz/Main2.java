package main.java.dbms_lyz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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
		testCommandeDBDefPourCreer();
//		testC();
//		creetest();
//		testCommandeDBDefPourCreere();
		
		// src/main/ressources/DB/catalogue.def
//		DBManager.init();
		
		//pour test sur la console
		//create S1 string2 int string4 int string5 int int int
		//create S 8 string2 int string4 float string5 int int int
		//insert S1 MO 97 Conc 180 Prod3 25 23 0
		//insert S1 PO 29 Conc 535.8 Prod6 6 32 1
	}

	public void testt(){
		
		//npouvelle version
		
		int cptDataFile=0;
		//recuperer les fichier commencant par "Data_" dans une listData
		File dir = new File(Constants.PATH);
		File [] foundFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				System.err.println("Affichage Y3");
				return name.startsWith("Data");
			}
		});
		//suppression des fichiers dans listData
		for (File file : foundFiles) {
			System.out.println("Affichage Y2 : suppression de fichier Data ");
			file.delete();
			cptDataFile ++;
		}
		System.out.println(" "+cptDataFile+" fichier(s) supprime(s)");
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
