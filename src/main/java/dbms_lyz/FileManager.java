package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
	private List <HeapFile> heapFiles;

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
		if(DBDef.getInstance().getRelDefTab().isEmpty()) {
			throw new RuntimeException("Il n'y a pas de RelDef stocke");
		} else {
			for(RelDef relDef : DBDef.getInstance().getRelDefTab()) {
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
		HeapFile hf = new HeapFile(relDef);
		heapFiles.add(hf);
		hf.createNewOnDisk();
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
		Rid rid = null ;
		/**
		 * Parcour du heapFiles pour inserer le bon record avec 
		 * le relName du record
		 **/
		boolean continu = true ;
		for(HeapFile hf : heapFiles) {
			System.out.println("Affichage X115 : Affichage du nom de la relation dans la liste des heaps : " + hf.getRelDef().getNomRelation());
			if(hf.getRelDef().getNomRelation().equals(relName) && continu) {
				rid = hf.insertRecord(record);
				System.out.println("Affichage X62 - Affichage du rid - Rid(" + rid.getSlotIdx() + "," + rid.getPageId()+")");
				continu = false ;
				return rid;
			}
			/**
			 * Si le relName n'existe pas, on creer un HeapFile a l'aideY
			 * de ce relDef, et on l'insere dedans
			 */
		}
		if(continu) {
				throw new RuntimeException("La relation n'existe pas ");
				/**
				 * TODO A traiter plus tard car une excpetion
				 */
		}
		return rid;
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
		for(HeapFile hf : heapFiles) {
			System.out.println("Affichage X94 - Affichage du relDef du hf - " + hf.getRelDef().getNomRelation());
			if(hf.getRelDef().getNomRelation().equals(relName)){
				ByteBuffer headerPageBuffer = BufferManager.getInstance().getPage(new PageId(0, hf.getRelDef().getFileIdx()));
				int nbPages = headerPageBuffer.getInt(0);
				
				BufferManager.getInstance().freePage(new PageId(0, hf.getRelDef().getFileIdx()),  false);
				for(int i=1; i<=nbPages; i++) {
					ByteBuffer pageBuffer = BufferManager.getInstance().getPage(new PageId(i, hf.getRelDef().getFileIdx()));
					for(int compteurRecord = 0; compteurRecord<hf.getRelDef().getSlotCount(); compteurRecord +=Byte.BYTES) {
						if(pageBuffer.get(compteurRecord) == (byte) 1) {
							int positionSlot = hf.getRelDef().getSlotCount() + compteurRecord * hf.getRelDef().getRecordSize();
							Record r = new Record(hf.getRelDef());
							System.out.println("Affichage X90 - Affichage du record " + r);
							r.readFromBuffer(pageBuffer, positionSlot);
							listRecord.add(r);
						}
					}
					BufferManager.getInstance().freePage(new PageId(i, hf.getRelDef().getFileIdx()), false);
				}
			}
			else {
				System.err.println("Aucune relation correspondante");
			}
		}
		System.out.println("Affichage X92 - Affichage si la lsite de record est vide depuis FileManager : " + listRecord.isEmpty());
		return listRecord;
	}

	/**
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
	
	// Getters 
	public List<HeapFile> getHeapFiles()	{ return heapFiles; }
	
	// Setters
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
