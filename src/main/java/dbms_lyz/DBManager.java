package main.java.dbms_lyz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * 
 * @author LYZ
 *
 */
public class DBManager {
	
	private static DBManager INSTANCE = null;
	
	public DBManager() { }
	
	public static DBManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DBManager();
		}
		return INSTANCE;
	}

	/**
	 * Fait appel au init de DBDef seulement
	 */
	public void init() {
		DBDef.getInstance().init();
		FileManager.getInstance().init();
	}

	/**
	 * Fait appel au finish de DBDef seulement
	 */
	public void finish() {
		DBDef.getInstance().finish();
		BufferManager.getInstance().finish();
	}

	/**
	 * @param commande, la commande qu'on va saisir Methode qui permet d'executer
	 *                  une commande entrer en parametre sous forme d'un String
	 */
	public void processCommand(String commande) {
		StringTokenizer commandeSaisie;
		commandeSaisie = new StringTokenizer(commande, " ");
		String typeCommande = commandeSaisie.nextToken() ;
		switch(typeCommande.toLowerCase()) {
		case "create" : create(commandeSaisie) ;
		break ;
		case "clean" : clean() ;
		break ;
		case "insert" : insert(commandeSaisie) ;
		break ;
		case "insertall" : insertAll(commandeSaisie) ;
		break ;
		case "select" : select(commandeSaisie);
		break ;
		case "selectall" : selectAll(commandeSaisie);
		break ;
		case "join" : join(commandeSaisie) ;
		break ;
		case "delete" : delete(commandeSaisie) ;
		break ;
		case "exit" : exit(commandeSaisie) ;
		break ;
		default : System.err.println("La commande n'est pas reconnu, veuillez resaisir");
		break ;
		}
	}

	/**
	 * Methode qui creer une relation de type RelDef avec son nom, le nb de col , et les types de col
	 * On ajotuera cette relation par la suite lors de l'appel 
	 * 
	 * @param nomRelation
	 * @param nombreCol
	 * @param typeCol     
	 * @return, une relation RelDef conformement aux arguments et ajoute dans DBDef
	 */
	private RelDef createRelation(String nomRelation, int nombreCol, List<String> typeCol) {
		RelDef reldef = new RelDef (nomRelation, typeCol); 
		
		// On calcul le recordSize et le nb de slot count et slotCount avec les donnees qu'on a	
		int recordSize = calculRecordSize(reldef);
		reldef.setRecordSize(recordSize);
		int slotCount = calculSlotCount(reldef);
		reldef.setSlotCount(slotCount);
		
		// On creer mtn cette nouvelle relation avec la taille du record et le nb de slot
		reldef = new RelDef(nomRelation, typeCol, DBDef.getInstance().getCompteurRelation(), recordSize, slotCount);
		FileManager.getInstance().createHeapFileWithRelation(reldef);
		return (reldef);
	}

	/**
	 * On calcule la taille d'un record dans une page
	 * @return  : ici qu'on calcule recordSize 
	 */
	private int calculRecordSize(RelDef rd) {
		int recordSize = 0;
		for(String col : rd.getTypeCol()) {
			if(col.equals("int")) {
				recordSize += 4;
			}
			else if(col.equals("float")) {
				recordSize += 4;
			}
			else {
				try {
					String size = col.substring(6);
					recordSize += Integer.parseInt(size)*2;
				} catch(NumberFormatException e) {
					System.err.println("Il n'y a pas la taille du String : le format doit etre stringx avec x un nombre");
				}
			}	
		}
		return recordSize;
	}
	
	/**
	 * Calcul du nombre de slot qu'on peut avoir sur une page 
	 * Donc division de la taille de la page par la taille d'un record + 1 pour la bytemap qui prend 1
	 * @param rd la relation concernee
	 * @return  : ici qu'on calcule slotCount
	 */
	private int calculSlotCount(RelDef rd) {
		return Constants.PAGE_SIZE/(rd.getRecordSize()+1);
	}
	
	/**
	 * TODO a completer
	 * Lit la commande et creer la relation
	 * @param commande la commande qui va creer la relation
	 */
	private void create(StringTokenizer commande) {
		String relName = commande.nextToken();
		int nbCol = Integer.parseInt(commande.nextToken()) ;
		List<String> typeCol = new ArrayList<String>();
		
		System.out.println("La relation " + relName + " a ete cree avec succes");
		
		try {
			for (int i = 3; commande.hasMoreElements(); i++) {
				if(i > 2) {
					for(int j=0 ; j<nbCol ; j++) {
						typeCol.add(commande.nextToken());
						if(j>nbCol) {
							System.err.println("[Attention] Vous avez saisie plus d'element qu'il ne faut, "
									+ "les elements en trop n'ont pas ete prise en compte");
						}
					}
				}
			}
		} catch(NumberFormatException e) {
			System.err.println("[Attention] Un element de la commande n'a pas ete saisie");
		} catch(NoSuchElementException e) {
			System.err.println("[Attention] Il vous manque des elements a remplir, le programme s'arrete");
			System.exit(0);
		}
		
		RelDef relDefcree = createRelation(relName, nbCol, typeCol);
		System.out.println("\n\t[Info sur la relation cree]" + relDefcree.toString());
		DBDef.getInstance().addRelationInRelDefTab(relDefcree);
	}
	
	/**
	 * Cette commande demande l'inserttion d'un record dans une
	 *  relation, en indiquant les valeurs (pour chaque 
	 *  colonne) du record et le nom de la relation.
	 *  TODO A detailler
	 * @param commande
	 */
	private void insert(StringTokenizer commande) {
		String relName = commande.nextToken();
		List<String> valeurs = new ArrayList<String>(); //list valeurs de records
		boolean relationExistante = false ;
		//System.out.println("Les valeurs ont bien ete saisie dans la relation " + relName);
		
		while(commande.hasMoreTokens()) {
			valeurs.add(commande.nextToken());
		}
		
		List <HeapFile> heapFiles = (ArrayList<HeapFile>) FileManager.getInstance().getHeapFiles();

		//parcourir Heapfiles pour comparer les relName
		for(int i=0; i<heapFiles.size(); i++) {
			RelDef reldef = heapFiles.get(i).getRelDef() ; 
			if(reldef.getNomRelation().equals(relName)) {
				relationExistante = true ;
				//ecriture du record dans la relation
				Record r = new Record(reldef, valeurs);
				FileManager.getInstance().insertRecordInRelation(r, reldef.getNomRelation());
			}
		}
		if(!relationExistante) {
			System.err.println("Vous essayez d'inserer dans une relation qui n'existe pas, veuillez la cree auparavant");
		}
		
	}
	
	/**
	 * Cette commande demande l insertion de plusieurs records dans une relation.
	 * Les valeurs des records sont dans un fichier csv : 1 record par ligne, avec la virgule comme
	 * separateur.
	 * On suppose que le fichier se trouve a la racine de votre dossier projet (au meme niveau donc que
	 * les sous-repertoires Code et DB).
	 * @param commande
	 */
	private void insertAll(StringTokenizer commande) {
		String relName = commande.nextToken();
		
		String nomFichierCSV = commande.nextToken();
		String path = new String("src" + File.separator + "main" + 
						File.separator + "resources" + File.separator );
	
		try(FileReader readFile = new FileReader(path+nomFichierCSV)) {
			BufferedReader br = new BufferedReader(readFile);
			String ligne;
			while((ligne = br.readLine() )!= null) {
				
				String[]values = ligne.split(",");
				StringBuffer sbValues = new StringBuffer();
				sbValues.append(relName + " ");
				for(int i=0; i<values.length; i++) {
					sbValues.append(values[i]);
					sbValues.append(" ");
				}
				insert(new StringTokenizer(sbValues.toString()));
			} 
		}catch (FileNotFoundException e) {
			System.err.println("Le fichier CSV n'a pas ete trouve");
		}catch (IOException e) {
			System.err.println("Erreur I/O par rapport au contenu du fichier CSV");
		}
		System.out.println("Tous les tuples du fichier "+ nomFichierCSV + " ont ete ajoute a " + relName);
	}
	
	/**
	 * TODO description
	 * @param commande
	 */
	private void selectAll(StringTokenizer commande) {
		String nomRelation = "";
		int compteurRecord = 0;
		nomRelation = commande.nextToken();
		
		List<Record> listRecords = FileManager.getInstance().selectAllFromRelation(nomRelation);
		for(Record r : listRecords) {
			StringBuffer stringBuffRecord = new StringBuffer("[SELECTALL] Affichage des valeurs : ");
			for(String s : r.getValues()) {
				stringBuffRecord.append(s);
				stringBuffRecord.append(" | ");
			}
			String stringRecord = stringBuffRecord.substring(0, stringBuffRecord.toString().length()-3);
			System.out.println(stringRecord);
			compteurRecord ++;
		}
		System.out.println("\n\tTotal Records : "+ compteurRecord);
	}
	
	/**
	 * TODO description
	 * @param commande
	 */
	private void select(StringTokenizer commande) {
		String nomRelation = commande.nextToken();
		String colonne = commande.nextToken();
		String valeur = commande.nextToken(); 
		int column = Integer.parseInt(colonne);
		int cptRelation = 0 ;
		
		List<Record> listRecords = FileManager.getInstance().selectAllFromRelation(nomRelation);
		
		for(Record record : listRecords) {
			List<String> values = record.getValues();
			//column-1 car l'index commence a partir de 0
			if(values.get(column-1).equals(valeur)) {
				System.out.println("[SELECT] Affichage des valeurs : " + record.toString().substring(0, record.toString().length()-3)); 
				cptRelation++;
			}
		}
		System.out.println("\n\tTotal Records : " + cptRelation);
	}
		
	/**
	 * TODO description pour join : produit cartesien puis selection
	 * On charge la premiere page de la relation R
	 * On charge la premiere page de la relation S
	 * On ne fait pas de sélection (select)
	 * On compare tuple par tuple entre R et S sur ces 2 pages
	 * On parcours les pages de S tant qu'il y en a
	 * On passe à la deuxième page de R 
	 * etc...
	 */
	private void join(StringTokenizer commande) {
		String relName1 = commande.nextToken();
		String relName2 = commande.nextToken();
		int indiceCol1 = (int) Integer.valueOf(commande.nextToken());
		int indiceCol2 = (int) Integer.valueOf(commande.nextToken());
		
		int compteurRelation = 0 ;

		RelDef reldef1 = null ;
		RelDef reldef2 = null ;
		for(RelDef r : DBDef.getInstance().getRelDefTab()) {
			if(r.getRelName().equals(relName1)) {
				reldef1 = r;
			}
			if(r.getRelName().equals(relName2)) {
				reldef2 = r;
			}
		}
		
		if(reldef1 == null || reldef2 == null ) {
			System.err.println("Il y a une relation qui n'existe pas");
		}
		else {
			// Nb de page pour rel1
			ByteBuffer headerBuffer1 = BufferManager.getInstance().getPage(new PageId(0, reldef1.getFileIdx()));
			int nbPageRel1 = headerBuffer1.getInt(0);
			BufferManager.getInstance().freePage(new PageId(0, reldef1.getFileIdx()), false);
			
			// Nb de page pour rel2
			ByteBuffer headerBuffer2 = BufferManager.getInstance().getPage(new PageId(0, reldef2.getFileIdx()));
			int nbPageRel2 = headerBuffer2.getInt(0);
			BufferManager.getInstance().freePage(new PageId(0, reldef2.getFileIdx()), false);
//			String affichage ;
			
//			try(Scanner scan = new Scanner(System.in)){
//				System.out.print("Voulez vous afficher les resultats de la jointure : ");
//				affichage = scan.nextLine();
//			}
			
			for(int indicePageRel1 = 1 ; indicePageRel1<=nbPageRel1 ; indicePageRel1++) {
				for(int indicePageRel2 = 1 ; indicePageRel2<=nbPageRel2 ; indicePageRel2++) {
					// On affecte une page a chaque fois (du coup la page est selectionnee)
					List<String> listeDeJoinDeUnTourDeBoucle = 
							FileManager.getInstance().joinPageOriented2Relation(indiceCol1, indiceCol2, indicePageRel1, indicePageRel2, searchHeapFile(relName1), searchHeapFile(relName2));
					
					
					// Le nombre total de record selectionnee correspond a la taille de la liste
					compteurRelation+=listeDeJoinDeUnTourDeBoucle.size() ;
					
					// Affichage des records 
//					if(affichage.equals("oui")) {
						for(String uneLigneJoin : listeDeJoinDeUnTourDeBoucle) {
							System.out.println("[JOIN] Affichage des records : " + uneLigneJoin.substring(0, uneLigneJoin.length()-3));
						}
//					}
				}
			}
			System.out.println("\n\tTotal Records : " + compteurRelation);
		}	
	}

	/**
	 * Remet a 0 le programme
	 * Efface le contenu du catalogue.def
	 * Supprime les fichiers Data
	 * TODO a completer
	 */
	private void clean(){
		BufferManager.getInstance().flushAll();
//		int compteurRelation = DBDef.getCompteurRelation() ;
		int cptDataFile=0;
		
		//recuperer les fichier commencant par "Data_" dans une listData
		File dir = new File(Constants.PATH);
		File [] foundFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".rf");
			}
		});
		//suppression des fichiers dans listData
		for (File file : foundFiles) {
			file.delete();
			cptDataFile ++;
		}
		if(cptDataFile > 0){
			System.out.println("Il y a "+cptDataFile+" fichier(s) supprime(s)");
		}
		System.out.println("Nombre de relation en cours : " + DBDef.getInstance().getCompteurRelation());
		DBDef.getInstance().reset();
		FileManager.getInstance().reset();
	}
	
	/**
	 * Supprime le record
	 * Remplace par 0 le contenu de la ligne
	 * Remplace le 1 de la byteMap par 0
	 * Incremente le slotCount de la page sur le headerPage
	 * @param commande
	 */
	private void delete(StringTokenizer commande){
		String relName = commande.nextToken();
		String colonne = commande.nextToken();
		int numeroColonne = Integer.parseInt(colonne)-1;
		String valeurASup = commande.nextToken();
		RelDef reldef = null;
		int compteurRecordSup = 0;
		for(RelDef r : DBDef.getInstance().getRelDefTab()) {
			if(r.getRelName().equals(relName)) {
				reldef = r;
			}
		}
		if(reldef == null) {
			System.err.println("Cette relation n'existe pas");
		}
		else {
			ByteBuffer headerPage = BufferManager.getInstance().getPage(new PageId(0, reldef.getFileIdx()));
			int nbPage = headerPage.getInt(0);
			boolean headerPageModifiee = false;
			
			for(int i =1; i<=nbPage; i++) {
				boolean pageModifiee = false;
				ByteBuffer bufferPage = BufferManager.getInstance().getPage(new PageId(i, reldef.getFileIdx()));
				for(int positionSlot = 0; positionSlot < reldef.getSlotCount(); positionSlot++) {
					int byteMapSlot = bufferPage.get(positionSlot * Byte.BYTES);
					if(byteMapSlot == (byte) 1) {
						Record record = new Record(reldef);
						record.readFromBuffer(bufferPage, reldef.getSlotCount()+ positionSlot * reldef.getRecordSize());
						
						if(record.getValues().get(numeroColonne).equals(valeurASup)){
							bufferPage.put(positionSlot, (byte) 0);
							bufferPage.position(reldef.getSlotCount()+ positionSlot * reldef.getRecordSize());
							
							for(int j=0; j<reldef.getRecordSize(); j++) {
								bufferPage.put((byte) 0);
							}
							pageModifiee = true;

							int positionPageSlot = headerPage.getInt(Integer.BYTES + (i-1) * Integer.BYTES );
							int slotCount = headerPage.getInt(positionPageSlot)+1;
							headerPage.putInt(positionPageSlot, slotCount);
							headerPageModifiee = true;
							compteurRecordSup++;
						}
					}
				}
				BufferManager.getInstance().freePage(new PageId(i, reldef.getFileIdx()), pageModifiee);
			}
			BufferManager.getInstance().freePage(new PageId(0, reldef.getFileIdx()), headerPageModifiee);
		}
		System.out.println("\n\tTotal Records supprimes : "+compteurRecordSup);
	
	}
	
	public HeapFile searchHeapFile(String relName) {
		for(HeapFile hf : FileManager.getInstance().getHeapFiles()) {
			// Recupere une page de record de la relation 1
			if(hf.getRelDef().getNomRelation().equals(relName)) {
				return hf ;
			}
		}
		return null;
	}
	
	/**
	 * TODO description
	 * @param commande
	 */
	private void exit(StringTokenizer commande) {
		DBManager.getInstance().finish();
		System.out.println("----------- [BASE DE DONNEE - FIN] -----------");
	}

}
