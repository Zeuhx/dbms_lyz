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
		 * _parcourir la liste des relDef de DBDef
		 * _creer pour chq RelDef un objet HeapFile en lui
		 * attribuant la RelDef en question
		 * _rajouter le HeapFile a heapfiles
		 */
		int i=0;
		DBDef.getInstance();
		//on va parcourir la liste des relDef de DBDef
		//creer pour chq RelDef un objet HeapFile en lui
		for(RelDef relDef : DBDef.getList()) {
			if (i<DBDef.getListSize()) {
				DBDef.getListIndice(i);
			}
			else break;
		}

		
	}
	
	public void createRelationFile(RelDef relDef) {
		
		/**
		 * Cette methode doit :
		 * _cr�er un nouvel objet de type HeapFile 
		 * et lui attribuer relDef
		 * _le rajouter � la liste heapFiles
		 * _puis appeler sur cet objet 
		 * la m�thode createNewOnDisk
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
		 * _retourner une liste contenant tous les records
		 * a relation nomm�erelName pour lesquels la valeur 
		 * ur la colonne idxCol (convertie en cha�ne de caract�res)
		 * est �gale � valeur.
		 */
	}
	
}
