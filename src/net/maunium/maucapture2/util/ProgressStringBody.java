package net.maunium.maucapture2.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JProgressBar;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.StringBody;

public class ProgressStringBody extends StringBody {
	private String text;
	private JProgressBar progress;
	
	public ProgressStringBody(String text, ContentType contentType, JProgressBar progress) {
		super(text, contentType);
		this.text = text;
		this.progress = progress;
	}
	
	@Override
	public void writeTo(final OutputStream out) throws IOException {
		if (out == null) throw new IllegalArgumentException("Output stream may not be null");
		InputStream in = new ByteArrayInputStream(text.getBytes());
		byte[] tmp = new byte[4096];
		int l;
		while ((l = in.read(tmp)) != -1) {
			out.write(tmp, 0, l);
		}
		out.flush();
	}
}
