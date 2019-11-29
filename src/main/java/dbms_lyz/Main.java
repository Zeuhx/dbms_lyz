package main.java.dbms_lyz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;


/**
 * CECI EST UN MAIN <BROUILLON>, pour tester les fonctions 
 * @author cedzh
 *
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		//DBManager.init();
		//testCommandeDBDef();
		testCommandeDBDefPourCreer();
		//testCreationFichiersAvecLeurPage();
		//testEcrireFichierAvecLeurPage();
		//testRelDefEtRecord();
		//testClean();
//		testDiskManagerWriteAndReadPage();
//		testFrame();
	}
	
	public static void testClean() {
		DBManager.getInstance().cleanCommande();
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
	
	public static void testCreationFichiersAvecLeurPage()  {
		// TD2 
		DiskManager.getInstance().createFile(0);
		DiskManager.getInstance().createFile(1);
		
//		PageId pageId1 = new PageId("Data_1.rf");
//		PageId pageId2 = new PageId("Data_2.rf");
	}
	
	public static void testEcrireFichierAvecLeurPage() {
		DiskManager.getInstance().createFile(5);
//		PageId pageId5 = new PageId("Data_5.rf");
		
		ByteBuffer bf = ByteBuffer.allocate(Constants.PAGE_SIZE);
		
		File file = new File("C:\\Users\\cedzh\\git\\dbms_lyz\\src\\main\\resources\\DB\\Data_5.rf");
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(file,"rw");
			//fc = f.getChannel();
			bf.putInt(1);
			bf.putInt(5);
			//DiskManager.writePage(pageId5, bf);
			f.write(bf.array());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("Affichage X3 - ByteBuffer : " + Arrays.toString(bf.array()));
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
		System.out.println("Affichage X4 -ByteBuffer : " + Arrays.toString(bf.array()));
		
	}
	
	public static void testDiskManagerWriteAndReadPage() {
		
		File f = new File(Constants.PATH + "Data_10.rf");
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PageId p = DiskManager.getInstance().addPage(10);
		ByteBuffer bf = ByteBuffer.allocate(Constants.PAGE_SIZE);
		bf.putInt(6);
		DiskManager.getInstance().writePage(p, bf);
		bf.position(0);
		DiskManager.getInstance().readPage(p, bf);
	}
	
	public static void testRelDefEtRecord() {
		testCommandeDBDefPourCreer();
	}
	
//	public static void testFrame() {
//		BufferManager bfm = BufferManager.getInstance();
//		ByteBuffer bf = ByteBuffer.allocate(Constants.PAGE_SIZE);
//		DiskManager.getInstance().createFile(5);
//		PageId pageId5 = new PageId("Data_5.rf");
//		
//		File file = new File("C:\\Users\\willy\\git\\dbms_lyz\\src\\main\\resources\\DB\\Data_5.rf");
//		RandomAccessFile f = null;
//		System.out.println("ok");
//		
//		try {
//			f = new RandomAccessFile(file,"rw");
//			//fc = f.getChannel();
//			bf.putInt(1);
//			bf.putInt(5);
//			//DiskManager.writePage(pageId5, bf);
//			f.write(bf.array());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("# GET PAGE #");
//		bfm.getPage(pageId5);
//		System.out.println("# FREE PAGE #");
//		bfm.freePage(pageId5, true);
//	}

}
