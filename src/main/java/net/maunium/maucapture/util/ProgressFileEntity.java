package net.maunium.maucapture.util;

import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.Args;

import javax.swing.*;
import java.io.*;

/**
 * A FileBody extension that supports progress bars.
 *
 * @author tulir
 * @since 2.1.0
 */
public class ProgressFileEntity extends FileEntity {
	private JProgressBar progress;

	public ProgressFileEntity(File file, JProgressBar progress) {
		super(file);
		this.progress = progress;
	}

	@Override
	public void writeTo(final OutputStream out) throws IOException {
		Args.notNull(out, "Output stream");
		final InputStream in = getContent();
		long st = System.currentTimeMillis();
		progress.setMaximum(in.available());
		progress.setString("Uploading - 0% - " + (System.currentTimeMillis() - st) / 1000 + "s");
		progress.setIndeterminate(false);
		try {
			final byte[] tmp = new byte[1024];
			int l;
			while ((l = in.read(tmp)) != -1) {
				progress.setValue(progress.getValue() + l);
				progress.setString("Uploading - " + (int) (progress.getValue() * 100.0f / progress.getMaximum()) + "% - "
						+ (System.currentTimeMillis() - st) / 1000 + "s");
				out.write(tmp, 0, l);
			}
			out.flush();
		} finally {
			in.close();
		}
	}
}