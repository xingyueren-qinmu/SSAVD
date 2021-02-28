package util;

import java.io.IOException;

public class Util {

	public static void testOutput(String out){
		System.out.println("[test]: " + out);
	}

	public static void recoverOutput(String out){
		System.out.println("[recover]: " + out);
	}

	public static void ssavdOutput(String out){
		System.out.println("[SSAVD]: " + out);
	}

	public static void errorOutput(String out){
		System.out.println("[error]: " + out);
	}

	public static void killProcess(Process process){

		if(process != null){
			try {
				process.getInputStream().close();
				process.getErrorStream().close();
				process.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			process.destroy();
		}
	}

}
