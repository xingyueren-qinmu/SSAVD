package util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import vulerability.VulnerabilityInfo;

@SuppressWarnings("ALL")
public class Command {


	 //在开始尝试恢复之前等待的时间（以ms为单位）
	protected static final long REVOCER_PAUSE_TIME = 15 * 1000;

	public static final String COMMAND_EXIT = "exit\n";
	public static final String COMMAND_LINE_END = "\n";

	static String testapkpath=System.getProperty("user.dir")+File.separator+"tools"+File.separator+"cvd_app.apk";

	static String appPackageName="com.nowsecure.android.vts";
	static String startactivityString="fuzion24.device.vulnerability.test.ui.MainTest";
   private String filePath ="sdcard/result.json";
   private String localfilePath =System.getProperty("user.dir")+File.separator+"result";

//   private static String adbpath="tools"+File.separator+"adb";
//   private static String fastbootpath="tools"+File.separator+"fastboot";
//   private static String targetdevice="";
	private static String platform="";
	private static String adbpath="";
	private static String fastbootpath="";
	private static String targetdevice="";
	private static String targetIp = "";
	public static String adbCommand = "";

	public void kill(){
		String cmd = adbpath + " kill-server";
	}

	public void buildPocPath(){
		String rmCmd, mkdirCmd;
//		rmCmd = adbCommand + " rm -rf " + Config.TEMP_POC_LOCATION;
//		runCommand(rmCmd);
//		 mkdirCmd = adbCommand + " mkdir -p " + Config.TEMP_POC_LOCATION;
//		runCommand(mkdirCmd);
		rmCmd = adbCommand + " rm -rf " + Config.FINAL_POC_LOCATION;
		runCommand(rmCmd);
		mkdirCmd = adbCommand + " mkdir -p " + Config.FINAL_POC_LOCATION;
		runCommand(mkdirCmd);
	}

   public void platForm() {
		String os = System.getProperty("os.name");
		if(os.toLowerCase().startsWith("win")){
			platform = "Windows";
			adbpath="tools"+File.separator+platform+File.separator+"adb";

		} else if(os.toLowerCase().startsWith("linux")) {
			platform = "Linux";
			adbpath="./tools"+File.separator+platform+File.separator+"adb";

		} else if(os.toLowerCase().startsWith("mac")){
			platform = "Mac";
			adbpath="./tools"+File.separator+platform+File.separator+"adb";

		} else {
			System.out.println("Unsupported OS");
		}



		fastbootpath="tools"+File.separator+platform+File.separator+"fastboot";
		if(targetdevice != "") adbCommand = adbpath + " -s " + targetdevice + " shell";
		else if(targetIp != "") adbCommand = adbpath + " -s " + targetIp + " shell";
		else adbCommand = adbpath + " shell";
	}

	//执行单条命令
	public HashMap<String, ArrayList<String>> runCommand(String cmd) {
		HashMap<String, ArrayList<String>> result=new HashMap<String, ArrayList<String>>();
		ArrayList<String> inArrayList=new ArrayList<String>();
		ArrayList<String> errArrayList=new ArrayList<String>();
		ArrayList<String> failArrayList=new ArrayList<String>();
		try
		{
			if(cmd!=null) {
				final Process process = Runtime.getRuntime().exec(cmd);
				int res = handleInputThread(process, inArrayList, errArrayList);
				// inArrayList最后一单元携带process返回值
				inArrayList.add(Integer.toString(process.waitFor()));
				Util.killProcess(process);
			}
		} catch (Exception e) {
			 failArrayList.add(e.getMessage());
		}
		result.put("in", inArrayList);
		result.put("err", errArrayList);
		result.put("fail",failArrayList);
		return result;
	}

