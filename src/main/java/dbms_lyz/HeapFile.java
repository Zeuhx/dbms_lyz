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
		System.out.println("Affichage X11 - Affichage du relDef : \n "+ "\t" +relDef.toString());
		DiskManager.getInstance();
		int fileIdx = relDef.getFileIdx(); 
//		System.out.println("Affichage X12 \n[Creation du fichier via l'id du fichier] : " + fileIdx);
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
		System.out.println("Affichage X45 - " + headerPage);
		ByteBuffer bufferHeaderPage = BufferManager.getInstance().getPage(headerPage); // get
		System.out.println("Affichage X48 - Affichage buffer de la headerPage " + Arrays.toString(bufferHeaderPage.array()));
		// On parcours jusqu'au dernier et on ajoute le slotCount
		System.out.println("Affichage X49 : Affichage de la headerPage");
		for(int i=0 ; i<Constants.PAGE_SIZE ; i+=4) {
			System.out.print(bufferHeaderPage.getInt(i)+ " ");
		}
		System.out.println();
		
		System.out.println("Affichage X102 - Affichage du get(0) du headerPage depuis addDataPage "+bufferHeaderPage.getInt(0));
		bufferHeaderPage.putInt(0,bufferHeaderPage.getInt(0)+1);
		
		System.out.println("Affichage X103 - Affichage du get(0) apres actualisation du headerPage depuis addDataPage "+bufferHeaderPage.getInt(0));
		bufferHeaderPage.putInt(bufferHeaderPage.getInt(0) * Integer.BYTES, relDef.getSlotCount());
		System.out.println("Affichage X104 - Affichage du lieu ou c'est ajoute et du slot count:"+ bufferHeaderPage.getInt(0)*Integer.BYTES+" "+relDef.getSlotCount());
		System.out.println("Affichage X76 - Affichage du buffer apres le put : " + Arrays.toString(bufferHeaderPage.array()));
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
		System.err.println("Affichage X53 - Affichage de la page cree [" + page + "] avec pageIdx et fileIdx " + page.getPageIdx() + ", " + page.getFileIdx());
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(page); // get
		System.out.println("Erreur X54 - Affichage du buffer - " + bufferPage);
		System.out.println("Erreur X46 - Affichage du buffer - " + Arrays.toString(bufferPage.array()));
		// On parcours tant que il n'y a pas de place
		int i = 0; 
		boolean deLaPlace = false;
		System.out.println("Affichage X52 : Affichage get(0) de getFreeDataPageId : " +bufferPage.getInt(0));
		// Si il n'y a pas de page, on doit creer une page et on actualise la headePage
		
	
		System.out.println("Affichage X70 : Affichage get(0) de getFreeDataPageId & le nombre de slot dispo " +bufferPage.getInt(0) + ", " + bufferPage.getInt(1));
		// On parcours les pages
		int nbpages = bufferPage.getInt(0);
		System.out.println("Affichage X84 - Affichage du get(0) : " + bufferPage.getInt(0));
		boolean b = !deLaPlace && i < nbpages ;
		System.out.println("Affichage X85 - Boolean b rentre dans le while : " + b);
		while (!deLaPlace && i < nbpages) {
			i++ ;
			// Il parcours les slots count, si il a plus de 0 places, on peut sortir de la boucle, sinon on continue
			System.out.println("Affichage X83 - Affichages des getInt(i) : " + bufferPage.getInt(i*Integer.BYTES));
			if (bufferPage.getInt(i*Integer.BYTES) != 0) {
				deLaPlace = true;
			} 
		}
		System.err.println("Affichage 71bis - Affichage du numero de page retourne : " + i);
		boolean flagDirty = false ;

		BufferManager.getInstance().freePage(page, flagDirty); // free
		// TODO voir quel methode utilsie le cas du null
		if (deLaPlace == false) {
			System.out.println("Affichage X75 - Il n'y a pas de place"); 
			return null;
		}
		System.err.println("Affichage X71 - Affichage du numero de page retourne : " + i);
		System.out.println("Affichage X50 - Affichage de la page a retourne : " + new PageId(i/4,fileIdx));
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
		
		System.err.println("Affichage X7 : Verification - Affichage du fichierId saisie en parametre " + pageId.getFileIdx());
