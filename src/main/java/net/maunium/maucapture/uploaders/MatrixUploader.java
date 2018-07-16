package net.maunium.maucapture.uploaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.maunium.maucapture.MauCapture;
import net.maunium.maucapture.util.ProgressFileBody;
import net.maunium.maucapture.util.ProgressFileEntity;
import net.maunium.maucapture.util.ProgressStringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * An Uploader implementation for Matrix media repositories
 *
 * @author tulir
 * @since 2.1.0
 */
public class MatrixUploader extends Uploader {
	private String addr, fileName, accessToken;
	private boolean hidden;

	public MatrixUploader(JFrame host, String addr, String fileName, String accessToken) {
		super(host);
		if (!addr.endsWith("/")) {
			addr += "/";
		}
		this.addr = addr;
		this.fileName = fileName;
		this.accessToken = accessToken;
		frame.setTitle("mauCapture Matrix Uploader");
		p.setString("Preparing to upload to " + addr);
	}

	@Override
	public void upload(BufferedImage bi) {
		long st = System.currentTimeMillis();
		File f = new File(System.getProperty("java.io.tmpdir") + File.separator + "maucapture_matrix.png");
		try {
			ImageIO.write(bi, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		HttpClient hc = HttpClientBuilder.create().build();
		HttpContext context = new BasicHttpContext();
		HttpPost post = new HttpPost(this.addr + "/_matrix/media/r0/upload?filename=" + this.fileName);
		post.setHeader("Authorization", "Bearer " + this.accessToken);
		post.setHeader("Content-Type", "image/png");

		try {
			post.setEntity(new ProgressFileEntity(f, p));

			HttpResponse httpresp = hc.execute(post, context);

			JsonElement e = new JsonParser().parse(EntityUtils.toString(httpresp.getEntity()));
			JsonObject main = e.getAsJsonObject();
			if (main.has("content_uri")) {
				Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
				String uri = main.get("content_uri").getAsString();
				StringSelection ss = new StringSelection(uri);
				c.setContents(ss, ss);

				p.setValue(1);
				p.setMaximum(1);
				p.setIndeterminate(false);
				p.setString("All done in " + (System.currentTimeMillis() - st) / 1000 + " seconds!");
				address.setText(uri);
			} else {
				String error = "";
				try {
					error = main.get("error").getAsString();
				} catch (Exception e2) {
					//
				}

				JOptionPane.showMessageDialog(frame, "Error message: " + error +"\nHTTP Status " + httpresp.getStatusLine().getReasonPhrase(),
						"Upload failed", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		f.delete();
	}
}