package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LYZ
 *
 */

public class Record {
	public int recordLength = 0;
	private RelDef relDef; // Relation a laquelle Records appartient
	private List<String> values; // Valeur de Records

	public Record(RelDef reldef, List<String> values) {
		relDef = reldef;
		values = new ArrayList<>();
	}

	public Record(RelDef reldef) {
		this(reldef, null);
	}
	
	public void affiche() {
		for(String s : values) {
			System.out.print(s);
			System.out.print(" ");
		}
		System.out.println();
	}
	
	public List<String> getValues(){
		return values;
	}

	/**
	 * Cette methode va permettre de recuperer les valeurs de la liste de listes de
	 * relDef Sous la forme de string NE pas Oublier d'utiliser des StringBuffer
	 */
	
	/*
	
	public void remplirValues() {
		for (int i = 0; i < relDef.getRecord().size(); i++) {
			String s = "";
			// je vais supposer que chq col a le meme nombre d'elements
			for (int j = 0; j < relDef.getNbCol(); j++) {
				String t = (String) (relDef.getRecord().get(i).get(j));
				if (j > 0)
					s.concat(" ");
				s.concat(t);
			}
			values.add(s);
		}
	}
	*/
	
	
	/**
	 * Methode qui ecrit les valeurs du Records les unes a la suite des autres
	 * Ajoute dans le bytebuffer en fonction des types de col
	 * La boucle permet d'ajouter en fonction des types
	 * 
	 * @param buff
	 * @param position
	 */
	public void writeToBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		
		int compteur = 0;
		
		for(String s : values) {
			
			boolean isFloat = false;
			boolean isString = false;
			boolean isInt = false;
			
			if(relDef.getTypeCol().get(compteur).equals("int")){
				isInt = true;
			}
			else if(relDef.getTypeCol().get(compteur).equals("float")){
				isFloat = true;
			}
			else
				isString = true;
			
			if(isString) {
				
				int j;
				int tailleString = s.length();
				String taille = relDef.getTypeCol().get(compteur).substring(6);
				int tailleType = Integer.parseInt(taille);
				for(j=0; j<tailleType; j++) {
					
					if(j>=tailleString) {
						buff.putChar(' ');
					}
					else {
						buff.putChar(s.charAt(j));
					}
				}
				//buff.position(Character.BYTES);
			}
			
			else if(isInt) {
				
				buff.putInt(Integer.parseInt(s));
				//buff.position(Integer.BYTES);
			}
			
			else {
				
				buff.putFloat(Float.parseFloat(s));
	
			}
		
		compteur ++;
			
		}
		
	}

	/**
	 * On prend un bytebuffer a une certaine position
	 * Le record est lie a un relDef donc on connait deja les types des cols
	 * La boucle permet de lire le bytebuffer en fonction des types
	 * et les affiche avec println
	 * @param buff
	 * @param position
	 * 
	 */
	public void readFromBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		int compteur = 0;
		
		for(String s : relDef.getTypeCol()) {
			if(s.equals("int")) {
				System.out.println(buff.getInt(buff.position()));
				buff.position(buff.position()+ Integer.BYTES);
			}
			else if(s.equals("float")) {
				System.out.println(buff.getFloat(buff.position()));
				buff.position(buff.position()+ Float.BYTES);
			}
			else {
				String taille = relDef.getTypeCol().get(compteur).substring(6);
				int t = Integer.parseInt(taille);
				
				for(int i = 0; i<t; i++) {
					System.out.print(buff.getChar());
				}
				
				System.out.println();
			}
			
			compteur ++;
		}
		
		
		
	}

	/**
	 * Retourne la taille du record selon le type (en octets)
	 * 
	 * @return
	 */
//	public int getRecordSize() {
//		RelDef rd = relDef;
//		int i = 0;
//
//		// Verifie si c'est bien un Integer
//		if (rd.getTypeCol().get(i).getClass().toString().contains("Integer")) {
//			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+4");
//			recordLength += 4;
//		}
//		// Float
//		else if (rd.getTypeCol().get(i).getClass().toString().equals("Float")) {
//			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+4");
//			recordLength += 4;
//		}
//		// String
//		/**
//		 * ATTENTION : RESTE A MULTIPLIER PAR LE NB DE CHAR
//		 */
//		else if (rd.getTypeCol().get(i).getClass().toString().equals("String")) {
//			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+2");
//			recordLength += 2;
//		} else
//			recordLength += 0;
//
//		return recordLength;
//	}

}
