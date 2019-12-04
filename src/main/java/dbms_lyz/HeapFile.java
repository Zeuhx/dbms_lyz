package main.java.dbms_lyz;


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
		DiskManager.getInstance();
		int fileIdx = relDef.getFileIdx(); 
		DiskManager.getInstance().createFile(fileIdx);
		DiskManager.getInstance().addPage(fileIdx);
		PageId headerPage = new PageId(0, fileIdx);
		ByteBuffer headerPageBuff = BufferManager.getInstance().getPage(headerPage); // get
		for (int i = 0 ; i < Constants.PAGE_SIZE ; i += Integer.BYTES) {
			headerPageBuff.putInt(0);
		}
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
		PageId pageId = DiskManager.getInstance().addPage(relDef.getFileIdx());
		
		PageId headerPage = new PageId(0, relDef.getFileIdx());
		ByteBuffer bufferHeaderPage = BufferManager.getInstance().getPage(headerPage); // get
		// On parcours jusqu'au dernier et on ajoute le slotCount
		for(int i=0 ; i<Constants.PAGE_SIZE ; i+=4) {
			System.out.print(bufferHeaderPage.getInt(i)+ " ");
		}
		System.out.println();
		bufferHeaderPage.putInt(0,bufferHeaderPage.getInt(0)+1);
		bufferHeaderPage.putInt(bufferHeaderPage.getInt(0) * Integer.BYTES, relDef.getSlotCount());
		BufferManager.getInstance().freePage(headerPage, true); // free
		return pageId;
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
		
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(page); // get
		// On parcours tant que il n'y a pas de place
		int i = 0; 
		boolean deLaPlace = false;
		
		// Si il n'y a pas de page, on doit creer une page et on actualise la headePage
		// On parcours les pages
		int nbpages = bufferPage.getInt(0);
		while (!deLaPlace && i < nbpages) {
			i++ ;
			// Il parcours les slots count, si il a plus de 0 places, on peut sortir de la boucle, sinon on continue
			if (bufferPage.getInt(i*Integer.BYTES) != 0) {
				deLaPlace = true;
			} 
		}
		boolean flagDirty = false ;

		BufferManager.getInstance().freePage(page, flagDirty); // free
		if (deLaPlace == false) {
			return null;
		}
		return new PageId(i,fileIdx);
	}
	

	/**
	 * Ecrit les record dans une page
	 * 
	 * @param record
	 * @param pageId
	 * @return
	 */
	public Rid writeRecordToDataPage(Record record, PageId pageId) {

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
		
		bufferPage.put(positionByteMap, (byte) 1); // C'est occupe mtn --> slotCount a 1

		// On insere apres avoir focus la place dans la page
		int positionSlot = relDef.getSlotCount() + relDef.getRecordSize() * positionByteMap;
		record.writeToBuffer(bufferPage, positionSlot);
		
		BufferManager.getInstance().freePage(pageId, true);	// free
		/**
		 * On retourne a la headerPage et on arrive jusqu'a la pageIdx et on enleve 1
		 */
		ByteBuffer headerPageBuff = BufferManager.getInstance().getPage(new PageId(0, pageId.getFileIdx()));
		int pos = pageId.getPageIdx()*Integer.BYTES;
		headerPageBuff.position(pos);
		int oldcount = headerPageBuff.getInt();
		headerPageBuff.position(pos);
		headerPageBuff.putInt(oldcount-1);	
		BufferManager.getInstance().freePage(new PageId(0, pageId.getFileIdx()), true);
		return new Rid(pageId, positionByteMap);
	}

	/**
	 * Recupere les records de la page et renvoie une liste de records
	 * 
	 * @param pageId
	 * @return
	 */
	public List<Record> getRecordInDataPage(PageId pageId) {
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
		PageId pageLibre = getFreeDataPageId();
		if(pageLibre == null) {
			pageLibre = addDataPage();
			System.out.println("Affichage X80 : Affichage pageLibre : " + pageLibre);
		}
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
		int fileIdx = this.relDef.getFileIdx(); //nom du fichier a recuperer les records
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
	
	public RelDef getRelDef() 	{ return relDef; }

	@Override
	public String toString() {
		return "HeapFile [relDef=" + relDef + "]";
	}
}
