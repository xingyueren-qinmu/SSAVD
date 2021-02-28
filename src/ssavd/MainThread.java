package ssavd;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import org.json.JSONObject;

import device.DeviceInfo;
import util.*;
import vulerability.VulnerabilityTestResult;
import vulerability.VulnerabilityTestRunner;
import vulnerability_tests.NewPushTest;

public class MainThread {
	private ArrayList<VulnerabilityTestResult> testResults;
	private DeviceInfo deviceInfo;
	private static String localfilePath =System.getProperty("user.dir")+File.separator+"result";
	private static String devicesIP = null;
	private int task = 0;

	public void init(String args[]){
		Command com=new Command();
		if(args.length >= 1){
			for(String arg : args){
				if(arg.charAt(0) == '-') {
					if(arg.equals("-d")){
						task = Config.SHOW_DEVICE_INFO_TASK;
					} else if(arg.equals("-t")){
						task = Config.DO_TEST_TASK;
					} else if(arg.equals("-l")){
						VulnerabilityTestRunner.testViolent = false;
					} else if(arg.equals("-a")) {
						NewPushTest.showAllResults = true;
					}
				} else {
					String regexIP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)";
					String regexPort = "(:[1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]{1}|6553[0-5])?";
					if(Pattern.matches(regexIP + regexPort, arg)){
						com.setTargetIp(arg);
						CommandCpu.setTargetIp(arg);
					} else {
						com.setTargetDevice(arg);
						CommandCpu.setTargetDevice(arg);
					}
				}
			}
		} else {
			System.out.println("help");
		}
		com.platForm();
	}

	public void generateRes(){
		String date = new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date());

		if(testResults != null){
			jsonResFile(localfilePath+File.separator + date + "_" + NewPushTest.deviceID + ".json");
		}
	}


	public void mainFun() {

		Util.ssavdOutput("Start the second test, please wait...");
		//检测设备状态
		Util.ssavdOutput("Start testing adb connect...");
		Command com=new Command();
		String adbconnect_result=com.adbconnect();

		if(!adbconnect_result.contains("success"))
			Util.errorOutput("connect device "+adbconnect_result+" and please check...");
		else {
			//设置设备信息
			deviceInfo=DeviceInfo.getDeviceInfo();
			if(task == Config.SHOW_DEVICE_INFO_TASK) {
				deviceInfo.outputDeviceInfo();
				System.exit(0);
			}
			if(task == Config.DO_TEST_TASK){
				VulnerabilityTestRunner vulnerabilityTestRunner= new VulnerabilityTestRunner();
				testResults = vulnerabilityTestRunner.doTest(deviceInfo);
				if(testResults != null){
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				NewPushTest.exitReady = true;
			}
		}
	}
	
	public void jsonResFile(String filepath) {	    
		Util.ssavdOutput("start generate json report.");
		File file = new File(filepath);
		if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
            file.getParentFile().mkdirs();
        }
        if (file.exists()) { // 如果已存在,删除旧文件
            file.delete();
        }
        try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
     // 构造json对象
        JSONObject json = JsonResult.serializeAllResultsToJson(testResults, deviceInfo);
	    	
        // 将格式化后的字符串写入文件
        Writer writer;
		try {
			writer = new FileWriter(file);
	        writer.write(json.toString());
	        writer.flush();
	        writer.close();
	        Util.ssavdOutput("test2 success, report at : " + filepath);
	        new Command().kill();
		} catch (UnsupportedEncodingException e) {
			Util.errorOutput("test2 error: "+e.getMessage() );
		} catch (FileNotFoundException e) {
			Util.errorOutput("test2 error: "+e.getMessage() );
		} catch (IOException e) {
			Util.errorOutput("test2 error: "+e.getMessage() );
		}
		
	}

}
