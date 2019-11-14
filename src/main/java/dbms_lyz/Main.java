package main.java.dbms_lyz;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CECI EST UN MAIN <BROUILLON>, pour tester les fonctions 
 * @author cedzh
 *
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// testCommandeDBDef();
		testDesFichiersAvecLeurPage();
		
	}
	
	public static void testCommandeDBDef() {
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
				System.out.println("Saisir votre commande de processCommand");
				System.out.println("Ex : create NomRelation NbCol TypeColl[1] TypeCol[2] … TypeCol[NbCol]");
				commande = scan.nextLine();
				manager.processCommand(commande);	
			}
			
			
		} while(!choix.equals("exit"));
	}
	
	public static void testDesFichiersAvecLeurPage() {
		// TD2 
		DiskManager.getInstance().createFile(0);
		DiskManager.getInstance().createFile(1);
		
		PageId pageId1 = new PageId("Data_1.rf");
		PageId pageId2 = new PageId("Data_2.rf");

		ByteBuffer bf = ByteBuffer.allocate(Constants.PAGE_SIZE);
		/**
		 * Probleme d'allocation d'espace ?
		 */
		DiskManager.addPage(4);
//		DiskManager.readPage(pageId1, bf);
//		DiskManager.writePage(pageId2, bf);
		
		/**
		 * Il faut tester read and write
		 */
		
	}
	
	public static void test() {
		String path = new String("src" + File.separator + "main" + 
				File.separator + "resources" + File.separator + "DB" + File.separator );
		
		FileOutputStream test = null;
		ObjectOutputStream ous = null;
		try {
			test = new FileOutputStream(path+"test.def");
			ous = new ObjectOutputStream(test);
			ous.writeInt(3);
			ous.writeInt(5);
			ous.writeObject("Bonjour");
			
		} catch(FileNotFoundException e) {
			System.err.println("Not found");
		} catch (IOException e) {
			System.err.println("IO");
		} finally {
			try {
				ous.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				test.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileInputStream testRead = null;
		ObjectInputStream ois = null;
		try {
			testRead = new FileInputStream(path+"test.def");
			ois = new ObjectInputStream(testRead);
			System.out.println(ois.readInt());
			System.out.println(ois.readInt());
			System.out.println(ois.readObject());

		}catch(FileNotFoundException e) {
			System.err.println("Not found");
		} catch (IOException e) {
			System.err.println("IO");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				testRead.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	public static void testReadPage() {
		
	}

	//public File file 

}
