package net.maunium.maucapture.util;

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
 * @author tulir
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
		if (out == null) {
			throw new IllegalArgumentException("Output stream may not be null");
		}

		InputStream in = new ByteArrayInputStream(content);

		long startTime = System.currentTimeMillis();
		progress.setMaximum(in.available());
		progress.setString("Uploading - 0% - " + (System.currentTimeMillis() - startTime) / 1000 + "s");
		progress.setIndeterminate(false);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
			progress.setValue(progress.getValue() + length);
			progress.setString("Uploading - " + (int) (progress.getValue() * 100.0f / progress.getMaximum()) + "% - " + (System.currentTimeMillis() - startTime) / 1000 + "s");
			out.write(buffer, 0, length);
		}
		out.flush();
	}
}
