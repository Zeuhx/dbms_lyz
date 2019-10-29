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
	private String relName;
	private int nbCol;
	private List<String> typeCol;
	//private List<Record> records;

	private int fileIdx;	// Indice du fichier disque qui stocke la relation
	private int recordSize = 0;	// taille d'un record
	private int slotCount;	// nb de case (slots) sur une page


	public RelDef(String nomRelation, List<String> typeCol) {
		this.relName = nomRelation;
		this.typeCol = typeCol;
		nbCol = typeCol.size();
		
		
	}

	/**
	 * TODO Verifier le fileIdx
	 * 
	 * @param nomRelation
	 * @param typeCol
	 * @param fileIdx
	 * @param recordSize
	 * @param slotCount
	 */
	public RelDef(String nomRelation, List<String> typeCol, int fileIdx, int recordSize, int slotCount) {
		this.relName = nomRelation;
		this.nbCol = typeCol.size();
		this.fileIdx = fileIdx;		// le fichier
		this.recordSize = recordSize; // Attention : il faut directement initialiser selon la taille du record
		this.slotCount = slotCount;	// Attention : calculer en fonction de pageSize et de recordSize
	}

	public void affiche() {
		System.out.println("Nom de la relation : "+ relName);
		System.out.println("Cols : ");
		
		for(String s : typeCol) {
			System.out.print(s+" ");
		}
		System.out.println();		
	}

	public void insertRecord(Record r) {
		//ajoute un string a la relation en tant que record
		List<String>values = r.getValues();
		String ligneRecord2 ="";
		for(String s : values) {
			ligneRecord2.concat(s);
			ligneRecord2.concat(" ");
		}
		
		String ligneRecord = ligneRecord2.substring(0, ligneRecord2.length()-1);
		
		StringTokenizer recordASeparer = new StringTokenizer(ligneRecord);
		int compteurCol = 0; //Compteur pour savoir a quelle colonne on est
		int compteurRecord = recordASeparer.countTokens();
		boolean verifyTypeOfCols = false; //Si les elements ne correspondent pas aux types des cols

		if(nbCol == compteurRecord) {
			while(recordASeparer.hasMoreElements()) {

				/**
				 * 	On verifie que chaque element correspond aux types de la relation
				 */

				boolean hasDigit = false;
				boolean hasPoint = false;
				boolean isString = false;

				String s = recordASeparer.nextToken();

				/**
				 * Pour verifier le type de l'element
				 */

				for(int i = 0; i<s.length(); i++) {

					if(Character.isDigit(s.charAt(i)))
						hasDigit = true;
					else if(hasPoint && s.charAt(i)=='.') {
						isString = true;
					}
					else if(s.charAt(i)=='.') {
						hasPoint = true;
					}
					else {
						isString = true;
					}
				}

				if(isString) {
					String col = typeCol.get(compteurCol);
					String tailleString = col.substring(6);
					int taille = Integer.parseInt(tailleString);
					//Exception possible
					if(!typeCol.get(compteurCol).contains("String") || s.length()>taille)
						verifyTypeOfCols = true;
				}
				else if(hasDigit && !hasPoint) {
					//si on met 3.3 c'est compt� comme int et aussi String?
					if(!typeCol.get(compteurCol).equals("int"))
						verifyTypeOfCols = true;
				}
				else if(hasDigit && hasPoint) {

					if(!typeCol.get(compteurCol).equals("float"))
						verifyTypeOfCols = true;
				}
				else {
					verifyTypeOfCols = true;
				}
				compteurCol++;
			}
			if(!verifyTypeOfCols) {
				
				/**
				 * Les types correspondes on ajoute la ligne en tant que record
				 *  a la liste De records
				 */
				StringTokenizer st = new StringTokenizer(ligneRecord);
				List<String> elements = new ArrayList<>();
				while(st.hasMoreElements()) {
					elements.add(st.nextToken());
				}
				//records.add(r);
			}
			else {
				System.out.println("La relation ne correspond aux types des col");
			}
		}
		else {
			System.out.println("La relation ne correspond au nb de types des col");		
		}
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
	
	public String getNomRelation() { return relName; }
	
	public int getNbCol() { return (nbCol); }

	public int getFileIdx() { return fileIdx ; }

	public List<String> getTypeCol(){ return typeCol ; }
	
	public void setRecordSize(int recordSize) { this.recordSize = recordSize; }

	public void setSlotCount(int slotCount) { this.slotCount = slotCount; }
	
	public int getRecordSize() { return recordSize;	}

	public int getSlotCount() { return slotCount; }
}
