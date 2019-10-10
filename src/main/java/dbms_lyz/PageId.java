package main.java.dbms_lyz;

public class PageId {
	public static int ID = 0 ;
	
	private int fileIdx;
	private int pageIdx;
	
	public PageId(String nomFichier) {
		fileIdx = IdFichier(nomFichier);
		pageIdx = ID ;
		ID += 1 ;
	}
	
	/**
	 * 
	 * @param nomFichier
	 * @return le x de Data_x
	 */
	public int IdFichier(String nomFichier) {
		int id ;
		String s = new String(nomFichier.substring(5, nomFichier.indexOf(".")));
		id = Integer.parseInt(s);
		return(id);
	}
	
	public int getFileIdx(){
		return(fileIdx);
	}
	
	public int getPageIdx() {
		return(pageIdx);
	}
	
	public boolean equals(PageId p) {
		boolean bool = false ;
		if(this.fileIdx == p.getFileIdx() && this.pageIdx == p.getFileIdx()) {
			bool = true ;
		}
		return(bool);	
	}
	
	
}
