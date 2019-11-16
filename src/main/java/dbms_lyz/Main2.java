package main.java.dbms_lyz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
		creetest();
		
	}
	

	public static void creetest() throws IOException {
		DiskManager.getInstance().createFile(9); //creation fichier		
		PageId pageId = new PageId("Data_9.rf"); //creation page 
		PageId pageId2 = new PageId("Data_8.rf");
		
//		////
//		ByteBuffer bf = ByteBuffer.allocate(Constants.PAGE_SIZE);
//		File file = new File("C:\\Users\\willy\\git\\dbms_lyz\\src\\main\\resources\\DB\\Data_0.rf");
//		RandomAccessFile f = new RandomAccessFile(file,"rw");
//		bf.putInt(1);
//		bf.putInt(5);
//		//DiskManager.writePage(pageId5, bf);
//		f.write(bf.array());
//		System.out.println("ByteBuffer : " + Arrays.toString(bf.array()));
//		///
		
		List<String> typeCol = new ArrayList<String>(); 
		typeCol.add("int");
		RelDef relDef = new RelDef ("R", typeCol); // reldef R 1 int
		HeapFile heapFile = new HeapFile (relDef); 
		
		heapFile.createNewOnDisk(); //creation header page via cette methode
		
		File file = new File("C:\\Users\\willy\\git\\dbms_lyz\\src\\main\\resources\\DB\\Data_9"+ ".rf");
		RandomAccessFile f = new RandomAccessFile(file,"rw");
		testLireFichierAvecLeurPage(f);
		
		DiskManager.getInstance();
		PageId newPage = DiskManager.addPage(7);
		testLireFichierAvecLeurPage(f);
		
//		ByteBuffer bufferDeHeaderPage = BufferManager.getPage(0);
		
		ByteBuffer bufferDeHeaderPage = ByteBuffer.allocate(Constants.PAGE_SIZE);
		for (int i = 0; i < Constants.PAGE_SIZE; i += Integer.BYTES) {
			bufferDeHeaderPage.putInt(0);
		}
		
		System.out.println("#TEST# cree header pager [OK]");
		
		heapFile.addDataPage(pageId); //ajout de la page au heapfile
		System.out.println("ByteBuffer : " + Arrays.toString(bufferDeHeaderPage.array()));
		
		
//		//	suite ajoute de la page
//		DiskManager.getInstance();
//		DiskManager.addPage(1);
//		
//		System.out.println("#TEST# ajout 1page au headerpage [OK]");
////		testLireFichierAvecLeurPage(rf);
//		
//		System.out.println("get rel def "+heapFile.getRelDef());

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
	
	public static void testGetPagee() {
		List<Frame> listFrame = new ArrayList<Frame>();
		PageId pageId = new PageId("Data_11.rf");
		PageId pageId2 = new PageId("Data_22.rf");
		Frame frame = new Frame(pageId);
		Frame frame2 = new Frame(pageId2);
		listFrame.add(frame);
		listFrame.add(frame2);

		PageId pageId3 = new PageId("Data_33.rf");	
		pageId3.setPageIdx(3);
		//		testAfficheFrame(listFrame);
		//		testSearchFrame(pageId3, listFrame);
		testAfficheFrame(listFrame);
		testFreePage(pageId2, false, listFrame);
		testAfficheFrame(listFrame);
		testFreePage(pageId, false, listFrame);
//		testGetPage(pageId3, listFrame);
//		testAfficheFrame(listFrame);
		
	}
	
	
	public static ByteBuffer testGetPage(PageId pageId, List<Frame> listFrame) {
		boolean pageExist = false;
		Frame getFrame = new Frame(pageId);
		ByteBuffer bf = getFrame.getBuffer();
		int indexFrame = testSearchFrame(getFrame.getPageId(), listFrame);
		//si la new pageId n'est pas dans la liste 
		if (indexFrame == 2)
			pageExist = false;
		//si il est dans la liste on r�cup la Frame qui contient cette page existant 
		else {
			pageExist = true;
			getFrame = listFrame.get(indexFrame);
		}

		//si newFrame est dans la liste
		if (pageExist) {
			//on ajoute si frame exist dans la liste le buffer de newFrame 
			getFrame.getPlus(); //pin_count ++

			//Maj du LRU_change
			//si newFrame correspond au premier
			if (pageId == (listFrame.get(0)).getPageId()) {

				// si jamais l'autre frame a pin_count>0 
				if(!(listFrame.get(1)).getLRU_change()) {
					//les deux frame ne peuvent �tre remplacer
					(listFrame.get(0)).setLRU_change(false);
				}
				else {
					(listFrame.get(0)).setLRU_change(false);
					(listFrame.get(1)).setLRU_change(true);
				}

				//si newFrame correspond au deuxieme
			} else {

				// si jamais l'autre frame a pin_count>0 
				if(!(listFrame.get(0)).getLRU_change()) {
					//les deux frame ne peuvent etre remplacer
					(listFrame.get(1)).setLRU_change(false);
				}
				else {
					(listFrame.get(0)).setLRU_change(true);
					(listFrame.get(1)).setLRU_change(false);
				}
			}
		}

		//si jamais le newFrame (pageId en entre) n est pas dans la liste on remplaces 
		else {
			System.out.println("si newFrame n'est pas dedans");
			//condition si pin count > 0
			System.out.println((listFrame.get(0)).getLRU_change()+" pour pin count � "+(listFrame.get(0)).getPin_count());
			System.out.println((listFrame.get(1)).getLRU_change()+" pour pin count � "+(listFrame.get(1)).getPin_count());
			if (((listFrame.get(0)).getPin_count() == 0) || (listFrame.get(1).getLRU_change())){
				System.out.println("les pin count sont � z�ro");
	
				//TEST affiche
//				System.out.println((listFrame.get(0)).getLRU_change());
//				System.out.println((listFrame.get(0)).getPin_count());
				//changement new frame par rapport a LRU
				
				if((listFrame.get(0)).getLRU_change() == true) {
					System.out.println("changement du premier frame ");
					listFrame.remove(0);
					listFrame.add(getFrame);

					//maj LRU etat si jamais l'autre frame a pin_count>0 
					if(!(listFrame.get(1)).getLRU_change()){
						(listFrame.get(0)).setLRU_change(true);
					}
					else {
						//maj de la valeur de LRU etat
						(listFrame.get(0)).setLRU_change(false);
						(listFrame.get(1)).setLRU_change(true);
					}
				}
				else if ((listFrame.get(1)).getLRU_change() == true){
					System.out.println("changement du second frame");
					listFrame.remove(1);
					listFrame.add(getFrame);
					// si jamais l'autre frame a pin_count>0 
					if(!(listFrame.get(0)).getLRU_change()){
						(listFrame.get(1)).setLRU_change(true);
					}
					else {
						//maj de la valeur de LRU etat
						(listFrame.get(0)).setLRU_change(true);
						(listFrame.get(1)).setLRU_change(false);
					}
				}
			}
			else
				System.out.println("aucune condition n'est realise : aucun frame dispo");
		}
		return (bf);
	}
	
	public static void testFreePage(PageId pageId, boolean valdirty, List<Frame> listFrame) {
		int indexFrame = testSearchFrame(pageId, listFrame);
		if (indexFrame == 2)
			System.out.println("Frame pas trouve");
		else {
			Frame frame = listFrame.get(indexFrame);
			frame.freeMoins(valdirty);
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

	public static void testAfficheFrame(List<Frame> listFrame) {
		for (int i = 0; i < listFrame.size(); i++) {
			System.out.print("[frame " + i + "] : ");
			System.out.println("page id : " + (listFrame.get(i)).getPageIdx() + "| pin count : "
					+ (listFrame.get(i)).getPin_count() + "| dirty : " + (listFrame.get(i)).getFlag_dirty()+ "| LRU : " + (listFrame.get(i)).getLRU_change());
		}
	}
}
