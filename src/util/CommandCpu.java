package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandCpu {
	
	 private static String targetdevice="";
	 private static String targetIP = "";
	 private static String adbCommand;

	 static {
		 String os = System.getProperty("os.name");
		 String platform = "";
		 if(os.toLowerCase().startsWith("win")){
			 platform = "Windows";
			 adbCommand = "tools"+File.separator + platform + File.separator+"adb";

		 } else if(os.toLowerCase().startsWith("linux")) {
			 platform = "Linux";
			 adbCommand = "./tools"+File.separator + platform + File.separator+"adb";

		 } else if(os.toLowerCase().startsWith("mac")){
			 platform = "Mac";
			 adbCommand = "./tools"+File.separator + platform + File.separator+"adb";

		 } else {
			 Util.errorOutput("Unsupported OS");
		 }
	 }


	/**
	 * 执行adb命令
	 * @param cmd
	 * @return
	 */
	public String runAdbCmd(String cmd) {
		Process process=null;
		String result=null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			String s = null;
		    while ((s = input.readLine()) != null) {
		    	result= result==null? s : result+s;
		    }
			input.close();		    
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    if(process != null){
			    process.destroy();
			}
		}
		return result;
		
	}
	
	/**
	 * 执行adb命令，不会返回null
	 * @param cmd
	 * @return
	 */
	public String runDeviceInfoCmd(String cmd) {
		Process process=null;
		String result="";
		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			String s = null;
		    while ((s = input.readLine()) != null) {
		    	result=result+s;
		    }
			input.close();		    
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    Util.killProcess(process);
		}
		return result;
		
	}
	
	/**
	 * 获取未指定设备架构信息
	 * @return
	 */
	
	public HashMap<String, String> getCpu() {
//		String cpuCmd="adb shell cat /proc/cpuinfo";
		HashMap<String, String> cpuinfo=new HashMap<String, String>();
		String kernelVersionCmd= adbCommand +" shell cat /proc/version";
		String buildFingerPrintCmd= adbCommand +" shell getprop ro.build.fingerprint";
		String releaseCmd= adbCommand +" shell getprop ro.build.version.release";
		String idCmd= adbCommand +" shell getprop ro.build.id";
		String brandCmd= adbCommand +" shell getprop ro.product.brand";
		String manufacturerCmd= adbCommand +" shell getprop ro.product.manufacturer";
		String modelCmd= adbCommand +" shell getprop ro.product.model";
		String sdkCmd= adbCommand +" shell getprop ro.build.version.sdk";
		String builddateCmd= adbCommand +" shell getprop ro.build.date.utc";
		String abiCmd= adbCommand +" shell getprop ro.product.cpu.abi";
		String abi2Cmd= adbCommand +" shell getprop ro.product.cpu.abi2";
				
		String kernelVersion=runDeviceInfoCmd(kernelVersionCmd);
		String buildFingerPrint=runDeviceInfoCmd(buildFingerPrintCmd);
		String release=runDeviceInfoCmd(releaseCmd);
		String id=runDeviceInfoCmd(idCmd);
		String brand=runDeviceInfoCmd(brandCmd);
		String manufacturer=runDeviceInfoCmd(manufacturerCmd);
		String model=runDeviceInfoCmd(modelCmd);
		String sdk=runDeviceInfoCmd(sdkCmd);
		String builddate=runDeviceInfoCmd(builddateCmd);
		String abi=runDeviceInfoCmd(abiCmd);
		String abi2=runDeviceInfoCmd(abi2Cmd);		
		
		cpuinfo.put("kernelVersion", kernelVersion);
		cpuinfo.put("buildFingerPrint", buildFingerPrint);
		cpuinfo.put("release", release);
		cpuinfo.put("id", id);
		cpuinfo.put("brand", brand);
		cpuinfo.put("manufacturer", manufacturer);
		cpuinfo.put("model", model);
		cpuinfo.put("sdk", sdk);
		cpuinfo.put("builddate", builddate);
		cpuinfo.put("abi", abi);
		cpuinfo.put("abi2", abi2);
		
		return cpuinfo;
	}

	//获取系统api版本
	public  int getSysApi() {
		String command;
			command=adbCommand + " shell getprop ro.build.version.sdk";
		String api=runDeviceInfoCmd(command);
		return Integer.valueOf(api);
	}
	
	
	//指定设备进行测试
	public static void setTargetDevice(String number) {
		targetdevice=number;
		adbCommand += " -s "+targetdevice;
	}

	//指定设备地址
	public static void setTargetIp(String ip) {
		targetIP = ip + (ip.contains(":")? "" : ":5555");
		adbCommand += " -s " + targetIP;
	}
	
	public static void main(String args[]) {
		CommandCpu commandCpu=new CommandCpu();
		System.out.println(commandCpu.getSysApi());
	}

}
