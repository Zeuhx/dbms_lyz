package main.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Classe pour garder les infos de schema d'une relation
 * @author LYZ : Le Marcel, Yu Willy, Zhang Cedric 
 *
 */
public class RelDef {
	private String nomRel;
	private int nbCol;
	private List<String> typeCol;
	private List<Record> records;


	private int fileIdx;	// Indice du fichier disque qui stocke la relation
	private int recordSize;	// taille d'un record
	private int slotCount;	// nb de case (slots) sur une page


	public RelDef(String nomRelation, List<String> typeCol) {
		this.nomRel = nomRel;
		this.typeCol = typeCol;
		nbCol = typeCol.size(); 
		this.records = records;
	}

	public RelDef(String nomRelation, List<String> typeCol, int fileIdx, int recordSize, int slotCount) {
		this.nomRel = nomRelation;
		this.nbCol = typeCol.size();
		this.fileIdx = 0;
		this.recordSize = recordSize; // Attention : il faut directement initialiser selon la taille du record
		this.slotCount = slotCount;	// Attention : calculer en fonction de pageSize et de recordSize
	}

	public void affiche() {
		System.out.println("Nom de la relation : "+ nomRel);
		System.out.println("Cols : ");
		for(String s : typeCol) {
			System.out.print(s+" ");
		}
		System.out.println();

		System.out.println("Records :");
		for(Record r : records) {
			r.affiche();
		}
	}

	public String getNomRelation() {
		return nomRel;
	}

	public int getNbCol() {
		return (nbCol);
	}


	public void addRecord(String ligneRecord) {
		//ajoute un string a la relation en tant que record

		StringTokenizer recordASeparer = new StringTokenizer(ligneRecord);
		int compteurCol = 0; //Compteur pour savoir a quelle colonne on est
		int compteurRecord = recordASeparer.countTokens();
		boolean fail = false; //Si les elements ne correspondent pas aux types des cols

		if(nbCol == compteurRecord) {
			while(recordASeparer.hasMoreElements()) {

				//On verifie que chaque element correspond aux types
				//De la relation

				boolean hasDigit = false;
				boolean hasPoint = false;
				boolean isString = false;


				String s = recordASeparer.nextToken();

				//Pour verifier le type de l'element

				for(int i = 0; i<s.length(); i++) {

					if(Character.isDigit(s.charAt(i)))
						hasDigit = true;
					else if(hasPoint && s.charAt(i)=='.') {
						isString = true;
					}
					else if(s.charAt(i)=='.') {
						hasPoint = true;
					}
					else
						isString = true;

				}

				if(isString) {
					String col = typeCol.get(compteurCol);
					String tailleString = col.substring(6);
					int taille = Integer.parseInt(tailleString);
					//Exception possible
					if(!typeCol.get(compteurCol).contains("String") || s.length()>taille)
						fail = true;

				}


				else if(hasDigit && !hasPoint) {
					//si on met 3.3 c'est compt� comme int et aussi String?
					if(!typeCol.get(compteurCol).equals("int"))
						fail = true;
				}

				else if(hasDigit && hasPoint) {

					if(!typeCol.get(compteurCol).equals("float"))
						fail = true;
				}

				else
					fail = true;
				compteurCol++;

			}

			if(!fail) {

				//Les types correspondes
				//On ajoute la ligne en tant que record a la liste
				//De records

				StringTokenizer st = new StringTokenizer(ligneRecord);
				List<String> elements = new ArrayList<>();
				while(st.hasMoreElements()) {
					elements.add(st.nextToken());
				}
				Record r = new Record(this, elements);
				records.add(r);
			}

			else
				System.out.println("La relation ne correspond aux types des col");
		}

		else
			System.out.println("La relation ne correspond au nb de types des col");		
	}


	public boolean equals(String type) {
		int i = 0;
		Object obj = typeCol.get(i).getClass();

		// stringx
		if (type.contains("string")) {
			return true;
		}
		// int float
		else if (obj.toString().contains(type)) {
			return true;
		} else
			return false;
	}

	public int getFileIdx() {
		return fileIdx ;
	}

	public List<String> getTypeCol(){
		return typeCol;
	}

	public List<Record> getRecord() {
		return records;
	}

}
