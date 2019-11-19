package main.java.dbms_lyz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
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
		case "selectAll" : case "selectall" : selectAllCommande(stCommandaCouper);
		break ;
		case "exit" : exitCommande(stCommandaCouper) ;
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
//		System.out.println("Affichage X10 [Fonction createRelation]");
//		System.err.println("Erreur X5 : " + typeCol);
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
//		System.err.println("Erreur X6 : " + reldef.getTypeCol());
		// System.out.println("Affichage du compte (bis) : " + DBDef.getCompteurRelation());
		
		// On creer le heapFile
		// System.out.println("Affichage X19 - Affichage du relDef : " + reldef.toString());
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
						// TODO Pas placer comme il faut
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
			System.exit(-1);
		}
		System.out.print("La relation cree est la suivante : ");
		for (int i = 0; i < typeCol.size(); i++) {
			System.out.print(typeCol.get(i) + " ");
		}
		
		System.out.println(); System.out.println();
		System.out.println("----- INFORMATION SUR LA RELATION CREEE  -----");
		RelDef relDefcree = createRelation(nomRelation, nbCol, typeCol);
		System.out.println("Affichage X1 : relDef cree " + relDefcree.toString());
		DBDef.getInstance().addRelationInRelDefTab(relDefcree);
		System.out.println();
		System.out.println("----- FIN COMMANDE CREATE -----");
	}
	
	/**
	 * Remet a 0 le programme
	 * Efface le contenu du catalogue.def
	 * Supprime les fichiers Data
	 */
	public void cleanCommande(){
		System.err.println("Affichage X21 : Compteur relation de cleanCommande : " + DBDef.getCompteurRelation());
//		int compteurRelation = DBDef.getCompteurRelation() ;
		int cptDataFile=0;
		
		//npouvelle version
		//recuperer les fichier commencant par "Data_" dans une listData
		File dir = new File(Constants.PATH);
		File [] foundFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("Data");
			}
		});
		//suppression des fichiers dans listData
		for (File file : foundFiles) {
			System.out.println("Affichage Y2 : suppression de fichier Data ");
			file.delete();
			cptDataFile ++;
		}
		System.out.println(" "+cptDataFile+" fichier(s) supprime(s)");
		
		//ancienne version
//		for(int i = 0; i<compteurRelation; i++) {
//			try {
//				Files.deleteIfExists(Paths.get(Constants.PATH+"Data_"+i+".rf"));
//				System.out.println("Affichage X22 : Suppression des fichiers : "+ Constants.PATH+"Data_"+i+".rf");
//			}
//			catch(NoSuchFileException e) {
//				System.err.println("Il n'y a pas plus de fichier : "+DiskManager.getInstance().Constants.PATH()+i);
//				System.exit(-1);;
//				//On quitte la boucle car il n y a plus de fichiers
//			}
//			catch(IOException e) {
//				System.err.println("[Attention] Des fichiers viennent d'etre cree "
//						+ "et sont donc en cours d'utilisation par le systeme, "
//						+ "\nil ne peut pas etre supprimer, "
//						+ "pour cela il faut quitter le programme et faire la commande clean apres");
//			}
//		}
		
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
		System.out.println("Affichage X23 : Affichage values depuis insertCommande : " + valeurs);
		
		//accede au Heapfiles pour avoir la liste
		List <HeapFile> heapFiles = (ArrayList<HeapFile>) FileManager.getInstance().getHeapFiles();

		//parcourir Heapfiles pour comparer les relName
		for(int i=0; i<heapFiles.size(); i++) {
			System.out.println("Affichage X19 - Entre dans le insert");
			RelDef reldef = heapFiles.get(i).getRelDef() ; 
			System.err.println("Affichage X24 : Affichage du getRelDef de insertCommande : " + heapFiles.get(i).getRelDef());
			if(reldef.getNomRelation().equals(relName)) {
				//ecriture du record dans la relation
				System.out.println("Affichage X25 : Affichage des valeurs 2 : " + valeurs);
				Record r = new Record(reldef, valeurs);
				System.out.println("Affichage X26 : Affichage du record depuis Insert : " + r.toString());
				heapFiles.get(i).insertRecord(r);
			}
		}
	}
	
	/**
	 * Cette commande demande l�insertion de plusieurs records dans une relation.
	 * Les valeurs des records sont dans un fichier csv : 1 record par ligne, avec la virgule comme
	 * s�parateur.
	 * On suppose que le fichier se trouve � la racine de votre dossier projet (au meme niveau donc que
	 * les sous-r�pertoires Code et DB).
	 * @param commande
	 */
	public void insertAllCommande(StringTokenizer commande) {
		String relName = commande.nextToken();
		
		String nomFichierCSV = commande.nextToken();
		String path = new String("src" + File.separator + "main" + 
						File.separator + "resources" + File.separator );
		
		System.out.println("Affichage Y4 - nom du fichier recupere : " + nomFichierCSV);
		
		FileReader readFile = null ;
		try {
			//dans ce fichier lire les element et les classe selon la reldef
			readFile = new FileReader(path+nomFichierCSV);
		} catch (FileNotFoundException e) {
			System.err.println("Le fichier CSV n'a pas ete trouve");
		}	
		
		/**
		 * cree un string pour stCommande pour faire appel a insert()
		 * on recup les contenus de record
		 */		
		BufferedReader br = new BufferedReader(readFile);
		String uneLineDeCSV;
		StringTokenizer uneLigneInsert = new StringTokenizer("");
		System.err.println("Affichage Y5");
		
		//boucle tant qu'il existe des lignes
		System.err.println("Affichage 6 - fin de trycatch");
		try {
			System.out.println("Affichage Y6 - entre dans le try catch pour recuperer les lignes");
			while(br.readLine() != null) {
				System.out.println("Affichage Y7a - creation String pour recuperer la ligne");
				uneLineDeCSV = new String (relName+","+br.readLine());
				System.out.println("Affichage Y7b - creation String pour recuperer la ligne "+uneLineDeCSV);
				//contenu d'une ligne de csv pour la command insert()
				uneLigneInsert = new StringTokenizer(uneLineDeCSV, ",");
				insertCommande(uneLigneInsert);
				uneLineDeCSV.toString();
				System.err.println("Affichage YX");
			}
		} catch (IOException e) {
			System.out.println("Erreur I/O par rapport au contenu du fichier CSV");
		} 
		finally {
			try {
				br.close();
				readFile.close();
			} catch (IOException e) {
				System.out.println("Erreur I/O a la fermeture des readers");
			}
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
			System.out.println("Affichage X42 - "+ r);
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
	public void exitCommande(StringTokenizer commande) {
		DBManager.finish();
	}
}
