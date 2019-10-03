package dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Record {
	private RelDef relDef ;
	private List<Character> listChar;
	
	public Record(RelDef reldef, List<Character> listChar) {
		this.relDef = reldef ;
		listChar = new ArrayList<>();
	}
	
	public Record(RelDef reldef) {
		this(reldef, null);
	}
	
	public void writeToBuffer(ByteBuffer buff, int position) {
		if() {
			
		}
	}
}
