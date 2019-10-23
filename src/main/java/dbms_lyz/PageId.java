package main.java.dbms_lyz;

/**
 * 
 * @author Groupe LYZ Chaque fichier est identifier par un PageId Permet
 *         d'identifier donc un fichier et donc retrouver le contenu d'un
 *         fichier
 *
 */
public class PageId {
	public static int ID = 0;

	private int fileIdx;
	private int pageIdx;

	public PageId(String nomFichier) {
		fileIdx = idFichier(nomFichier);
		System.out.println("file id : " + fileIdx);
		pageIdx = ID;
		ID += 1;
	}

	/**
	 * Retrouve l'ID (juste le nombre) du fichier
	 * 
	 * @param nomFichier
	 * @return le x de Data_x
	 */
	public int idFichier(String nomFichier) {
	/*
		int id;
		String s = null;
		// Si le fichier possede un nom court (chemin nom absolue)
		if (nomFichier.length() < 20) {
			s = new String(nomFichier.substring(5, nomFichier.indexOf(".rf")));
		}
		// Sinon c'est le chemin absolue
		else {
			// 27 car c'est nb de caractere avant l'acces au fichier
			s = new String(nomFichier.substring(27, nomFichier.indexOf(".rf")));
		}

		id = Integer.parseInt(s);
		return (id);
	*/
		
		int taille = nomFichier.length()-4;
		int debut = nomFichier.length()-3;
		String nbARecup;
		
		boolean isInteger = true;
		
		int nbInt = 0;
		
		while(isInteger) {
			
			if(Character.isDigit(nomFichier.charAt(taille))) {
				nbInt++;
				taille--;
			}
			
			else {
				
				isInteger = false;	
			}
		}
		
		nbARecup = nomFichier.substring(debut-nbInt, debut);
		
		return Integer.parseInt(nbARecup);
		
	}

	public int getFileIdx() {
		return (fileIdx);
	}

	public int getPageIdx() {
		return (pageIdx);
	}

	public boolean equals(PageId p) {
		boolean bool = false;
		if (this.fileIdx == p.getFileIdx() && this.pageIdx == p.getFileIdx()) {
			bool = true;
		}
		return (bool);
	}

}
