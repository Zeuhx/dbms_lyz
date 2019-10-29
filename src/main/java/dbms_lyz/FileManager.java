package main.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;

public class FileManager {
	private List <HeapFile> heapFiles;
	private static FileManager INSTANCE = null;
	public FileManager() {
		heapFiles = new ArrayList<>();
	}
	public FileManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new FileManager();
		}
		return INSTANCE;
	}
	
	public void init() {
	
	/**
	 * Cette methode doit :
	 * parcourir la liste des relDef de DBDef
	 * creer pour chq RelDef un objet HeapFile en lui
	 * attribuant la RelDef en question
	 * rajouter le HeapFile a heapfiles
	 */
	
	}
	
	public void createRelationFile(RelDef relDef) {
		
		/**
		 * Cette methode doit :
		 * creer un nouvel objet de type HeapFile 
		 * et lui attribuer relDef
		 * le rajouter la liste heapFiles
		 * puis appeler sur cet objet 
		 * la methode createNewOnDisk
		 * 
		 */
		
		HeapFile hf = new HeapFile(relDef);
		heapFiles.add(hf);
		heapFiles.get(heapFiles.size()+1).createNewOnDisk();
		
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
		
		for(HeapFile h : heapFiles) {
			if(h.getRelDef().getNomRelation().equals(relName)) {
				
			}
		}
		return null;
		
	}
	
	public List<Record> SelectAllFromRelation (String relName){
		return null;
		
		/**
		 * Cette methode doit :
		 * _retourner une liste contenant tous les
		 * records de la relation
		 *
		 */
	}
	
	public List<Record> selectFromRelation(String relName, int idxCol, String valeur){
		return null;
		
		/**
		 * Cette methode doit :
		 * retourner une liste contenant tous les records
		 * de la relation RelName pour lesquels la valeur 
		 * la valeur surr la colonne idxCol (convertie en cha�ne de caracteres)
		 * est egale a "valeur" .
		 */
	}
	
}
