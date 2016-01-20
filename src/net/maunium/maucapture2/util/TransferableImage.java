package net.maunium.maucapture2.util;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A simple Transferable implementation to allow copying images to the clipboard.
 * 
 * @author Tulir293
 * @since 2.0.0
 */
public class TransferableImage implements Transferable, ClipboardOwner {
	private Image i;
	
	public TransferableImage(Image i) {
		this.i = i;
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(DataFlavor.imageFlavor) && i != null) return i;
		else throw new UnsupportedFlavorException(flavor);
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors = new DataFlavor[1];
		flavors[0] = DataFlavor.imageFlavor;
		return flavors;
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		DataFlavor[] flavors = getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++)
			if (flavor.equals(flavors[i])) return true;
			
		return false;
	}
	
	@Override
	public void lostOwnership(Clipboard cb, Transferable c) {}
}