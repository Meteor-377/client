package jagex2.client;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.util.zip.ZipFile;

public class loader extends Applet implements Runnable {
	private boolean maxpri = false;
	private Applet inner;
	private static final int swid = 789;
	private static final int shei = 532;

	public void init() {
		Graphics g = getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, loader.swid, loader.shei);

		try {
			Signlink.mainapp = this;
			Signlink.startpriv(InetAddress.getByName(getCodeBase().getHost()));

			String vendor = System.getProperties().getProperty("java.vendor");
			if (vendor.toLowerCase().indexOf("sun") != -1 || vendor.toLowerCase().indexOf("apple") != -1) {
				Signlink.sunjava = true;
			}

			new Thread(this).start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		try {
			byte[] jar = Signlink.cacheload("runescape.jar");

			if (!verify(jar)) {
				updatecache();

				jar = Signlink.cacheload("runescape.jar");
				if (!verify(jar)) {
					return;
				}
			}

			cloader classLoader = new cloader();
			classLoader.jar = new ZipFile(Signlink.findcachedir() + "/" + Signlink.gethash("runescape.jar"));
			classLoader.link = Class.forName("jagex2.client.Signlink");

			inner = (Applet) classLoader.loadClass("jagex2.client.client").newInstance();
			inner.init();
			inner.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void updatecache() throws Exception {
		Graphics g = getGraphics();
		Font bold = new Font("Helvetica", Font.BOLD, 13);
		FontMetrics boldMetrics = getFontMetrics(bold);
		Font plain = new Font("Helvetica", Font.PLAIN, 13);
		FontMetrics plainMetrics = getFontMetrics(plain);
		Color barColor = new Color(140, 17, 17);

		// name + sha has a benefit of cache busting in addition to being harder to find
		byte[] src = new byte[sig.len];
		String uriSha = "";
		for (int i = 0; i < 10; i++) {
			uriSha += sig.sha[i];
		}
		InputStream stream = new URL(getCodeBase(), "runescape" + uriSha + ".jar").openStream();

		int lastPercent = 0;
		int read = 0;
		while (read < sig.len) {
			int remaining = sig.len - read;
			if (remaining > 1000) {
				remaining = 1000;
			}

			read += stream.read(src, read, remaining);

			int percent = read * 100 / sig.len;
			if (percent != lastPercent) {
				g.setColor(Color.black);
				g.fillRect(0, 0, loader.swid, loader.shei);

				g.setColor(barColor);
				g.drawRect(242, 248, 304, 34);

				String str = "Loading game code - " + percent + "%";
				g.setFont(bold);
				g.setColor(Color.white);
				g.drawString(str, ((loader.swid - boldMetrics.stringWidth(str)) / 2), 270);

				lastPercent = percent;
			}
		}

		stream.close();
		Signlink.cachesave("runescape.jar", src);
	}

	private boolean verify(byte[] src) throws Exception {
		if (src == null) {
			return false;
		}

		MessageDigest shasum = MessageDigest.getInstance("SHA");
		shasum.reset();
		shasum.update(src);

		byte[] sha = shasum.digest();
		for (int i = 0; i < 20; i++) {
			if (sha[i] != sig.sha[i]) {
				return false;
			}
		}

		return true;
	}

	public void start() {
		if (inner != null) {
			inner.start();
		}
	}

	public void stop() {
		if (inner != null) {
			inner.stop();
		}
	}

	public void destroy() {
		if (inner != null) {
			inner.destroy();
		}
	}

	public void update(Graphics g) {
		if (inner != null) {
			inner.update(g);
		}
	}

	public void paint(Graphics g) {
		if (inner != null) {
			inner.paint(g);
		}
	}

	public String getmidi() {
		if (Signlink.midi == null) {
			return "none";
		}

		String str = Signlink.midi;
		Signlink.midi = null;
		return str;
	}

	public int getmidivol() {
		return Signlink.midivol;
	}

	public int getmidifade() {
		return Signlink.midifade;
	}

	public String getwave() {
		if (Signlink.wave == null) {
			return "none";
		}

		String str = Signlink.wave;
		Signlink.wave = null;
		return str;
	}

	public int getwavevol() {
		return Signlink.wavevol;
	}
}
