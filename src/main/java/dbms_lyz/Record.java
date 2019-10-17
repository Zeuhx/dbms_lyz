package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LYZ
 * 
 *
 */
public class Record {
	public static int recordLength = 0;
	private RelDef relDef; // Relation a laquelle Records appartient
	private List<String> values; // Valeur de Records

	public Record(RelDef reldef, List<String> values) {
		this.relDef = reldef;
		values = new ArrayList<>();
	}

	public Record(RelDef reldef) {
		this(reldef, null);
	}

	/**
	 * Cette methode va permettre de recuperer les valeurs de la liste de listes de
	 * relDef Sous la forme de string NE pas Oublier d'utiliser des StringBuffer
	 */
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

	/**
	 * TODO 
	 * 
	 * Methode qui ecrit les valeurs du Records les unes a la suite des autres
	 * 
	 * @param buff
	 * @param position
	 */
	public void writeToBuffer(ByteBuffer buff, int position) {
		position = buff.position();
		for (int i = 0; i < values.size(); i++) {
			if (values.getClass().toString().contains("Integer")) {
				buff.putInt((int) Integer.parseInt(values.get(i)));
				position++;
			}
			if (values.getClass().toString().contains("Float")) {
				buff.putFloat((float) Float.parseFloat(values.get(i)));
				position++;
			}
			if (values.getClass().toString().contains("String")) {
				int j = 0;
				int tailleString = values.toString().length();
				;
				do {
					buff.putChar(values.get(i).charAt(j));
					j++;
				} while (tailleString > j);
				position++;
			} else {
				System.out.println("Erreur : Aucune classe ne correspond");
			}
		}
	}

	/**
	 * " Le contraire " de la version precedente, MANQUE les conditions
	 * @param buff
	 * @param position
	 * 
	 */
	public void readFromBuffer(ByteBuffer buff, int position) {
		for (int i = 0; i < buff.capacity(); i++) {
			buff.getInt(position);
			position++;
			buff.putFloat((float) Float.parseFloat(values.get(i)));
			position++;

			int j = 0;
			int tailleString = values.toString().length();
			do {
				buff.putChar(values.get(i).charAt(j));
				j++;
			} while (tailleString > j);
			position++;
		}
	}

	/**
	 * TODO : Refaire la fonction comme c'etait des string 
	 * 
	 * Retourne la taille du record selon le type (en octets)
	 * 
	 * @return
	 */
	public int recordSize() {
		RelDef rd = relDef;
		int i = 0;

		// Verifie si c'est bien un Integer
		if (rd.getTypeCol().get(i).getClass().toString().contains("Integer")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+4");
			recordLength += 4;
		}
		// Float
		else if (rd.getTypeCol().get(i).getClass().toString().equals("Float")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+4");
			recordLength += 4;
		}
		// String
		/**
		 * ATTENTION : RESTE A MULTIPLIER PAR LE NB DE CHAR
		 */
		else if (rd.getTypeCol().get(i).getClass().toString().equals("String")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+2");
			recordLength += 2;
		} else
			recordLength += 0;

		return recordLength;
	}

}
