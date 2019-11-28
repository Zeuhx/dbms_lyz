package main.java.dbms_lyz;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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
		//TODO : ACTUALISER LE HeaderPage !!!!
		Rid rid = null ;
		/**
		 * Parcour du heapFiles pour inserer le bon record avec 
		 * le relName du record
		 */
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
		RandomAccessFile rf = null;
		
		//Parcours du heapfile pour recuperer la liste de records dont le relName correspond
		for(HeapFile hf : heapFiles) {
			if(hf.getRelDef().getNomRelation().equals(relName)){
				int fileIdx = hf.getRelDef().getFileIdx();
				try {
					rf = new RandomAccessFile(Constants.PATH + "Data_" + fileIdx + ".rf" , "r" );
					int nbPage = rf.readInt();
					System.err.println("Affichage X39 - Affichage du nombre de page dans le heapFile : " + nbPage + " page(s)");
					rf.seek(Constants.PAGE_SIZE);
					System.err.println("Affichage X40 - Affichage du pointeur " + rf); 
					for(int numeroPage = 1; numeroPage<=nbPage ; numeroPage++) {
						PageId pageId = new PageId(numeroPage, hf.getRelDef().getFileIdx());
						for (Record record : hf.getRecordInDataPage(pageId)) {
							System.out.println("Affichage X66 - Affichage record : " + record);
							listRecord.add(record);
							System.err.println("Affichage X43 : " + record);
						}
					}
					System.out.println("Affichage X41 - Affichage de la list de record " + listRecord);
				} catch (FileNotFoundException e) {
					System.err.println("Pas de fichier trouve");
				} catch (IOException e) {
					System.err.println("On a pas acces a la page depuis le selectAll");
				}
			}
			else {
				throw new RuntimeException("Il n'y a pas heapFile avec ce relName");
			}
		}
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
