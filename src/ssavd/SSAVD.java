package ssavd;

import util.Util;
import vulnerability_tests.NewPushTest;

@SuppressWarnings("Duplicates")
public class SSAVD implements Runnable {
	MainThread mainThread = new MainThread();

	private static String[] args;

	public SSAVD() {
		Runtime.getRuntime().addShutdownHook(new ExitListener());
	}

	@Override
	public void run() {
		mainThread = new MainThread();
		mainThread.init(args);
		mainThread.mainFun();
	}

	public static void main(String[] args) throws InterruptedException {
		SSAVD.args = args;
		SSAVD ssavd = new SSAVD();
		Thread thread = new Thread(ssavd);
		thread.run();
		thread.join();
	}



	private class ExitListener extends Thread{

		@Override
		public void run() {
			NewPushTest.exitSignal = true;
			while(!NewPushTest.exitReady && NewPushTest.running){

			}
			mainThread.generateRes();
			Util.ssavdOutput("SSAVD Finished!");
		}
	}
}



//
//	public void mainFun() {
//
//		Util.ssavdOutput("start second test,please wait...");
//		//检测设备状态
//		Util.ssavdOutput("start test adb connect...");
//		Command com=new Command();
//		String adbconnect_result=com.adbconnect();
//
//		if(!adbconnect_result.contains("success"))
//			Util.errorOutput("connect device "+adbconnect_result+" and please check...");
//		else {
//			//设置设备信息
//			deviceInfo=DeviceInfo.getDeviceInfo();
//			if(!deviceInfo.outputDeviceInfo()) return;
//
//			VulnerabilityTestRunner vulnerabilityTestRunner= new VulnerabilityTestRunner();
//			testResults = vulnerabilityTestRunner.doTest(deviceInfo);
//
//		}
//	}
