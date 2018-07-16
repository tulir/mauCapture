package net.maunium.maucapture.uploaders;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.maunium.maucapture.util.ProgressFileBody;

/**
 * An Uploader implementation for Imgur.
 *
 * @author tulir
 * @since 2.0.0
 */
public class ImgurUploader extends Uploader {
	public ImgurUploader(JFrame host) {
		super(host);
		frame.setTitle("mauCapture Imgur Uploader");
		p.setString("Preparing to upload to imgur.com");
	}

	@Override
	public void upload(BufferedImage bi) {
		long st = System.currentTimeMillis();
		File f = new File(System.getProperty("java.io.tmpdir") + File.separator + "maucapture_imgur.png");
		try {
			ImageIO.write(bi, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		HttpClient hc = HttpClientBuilder.create().build();
		HttpContext context = new BasicHttpContext();
		HttpPost post = new HttpPost("https://api.imgur.com/3/upload.json");
		post.setHeader("Authorization", "Client-ID fc08179866ff8df");

		try {
			MultipartEntityBuilder meb = MultipartEntityBuilder.create();
			meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			meb.addPart("image", new ProgressFileBody(f, p));
			meb.addPart("key", new StringBody("7577ebb97fd6f62f65eae0839c2fb7e2e1d615bc", ContentType.TEXT_PLAIN));
			post.setEntity(meb.build());

			HttpResponse httpresp = hc.execute(post, context);

			JsonElement e = new JsonParser().parse(EntityUtils.toString(httpresp.getEntity()));
			JsonObject main = e.getAsJsonObject();
			JsonObject data = main.get("data").getAsJsonObject();
			if (main.get("success").getAsBoolean()) {
				String url = "http://i.imgur.com/" + data.get("id").getAsString() + ".png";

				Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection ss = new StringSelection(url);
				c.setContents(ss, ss);

				p.setValue(1);
				p.setMaximum(1);
				p.setIndeterminate(false);
				p.setString("All done in " + (System.currentTimeMillis() - st) / 1000 + " seconds!");
				address.setText(url);
			} else JOptionPane.showMessageDialog(frame, "Error message: " + data.get("error") + "\nHTTP Status code " + main.get("status").getAsInt(),
					"Upload failed", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		f.delete();
	}
}