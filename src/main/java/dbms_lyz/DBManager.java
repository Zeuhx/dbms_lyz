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
	public static void init() {
		DBDef.getInstance().init();
		//FileManager.getInstance().init();
	}

	/**
	 * Fait appel au finish de DBDef seulement
	 */
	public static void finish() {
		DBDef.getInstance().finish();
		BufferManager.getInstance().flushAll();
	}

	/**
	 * @param commande, la commande qu'on va saisir Methode qui permet d'executer
	 *                  une commande entrer en parametre sous forme d'un String
	 */
	public void processCommand(String commande) {
		StringTokenizer stCommandaCouper;

		stCommandaCouper = new StringTokenizer(commande, " ");
		String typeCommande = stCommandaCouper.nextToken() ;
		switch(typeCommande) {
		case "create" : createCommande(stCommandaCouper) ;
		break ;
		case "clean" : cleanCommande() ;
		break ;
		case "insert" : insertCommande(stCommandaCouper) ;
		break ;
		case "insertAll" : case "insertall" : insertAllCommande(stCommandaCouper) ;
		break ;
		case "select" : selectCommande(stCommandaCouper);
		break ;
		case "selectAll" : case "selectall" : selectAllCommande(stCommandaCouper);
		break ;
		case "delete" : deleteCommande(stCommandaCouper) ;
		break ;
		case "exit" : exitCommande(stCommandaCouper) ;
		break ;
		default : System.err.println("commande incorrect");
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
	public RelDef createRelation(String nomRelation, int nombreCol, List<String> typeCol) {
		RelDef reldef = new RelDef (nomRelation, typeCol); 
		
		// On calcul le recordSize et le nb de slot count et slotCount avec les donnees qu'on a	
		// Calcul de la taille du record
		
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
	public int calculRecordSize(RelDef rd) {
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
	public int calculSlotCount(RelDef rd) {
		return Constants.PAGE_SIZE/(rd.getRecordSize()+1);
	}
	
	/**
	 * Lit la commande et creer la relation
	 * @param commande la commande qui va creer la relation
	 */
	public void createCommande(StringTokenizer commande) {
		String nomRelation = new String();
		int nbCol = 0;
		List<String> typeCol = new ArrayList<String>();
		
		try {
			for (int i = 1; commande.hasMoreElements(); i++) {
				if(i == 1) {
					nomRelation = commande.nextToken();
				}
				if(i == 2) {
					nbCol = Integer.parseInt(commande.nextToken());
				}
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
		for (int i = 0; i < typeCol.size(); i++) {
			System.out.print(typeCol.get(i) + " ");
		}
		
		RelDef relDefcree = createRelation(nomRelation, nbCol, typeCol);
		System.out.println("Affichage X1 : relDef cree " + relDefcree.toString());
		DBDef.getInstance().addRelationInRelDefTab(relDefcree);
	}
	
	/**
	 * Remet a 0 le programme
	 * Efface le contenu du catalogue.def
	 * Supprime les fichiers Data
	 */
	public void cleanCommande(){
		System.out.println("Affichage X21 : Nombre de relation en cours: " + DBDef.getInstance().getCompteurRelation());
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
		DBDef.getInstance().reset();
		FileManager.getInstance().reset();
	}
	
	/**
	 * Cette commande demande l'inserttion d'un record dans une
	 *  relation, en indiquant les valeurs (pour chaque 
	 *  colonne) du record et le nom de la relation.
	 * @param commande
	 */
	public void insertCommande(StringTokenizer commande) {
		String relName = commande.nextToken();
		List<String> valeurs = new ArrayList<String>(); //list valeurs de records

		while(commande.hasMoreTokens()) {
			valeurs.add(commande.nextToken());
		}
		
		List <HeapFile> heapFiles = (ArrayList<HeapFile>) FileManager.getInstance().getHeapFiles();

		//parcourir Heapfiles pour comparer les relName
		for(int i=0; i<heapFiles.size(); i++) {
			RelDef reldef = heapFiles.get(i).getRelDef() ; 
			if(reldef.getNomRelation().equals(relName)) {
				//ecriture du record dans la relation
				Record r = new Record(reldef, valeurs);
				FileManager.getInstance().insertRecordInRelation(r, reldef.getNomRelation());
			}
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
	public void insertAllCommande(StringTokenizer commande) {
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
				insertCommande(new StringTokenizer(sbValues.toString()));
			} 
		}catch (FileNotFoundException e) {
			System.err.println("Le fichier CSV n'a pas ete trouve");
		}catch (IOException e) {
			System.err.println("Erreur I/O par rapport au contenu du fichier CSV");
		}
	}
	
	public void selectAllCommande(StringTokenizer commande) {
		String nomRelation = "";
		int compteurRecord = 0;
		nomRelation = commande.nextToken();
		
		List<Record> listRecords = FileManager.getInstance().selectAllFromRelation(nomRelation);
		for(Record r : listRecords) {
			StringBuffer stringBuffRecord = new StringBuffer("");
			for(String s : r.getValues()) {
				stringBuffRecord.append(s);
				stringBuffRecord.append(" ; ");
			}
			String stringRecord = stringBuffRecord.substring(0, stringBuffRecord.toString().length()-3);
			System.out.println(stringRecord);
			compteurRecord ++;
		}
		System.out.println("Total Records : "+ compteurRecord);
		System.out.println("Nombre de records : "+ listRecords.size());

	}
	
	public void selectCommande(StringTokenizer commande) {
		String nomRelation = commande.nextToken();
		String colonne = commande.nextToken();
		String valeur = commande.nextToken(); 
		int column = Integer.parseInt(colonne);
		
		List<Record> listRecords = FileManager.getInstance().selectAllFromRelation(nomRelation);
		
		for(Record r : listRecords) {
			List<String> values = r.getValues();
			//column-1 car l'index commence a partir de 0
			if(values.get(column-1).equals(valeur)) {
				System.out.println(r); 
			}
		}
	}
	
	public void exitCommande(StringTokenizer commande) {
		DBManager.finish();
	}
	
	/**
	 * Supprime le record
	 * Remplace par 0 le contenu de la ligne
	 * Remplace le 1 de la byteMap par 0
	 * Incremente le slotCount de la page sur le headerPage
	 * @param commande
	 */
	public void deleteCommande(StringTokenizer commande){
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
		System.out.println(" Nombre total de record supprime : "+compteurRecordSup);
	
	}
	
}
