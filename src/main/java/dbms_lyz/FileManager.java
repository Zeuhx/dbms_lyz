package main.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;

public class FileManager {
	private List <HeapFile> heapFiles;
	
	/** Singleton */
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
	 * 	 Cette methode doit :
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
		//TODO : ACTUALISER LE HeaderPage !!!!
		Rid rid = null ;
		/**
		 * Parcour du heapFiles pour inserer le bon record avec 
		 * le relName du record
		 */
		for(HeapFile hf : heapFiles) {
			if(hf.getRelDef().getNomRelation().equals(relName)) {
				rid = hf.insertRecord(record);
			}
			/**
			 * Si le relName n'existe pas, on creer un HeapFile a l'aide
			 * de ce relDef, et on l'insere dedans
			 */
			RelDef relDef = new RelDef(relName, record.getValues());
			HeapFile heap = new HeapFile(relDef);
			rid = heap.insertRecord(record);
		}
		return rid ;
	}
	
	/**
	 * Cette methode doit :
	 * retourner une liste contenant tous les records de la relation
	 * @param relName
	 * @return
	 * @throws RuntimeException
	 */
	public List<Record> selectAllFromRelation (String relName) throws RuntimeException{
		List<Record> listeDeRecords = new ArrayList<Record>();
		
		//Parcours du heapfile pour recuperer la liste de records dont le relName correspond
		for(HeapFile hf : heapFiles) {
			if(hf.getRelDef().getNomRelation().equals(relName)) {
				listeDeRecords.addAll(hf.getAllRecords());
			}
			else {
				throw new RuntimeException("Il n'y a pas heapFile avec ce relName");
			}
		}
		return listeDeRecords;
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
