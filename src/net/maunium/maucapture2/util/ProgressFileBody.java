package net.maunium.maucapture2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JProgressBar;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.Args;

public class ProgressFileBody extends FileBody {
	private JProgressBar progress;
	
	public ProgressFileBody(File file, JProgressBar progress) {
		super(file);
		this.progress = progress;
	}
	
	@Override
	public void writeTo(final OutputStream out) throws IOException {
		Args.notNull(out, "Output stream");
		final FileInputStream in = new FileInputStream(getFile());
		long st = System.currentTimeMillis();
		progress.setMaximum(in.available());
		progress.setString("Uploading - 0% - " + (System.currentTimeMillis() - st) / 1000 + "s");
		progress.setIndeterminate(false);
		try {
			final byte[] tmp = new byte[1024];
			int l;
			while ((l = in.read(tmp)) != -1) {
				progress.setValue(progress.getValue() + l);
				progress.setString("Uploading - " + (int) (progress.getValue() * 100.0f / progress.getMaximum()) + "% - " + (System.currentTimeMillis() - st)
						/ 1000 + "s");
				out.write(tmp, 0, l);
			}
			out.flush();
		} finally {
			in.close();
		}
	}
}