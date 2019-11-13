package main.java.dbms_lyz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author LYZ
 *
 */
public class DBManager {
	
	private static DBManager INSTANCE = null;
	
	public DBManager() {
	}
	
	public DBManager getInstance() {
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
		FileManager.getInstance().init();
	}

	/**
	 * Fait appel au finish de DBDef seulement
	 */
	public static void finish() {
		DBDef.getInstance().finish();
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
		case "create" : create(stCommandaCouper) ;
		break ;
		case "clean" : clean() ;
		break ;
		case "insert" : insert(stCommandaCouper) ;
		break ;
		case "insertall" : insertAll(stCommandaCouper) ;
		}

		/**
		 * On coupe le StringTokenizer en plusieurs partie : On compare le premier
		 * element avec create Le deuxieme element sera stocker pour etre passer en
		 * argument dans la fonction Le troisieme element sera convertit en int Et a
		 * partir du 4eme element on transforme en sous une liste
		 */

		
	}

	/**
	 * Methode qui creer une relation de type RelDef avec son nom, le nb de col , et les types de col
	 *  
	 * @param nomRelation
	 * @param nombreCol
	 * @param typeCol     
	 * @return, une relation RelDef conformement aux arguments et ajoute dans DBDef

	 */
	public RelDef createRelation(String nomRelation, int nombreCol, List<String> typeCol) {
		// appel du 1er constructeur 
		RelDef reldef = new RelDef (nomRelation, typeCol); 

		/**
		 * On initialise le recordSize et slotCount car le 1er constructeur 
		 */
		reldef.setRecordSize(recordSize(reldef));
		reldef.setSlotCount(slotCount(reldef));
		DBDef.getInstance().addRelation(reldef);
		
		// Calcul de la taille du record
		int recordSize = recordSize(reldef);
		int slotCount = slotCount(reldef);
		
		// WARNING : TODO on connait pas le fileIdx -> il faudra recuperer le fileIdx
		reldef = new RelDef(nomRelation, typeCol, 0, recordSize, slotCount); 
		DBDef.getInstance().addRelation(reldef);
		FileManager.getInstance().createRelationFile(reldef);
		return (reldef);
		
	}
	
	/**
	 * On calcule la taille d'un record dans une page
	 * @return  : ici qu'on calcule recordSize 
	 */
	
	public int recordSize(RelDef rd) {
		int recordSize = 0;
		for(String col : rd.getTypeCol()) {
			if(col.equals("int")) {
				recordSize += 4;
			}
			else if(col.equals("float")) {
				recordSize += 4;
			}
			else {
				String size = col.substring(6);
				recordSize += Integer.parseInt(size)*2;
			}	
		}
		/*
		 * TODO a supprimer car non necessaire
		 * //recordSize = taille record * le nb de record qui on la taille fixe
		 * recordSize *= rd.getRecordLenght();
		 * Parce que on a besoin que de la taille de 1 seul record
		 */
		return recordSize;
		}
	
	/**
	 * Calcul du nombre de slot qu'on peut avoir sur une page 
	 * Donc division de la taille de la page par la taille d'un record + 1 pour la bytemap qui prend 1
	 * @param rd la relation concernee
	 * @return  : ici qu'on calcule slotCount
	 */
	public int slotCount(RelDef rd) {
		return Constants.PAGE_SIZE/(rd.getRecordSize()+1);
	}
	
	
	public void create(StringTokenizer commande) {
		String nomRelation = new String("");
		int nbCol = 0;
		List<String> typeCol = new ArrayList<String>();
		int j = 0;
		
		for (int i = 1; commande.hasMoreElements(); i++) {
			if(i == 1) {
				nomRelation = commande.nextToken();
			}
			if(i == 2) {
				nbCol = Integer.parseInt(commande.nextToken());
			}
			if(i > 2) {
				while(j < nbCol) {
					typeCol.add(commande.nextToken());
					j++;
				}
			}
		}

		/**
		 * Verification
		 */
		for (int i = 0; i < typeCol.size(); i++) {
			System.out.println(typeCol.get(i) + ", ");
		}

		/**
		 * Appel de la fonction
		 */
		createRelation(nomRelation, nbCol, typeCol);
	}
	
	
	/**
	 * Remet a 0 le programme
	 * TODO s'occuper de BufferManager
	 */
	public void clean() {
		for(int i = 0; i<DBDef.getListSize(); i++) {
			try {
				Files.deleteIfExists(Paths.get(DiskManager.getInstance().getPath()+i));
			}
			catch(NoSuchFileException e) {
				System.out.println("No such file existed : "+DiskManager.getInstance().getPath()+i);
				break;
				//On quitte la boucle car il n y a plus de fichiers
			}
			catch(IOException e) {
				System.out.println("Erreur IO");
			}
		}
		DBDef.getInstance().reset();
		FileManager.getInstance().reset();
		
		/**
		 * 
		 * S'occuper de BufferManager
		 */
	}
	/**
	 * Cette commande demande l’insertion d’un record dans une
	 *  relation, en indiquant les valeurs (pour chaque 
	 *  colonne) du record et le nom de la relation.
	 * @param commande
	 */
	public void insert(StringTokenizer commande) {
		String relName = commande.nextToken();
		List<String> valeurs = new ArrayList<String>(); //list valeurs de chaque colonne
		
		//stock les valeur dans la liste
		while(commande.hasMoreTokens()) {
			valeurs.add(commande.nextToken());
		}
		
		//accede au Heapfiles pour avoir la liste
		List <HeapFile> heapFiles = (ArrayList<HeapFile>) FileManager.getInstance().getHeapFiles();

		//parcourir Heapfiles pour comparer les relName
		for(int i=0; i<heapFiles.size(); i++) {
			RelDef reldef = heapFiles.get(i).getRelDef() ; 
			if(reldef.getNomRelation().equals(relName)) {
				
				//ecriture du record dans la relation
				Record r = new Record(reldef, valeurs);
				heapFiles.get(i).insertRecord(r);
			}
		}
	}
	
	/**
	 * Cette commande demande l’insertion de plusieurs records dans une relation.
	 * Les valeurs des records sont dans un fichier csv : 1 record par ligne, avec la virgule comme
	 * séparateur.
	 * On suppose que le fichier se trouve à la racine de votre dossier projet (au même niveau donc que
	 * les sous-répertoires Code et DB).
	 * @param commande
	 */
	public void insertAll(StringTokenizer commande) {
		String relName = commande.nextToken();
		List<String> valeurs = new ArrayList<String>(); //list valeurs de chaque colonne
		
		String nomFichierCSV = commande.nextToken();
		String path = new String("src" + File.separator + "main" + 
						File.separator + "resources" + File.separator );
		
		FileReader readFile = null ;
		try {
			//dans ce fichier lire les element et les classe selon la reldef
			readFile = new FileReader(path+nomFichierCSV);
			
		} catch (FileNotFoundException e) {
			System.err.println("Le fichier CSV n'a pas été trouvé");
		} finally {
			try {
				readFile.close();
			} catch (IOException e) {
				System.err.println("Erreur I/O pour le fichier CSV");
				e.printStackTrace();
			}
		}
		
		List<HeapFile> heapFiles = FileManager.getInstance().getHeapFiles();
		
		HeapFile leBonHeapFile;
		
		//parcourir Heapfiles pour comparer les relName
		for(int i=0; i<heapFiles.size(); i++) {
			RelDef reldef = heapFiles.get(i).getRelDef() ; 
			if(reldef.getNomRelation().equals(relName)) {
				leBonHeapFile = heapFiles.get(i);
			} else {
				//on entrera jamais car on part du principe que relName existe 
				leBonHeapFile = new HeapFile(null);
			}
		}	
		
		/**
		 * cree un string pour stCommande pour faire appel a insert()
		 * on recup les contenus de record
		 */		
		BufferedReader br = new BufferedReader(readFile);
		String uneLineDeCSV;
		StringTokenizer uneLigneInsert = new StringTokenizer("");
		
		//boucle tant qu'il existe des lignes
		try {
			while(br.readLine() != null) {
				uneLineDeCSV = new String (relName+","+br.readLine());
				
				//contenu d'une ligne de csv pour la command insert()
				uneLigneInsert = new StringTokenizer(uneLineDeCSV, ",");
				insert(uneLigneInsert);
			}
		} catch (IOException e) {
			System.out.println("Erreur I/O par rapport au contenu du fichier CSV");
		}
	}
	
	public void selectAll(StringTokenizer commande ) {
		
		String nomRelation = "";
		int compteurRecord = 0;
		
		commande.nextElement();
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
	
	public void select(StringTokenizer commande) {
		
		String nomRelation = "";
		int colonne;
		String valeur = "";
		
		commande.nextElement();
		nomRelation = commande.nextToken();
		colonne = (int) commande.nextElement();
		valeur = commande.nextToken(); 
		
		List<Record> listRecords = FileManager.getInstance().selectAllFromRelation(nomRelation);
		
		for(Record r : listRecords) {
			
			List<String> values = r.getValues();
			
			if(values.get(colonne).equals(valeur)) {
				StringBuffer stringBuffRecord = new StringBuffer("");
				
				for(String s : r.getValues()) {
					stringBuffRecord.append(s);
					stringBuffRecord.append(" ; ");
				}
				
				String stringRecord = stringBuffRecord.substring(0, stringBuffRecord.toString().length()-3);
				System.out.println(stringRecord);
			}
		}
		
	}
}
