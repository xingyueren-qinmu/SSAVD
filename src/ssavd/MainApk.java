package ssavd;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import util.Command;
import util.CommandCpu;


public class MainApk {
	public static Logger logger = LogManager.getLogger(MainApk.class.getName());
	static String testapkpath=System.getProperty("user.dir")+File.separator+"tools"+File.separator+"cvd_app.apk";
	
	static String appPackageName="com.nowsecure.android.vts";
    private String filePath ="sdcard/result.json";   
    private String localfilePath =System.getProperty("user.dir")+File.separator+"result";
	
	public static void main(String args[]) {
		Command com=new Command();
		if(args.length==1) {
			com.setTargetDevice(args[0]);
		}else if(args.length>1){
			logger.error("please do not input or input one argument(the device serial number)");
			System.exit(0);
		}
		com.platForm();
		MainApk mainApk=new MainApk();
		boolean result=mainApk.mainFun();
		if(result==true)
			logger.info("test success and local result file :"+mainApk.localfilePath+File.separator+"result.json");
		else {
			logger.info("test failure");
		}
	}

	public boolean mainFun() {
		CommandCpu commandCpu=new CommandCpu();
		boolean result=false;
		logger.info("start first test,please wait...");
		
		logger.info("start test adb connect...");
		Command com=new Command();
		String adbconnect_result=com.adbconnect();
		
		if(!adbconnect_result.contains("success"))
			logger.error("connect device "+adbconnect_result+" and please check...");
		else {
			String install_result=com.install();
			if(install_result.contains("success")) {
				logger.info("install success");
				if(com.startapp().contains("success")) {
					if(commandCpu.getSysApi()>=23) {
						//需要申请运行时权限		
						logger.info("first start app success and wait 10s to grant permissions ...");
						try {
								TimeUnit.SECONDS.sleep(10);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						
						if(com.startapp().contains("success")) {
							try {
								logger.info("second start app success and wait 10s to test...");
								TimeUnit.SECONDS.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							String pulljson_result=com.pulljson();
							if(pulljson_result.contains("success")) {
								logger.info("pull json_result success");
								result=true;
							}else { 
								logger.error("pull json_result :"+pulljson_result);
							}	
						}									
					}else {
						try {
								logger.info("start app success and wait 10s to test...");
								TimeUnit.SECONDS.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}					
							String pulljson_result=com.pulljson();
							if(pulljson_result.contains("success")) {
								logger.info("pull json_result success...");
								result=true;
							}else { 
								logger.error("pull json_result :"+pulljson_result);
							}	
						}									
					}	
				
				String uninstall_result=com.uninstall();				
				if(uninstall_result.contains("failure") || uninstall_result.contains("error")) {
					logger.error("uninstall:"+uninstall_result+"  and try again");
					com.uninstall();
				}				
				
			}
		}
		
		return result;
				
	}

	
	
}
