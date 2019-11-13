package org.nuxeo.ecm.core.blob.s3.minio;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class DockerHelper {

	
	public static void exec() throws Exception {
		String[] cmdWithParams = "docker run -t -p 9000:9000 minio/minio server /data".split(" ");
		try {
			ProcessBuilder builder = new ProcessBuilder(cmdWithParams);
			//builder.redirectErrorStream(true);
			Process process = builder.start();
			
			//process.getOutputStream().
			//process.getOutputStream().close();

			
			if (!process.isAlive()) {
				System.out.println(IOUtils.readLines(process.getErrorStream(), "UTF-8"));
				System.out.println(IOUtils.readLines(process.getInputStream(), "UTF-8"));
				
			} else {					
				Thread.sleep(1000);
				//process.wait(5000);
				InputStream out = process.getInputStream();
				int available= out.available();
				StringBuffer sb = new StringBuffer();
				while (available > 0) {
					byte[] bytes = new byte[available];
					out.read(bytes, 0, available);
					available= out.available();
					sb.append(new String(bytes));
				}
				
				
				//st<String> out = IOUtils.readLines(process.getInputStream(), "UTF-8");				
				System.out.println("out:");
				System.out.println(sb.toString());
				System.out.println("err:");
				System.out.println(IOUtils.readLines(process.getErrorStream(), "UTF-8"));
			}
	
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
