package main.java.dbms_lyz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Liste de page de donnee dont la premiere page se nomme HeaderPage avec pour
 * indice 0 possedant un entier N (nb de page en tout) et ensuite X entier pour
 * les nb de cases dispo dans chaque page de donnee
 * 
 * @author cedzh
 *
 */
public class HeapFile {
	private RelDef relDef;

	public HeapFile(RelDef relDef) {
		this.relDef = relDef;
	}

	/**
	 * On creer, on ajoute la page On prend un buffer qu'on remplit de 0 Et on "
	 * affecte " ce buffer a la page et on libere avec 1 car on a modifier la page
	 */
	public void createNewOnDisk() {
		// L'indice du fichier est donnee par relDef
		System.out.println("Affichage X11 - Affichage du relDef : \n "+ "\t" +relDef.toString());
		DiskManager.getInstance();
		int fileIdx = relDef.getFileIdx(); 
//		System.out.println("Affichage X12 \n[Creation du fichier via l'id du fichier] : " + fileIdx);
		DiskManager.getInstance().createFile(fileIdx);
		DiskManager.getInstance().addPage(fileIdx);
		PageId headerPage = new PageId(0, fileIdx);
		ByteBuffer bufferDeHeaderPage = BufferManager.getInstance().getPage(headerPage); // get
		for (int i = 0 ; i < Constants.PAGE_SIZE ; i += Integer.BYTES) {
			bufferDeHeaderPage.putInt(0);
		}
		DiskManager.getInstance().writePage(headerPage, bufferDeHeaderPage);
		BufferManager.getInstance().freePage(headerPage, true);	// free
	}

	/**
	 * On creer un page via l'ID de page en parametre On prend son fileIdx et on
	 * ajoute la page On ecrit via le buffer ? On incremente le slotCount de la
	 * headerPage et on libere le buffer en disant qu'on a pas toucher la page
	 * (true)
	 * 
	 * @param pageId
	 */
	public PageId addDataPage() {
		PageId headerPage = new PageId(0, relDef.getFileIdx());
		System.out.println("Affichage X45 - " + headerPage);
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(headerPage); // get
		System.out.println("Affichage X48 - Affichage buffer de la headerPage (AVANT) " + Arrays.toString(bufferPage.array()));
		bufferPage.putInt(0, bufferPage.getInt(0)+1); // A l'indice 0, on ajoute 1
		System.out.println("Affichage X46 - Affichage buffer de la headerPage (APRES) " + Arrays.toString(bufferPage.array()));
		// On parcours jusqu'au dernier et on ajoute le slotCount
		System.out.println("Affichage X49 : Affichage de la headerPage");
		
		for(int i=0 ; i<Constants.PAGE_SIZE ; i+=4) {
			System.out.print(bufferPage.getInt(i)+ " ");
		}
		System.out.println();
		int i;
		System.err.println("Affichage X9 : Nombre de page, lecture de headerPage get(0) " + bufferPage.getInt(0));
		for (i = 1; i < bufferPage.getInt(0); i++) ; //juste pour parcourir //test//
		System.out.println("Affichage X47 : Affichage du i " +"("+i+ ") "+ "et du slotCount " + "(" +relDef.getSlotCount()+ ")" );
		bufferPage.putInt(i * Integer.BYTES, relDef.getSlotCount());
		// DiskManager.writePage(pageId, bufferPage);
		BufferManager.getInstance().freePage(headerPage, true); // free
		return DiskManager.getInstance().addPage(relDef.getFileIdx());
	}

