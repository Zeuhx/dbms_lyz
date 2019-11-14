
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
		testGetPagee();


	}
	public static void testGetPagee() {
		List<Frame> listFrame = new ArrayList<Frame>();
		PageId pageId = new PageId("Data_11.rf");
		PageId pageId2 = new PageId("Data_22.rf");
		Frame frame = new Frame(pageId);
		Frame frame2 = new Frame(pageId2);
		listFrame.add(frame);
		listFrame.add(frame2);

		PageId pageId3 = new PageId("Data_33.rf");		
		pageId3.setPageIdx(3);
		//		testAfficheFrame(listFrame);
		//		testSearchFrame(pageId3, listFrame);
		testAfficheFrame(listFrame);
		testFreePage(pageId2, false, listFrame);
		testAfficheFrame(listFrame);
		testFreePage(pageId, false, listFrame);
//		testGetPage(pageId3, listFrame);
//		testAfficheFrame(listFrame);
		
	}
	public static ByteBuffer testGetPage(PageId pageId, List<Frame> listFrame) {
		boolean pageExist = false;
		//TODO : on recupere le buff de newFrame puis on l'ajoute sur la listFrame si exist
		Frame getFrame = new Frame(pageId);
		ByteBuffer bf = getFrame.getBuffer();
		int indexFrame = testSearchFrame(getFrame.getPageId(), listFrame);
		//si la new pageId n'est pas dans la liste 
		if (indexFrame == 2)
			pageExist = false;
		//si il est dans la liste on récup la Frame qui contient cette page existant 
		else {
			pageExist = true;
			getFrame = listFrame.get(indexFrame);
		}

		//si newFrame est dans la liste
		if (pageExist) {
			//on ajoute si frame exist dans la liste le buffer de newFrame 
			getFrame.get(); //pin_count ++

			//Maj du LRU_change
			//si newFrame correspond au premier
			if (pageId == (listFrame.get(0)).getPageId()) {

				// si jamais l'autre frame a pin_count>0 
				if(!(listFrame.get(1)).getLRU_change()) {
					//les deux frame ne peuvent ï¿½tre remplacer
					(listFrame.get(0)).setLRU_change(false);
				}
				else {
					(listFrame.get(0)).setLRU_change(false);
					(listFrame.get(1)).setLRU_change(true);
				}

				//si newFrame correspond au deuxieme
			} else {

				// si jamais l'autre frame a pin_count>0 
				if(!(listFrame.get(0)).getLRU_change()) {
					//les deux frame ne peuvent etre remplacer
					(listFrame.get(1)).setLRU_change(false);
				}
				else {
					(listFrame.get(0)).setLRU_change(true);
					(listFrame.get(1)).setLRU_change(false);
				}
			}
		}

		//si jamais le newFrame (pageId en entre) n est pas dans la liste on remplaces 
		else {
			System.out.println("si newFrame n'est pas dedans");
			//condition si pin count > 0
//			System.out.println((listFrame.get(0)).getLRU_change()+" pour pin count à "+(listFrame.get(0)).getPin_count());
//			System.out.println((listFrame.get(1)).getLRU_change()+" pour pin count à "+(listFrame.get(1)).getPin_count());
			if (((listFrame.get(0)).getPin_count() == 0) || (listFrame.get(1).getLRU_change())){
				System.out.println("les pin count sont à zéro");
	
				//TEST affiche
//				System.out.println((listFrame.get(0)).getLRU_change());
//				System.out.println((listFrame.get(0)).getPin_count());
				//changement new frame par rapport a LRU
				
				if((listFrame.get(0)).getLRU_change() == true) {
					System.out.println("changement du premier frame ");
					listFrame.remove(0);
					listFrame.add(getFrame);

					//maj LRU etat si jamais l'autre frame a pin_count>0 
					if(!(listFrame.get(1)).getLRU_change()){
						(listFrame.get(0)).setLRU_change(true);
					}
					else {
						//maj de la valeur de LRU etat
						(listFrame.get(0)).setLRU_change(false);
						(listFrame.get(1)).setLRU_change(true);
					}
				}
				else if ((listFrame.get(1)).getLRU_change() == true){
					System.out.println("changement du second frame");
					listFrame.remove(1);
					listFrame.add(getFrame);
					// si jamais l'autre frame a pin_count>0 
					if(!(listFrame.get(0)).getLRU_change()){
						(listFrame.get(1)).setLRU_change(true);
					}
					else {
						//maj de la valeur de LRU etat
						(listFrame.get(0)).setLRU_change(true);
						(listFrame.get(1)).setLRU_change(false);
					}
				}
			}
			else
				System.out.println("aucune condition n'est realise : aucun frame dispo");
		}
		return (bf);
	}
	
	public static void testFreePage(PageId pageId, boolean valdirty, List<Frame> listFrame) {
		int indexFrame = testSearchFrame(pageId, listFrame);
		if (indexFrame == 2)
			System.out.println("Frame pas trouve");
		else {
			Frame frame = listFrame.get(indexFrame);
			frame.free(valdirty);
		}
	}

	/////////////////////////////////////////////////////
	public static int testSearchFrame(PageId pageId, List<Frame> listFrame) {
		BufferManager.getInstance();
		int i = 0;
		System.out.println("id de pageId a chercher : "+pageId.getPageIdx());

		for (Frame f : listFrame) {
			System.out.println("id de la page dans frame : "+i+" est : "+f.getPageId().getPageIdx());
			if (f.getPageId().getPageIdx() == (pageId.getPageIdx())) {

				System.out.println("résultat indice du frame: "+i);
				return (i);
			}
			i++;
		}		
		return (2);
	}

	public static void testAfficheFrame(List<Frame> listFrame) {
		for (int i = 0; i < listFrame.size(); i++) {
			System.out.print("[frame " + i + "] : ");
			System.out.println("page id : " + (listFrame.get(i)).getPageIdx() + "| pin count : "
					+ (listFrame.get(i)).getPin_count() + "| dirty : " + (listFrame.get(i)).getFlag_dirty()+ "| LRU : " + (listFrame.get(i)).getLRU_change());
		}
	}
}
