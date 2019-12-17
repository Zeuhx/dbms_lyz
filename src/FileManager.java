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
		if(!DBDef.getInstance().getRelDefTab().isEmpty()) {
//			System.out.println("La table des relations contient " + DBDef.getInstance().getRelDefTab().size() + " relations");
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
		hf.createNewOnDisk();		// Attention --> si on supprime, probleme de insert
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
			if(hf.getRelDef().getNomRelation().equals(relName) && continu) {
				rid = hf.insertRecord(record);
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
							r.readFromBuffer(pageBuffer, positionSlot);
							listRecord.add(r);
						}
					}
					BufferManager.getInstance().freePage(new PageId(i, hf.getRelDef().getFileIdx()), false);
				}
			}
		}
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
	
	public List<String> joinPageOriented2Relation(int indexColRel1, int indexColRel2, PageId page1, PageId page2, HeapFile heapFileRel1, HeapFile heapFileRel2){
		// Maintenant qu'on a nos relations et les pages qu'on souhaite, on verifie qu'elle existe bien
		
		List<Record> listeDeRecord_relation1 = new ArrayList<>();
		List<Record> listeDeRecord_relation2 = new ArrayList<>();
		List<String> listeDeValeur = new ArrayList<>();
		
		/**
		 * On affecte a la liste de record les record de la page
		 * On fait une recherche en parallele pour evite la consommation inutile de ressources
		 */

		// On recupere la page qu'on souhaite
		ByteBuffer pageBufferRel1 = BufferManager.getInstance().getPage(page1); // get
		
		// Parcours pour recuper les records de la relation 1 d'une page qu'on ajoute dans la liste
		for(int compteurRecord = 0; compteurRecord<heapFileRel1.getRelDef().getSlotCount(); compteurRecord +=Byte.BYTES) {
			if(pageBufferRel1.get(compteurRecord) == (byte) 1) {
				int positionSlot = heapFileRel1.getRelDef().getSlotCount() + compteurRecord * heapFileRel1.getRelDef().getRecordSize();
				Record r = new Record(heapFileRel1.getRelDef());
				r.readFromBuffer(pageBufferRel1, positionSlot);
				listeDeRecord_relation1.add(r);
			}
		}
		BufferManager.getInstance().freePage(page1, false); // free
	
		// Recupere une page de record de la relation 2

		// On recupere la page qu'on souhaite
		ByteBuffer pageBufferRel2 = BufferManager.getInstance().getPage(page2); // get
		
		// Parcours pour recuper les records de la relation 2 d'une page qu'on ajoute dans la liste
		for(int compteurRecord = 0; compteurRecord<heapFileRel2.getRelDef().getSlotCount(); compteurRecord +=Byte.BYTES) {
			if(pageBufferRel2.get(compteurRecord) == (byte) 1) {
				int positionSlot = heapFileRel2.getRelDef().getSlotCount() + compteurRecord * heapFileRel2.getRelDef().getRecordSize();
				Record r = new Record(heapFileRel2.getRelDef());
				r.readFromBuffer(pageBufferRel2, positionSlot);
				listeDeRecord_relation2.add(r);
			}
		}
		
		BufferManager.getInstance().freePage(page2 , false);	// free

	
		// Si les deux liste ne sont pas vide , on les croises
		if( (!listeDeRecord_relation1.isEmpty()) && (!listeDeRecord_relation2.isEmpty())) {
			// -1 la premiere colonne correspond a l'index 0 d'une liste
			listeDeValeur = fusion2Relation(listeDeRecord_relation1, indexColRel1-1, listeDeRecord_relation2, indexColRel2-1);
		}
		return listeDeValeur;
	}
	
	private List<String> fusion2Relation(List<Record> lr1, int indexColRel1, List<Record> lr2, int indexColRel2){
		List<String> listeDeValeur = new ArrayList<>();
		
		// Apres avoir recuperer les records des pages maintenant en parametre, on cherche a comparer un par un les records entre eux
		for(Record record1 : lr1) {
			// Un record de liste de relation1
			String colValueRecord1 = record1.getValues().get(indexColRel1) ;
			
			for(Record record2 : lr2) {
				// Un record de liste de relation2
				String colValueRecord2 = record2.getValues().get(indexColRel2) ;
				
				// Croisement des deux relations
				if(colValueRecord1.equals(colValueRecord2)) {
					
					// On cree la chaine de caractere qui representera le croisement des deux relations qu'on ajoutera a une liste
					StringBuilder build = new StringBuilder();
					for(String s : record1.getValues()) {
						build.append(s + " ; ");
					}
					for(String s : record2.getValues()) {
						build.append(s + " ; ");
					}
					listeDeValeur.add(build.toString());
				}
			}
		}
		return listeDeValeur;
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
		BufferManager.getInstance().flushAll();
	}
}
