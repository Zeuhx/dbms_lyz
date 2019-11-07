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
		test();

	}
	public void testAlpha() {
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
				/*
				 * TD2
				 */

		ByteBuffer buff = ByteBuffer.allocate(Constants.PAGE_SIZE) ;
		/*
		 * try { DiskManager.getInstance().readPage(1, buff); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		/**
		 * Test pour la creation de page #1 #2, puis transfer de contenu #1 � #2 via ByteBuffer
		 */
		DiskManager.createFile(1);
		DiskManager.createFile(2);

		PageId pageId1 = new PageId("Data_1.rf");
		PageId pageId2 = new PageId("Data_2.rf");

		DiskManager.addPage(1);
		ByteBuffer bf = ByteBuffer.allocate(Constants.PAGE_SIZE);

		DiskManager.readPage(pageId1, bf);
		DiskManager.writePage(pageId2, bf);

		/*
		 * Test LRU Frame
		 */

		//ajout page dans frame id�e de proc�dure
		/** TODO : Cr�ation list de Frame via BufferManager
		 *nous avons que frame1 et frame2 dans ce cas;
		 *
		 *TEST : m�thode searchFrame, LRU, getPage, freePage, flushAll
		 *
		 *Et enfin v�rifier si flushAll a bien ferm� les frames
		 */

		Frame frame1 = new Frame(pageId1);
		Frame frame2 = new Frame(pageId2);

		List<Frame> listFrame = new ArrayList<>();
		listFrame.add(frame1);
		listFrame.add(frame2);
		System.out.println("la liste des frame : ");
		//Etat des Frames
		for(int i=0; i<listFrame.size(); i++) {
			System.out.println("frame "+i);
			System.out.println("page id : "+ (listFrame.get(i)).getPageIdx()+", pin count : "+(listFrame.get(i)).getPin_count()+", dirty : "+(listFrame.get(i)).getFlag_dirty());
		}

		/** BufferManager instance unique donc singleton java
		 * 
		 * Cannot make a static reference to the non-static 
		 * method afficheFrame(List<Frame>) from the type 
		 * BufferManager
		 */
		//		BufferManager.getInstance();
		//		BufferManager.afficheFrame(listFrame);
		//		
		//
		//		
		//		PageId pageId3 = new PageId("Data_3.rf");
		//		
		//		BufferManager.searchFrame(pageId3);
		//		BufferManager.searchFrame(pageId1);
		//		
		//		Frame frameLRU = BufferManager.LRU();
		//		BufferManager.getPage(pageId3);


		//Test pour les relations et record

		DiskManager.createFile(1);
		PageId p = DiskManager.addPage(1);

		BufferManager.getInstance().getPage(p);
		BufferManager.getInstance().afficheFrame(listFrame);

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


	//public File file 

}
