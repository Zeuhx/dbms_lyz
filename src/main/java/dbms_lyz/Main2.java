
package main.java.dbms_lyz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * CECI EST UN MAIN <BROUILLON>, pour tester les fonctions 
 * @author cedzh
 *
 */
public class Main2 {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		//		test();
		//		
		//		PageId pageId1 = new PageId("Data_1.rf");
		//		PageId pageId2 = new PageId("Data_2.rf");
		//
		
		testSearchFrame();

	}
	public static void testSearchFrame() {
		System.out.println("##### DEBUT testSearchFrame() #####");
		
		List<Frame> listFrame = new ArrayList<Frame>();
		PageId pageId = new PageId("Data_1.rf");
		PageId pageId2 = new PageId("Data_2.rf");
		Frame frame = new Frame(pageId);
		Frame frame2 = new Frame(pageId2);
		listFrame.add(frame);
		listFrame.add(frame2);
		
		PageId celuiQChercher = pageId;
		BufferManager.getInstance();
		frame = null;
		int i = 0;
		System.out.println("id de la classe pageId : "+pageId.getPageIdx());
		TestAfficheFrame(listFrame);
		for (Frame f : listFrame) {
			if (f.getPageId().equals(celuiQChercher))
				System.out.println("résultat : "+i);
			else
				System.out.println("Attention : la PageId de id : "+celuiQChercher.getPageIdx()+" n'est pas dans les frames");			
		}
		
		System.out.println("##### FIN testSearchFrame() #####");
	}

	public static void TestAfficheFrame(List<Frame> listFrame) {
		System.out.println("##### DEBUT testAfficheFrame() #####");
		
		for (int i = 0; i < listFrame.size(); i++) {
			System.out.print("[frame " + i + "] : ");
			System.out.println("page id : " + (listFrame.get(i)).getPageIdx() + "| pin count : "
					+ (listFrame.get(i)).getPin_count() + "| dirty : " + (listFrame.get(i)).getFlag_dirty());
		}
		System.out.println("##### FIN testAfficheFrame() #####");
	}
}
