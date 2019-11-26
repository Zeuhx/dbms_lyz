package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.Arrays;
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
		this.values = values;
	}
	
	/**
	 * Methode qui ecrit les valeurs du Records les unes a la suite des autres dans le buffer
	 * Ajoute dans le bytebuffer en fonction des types de col
	 * La boucle permet d'ajouter en fonction des types
	 * 
	 * @param buff un buffer
	 * @param position un entier correspondant à une position dans le buffer
	 */
	public void writeToBuffer(ByteBuffer buff, int position) {
		System.out.println("Affichage X23bis - Affichage des values du record " + relDef.getNomRelation() + " : " + values);
		System.out.println("Affichage X29 : Affichage Buff depuis writeToBuffer - " + buff);
		//buff = ByteBuffer.allocate(Constants.PAGE_SIZE);
		System.out.println("Affichage X34 - Affichage de la postion : " + position);
		buff.position(position);
		System.out.println("Affichage X30 : Affichage APRES Buff depuis writeToBuffer - " + buff);
		int i = 0;
		List<String> list = relDef.getTypeCol(); //recupere la liste
		
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
				 * Si le string saisie est inferieur a la taille demande du stringx
				 * On rajoute des espaces a la fin pour avoir la taille x
				 */
				for(int j=0; j<taille; j++) {
					if(j>=tailleString)
						buff.putChar(' ');
					else {
						System.out.println("Affichage X31 : Affichage du charAt(j) : " + values.get(i).charAt(j));
						System.out.println("Affichage X32 - ByteBuffer : " + Arrays.toString(buff.array()));
						buff.putChar(values.get(i).charAt(j));
						System.out.println("Affichage X33 - ByteBuffer : " + Arrays.toString(buff.array()));
					}
				}
			}
			else if(isInt) {
				System.out.println("Affichage X9bis : Affichage du nombre passe en parametre: " + values.get(i));
				
				buff.putInt(Integer.parseInt(values.get(i)));
			}
			else if(isFloat)
				buff.putFloat(Float.parseFloat(values.get(i)));
		}
	}

	/**
	 * On prend un bytebuffer a une certaine position
	 * Le record est lie a un relDef donc on connait deja les types des cols
	 * La boucle permet de lire le bytebuffer en fonction des types
	 * et les affiche avec println
	 * @param buffun buffer
	 * @param position un entier correspondant à une position dans le buffer
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

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("[Record de " + relDef.getNomRelation() + "]" + " ");
		System.out.println("Affichage X28 : Affichage values : " + values);
		
		for(String s : values) {
			build.append(s);
			build.append(" ; ");
		}
		build.append("\n");
		return build.toString();
	}
	
	public List<String> getValues(){
		return values;
	}

	public RelDef getRelDef() { return relDef; }	
	
	

}