	//执行单条命令
//	public HashMap<String, ArrayList<String>> runPocCommand(String cmd) {
//		HashMap<String, ArrayList<String>> result=new HashMap<String, ArrayList<String>>();
//		ArrayList<String> inArrayList=new ArrayList<String>();
//		ArrayList<String> errArrayList=new ArrayList<String>();
//		ArrayList<String> failArrayList=new ArrayList<String>();
//		try
//		{
//			if(cmd!=null) {
//				final Process process = Runtime.getRuntime().exec(cmd);
//				new HandleInputThread(process.getInputStream(), "info").run();
//				new HandleInputThread(process.getErrorStream(), "info").run();
//				// inArrayList最后一单元携带process返回值
//				inArrayList.add(Integer.toString(process.waitFor()));
//				process.destroy();
//			}
//		} catch (Exception e) {
//			failArrayList.add(e.getMessage());
//		}
//		result.put("in", inArrayList);
//		result.put("err", errArrayList);
//		result.put("fail",failArrayList);
//		return result;
//	}

	public int handleInputThread(Process process, ArrayList<String> inArrayList, ArrayList<String> errArrayList) throws InterruptedException {

		//处理InputStream的线程
		Thread inThread=new Thread()
		{
			@Override
			public void run()
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = null;
				try {
					while((line = in.readLine()) != null)
					{
//						System.out.println(line);
						inArrayList.add(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try
					{
						in.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		};

		Thread errThread=new Thread()
		{
			@Override
			public void run()
			{
				BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line = null;

				try{
					while((line = err.readLine()) != null)
					{
//								System.out.println("err: " + line);
						errArrayList.add(line);
					}
				}catch (IOException e) {
					e.printStackTrace();
				}finally{
					try
					{
						err.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		inThread.start();
		errThread.start();
		inThread.join();
		errThread.join();
		int res = process.waitFor();
		return res;
	}

	//执行多条命令
	public HashMap<String, ArrayList<String>> runCommands(ArrayList<String> cmds) {
		HashMap<String, ArrayList<String>> result=new HashMap<String, ArrayList<String>>();
		ArrayList<String> inArrayList=new ArrayList<String>();
		ArrayList<String> errArrayList=new ArrayList<String>();
		ArrayList<String> failArrayList=new ArrayList<String>();
		try
		{
			if(cmds!=null) {
				final Process process = Runtime.getRuntime().exec(cmds.get(0));
				DataOutputStream os = new DataOutputStream(process.getOutputStream());

				for (int i = 1; i < cmds.size(); i++) {
					os.write(cmds.get(i).getBytes());
					os.writeBytes(COMMAND_LINE_END);
					os.flush();
				}

				os.writeBytes(COMMAND_EXIT);
				os.flush();
				handleInputThread(process, inArrayList, errArrayList);
			}

		} catch (Exception e) {
			 failArrayList.add(e.getMessage());
		}
		result.put("in", inArrayList);
		result.put("err", errArrayList);
		result.put("fail",failArrayList);
		return result;
	}

    /**
     * 检测设备连接状态:可以识别设备，并且可以执行终端命令
     * @return 连接成功：“success”；连接失败：“failure”；连接出错：“error+e.message”。
     */
	public String adbconnect() {
		String connect_result="failure";
		if(targetIp == ""){
			String adbconnectCmd = adbpath + " devices";
			HashMap<String, ArrayList<String>> result=runCommand(adbconnectCmd);
			ArrayList<String> inArray=result.get("in");
			ArrayList<String> errArray=result.get("err");
			ArrayList<String> failArray=result.get("fail");

			if(failArray!=null && !failArray.isEmpty()) {
				connect_result=arrayToString(failArray);
				Util.errorOutput(arrayToString(failArray));
			}else if(errArray!=null && !errArray.isEmpty()){
				connect_result=arrayToString(errArray);
				Util.errorOutput(arrayToString(errArray));
			}else if (inArray!=null && inArray.size()>1 && inArray.get(0).equals("List of devices attached")) {
				for(int i=1;i<inArray.size();i++) {
					String s=inArray.get(i);
					if(s!=null) {

					}

					if(targetdevice!="") {
						if(s!=null && s.contains(targetdevice)) {
							if(s.contains("device")) {
								connect_result="success:"+s.replace("device", "").trim();
								break;
							}else if (s.contains("unauthorized")) {
								connect_result="failure: device unauthorized";
								break;
							}
						}
					}else {
						if(s!=null) {
							if(s.contains("device")) {
								connect_result="success:"+s.replace("device", "").trim();
								break;
							} else if (s.contains("unauthorized")) {
								connect_result="failure: device unauthorized";
								break;
							}
						}
					}
				}
			}
		} else {
			String adbconnectCmd = adbpath + " connect " + targetIp;
//			System.out.println(adbconnectCmd);
			HashMap<String, ArrayList<String>> result=runCommand(adbconnectCmd);
			ArrayList<String> inArray=result.get("in");
			ArrayList<String> errArray=result.get("err");
			ArrayList<String> failArray=result.get("fail");

			if(failArray!=null && !failArray.isEmpty()) {
				connect_result=arrayToString(failArray);
				Util.errorOutput(connect_result);
			}else if(errArray!=null && !errArray.isEmpty()){
				connect_result=arrayToString(errArray);
				Util.errorOutput(connect_result);
			}else {
				for(int i=0;i<inArray.size();i++) {
					String s=inArray.get(i);
					if(s.contains("connected to")){
						if(s!=null) {}
						connect_result="success:"+s.replace("connected to", "").trim();
						break;
					}
					else if (s.contains("10061")) {
						connect_result="failure: device rejected.";
						break;
					}
				}
			}
		}
		return connect_result;
	}


	/**
	 * 安装应用
	 * @return 安装失败：“failure:failure_message”；安装成功：“success:success_message”；安装出错：“error:error_message”.
	 */
	public String install() {
		String installCmd = adbpath+" install -r " + testapkpath;
		String installTargetCmd=adbpath+" -s "+targetdevice+" install -r "+testapkpath;
		String install_result = "failure:";

		HashMap<String, ArrayList<String>> result=null;
		if(targetdevice!="") {
			result=runCommand(installTargetCmd);
		}else {
			result=runCommand(installCmd);
		}

		ArrayList<String> inArray=result.get("in");
		ArrayList<String> errArray=result.get("err");
		ArrayList<String> failArray=result.get("fail");

		if(failArray!=null && !failArray.isEmpty()) {
			install_result="error:"+arrayToString(failArray);
		}else if(errArray!=null && !errArray.isEmpty()){
			install_result="error:"+arrayToString(errArray);
		}else if (inArray!=null && !inArray.isEmpty()) {
			for(String s:inArray) {
				if(s.contains("Success")) {
					install_result="success:"+s;
					break;
				}else if (s.contains("error") || s.contains("Error") || s.contains("failure") || s.contains("Failure")) {
		    		install_result="failure:"+s;
		    		break;
				}
			}
		}
		return install_result;
	}


	/**
	 * 开启应用
	 * @return 成功：“success”；失败：“failure:failure_message”；错误：“error:error_message”
	 */
	public String startapp() {
		String startCmd = "am start -n " + appPackageName+File.separator+startactivityString;
		String adbshellCmd=adbpath+" shell";
		String adbshellTargetCmd=adbpath+" -s "+targetdevice+" shell";
		String startapp_result = "failure:";

		ArrayList<String> cmds=new ArrayList<String>();
		cmds.add(adbshellCmd);
		cmds.add(startCmd);
		ArrayList<String> target_cmds=new ArrayList<String>();
		target_cmds.add(adbshellTargetCmd);
		target_cmds.add(startCmd);

		HashMap<String, ArrayList<String>> result=null;
		if(targetdevice!="") {
			result=runCommands(target_cmds);
		}else {
			result=runCommands(cmds);
		}

		ArrayList<String> inArray=result.get("in");
		ArrayList<String> errArray=result.get("err");
		ArrayList<String> failArray=result.get("fail");

		if(failArray!=null && !failArray.isEmpty()) {
			startapp_result="error:"+arrayToString(failArray);
		}else if(errArray!=null && !errArray.isEmpty()){
			startapp_result="error:"+arrayToString(errArray);
		}else if (inArray!=null && !inArray.isEmpty()) {
			String inString=arrayToString(inArray);
			if(inString.contains("Error")) {
				for(String s:inArray) {
					if(s.contains("Error:")) {
						startapp_result="failure:"+s;
			    		break;
			    	}
				}
			}else {
				for(String s:inArray) {
					if (s.contains("Starting: Intent")) {
			    		startapp_result="success";
						break;
					}
				}
			}
		}
		return startapp_result;
	}


	/**
	 * 拉取结果文件
	 * @return 成功：“success”；失败：“failure:failure_message”；错误：“error:error_message”
	 */
	public String pulljson() {
		String pullCmd = adbpath+" pull " + filePath +" "+localfilePath;
		String deleteCmd=adbpath+" shell rm -rf " + filePath;
		String pullTargetCmd = adbpath+" -s "+targetdevice+" pull " + filePath +" "+localfilePath;
		String deleteTargetCmd = adbpath+" -s "+targetdevice+" shell rm -rf " + filePath;

		String pulljson_result = "failure:";

		HashMap<String, ArrayList<String>> result=null;
		if(targetdevice!="") {
			result=runCommand(pullTargetCmd);
		}else {
			result=runCommand(pullCmd);
		}

		ArrayList<String> inArray=result.get("in");
		ArrayList<String> errArray=result.get("err");
		ArrayList<String> failArray=result.get("fail");

		if(failArray!=null && !failArray.isEmpty()) {
			pulljson_result="error:"+arrayToString(failArray);
		}else if(errArray!=null && !errArray.isEmpty()){
			pulljson_result="error:"+arrayToString(errArray);
		}else if (inArray!=null && !inArray.isEmpty()) {
			for(String s:inArray) {
				if(s.contains("1 file pulled")) {
					pulljson_result="success";
		    		break;
		    	}else if(s.contains("error") || s.contains("Error") || s.contains("failure") || s.contains("Failure") ){
		    		pulljson_result="failure:"+s;
		    		break;
				}
			}
		}

		if(targetdevice!="") {
			runCommand(deleteTargetCmd);
		}else {
			runCommand(deleteCmd);
		}
		return pulljson_result;
	}


	/**
	 * 卸载应用
	 * @return 成功：“success”；失败：“failure:failure_message”；错误：“error:error_message”
	 */
	public String uninstall() {
		String uninstallCmd = adbpath+" uninstall " + appPackageName;
		String uninstallTragetCmd = adbpath+" -s "+targetdevice+" uninstall " + appPackageName;
		String uninstall_result = "failure:";


		HashMap<String, ArrayList<String>> result=null;
		if(targetdevice!="") {
			result=runCommand(uninstallTragetCmd);
		}else {
			result=runCommand(uninstallCmd);
		}

		ArrayList<String> inArray=result.get("in");
		ArrayList<String> errArray=result.get("err");
		ArrayList<String> failArray=result.get("fail");

		if(failArray!=null && !failArray.isEmpty()) {
			uninstall_result="error:"+arrayToString(failArray);
		}else if(errArray!=null && !errArray.isEmpty()){
			uninstall_result="error:"+arrayToString(errArray);
		}else if (inArray!=null && !inArray.isEmpty()) {
			for(String s:inArray) {
				if(s.contains("Success")) {
					uninstall_result="success";
		    		break;
		    	}else if (s.contains("error") || s.contains("Error") || s.contains("failure") || s.contains("Failure") ) {
		    		uninstall_result="failure:"+s;
					break;
				}
			}
		}
		return uninstall_result;
	}


	/**
	 * 向设备中push文件
	 * @param path
	 * @param remotePath
	 * @return 成功：“success”；失败：“failure:failure_message”；错误：“error:error_message”
	 */
	public String pushFile(String path,String remotePath) {

		String pushFile_result = "failure:";

		HashMap<String, ArrayList<String>> result=null;

//		String pushCmd = adbCommand + " push " + path +" "+remotePath;
//		result = runCommand(pushCmd);
		if(targetdevice!="") {
			String pushTargetCmd = adbpath+" -s "+targetdevice+" push " + path +" "+remotePath;
			result=runCommand(pushTargetCmd);
		}else if(targetIp != "") {
			String pushTargetIpCmd = adbpath + " -s " + targetIp + " push " + path +" "+remotePath;
			result = runCommand(pushTargetIpCmd);
		} else {
			String pushCmd = adbpath+" push " + path +" "+remotePath;
			result=runCommand(pushCmd);
		}

		ArrayList<String> inArray=result.get("in");
		ArrayList<String> errArray=result.get("err");
		ArrayList<String> failArray=result.get("fail");

		if(failArray!=null && !failArray.isEmpty()) {
			pushFile_result="error:"+arrayToString(failArray);
		}else if(errArray!=null && !errArray.isEmpty()){
			pushFile_result="error:"+arrayToString(errArray);
		}else if (inArray!=null && !inArray.isEmpty()) {
			for(String s:inArray) {
				if(s.contains("1 file pushed")) {
					pushFile_result="success";
		    		break;
				}else if(s.contains("error") || s.contains("Error") || s.contains("failure") || s.contains("Failure") ){
					pushFile_result="failure:"+s;
		    		break;
				}
			}
		}
		return pushFile_result;
	}

	private boolean noError(HashMap<String, ArrayList<String>> result){
		return ((result.get("err")==null || result.get("err").isEmpty()) && (result.get("fail")==null || result.get("fail").isEmpty()));
	}

	public String copyPoc(){

		//将poc文件移动到/data/local/tmp/SSAVD/pocs/目录
		String move_poc=adbCommand + " cp " + Config.TEMP_POC_LOCATION + "* " + Config.FINAL_POC_LOCATION;
		//赋予可执行权限
		String permission=adbCommand + " chmod +x " + Config.FINAL_POC_LOCATION + "*";

		HashMap<String, ArrayList<String>> move_poc_result=runCommand(move_poc);
		if(noError(move_poc_result)) {
			HashMap<String, ArrayList<String>> permission_result=runCommand(permission);
			if(noError(permission_result)) {
				return "success";
			}else {
				return "error:"+arrayToString(permission_result.get("err"))+arrayToString(permission_result.get("fail"));
			}
		}else {
			return "error:"+arrayToString(move_poc_result.get("err"))+arrayToString(move_poc_result.get("fail"));
		}
	}

	/**
	 * 执行poc脚本
	 * @param vtr
	 * @return 成功：“success:+返回信息”；失败：“failure:failure_message”；错误：“error:error_message”
	 */
	public HashMap<String, ArrayList<String>> runPoc(VulnerabilityInfo vul) {
		String pocName = vul.getPocName();
		String runPocCommand = adbCommand + " su -c ." + Config.FINAL_POC_LOCATION + pocName;
		HashMap<String, ArrayList<String>> commandReturn = runCommand(runPocCommand);
		return commandReturn;
	}

	public void cleanPocPaths(){
		String rmCmd = adbCommand + " rm -rf " + Config.FINAL_POC_LOCATION;
		runCommand(rmCmd);
//		rmCmd = adbCommand + " rm -rf " + Config.TEMP_POC_LOCATION;
//		runCommand(rmCmd);
	}

	//处理array
	public String arrayToString(ArrayList<String> arrayList) {
		String result="";
		if(arrayList!=null) {
			for(String string:arrayList) {
				result=result+string;
			}
		}
		return result;
	}

	public boolean recoverFromReboot(){
		int sleepTime = 0;
		boolean isCartoonPlaying = false;
		String hasRebooted = adbCommand + " getprop init.svc.bootanim";
		do {
			try {
				Thread.sleep(5 * 1000);
				Util.recoverOutput("Recovering...");
				if(!isCartoonPlaying){
					String connect = adbconnect();
					if(connect.contains("success")) isCartoonPlaying = true;
				}
				if(isCartoonPlaying){
					HashMap<String, ArrayList<String>> rebootRes = runCommand(hasRebooted);
					if(rebootRes.get("in") != null)
						if(rebootRes.get("in").get(0).contains("stopped")) {
							Util.recoverOutput("Reboot success! Please wait seconds until the devices is ready.");
							Thread.sleep(5 * 1000);
							runCommand(adbCommand + " kill-server");
							Thread.sleep(2 * 1000);
							if(adbconnect().contains("success"))
								return true;
							else continue;
						}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while((++sleepTime) < 20);
		return false;
	}

	/**
	 * 尝试恢复设备
	 * @return 成功：success；失败：fail；未授权需要人工处理：unauthorized。
	 */
	public String recoverDevice() {
		String recoverResult="fail";

		Util.recoverOutput("device offline and try to recover...");
		try {
			//设备可能在自动重启，休眠一会
			Thread.sleep(REVOCER_PAUSE_TIME);
			String connect=adbconnect();
    		if(connect.contains("success")) {
    			Util.recoverOutput("device "+connect.split(":")[1]+" recover success!");
    			return "success";
    		}else if (connect.contains("unauthorized")) {
    			return "unauthorized";
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String queryfastboot = fastbootpath+" devices";
		String fastbootreboot = fastbootpath+" reboot";
		String reboot=adbpath + " reboot";

		String queryTargetFastboot = fastbootpath+" -s "+targetdevice+" devices";
		String fastbootTargetReboot = fastbootpath+" -s "+targetdevice+" reboot";
		String rebootTarget=adbpath + " -s "+targetdevice+" reboot";

		try {
			//判断设备是否进入了fastboot模式
			ArrayList<String> fastbootResult=null;
			if(targetdevice!="") {
				fastbootResult=runCommand(queryTargetFastboot).get("in");
			 }else {
				fastbootResult=runCommand(queryfastboot).get("in");
			 }


		    //设备进入fastboot模式后恢复设备
		    if(arrayToString(fastbootResult).contains("fastboot")) {
		    	Util.recoverOutput("device in fastboot mode and recover ...");
		    	ArrayList<String> fastboot_reboot_result=null;
				if(targetdevice!="") {
					fastboot_reboot_result=runCommand(fastbootTargetReboot).get("err");
				 }else {
					 fastboot_reboot_result=runCommand(fastbootreboot).get("err");
				 }

		    	if(arrayToString(fastboot_reboot_result).contains("Finished")) {
		    		Thread.sleep(REVOCER_PAUSE_TIME);
		    		String connect=adbconnect();
		    		if(connect.contains("success")) {
					    Util.recoverOutput("device "+connect.split(":")[1]+" recover success!");
		    			return "success";
		    		}else if (connect.contains("unauthorized")) {
		    			return "unauthorized";
					}
		    	}
		    }else {
		    	//设备未进入fastboot模式，尝试重启设备进行恢复
			    Util.recoverOutput("reboot device... ");
				ArrayList<String> reboot_result=null;
				if(targetdevice!="") {
					reboot_result=runCommand(rebootTarget).get("in");
				 }else {
					 reboot_result=runCommand(reboot).get("in");
				 }
				if(arrayToString(reboot_result).contains("error")) {
					Util.recoverOutput("reboot device failure, please check device debug mode.");
					return "false";
				}else {
					Thread.sleep(REVOCER_PAUSE_TIME);
		    		String connect=adbconnect();
		    		if(connect.contains("success")) {
					    Util.recoverOutput("device "+connect.split(":")[1]+" recover success!");
		    			return "success";
		    		}else if (connect.contains("unauthorized")) {
		    			return "unauthorized";
					}
				}
			}

		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		return recoverResult;
	}

	//指定设备进行测试
	public void setTargetDevice(String number) {
		targetdevice=number;
	}

	//指定设备地址
	public void setTargetIp(String ip) {
		targetIp = ip + (ip.contains(":")? "" : ":5555");
	}

	public static void main(String args[]) {
		Command commandRun=new Command();
//		System.out.println(commandRun.pushPoc("/Users/wxt/eclipse-workspace/ssavd/Cases/armeabi-v7a/cve-2014-3153check"));
		System.out.println(commandRun.recoverDevice());
//		System.out.println(commandRun.test());

	}
}

class HandleInputThread extends Thread{
	InputStream is;
	String type;
	HandleInputThread(InputStream is, String printType){
		this.is = is;
		this.type = printType;
	}

	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {
			while((line = br.readLine()) != null) {
				System.out.println("[" + type + "]:" + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}