//		int fileIdx = pageId.getFileIdx();
//		
//		try(RandomAccessFile rf = new RandomAccessFile(new File(Constants.PATH + "Data_" + fileIdx + ".rf"), "rw")) {
//		} catch (FileNotFoundException e) {
//			System.err.println("le fichier " +Constants.PATH + "Data n'a pas ete trouver");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(pageId);	// get
		System.out.println("Affichage X86 - Affichage du buffer de la page ou on va ecrire le record : " + Arrays.toString(bufferPage.array()));
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
		System.err.println("Affichage X36 - Affichage positionSlot byteMap depuis depuis le writeToDataPage de Heap : " + positionSlot);
		record.writeToBuffer(bufferPage, positionSlot);
		System.out.println("Affichage X37 : Affichage du PageId " + pageId.toString());
		System.out.println("Affichage X38 : Affichage du bufferPage " + bufferPage);
		
		BufferManager.getInstance().freePage(pageId, true);	// free
		/**
		 * On retourne a la headerPage et on arrive jusqu'a la pageIdx et on enleve 1
		 */
		/**
		 * TODO A delete, test pour verifier la header 
		 */
		ByteBuffer headerPageBuff = BufferManager.getInstance().getPage(new PageId(0, pageId.getFileIdx()));
		System.out.println("Affichage X82 : Affichage de la headerPage");
		for(int i=0 ; i<Constants.PAGE_SIZE ; i+=Integer.BYTES) {
			System.out.print(headerPageBuff.getInt(i)+ " ");
		}
		System.out.println();
		
		int pos = pageId.getPageIdx()*Integer.BYTES;
		System.out.println("Affichage X100 - Affichage de la postion : " + pos);
		headerPageBuff.position(pos);
		int oldcount = headerPageBuff.getInt();
		System.out.println("Affichage X101 - Affichage nbPage dans la header : " + oldcount);
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
		System.out.println("----------------- METHODE INSERT --------------------"); 
		System.out.println("Affichage X96bis - Affichage du headerPage : " +  Arrays.toString(BufferManager.getInstance().getPage(new PageId(0, relDef.getFileIdx())).array()));
		BufferManager.getInstance().freePage(new PageId(0, relDef.getFileIdx()), false);
		System.out.println("Affichage X27 : Affichage du record passer en parametre du insertRecord : " + record.toString());
		PageId pageLibre = getFreeDataPageId();
		
		
//		System.out.println("Affichage X72 : Affichage de la page libre - " + pageLibre);
//		ByteBuffer bufferLibre = BufferManager.getInstance().getPage(pageLibre);
//		System.out.println("Affichage X88 - Affichage buffer de la page libre : " + Arrays.toString(bufferLibre.array()));
//		BufferManager.getInstance().freePage(pageLibre, false);
//		PageId headerPage = ) ;
		ByteBuffer bufferHeader  = BufferManager.getInstance().getPage(new PageId(0, relDef.getFileIdx()));
		
		System.out.println("Affichage X77 - Affichage headerPage Buffer " + Arrays.toString(bufferHeader.array()));
		

		if(pageLibre == null) {
			System.err.println("Affichage X73 : Entre dans la boucle null ou 0 du insertRecord" );
			pageLibre = addDataPage(); //TODO actualiser le header dans cette methode
			System.out.println("Affichage X80 : Affichage pageLibre : " + pageLibre);
			System.out.println("Affichage X79 - Affichage headerPage Buffer " + Arrays.toString(bufferHeader.array()));
		}
		
		BufferManager.getInstance().freePage(new PageId(0, relDef.getFileIdx()) , true);

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
	
	public RelDef getRelDef() {
		return relDef;
	}

	@Override
	public String toString() {
		return "HeapFile [relDef=" + relDef + "]";
	}
	
	
}
