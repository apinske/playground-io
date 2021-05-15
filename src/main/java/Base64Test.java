import static java.lang.String.format;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

public class Base64Test {

	public static void main(String[] args) throws Exception {
		File f = new File("target/data.b64");
		long start = System.nanoTime();
		if (args.length > 0 && "prepare".equals(args[0])) {
			Random r = new Random(System.currentTimeMillis());
			FileOutputStream fos = new FileOutputStream(f);
			Base64OutputStream bos = new Base64OutputStream(fos);
			byte[] data = new byte[1024 * 1024];
			for (int i = 0; i < 100; i++) {
				r.nextBytes(data);
				bos.write(data);
			}
			bos.close();
			fos.close();
		} else if (args.length > 0 && "jdk".equals(args[0])) {
			for (int i = 0; i < 10; i++) {
				InputStream is = Base64.getMimeDecoder().wrap(new BufferedInputStream(new FileInputStream(f)));
				IOUtils.copy(is, NullOutputStream.NULL_OUTPUT_STREAM);
				is.close();
			}
		} else {
			for (int i = 0; i < 10; i++) {
				Base64InputStream is = new Base64InputStream(new FileInputStream(f));
				IOUtils.copy(is, NullOutputStream.NULL_OUTPUT_STREAM);
				is.close();
			}
		}
		System.out.println("took " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
			System.out.println(format("%s: %d", gc.getName(), gc.getCollectionCount()));
		}
	}
}
