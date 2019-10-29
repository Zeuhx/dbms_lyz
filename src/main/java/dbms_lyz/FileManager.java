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
	 * Cette methode doit :
	 * creer un nouvel objet de type HeapFile 
	 * et lui attribuer relDef le rajouter la liste heapFiles
	 * puis appeler sur cet objet la methode createNewOnDisk
	 * 
	 */	
	public void createRelationFile(RelDef relDef) {
		HeapFile hf = new HeapFile(relDef);
		heapFiles.add(hf);
		hf.createNewOnDisk();	
	}
	
	public Rid insertRecordInRelation(Record record, String relName) {
		
		/**
		 * Cette methode s'occupe :
		 * _de l'insertion de record dans la relation
		 * dont le nom est relName
		 * En parcourant heapfiles pour trouver la relation
		 * et appeler sa methode InsertRecord
		 * 
		 */
		
//		int i = 0;
		/**
		 * parcour le heapFiles pour inserer le bon record
		 * avec le relName du record
		 */
		
		for(HeapFile hf : heapFiles) {
			if(hf.getRelDef().getNomRelation().equals(relName)) {
				hf.insertRecord(record);
				//return hf.rid;
			}
		}
		// TODO : return un rid ?
		return null;
		
	}
	
	/**
	 * Cette methode doit :
	 * retourner une liste contenant tous les records de la relation
	 *
	 */
//	public List<Record> SelectAllFromRelation (String relName){
//		List<Record> listeDeRecords = new ArrayList<Record>();
//		
//		HeapFile hf = null;
//		
//		
//		// parcour la liste de records de relDef dans le heapfile 
//		//mais c faux le relDef n'a pas de liste de records normalement
//		for(int i=0; i<heapFiles.size(); i++) {
////			for(HeapFile hf : heapFiles) {}
//			hf = heapFiles.get(i);
//
//			if(heapFiles.get(i).getRelDef().getNomRelation().equals(relName)) {
//				//parcour chaque record de la liste d Records
//				for (int j=0; j<heapFiles.get(i).getRecordInDataPage.size(); j++) {
//			//		//TODO : chaque record voir if relName de record == relName
//				}
////				listeDeRecords.addAll((heapFiles.get(i).getRelDef().getRecord()));
//				listeDeRecords.add((Record) heapFiles.get(i).getRelDef().getRecord());//faux ici 
//				// c pas un records mais une liste de records
//			}
//		}
//		return listeDeRecords;
//	
//	}
	
	/**
	 * TODO
	 * Cette methode doit :
	 * retourner une liste contenant tous les records
	 * de la relation RelName pour lesquels la valeur 
	 * la valeur sur la colonne idxCol (convertie en cha�ne de caracteres)
	 * est egale a "valeur" .
	 */
	public List<Record> selectFromRelation(String relName, int idxCol, String valeur){
		List<Record> listeDeRecords = new ArrayList<Record>();
		HeapFile aHeapFileOfList = null  ;
		for(int i=0 ; i<heapFiles.size() ; i++) {
			aHeapFileOfList = heapFiles.get(i);
			if(aHeapFileOfList.getRelDef().getNomRelation().equals(relName)) {
			//	aHeapFileOfList.getRelDef().getRecord();
			}
			
		}
		return null ;
	}

}
