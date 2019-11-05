package main.java.dbms_lyz;

import java.io.IOException;
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
	
	public void insert(StringTokenizer commande) {
		commande.nextToken();
		//ici le deuxieme element du token = relName
		String relName = commande.nextToken();
		
		List<String> typeCol = new ArrayList<String>();
		List<String> valeurs = new ArrayList<String>();
		/**
		 * TODO
		 */
		RelDef relDef = null ;
		// relDef = methodeQuiChercheLeRelDef() ;
		while(commande.hasMoreTokens()) {
			valeurs.add(commande.nextToken());
		}
		//récup la liste des heapfiles
//		List <HeapFile> heapFiles = (List<HeapFile>) FileManager.getInstance().getheapFiles();
		/**
		 * Acces au heapFile
		 */
		
		HeapFile heapFile = new HeapFile(relDef);
		
		Record record ;
		
		//boucle pour chaque heapfile de HeapFiles si le relname c bon
		//a la fin on insert le record 
//		FileManager.insertRecordInRelation(record, relName);
		
	}
	
	public void selectAll(StringTokenizer commande ) {
		List<Record> listRecords = FileManager.getInstance().selectAllFromRelation(commande.toString());
		System.out.print("Affichage des records de "+commande.toString());
		
		for(Record r : listRecords) {
			r.affiche();
		}
		
		System.out.println("Nombre de records : "+ listRecords.size());
	}
	
}
