package main.java.dbms_lyz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
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
		int fileIdx = relDef.getFileIdx(); // L'indice du fichier est donnee par relDef
		DiskManager.getInstance().createFile(fileIdx);
		DiskManager.addPage(fileIdx);
		PageId headerPage = new PageId(0, fileIdx);
		ByteBuffer bufferDePageVide = BufferManager.getInstance().getPage(headerPage);
		for (int i = 0; i < Constants.PAGE_SIZE; i += Integer.BYTES) {
			bufferDePageVide.putInt(0);
		}
		// DiskManager.writePage(pageId, bufferDePageVide);
		BufferManager.getInstance().freePage(headerPage, true);
	}

	/**
	 * On creer un page via l'ID de page en parametre On prend son fileIdx et on
	 * ajoute la page On ecrit via le buffer ? On incremente le slotCount de la
	 * headerPage et on libere le buffer en disant qu'on a pas toucher la page
	 * (true)
	 * 
	 * @param pageId
	 */
	public void addDataPage(PageId pageId) {
		pageId = new PageId(0, pageId.getFileIdx());
		DiskManager.addPage(pageId.getFileIdx());
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(pageId);
		bufferPage.putInt(0, bufferPage.getInt(0) + 1); // A l'indice 0, on ajoute 1
		// On parcours jusqu'au dernier et on ajoute le slotCount
		int i;
		for (i = 1; i < bufferPage.getInt(0); i++) ;
		bufferPage.putInt(i, relDef.getSlotCount());
		// DiskManager.writePage(pageId, bufferPage);
		BufferManager.getInstance().freePage(pageId, true);
	}

	/*
	 * return PageId d une page de donnees qui a encore des cases libres
	 */
	public PageId getFreeDataPageId() {
		/**
		 * Parcours tous les pageId, et cherche dans chaque fichier, le nombre de case
		 * vide a l'aide du headerPage indiquant le nombre de case vide Si la taile de
		 * la case vide est sup a la taille d'un record : OK
		 */
		int pageIdx = 0, fileIdx = relDef.getFileIdx();
		PageId page = new PageId(pageIdx, fileIdx);
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(page);
		// On parcours tant que il n'y a pas de place
		int i = 4;
		boolean deLaPlace = false;
		while (!deLaPlace && i < bufferPage.get(0)) {
			if (bufferPage.getInt(i) != 0) {
				deLaPlace = true;
			}
			i += Integer.BYTES;
		}
		;
		BufferManager.getInstance().freePage(page, false);
		if (deLaPlace == false) {
			return null;
		}
		return page;
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
		
		int fileIdx = pageId.getFileIdx();
		String path = new String("src" + File.separator + "main" + 
				File.separator + "resources" + File.separator + "DB" + File.separator + "Data_");;
		try {
			rf = new RandomAccessFile(new File(path  + fileIdx + ".rf"), "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		// TODO Marcel finir le channel et bytebuffer
		FileChannel channel = rf.getChannel();
		
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(pageId);
		int positionByteMap = 0;
		boolean caseLibre = false;
		while (!caseLibre && positionByteMap < bufferPage.getInt(0)) {
			if (bufferPage.get(positionByteMap) == 0) {
				caseLibre = true;
			}
			// Attention : ByteMap
			positionByteMap += Byte.BYTES;
		}
		;
		bufferPage.put(positionByteMap, (byte) 1); // C'est occupe mtn

		// On insere apres avoir focus la place dans la page
		int positionSlot = relDef.getSlotCount() + relDef.getRecordSize() * positionByteMap;
		record.writeToBuffer(bufferPage, positionSlot);
		BufferManager.getInstance().freePage(pageId, true);
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
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(pageId);
		List<Record> listRecord = new ArrayList<Record>();
		/**
		 * A partir du slotCount on lit les records que l'on va stocker dans une liste
		 */

		for (int positionByteMap = 0; positionByteMap < relDef.getSlotCount(); positionByteMap++) {
			/**
			 * Boucle pour lire la bytemap
			 * Si c est egal a 1, la position du ByteBuffer est actualise
			 * au record concerne et on recupere les valeurs
			 * Quand cette operation est terminee, on remet la position du 
			 * ByteBuffer au bytemap
			 * 
			 */
			int positionRecord = relDef.getSlotCount() + positionByteMap;
			bufferPage.position(positionByteMap);
			if (bufferPage.get(positionByteMap) == 1) {
				bufferPage.position(positionRecord);
				List<String> listElementRecord = new ArrayList<String>();
				for (int i = 0; i < relDef.getTypeCol().size(); i++) {
					if (relDef.getTypeCol().get(i).equals("int")) {
						String s = Integer.toString(bufferPage.getInt());
						listElementRecord.add(s);
					}
					else if (relDef.getTypeCol().get(i).equals("int")) {
						String s = Integer.toString(bufferPage.getInt());
						listElementRecord.add(s);
					}
					else {
						int taille = Integer.parseInt(relDef.getTypeCol().get(i).substring("string".length()));
						String valARecup = "";
						for (int j = 0; j < taille; j++) {
							String charBuff = Character.toString(bufferPage.getChar());
							valARecup.concat(charBuff);
						}
					}
				}
				listRecord.add(new Record(relDef, listElementRecord));
			}
		}
		return listRecord;
	}
	
	/**
	 * TODO : est ce que c'est �a ? est ce que il faut voir si il faut traiter la taille de record 
	 * entr� en argument avec des conditions
	 */
	public Rid insertRecord(Record record) {
		return writeRecordToDataPage(record, getFreeDataPageId());
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
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(page);
		int nbPage = bufferPage.getInt(0) ;
		// Tant qu'on atteint pas le nombre de page (premiere case du headerPage)
		
		for(int i=1 ; i<nbPage ; i++) {
			page =  new PageId(i, fileIdx);
			//bufferPage = BufferManager.getInstance().getPage(page)
			List <Record> listRecordOfPage = this.getRecordInDataPage(page);
			for(Record r : listRecordOfPage) {
				listRecordOfHeapFile.add(r);
			}
		}
		return listRecordOfHeapFile ;
	}
	
	public RelDef getRelDef() {
		return relDef;
	}

}
