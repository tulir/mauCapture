package net.maunium.maucapture2.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JProgressBar;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * A StringEntity extension that supports progress bars.
 * 
 * @author Tulir293
 * @since 2.0.0
 */
public class ProgressStringEntity extends StringEntity {
	private JProgressBar progress;
	
	public ProgressStringEntity(String text, ContentType contentType, JProgressBar progress) {
		super(text, contentType);
		this.progress = progress;
	}
	
	@Override
	public void writeTo(final OutputStream out) throws IOException {
		if (out == null) throw new IllegalArgumentException("Output stream may not be null");
		
		InputStream in = new ByteArrayInputStream(content);
		
		long st = System.currentTimeMillis();
		progress.setMaximum(in.available());
		progress.setString("Uploading - 0% - " + (System.currentTimeMillis() - st) / 1000 + "s");
		progress.setIndeterminate(false);
		
		byte[] tmp = new byte[1024];
		int l;
		while ((l = in.read(tmp)) != -1) {
			progress.setValue(progress.getValue() + l);
			progress.setString(
					"Uploading - " + (int) (progress.getValue() * 100.0f / progress.getMaximum()) + "% - " + (System.currentTimeMillis() - st) / 1000 + "s");
			out.write(tmp, 0, l);
		}
		out.flush();
	}
}
