package main.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;

public class FileManager {
	private List <HeapFile> heapFiles;
	private static FileManager INSTANCE = null;
	public FileManager() {
		heapFiles = new ArrayList<>();
	}

	public static FileManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new FileManager();
		}
		return INSTANCE;
	}
	
	public void init() {
		DBDef.getInstance();
		//on va parcourir la liste des relDef de DBDef
		//creer pour chq RelDef un objet HeapFile en lui
		for(RelDef relDef : DBDef.getList()) {
			HeapFile hf = new HeapFile (relDef);
			heapFiles.add(hf);
		}
	}
	
	/**
	 * 	 Cette methode doit :
	 * creer un nouvel objet de type HeapFile 
	 * et lui attribuer relDef le rajouter la liste heapFiles
	 * puis appeler sur cet objet la methode createNewOnDisk
	 * @param relDef
	 */
	public void createRelationFile(RelDef relDef) {
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
		
		if(listeDeRecords != null ) {
			for(Record r : listeDeRecords) {//TODO : 
			}
		}
		return listeDeRecords;
	}

}
