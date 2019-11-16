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
		case "insertall" : insertAllCommande(stCommandaCouper) ;
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
		System.out.println("[Fonction createRelation]");

		RelDef reldef = new RelDef (nomRelation, typeCol); 
		
		// On calcul le recordSize et le nb de slot count et slotCount avec les donnees qu'on a	
		// Calcul de la taille du record
		
		int recordSize = calculRecordSize(reldef);
		reldef.setRecordSize(recordSize);
		int slotCount = calculSlotCount(reldef);
		reldef.setSlotCount(slotCount);
		System.out.println("Taille d'un record de cette relation : "+ recordSize);
		System.out.println("La taille d'une page est de "+ Constants.PAGE_SIZE);
		System.out.println("On peut saisir "+ slotCount + " records sur une page");
		
		// On creer mtn cette nouvelle relation avec la taille du record et le nb de slot
		reldef = new RelDef(nomRelation, typeCol, DBDef.getCompteurRelation(), recordSize, slotCount);
		DBDef.getInstance().addRelationInRelDefTab(reldef);
		System.out.println("Affichage du compte (bis) : " + DBDef.getCompteurRelation());
		
		// On creer le heapFile
		System.out.println("Affichage du relDef : " + reldef.toString());
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
		System.out.println("----- COMMANDE CREATE -----");
		String nomRelation = new String();
		int nbCol = 0;
		List<String> typeCol = new ArrayList<String>();
		int j = 0;
		
		try {
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
		} catch(NumberFormatException e) {
			System.err.println("[Attention] Un element de la commande n'a pas ete saisie");
			System.exit(-1);
		}
		
		System.out.print("La relation cree est la suivante : ");
		for (int i = 0; i < typeCol.size(); i++) {
			System.out.print(typeCol.get(i) + " ");
		}
		
		System.out.println(); System.out.println();
		System.out.println("----- INFORMATION SUR LA RELATION CREEE  -----");
		RelDef relDefcree = createRelation(nomRelation, nbCol, typeCol);
		DBDef.getInstance().addRelationInRelDefTab(relDefcree);
		System.out.println();
		System.out.println("----- FIN COMMANDE CREATE -----");
	}
	
	/**
	 * Remet a 0 le programme
	 * TODO s'occuper de BufferManager
	 */
	public void cleanCommande() {
		
		//TODO faire en sorte qu'on lise le catalogue
		String path = new String("src\\main\\resources\\DB\\");
		int compteurRelation = 3;
		
		
		
		for(int i = 0; i<compteurRelation; i++) {
			try {
				Files.deleteIfExists(Paths.get(path+"Data_"+i+".rf"));
			}
			catch(NoSuchFileException e) {
				System.err.println("No such file existed : "+DiskManager.getInstance().getPath()+i);
				break;
				//On quitte la boucle car il n y a plus de fichiers
			}
			catch(IOException e) {
				System.err.println("Erreur IO");
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
	 * Cette commande demande l'inserttion d'un record dans une
	 *  relation, en indiquant les valeurs (pour chaque 
	 *  colonne) du record et le nom de la relation.
	 * @param commande
	 */
	public void insertCommande(StringTokenizer commande) {
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
	 * Cette commande demande l�insertion de plusieurs records dans une relation.
	 * Les valeurs des records sont dans un fichier csv : 1 record par ligne, avec la virgule comme
	 * s�parateur.
	 * On suppose que le fichier se trouve � la racine de votre dossier projet (au m�me niveau donc que
	 * les sous-r�pertoires Code et DB).
	 * @param commande
	 */
	public void insertAllCommande(StringTokenizer commande) {
		String relName = commande.nextToken();
		
		String nomFichierCSV = commande.nextToken();
		String path = new String("src" + File.separator + "main" + 
						File.separator + "resources" + File.separator );
		
		FileReader readFile = null ;
		try {
			//dans ce fichier lire les element et les classe selon la reldef
			readFile = new FileReader(path+nomFichierCSV);
			
		} catch (FileNotFoundException e) {
			System.err.println("Le fichier CSV n'a pas �t� trouv�");
		} finally {
			try {
				readFile.close();
			} catch (IOException e) {
				System.err.println("Erreur I/O pour le fichier CSV");
				e.printStackTrace();
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
				insertCommande(uneLigneInsert);
			}
		} catch (IOException e) {
			System.out.println("Erreur I/O par rapport au contenu du fichier CSV");
		} finally {
			
			try {
				br.close();
				readFile.close();
			} catch (IOException e) {
				System.out.println("Erreur I/O � la fermeture des readers");
			}
		}
	}
	
	public void selectAllCommande(StringTokenizer commande ) {
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
	
	public void selectCommande(StringTokenizer commande) {
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
