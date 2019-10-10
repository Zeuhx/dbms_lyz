package test.java.dbms_lyz;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 
 * Classe brouillon
 *
 */
public class Draft {
	
	/**
	 * Pour DiskManager
	 */
	/**
	 * 
	 * @param fichier_source
	 * @param fichier_dest
	 * @throws IOException
	 * Ajoute le fichier_source au fichier_dest avec les exceptions 
	 */
	private static void copier(String fichier_source, String fichier_dest) throws IOException{
		FileInputStream src = new FileInputStream(fichier_source);
		FileOutputStream dest = new FileOutputStream(fichier_dest);
		
		FileChannel inChannel = src.getChannel();
		FileChannel outChannel = dest.getChannel();
		
		for (ByteBuffer buffer = ByteBuffer.allocate(1024*1024);
				inChannel.read(buffer) != -1;
				buffer.clear()) {
			buffer.flip();
			while (buffer.hasRemaining()) outChannel.write(buffer);
		}
		
		inChannel.close();
		outChannel.close();
		src.close();
		dest.close();
	}

//	public PageId addPage(int fileIdx) {
//	// FileInputStream fis = new FileInputStream;
//
//}
}
