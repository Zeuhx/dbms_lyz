package main.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour garder les infos de schema d'une relation
 * @author LYZ : Le Marcel, Yu Willy, Zhang Cedric 
 *
 */
public class RelDef {
	private String nomRelation;
	private int nbCol;
	private List<String> typeCol;
	
	private int fileIdx;	// Indice du fichier disque qui stocke la relation
	private int recordSize;	// taille d'un record
	private int slotCount;	// nb de case (slots) sur une page

	/**
	 * ATTENTION WTF ?
	 */
	private List<ArrayList<Object>> records; // ??? C'est quoi ca ??

	public RelDef(String nomRelation, List<String> typeCol) {
		this.nomRelation = nomRelation;
		this.nbCol = typeCol.size();
		this.typeCol = typeCol;
	}

	public RelDef(String nomRelation, List<String> typeCol, int fileIdx, int recordSize, int slotCount) {
		this.nomRelation = nomRelation;
		this.nbCol = typeCol.size();
		this.fileIdx = 0;
		this.recordSize = recordSize; // Attention : il faut directement initialiser selon la taille du record
		this.slotCount = slotCount;	// Attention : calculer en fonction de pageSize et de recordSize
	}

	public List<ArrayList<Object>> getRecord() {
		return records;
	}

	public String getNomRelation() {
		return (nomRelation);
	}

	public int getNbCol() {
		return (nbCol);
	}

	public List<String> getTypeCol() {
		return (typeCol);
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

}