	/*
	 * return PageId d'une page de donnees qui a encore des cases libres
	 */
	public PageId getFreeDataPageId() {
		/**
		 * Parcours tous les pageId, et cherche dans chaque fichier, le nombre de case
		 * vide a l'aide du headerPage indiquant le nombre de case vide Si la taile de
		 * la case vide est sup a la taille d'un record : OK
		 */
		int pageIdx = 0, fileIdx = relDef.getFileIdx();
		PageId page = new PageId(pageIdx, fileIdx);
		System.err.println("Affichage X53 - Affichage de la page cree [" + page + "] avec pageIdx et fileIdx " + page.getPageIdx() + ", " + page.getFileIdx());
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(page); // get
		System.out.println("Erreur X54 - Affichage du buffer - " + bufferPage);
		System.out.println("Erreur X54bis - Affichage du buffer - " + Arrays.toString(bufferPage.array()));
		// On parcours tant que il n'y a pas de place
		int i = 4;
		boolean deLaPlace = false;
		System.out.println("Affichage X52 : Affichage get(0) de getFreeDataPageId : " +bufferPage.getInt(0));
		// Si il n'y a pas de page, on doit creer une page et on actualise la headePage
		if(bufferPage.get(0)==0) {
			DiskManager.getInstance().addPage(0);
			bufferPage.putInt(0,1);
			bufferPage.putInt(1, relDef.getSlotCount());
		}
		System.out.println("Affichage X70 : Affichage get(0) de getFreeDataPageId & le nombre de slot dispo " +bufferPage.getInt(0) + ", " + bufferPage.getInt(1));
		// On parcours les pages
		while (!deLaPlace && i < bufferPage.getInt(0)) {	// TODO a verifier si c'est bien *4
			// Il parcours les slots count, si il a plus de 0 places, on peut sortir de la boucle, sinon on continue
			if (bufferPage.getInt(i) != 0) {
				deLaPlace = true;
			} else {
				i += Integer.BYTES;
			}
		}
		boolean flagDirty = false ;
		// Si on a creer la premiere page, on doit dire qu'on a modifier
		if(bufferPage.getInt(0)==1) {
			flagDirty = true ;
		}
		BufferManager.getInstance().freePage(page, flagDirty); // free
		if (deLaPlace == false) {
			return null;
		}
		System.out.println("Affichage 50 - Affichage de la page a retourne : " + new PageId(i/4,fileIdx));
		return new PageId(i/4,fileIdx);
	}
	
	

	/**
	 * Ecrit les record dans une page
	 * 
	 * @param record
	 * @param pageId
	 * @return
	 */
	public Rid writeRecordToDataPage(Record record, PageId pageId) {
		RandomAccessFile rf = null;
		
		System.err.println("Affichage X7 : Verification - Affichage du fichierId saisie en parametre " + pageId.getFileIdx());
		int fileIdx = pageId.getFileIdx();
		
		try {
			rf = new RandomAccessFile(new File(Constants.PATH + "Data_" + fileIdx + ".rf"), "rw");
		} catch (FileNotFoundException e) {
			System.err.println("le fichier " +Constants.PATH + "Data_" +rf + " n'a pas ete trouver");
			
		}
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(pageId);	// get
		int positionByteMap = 0;
		boolean caseLibre = false;
		while (!caseLibre && positionByteMap < bufferPage.getInt(0)) {
			if (bufferPage.get(positionByteMap) == 0) {
				caseLibre = true;
			}
			// Attention : ByteMap
			else {
				positionByteMap += Byte.BYTES;
			}
		}
		
		bufferPage.put(positionByteMap, (byte) 1); // C'est occupe mtn

		// On insere apres avoir focus la place dans la page
		System.out.println("Affichage X35 - Affichage positionbyteMap depuis depuis le writeToDataPage de Heap : " + positionByteMap);
		System.out.println("Affichage X68 - Affichage du slotCount et du recordSize : " + relDef.getSlotCount() + ", " + relDef.getRecordSize());
		int positionSlot = relDef.getSlotCount() + relDef.getRecordSize() * positionByteMap;
		System.out.println("Affichage X36 - Affichage positionSlot byteMap depuis depuis le writeToDataPage de Heap : " + positionSlot);
		record.writeToBuffer(bufferPage, positionSlot);
		
		System.out.println("Affichage X37 : Affichage du PageId " + pageId.toString());
		System.out.println("Affichage X38 : Affichage du bufferPage " + bufferPage);
		DiskManager.getInstance().writePage(pageId, bufferPage);
		
		BufferManager.getInstance().freePage(pageId, true);	// free
		/**
		 * On retourne a la headerPage et on arrive jusqu'a la pageIdx et on enleve 1
		 */
		pageId = new PageId(0, pageId.getFileIdx());
		for (int j = 0; j < bufferPage.get(pageId.getPageIdx()); j += Integer.BYTES);
		bufferPage.putInt(positionByteMap, bufferPage.getInt(positionByteMap) - 1);
		return new Rid(pageId, positionByteMap);
	}

