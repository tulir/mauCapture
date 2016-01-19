package net.maunium.maucapture2.uploaders;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.maunium.maucapture2.util.ProgressStringEntity;

public class MISUploader extends Uploader {
	public MISUploader(JFrame host) {
		super(host);
		frame.setTitle("MauCapture MIS Uploader");
		p.setString("Preparing to upload to localhost:29300");
	}
	
	@Override
	public void upload(BufferedImage bi) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(bi, "png", os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String image = Base64.getEncoder().encodeToString(os.toByteArray());
		
		long st = System.currentTimeMillis();
		
		HttpClient hc = HttpClientBuilder.create().build();
		HttpContext context = new BasicHttpContext();
		HttpPost post = new HttpPost("http://localhost:29300/insert");
		
		try {
			JsonObject payload = new JsonObject();
			payload.addProperty("image", image);
			payload.addProperty("image-name", "asddsa");
			payload.addProperty("username", "tulir293");
			payload.addProperty("auth-token", "T9Jn655lryGglcYrz9Pmac/ls+EjokDLXNYWmIkZCkk");
			post.setEntity(new ProgressStringEntity(new Gson().toJson(payload), ContentType.APPLICATION_JSON, p));
			
			HttpResponse httpresp = hc.execute(post, context);
			
			JsonElement e = new JsonParser().parse(EntityUtils.toString(httpresp.getEntity()));
			JsonObject main = e.getAsJsonObject();
			if (main.get("success").getAsBoolean()) {
				String url = "http://localhost:29300/" + "asdasdasd" + ".png";
				
				Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection ss = new StringSelection(url);
				c.setContents(ss, ss);
				
				p.setValue(1);
				p.setMaximum(1);
				p.setIndeterminate(false);
				p.setString("All done in " + (System.currentTimeMillis() - st) / 1000 + " seconds!");
				address.setText(url);
			} else JOptionPane.showMessageDialog(frame,
					"Error message: " + main.get("status-humanreadable") + "\nHTTP Status code " + httpresp.getStatusLine().getStatusCode(), "Upload failed",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}