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
	
	public void affiche() {
		for(String s : values) {
			System.out.print(s);
			System.out.print(" ; ");
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
		int i = 0;
		List<String> list = relDef.getTypeCol();
		for(i=0 ; i<list.size() ; i++) {
			boolean isFloat = false;
			boolean isString = false;
			boolean isInt = false;
			
			if(list.get(i).equals("int")) 
				isInt = true;
			else if(list.get(i).equals("float")) 
				isFloat = true;
			else
				isString = true;
			
			if(isString) {
				int tailleString = list.get(i).length();
				int taille = Integer.parseInt(list.get(i).substring(6));
				/**
				 * Si le string saisie est inferieur a la taille demandé du stringx
				 * On rajoute des espaces a la fin pour avoir la taille x
				 */
				for(int j=0; j<taille; j++) {
					if(j>=tailleString)
						buff.putChar(' ');
					else
						buff.putChar(list.get(i).charAt(j));
				}
			}
			else if(isInt)
				buff.putInt(Integer.parseInt(list.get(i)));
			else if(isFloat)
				buff.putFloat(Float.parseFloat(list.get(i)));
		}
		
	}

	/**
<<<<<<< HEAD
	 * On prend un bytebuffer a une certaine position
	 * Le record est lie a un relDef donc on connait deja les types des cols
	 * La boucle permet de lire le bytebuffer en fonction des types
	 * et les affiche avec println
=======
	 * " Le contraire " de la version precedente
>>>>>>> branch 'master' of https://github.com/Zeuhx/dbms_lyz
	 * @param buff
	 * @param position
	 * 
	 */
	public void readFromBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		List<String> list = relDef.getTypeCol();
		for(int i=0 ; i<list.size() ; i++) {
			if(list.get(i).equals("int")) {
				values.add(i, Integer.toString(buff.getInt(buff.position())));
				buff.position(buff.position()+ Integer.BYTES);
			}
			else if(list.get(i).equals("float")) {
				values.add(i, Float.toString(buff.getFloat(buff.position())));
				buff.position(buff.position()+ Float.BYTES);
			}
			else {
				String taille = list.get(i).substring("string".length());
				int t = Integer.parseInt(taille);
				for(int j = 0; j<t; j++) {
					StringBuilder sb = new StringBuilder();
					sb.append(buff.getChar());
					values.add(sb.toString());
				}
			}
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
