package main.java.dbms_lyz;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
	private List <HeapFile> heapFiles;
	
	/* Singleton */
	private static FileManager INSTANCE = null;
	private FileManager() { heapFiles = new ArrayList<>(); }
	public static FileManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new FileManager();
		}
		return INSTANCE;
	}
	
	/**
	 * Declancheur :
	 * Parcours la liste des relDef de DBDef pour creer un
	 * heapFile a chaque relDef
	 */
	public void init() {
		DBDef.getInstance();
		if(DBDef.getRelDefTab().isEmpty()) {
			throw new RuntimeException("Il n'y a pas de RelDef stocke");
		} else {
			for(RelDef relDef : DBDef.getRelDefTab()) {
				HeapFile hf = new HeapFile(relDef);
				heapFiles.add(hf);
			}
		}
	}
	
	/**
	 * Cette methode doit :
	 * creer un nouvel objet de type HeapFile 
	 * et lui attribuer relDef le rajouter la liste heapFiles
	 * puis appeler sur cet objet la methode createNewOnDisk
	 * @param relDef
	 */
	public void createHeapFileWithRelation(RelDef relDef) {
		System.out.println("\n----- CREATION DU FICHIER HEAP -----");
		HeapFile hf = new HeapFile(relDef);
		heapFiles.add(hf);
		hf.createNewOnDisk();
		System.out.println("\n");
	}
	

	/**
	 * Cette methode s'occupe :
	 * de l'insertion de record dans la relation
	 * dont le nom est relName
	 * En parcourant heapfiles pour trouver la relation
	 * et appeler sa methode InsertRecord nouvellement insere 
	 * 
	 * @param record
	 * @param relName
	 * @return le Rid du record
	 */
	public Rid insertRecordInRelation(Record record, String relName) {
		System.out.println("----------------- INSERT IN RELATION --------------------");
		Rid rid = null ;
		/**
		 * Parcour du heapFiles pour inserer le bon record avec 
		 * le relName du record
		 **/
		
		System.out.println("Affichage X69 - Test");
		System.out.println("Affichage X63 - Affichage de si heapFiles vide ou non :  " + heapFiles.isEmpty());
		boolean continu = true ;
		for(HeapFile hf : heapFiles) {
			System.err.println("Affichage X64 - Affichage du relDef d'un heapFiles - " + hf);
			System.err.println("Affichage X65 - Verification si la condition est respecte : " + hf.getRelDef().getNomRelation().equals(relName));
			if(hf.getRelDef().getNomRelation().equals(relName) && continu) {
				rid = hf.insertRecord(record);
				System.out.println("Affichage X62 - Affichage du rid - Rid(" + rid.getSlotIdx() + "," + rid.getPageId()+")");
				continu = false ;
				return rid;
			}
			/**
			 * Si le relName n'existe pas, on creer un HeapFile a l'aide
			 * de ce relDef, et on l'insere dedans
			 */
			else {
				throw new RuntimeException("La relation n'existe pas ");
				/**
				 * TODO A traiter plus tard car une excpetion
				 */
//				RelDef relDef = new RelDef(relName, record.getValues());
//				HeapFile heap = new HeapFile(relDef);
//				rid = heap.insertRecord(record); 
//				/**
//				 * TODO Verifier si c'est bon pour actualiser la headerPage
//				 */
//				
//				PageId pageId = new PageId(0, relDef.getFileIdx());
//				System.out.println("Affichage X55 - Traverse InsertRecordInRelation - " + pageId);
//				ByteBuffer bufferPage = BufferManager.getInstance().getPage(pageId); // get
//				System.out.println("Affichage X56 - Affichage du buffer " + bufferPage);
//				bufferPage.putInt(0, 1);
//				System.out.println("Affichage X57 - Affichage du buffer " + bufferPage);
//				BufferManager.getInstance().freePage(pageId, true); // free
//				// Ecriture dans le fichier
//				DiskManager.getInstance().writePage(pageId, bufferPage);
			 }
		}
		return rid;
	}
		/**
		 * Si le relName n'existe pas, on creer un HeapFile a l'aide
		 * de ce relDef, et on l'insere dedans
		 */
		/*
		RelDef relDef = new RelDef(relName, record.getValues());
		HeapFile heap = new HeapFile(relDef);
		rid = heap.insertRecord(record);
		
		PageId pageId = new PageId(0, relDef.getFileIdx());
		System.out.println("Affichage X55 - Traverse InsertRecordInRelation - " + pageId);
		ByteBuffer bufferPage = BufferManager.getInstance().getPage(pageId); // get
		System.out.println("Affichage X56 - Affichage du buffer " + bufferPage);
		bufferPage.putInt(0, 1);
		System.out.println("Affichage X57 - Affichage du buffer " + bufferPage);
		BufferManager.getInstance().freePage(pageId, true); // free
		// Ecriture dans le fichier
		DiskManager.getInstance().writePage(pageId, bufferPage);

		return rid ;
	}
	
	/**
	 * Cette methode doit :
	 * retourner une liste contenant tous les records de la relation
	 * @param relName
	 * @return
	 * @throws RuntimeException
	 */
	public List<Record> selectAllFromRelation (String relName) {
		List<Record> listRecord = new ArrayList<>();
		
		//Parcours du heapfile pour recuperer la liste de records dont le relName correspond
		System.out.println("Affichage X93 - Affichage si heapFiles de FileManager est vide : " + heapFiles.isEmpty());
		for(HeapFile hf : heapFiles) {
			System.out.println("Affichage X94 - Affichage du relDef du hf - " + hf.getRelDef().getNomRelation());
			if(hf.getRelDef().getNomRelation().equals(relName)){
				ByteBuffer headerPageBuffer = BufferManager.getInstance().getPage(new PageId(0, hf.getRelDef().getFileIdx()));
				int nbPages = headerPageBuffer.getInt(0);
				
				BufferManager.getInstance().freePage(new PageId(0, hf.getRelDef().getFileIdx()),  false);
				System.out.println("Affichage X97 - Affichage nb de page : " + nbPages);
				for(int i=1; i<=nbPages; i++) {
					ByteBuffer pageBuffer = BufferManager.getInstance().getPage(new PageId(i, hf.getRelDef().getFileIdx()));
					
					System.out.println("Affichage X98 - Affichage SlotCount " + hf.getRelDef().getSlotCount());
					for(int compteurRecord = 0; compteurRecord<hf.getRelDef().getSlotCount(); compteurRecord +=Byte.BYTES) {
						System.out.println("Affichage X99 - Affichage du bufferGet : " + pageBuffer.get(compteurRecord));
						if(pageBuffer.get(compteurRecord) == (byte) 1) {
							int positionSlot = hf.getRelDef().getSlotCount() + compteurRecord * hf.getRelDef().getRecordSize();
							Record r = new Record(hf.getRelDef());
							System.out.println("Affichage X90 - Affichage du record " + r);
							r.readFromBuffer(pageBuffer, positionSlot);
							listRecord.add(r);
							System.out.println("Affichage X96 - Affichage si la listRecord est vide " + listRecord.isEmpty());
						}
					}
					BufferManager.getInstance().freePage(new PageId(i, hf.getRelDef().getFileIdx()), false);
				}
			}
			else {
				throw new RuntimeException("Il n'y a pas heapFile avec ce relName");
			}
		}
		System.out.println("Affichage X92 - Affichage si la lsite de record est vide depuis FileManager : " + listRecord.isEmpty());
		return listRecord;
	}

	/**
	 * 	 TODO
	 * Cette methode doit :
	 * retourner une liste contenant tous les records
	 * de la relation RelName pour lesquels la valeur 
	 * la valeur sur la colonne idxCol (convertie en chaine de caracteres)
	 * est egale a "valeur" .
	 * @param relName une chaine de caracteres
	 * @param idxCol un entier correspondant a un indice de colonne
	 * @param valeur une chaine de caracteres
	 * @return
	 */
	public List<Record> selectFromRelation(String relName, int idxCol, String valeur){
		List<Record> listeDeRecords = new ArrayList<Record>(selectAllFromRelation(relName));
		List<Record> listeDeRecordsAvecValeur = new ArrayList<Record>();
		
		/**
		 * verifie si la liste n'est pas null
		 * sinon on parcours tous les records
		 * puis on compare les valeurs de chaque record de la idxCol
		 * si c est bon on enregistre dans listeDeRecordsAvecValeur 
		 */
		if(listeDeRecords != null ) {
			for(Record record : listeDeRecords) {
				//compare la valeu
				if(record.getValues().get(idxCol).equals(valeur)) {
					listeDeRecordsAvecValeur.add(record);
				}
			}
		}
		return listeDeRecordsAvecValeur;
	}
	/**
	 * 
	 * @return heapFiles liste des heapfiles
	 */
	
	public List<HeapFile> getHeapFiles(){
		return heapFiles;
	}
	
	public List<HeapFile> setHeapFiles(List<HeapFile> newHeapFiles){
		heapFiles = newHeapFiles;
		return heapFiles;
	}
	
	/**
	 * Remet FileManager a 0 avec heapFiles
	 */
	public void reset() {
		heapFiles = new ArrayList<>();
	}
}
