package device;

import java.io.IOException;
import java.util.HashMap;

import util.CommandCpu;
import vulnerability_tests.NewPushTest;

/**
 * 
 * @author wxt
 * 待测设备
 */
public class DeviceInfo {
		
	 	public String kernelVersion;
	 	public String buildManufacturer;
	 	public String buildBrand;
	 	public String buildModel;
	 	public String buildRelease;
	 	public String buildSDK;
	 	public String buildDateUTC;
	 	public String buildFingerPrint;
	 	public String buildCpuABI;
	 	public String buildCpuABI2;
	  
	    
	    public  String buildID;
	    
	    public DeviceInfo(){
	    	
	    }
	
	    private DeviceInfo(String kVer,String bManufacturer,String bBrand, String bModel,String bRelease, String bSDK, 
	    		String bDateUTC, String bFingerPrint,String bID,String bCPUABI,String bCPUABI2){
				 this.kernelVersion = kVer;
				 this.buildManufacturer = bManufacturer;
				 this.buildBrand = bBrand;
				 this.buildModel = bModel;
				 this.buildRelease = bRelease;
				 this.buildSDK = bSDK;
				 this.buildDateUTC = bDateUTC;
				 this.buildFingerPrint = bFingerPrint;
				 this.buildID = bID;
				 this.buildCpuABI  = bCPUABI;
				 this.buildCpuABI2 = bCPUABI2;
	    }

	    //!!!!需要判断可能为null的情况。
//	    public static void setDeviceInfo(){
//	    	Command command=new Command();
//	    	HashMap<String, String> cpuinfo=command.getCpu();
//	    	kernelVersion=cpuinfo.get("kernelVersion");
//			buildFingerPrint=cpuinfo.get("buildFingerPrint");
//			buildRelease=cpuinfo.get("release");
//			buildID=cpuinfo.get("id");
//			buildBrand=cpuinfo.get("brand");
//			buildManufacturer=cpuinfo.get("manufacturer");
//			buildModel=cpuinfo.get("model");
//			buildSDK=cpuinfo.get("sdk");
//			buildDateUTC=cpuinfo.get("builddate");
//			buildCpuABI=cpuinfo.get("abi");
//			buildCpuABI2=cpuinfo.get("abi2");
//	    }
	    
	    public static DeviceInfo getDeviceInfo(){
	    	CommandCpu command=new CommandCpu();
	    	HashMap<String, String> cpuInfo=command.getCpu();
	    	String kernelVersion=cpuInfo.get("kernelVersion");
	    	String buildFingerPrint=cpuInfo.get("buildFingerPrint");
	    	String buildRelease=cpuInfo.get("release");
	    	String buildID=cpuInfo.get("id");
	    	String buildBrand=cpuInfo.get("brand");
	    	String buildManufacturer=cpuInfo.get("manufacturer");
	    	String buildModel=cpuInfo.get("model");
	    	String buildSDK=cpuInfo.get("sdk");
	    	String buildDateUTC=cpuInfo.get("builddate");
	    	String buildCpuABI=cpuInfo.get("abi");
	    	String buildCpuABI2=cpuInfo.get("abi2");
			NewPushTest.deviceID = buildModel + " " + buildID;

			return new DeviceInfo(kernelVersion, buildManufacturer, buildBrand, buildModel, buildRelease, buildSDK, buildDateUTC, buildFingerPrint, buildID, buildCpuABI, buildCpuABI2);
	    }

	    public void outputDeviceInfo(){
		    StringBuilder sb = new StringBuilder();
		    sb.append("\n[SSAVD]: Deviceinfo:\n");
		    sb.append("\tbrand: ");
		    sb.append(buildBrand);
		    sb.append("\n");
		    sb.append("\tmanufacturer: ");
		    sb.append(buildManufacturer);
		    sb.append("\n");
		    sb.append("\tid: ");
		    sb.append(buildID);
		    sb.append("\n");
		    sb.append("\tsdk: ");
		    sb.append(buildSDK);
		    sb.append("\n");
		    sb.append("\tkernelVersion: ");
		    sb.append(kernelVersion);
		    sb.append("\n");
		    sb.append("\tmodel: ");
		    sb.append(buildModel);
		    sb.append("\n");
		    sb.append("\tbuilddate: ");
		    sb.append(buildDateUTC);
		    sb.append("\n");
		    sb.append("\tcpuABI: ");
		    sb.append(buildCpuABI);
		    sb.append("\n");
		    sb.append("\tcpuABI2: ");
		    sb.append(buildCpuABI2);
		    sb.append("\n");
		    sb.append("\trelease: ");
		    sb.append(buildRelease);
		    sb.append("\n");
		    sb.append("\tfingerprint: ");
		    sb.append(buildFingerPrint);
		    sb.append("\n");
		    System.out.println(sb.toString());
//		    System.out.println("\nDo test(y/n)? ");
//		    try {
//			    char input = (char)System.in.read();
//			    return input == 'y';
//		    } catch (IOException e) {
//			    e.printStackTrace();
//		    }
	    }

		public String getKernelVersion() {
			return kernelVersion;
		}

		public String getBuildManufacturer() {
			return buildManufacturer;
		}

		public String getBuildBrand() {
			return buildBrand;
		}

		public String getBuildModel() {
			return buildModel;
		}

		public String getBuildRelease() {
			return buildRelease;
		}

		public String getBuildSDK() {
			return buildSDK;
		}

		public String getBuildDateUTC() {
			return buildDateUTC;
		}

		public String getBuildFingerPrint() {
			return buildFingerPrint;
		}

		public String getBuildCpuABI() {
			return buildCpuABI;
		}

		public String getBuildCpuABI2() {
			return buildCpuABI2;
		}

		public String getBuildID() {
			return buildID;
		}
	    
	    
	    
	    

}
