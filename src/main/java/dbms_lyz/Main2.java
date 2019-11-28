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
import java.util.StringTokenizer;

public class Main2 {
	public static void main(String[] args) throws FileNotFoundException, IOException {
//		test();
//		testGetPagee();
//		testCommandeDBDefPourCreer();
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
		
//		testDelete();
//		PageId headerPage = new PageId("Data_0.rf"); 
//		String commande = "R 3 2";
//		StringTokenizer stCommandaCouper;
//		stCommandaCouper = new StringTokenizer(commande, " ");
//		DBManager.getInstance().deleteCommande(stCommandaCouper);
		testCom("select R 3 1");
	}
	
	
	public static void testCom(String commande) {
		StringTokenizer stCommandaCouper;

		stCommandaCouper = new StringTokenizer(commande, " ");
		String typeCommande = stCommandaCouper.nextToken() ;
		switch(typeCommande) {
		case "select" : selectCommande(stCommandaCouper);
		break ;
		default : System.err.println("commande incorrect");
		break ;
		}

	}
	public static void selectCommande(StringTokenizer commande) {
		String nomRelation = commande.nextToken();
		String colonne = commande.nextToken();
		String valeur = commande.nextToken(); 
		
		List<Record> listRecords = FileManager.getInstance().selectAllFromRelation(nomRelation);
		System.out.println("nomRelation :"+nomRelation);
		System.out.println("colonne :"+colonne);
		System.out.println("valeur :"+valeur);
		int column = Integer.parseInt(colonne);
				System.out.println("colonne de la boucle : "+ column);
//				StringBuffer stringBuffRecord = new StringBuffer("");
//				for(String s : r.getValues()) {
//					stringBuffRecord.append(s);
//					stringBuffRecord.append(" ; ");
//				}
//				String stringRecord = stringBuffRecord.substring(0, stringBuffRecord.toString().length()-3);
//				System.out.println(stringRecord);
			
		
	}
	private static int parseInt(String colonne) {
		// TODO Auto-generated method stub
		return 0;
	}


	public static void testDelete() {
		 List<String> typeCol = new ArrayList<String>();
		 typeCol.add("int");typeCol.add("string3");typeCol.add("int");
		 String relName ="R";
		 RelDef rd = new RelDef( relName, typeCol);
		 
		 List<String> valeurs = new ArrayList<String>();
		 valeurs.add("1"); valeurs.add("aab"); valeurs.add("2");
		 Record r = new Record(rd, valeurs);
		 FileManager.getInstance().insertRecordInRelation(r, rd.getNomRelation());
		 System.out.println("OKKK");
	}

	public void testt(){
		
		//npouvelle version
		int cptDataFile=0;
		//recuperer les fichier commencant par "Data_" dans une listData
		File dir = new File(Constants.PATH + "Data_" );
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



	
}