	/**
	 * Recupere les records de la page et renvoie une liste de records
	 * 
	 * @param pageId
	 * @return
	 */
	public List<Record> getRecordInDataPage(PageId pageId) {
		/**
		 * TODO Reverifier la position des info
		 */
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(pageId);	// get
		List<Record> listRecord = new ArrayList<Record>();

		/**
		 * Boucle pour lire la bytemap
		 * Si c est egal a 1, la position du ByteBuffer est actualise
		 * au record concerne et on recupere les valeurs
		 * Quand cette operation est terminee, on remet la position du 
		 * ByteBuffer au bytemap
		 * 
		 * A partir du slotCount on lit les records que l'on va stocker dans une liste
		 */
		for (int positionByteMap = 0; positionByteMap < relDef.getSlotCount(); positionByteMap++) {
			int positionRecord = relDef.getSlotCount() + positionByteMap;
			System.out.println("Affichage X67 - Position du record : " + positionRecord);
			bufferPage.position(positionByteMap);
			if (bufferPage.get(positionByteMap) == 1) {
				bufferPage.position(positionByteMap);
				List<String> listElementRecord = new ArrayList<String>();
				for (int i = 0; i < relDef.getTypeCol().size(); i++) {
					if (relDef.getTypeCol().get(i).equals("int")) {
						String s = Integer.toString(bufferPage.getInt());
						listElementRecord.add(s);
					}
					else if (relDef.getTypeCol().get(i).equals("float")) {
						String s = Float.toString(bufferPage.getFloat());
						listElementRecord.add(s);
					}
					else {
						int taille = Integer.parseInt(relDef.getTypeCol().get(i).substring("string".length()));
						String valARecup = "";
						for (int j = 0; j < taille; j++) {
							String charBuff = Character.toString(bufferPage.getChar());
							valARecup = valARecup.concat(charBuff);
							System.out.println("Affichage X44 - Affichage du charbuff" + valARecup);
						}
						listElementRecord.add(valARecup);
					}
				}
				listRecord.add(new Record(relDef, listElementRecord));
			}
		}
		BufferManager.getInstance().freePage(pageId, false);	// free
		return listRecord;
	}
	
	public Rid insertRecord(Record record) {
		System.out.println("Affichage X27 : Affichage du record passer en parametre du insertRecord : " + record.toString());
		PageId pageLibre = getFreeDataPageId();
		System.out.println("Affichage X51 : Affichage pageLibre : " + pageLibre);
		if(pageLibre == null) {
			pageLibre = addDataPage();
		}
		System.err.println("Affichage X8 : Affichage page libre " + pageLibre);
		return writeRecordToDataPage(record, pageLibre);
						
	}

	// Getters
	
	/**
	 * Cherche tous les records d'un heapfile
	 * 
	 * @return la liste de records dans le heapfile
	 */
	public List<Record> getAllRecords(){
		int pageIdx = 0 ;	// On incremetera au fur et a mesure des pages 
		int fileIdx = this.relDef.getFileIdx(); //nom du fichier � recuperer les records
		PageId page =  new PageId(pageIdx, fileIdx);
		List<Record> listRecordOfHeapFile = new ArrayList<>(); //variable pour stocker la liste des records d'un heapfile
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(page);	// get
		int nbPage = bufferPage.getInt(0) ;
		// Tant qu'on atteint pas le nombre de page (premiere case du headerPage)
		
		for(int i=1 ; i<nbPage ; i++) {
			page =  new PageId(i, fileIdx);
			List <Record> listRecordOfPage = this.getRecordInDataPage(page);
			for(Record r : listRecordOfPage) {
				listRecordOfHeapFile.add(r);
			}
		}
		BufferManager.getInstance().freePage(page, false);	// free
		return listRecordOfHeapFile ;
	}
	
	public RelDef getRelDef() {
		return relDef;
	}

	@Override
	public String toString() {
		return "HeapFile [relDef=" + relDef + "]";
	}
	
	
}
