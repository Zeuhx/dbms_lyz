package main.java.dbms_lyz;

/**
 * 
 * @author Groupe LYZ Chaque fichier est identifier par un PageId Permet
 *         d'identifier donc un fichier et donc retrouver le contenu d'un
 *         fichier
 *
 */
public class PageId {
	private int fileIdx;
	private int pageIdx;

	public PageId(String nomFichier) {
		try {
			fileIdx = idFichier(nomFichier);
		}
		catch(StringIndexOutOfBoundsException e) {
			System.err.println("Attention : Le fichier " + nomFichier + " n'est pas du bon format");
		}
	}
	
	public PageId(int pageIdx, int fileIdx) {
		this.pageIdx = pageIdx ;
		this.fileIdx = fileIdx ;
	}

	/**
	 * Retrouve l'ID (juste le nombre) du fichier
	 * 
	 * @param nomFichier
	 * @return le x de Data_x
	 */
	public int idFichier(String nomFichier) {
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

	public int getFileIdx() { return fileIdx ; }
	public int getPageIdx() { return pageIdx; }

	public boolean equals(PageId p) {
		boolean bool = false;
		if (this.fileIdx == p.getFileIdx() && this.pageIdx == p.getPageIdx()) {
			bool = true;
		}
		return (bool);
	}

	public void setPageIdx(int pageIdx) {
		this.pageIdx = pageIdx;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PageId 
				&& fileIdx == ((PageId) obj).getFileIdx() 
				&& pageIdx == ((PageId) obj).getPageIdx();
	}

	@Override
	public String toString() {
		return "P[fileIdx=" + fileIdx + ", pageIdx=" + pageIdx + "]";
	}
	
	
}
