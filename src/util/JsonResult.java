package util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import device.DeviceInfo;
import vulerability.VulnerabilityInfo;
import vulerability.VulnerabilityTestResult;

public class JsonResult {
	
	 public static JSONObject serializeAllResultsToJson(ArrayList<VulnerabilityTestResult> results,DeviceInfo deviceInfo) {
		 
		  	JSONArray testResults = new JSONArray();
	        JSONObject buildInfo = new JSONObject();
	        JSONObject combinedResults = new JSONObject();
	        if(deviceInfo!=null) {
	        	buildInfo.put("fingerprint", deviceInfo.getBuildFingerPrint());
	 	        buildInfo.put("kernelVersion", deviceInfo.getKernelVersion());
	 	        buildInfo.put("brand", deviceInfo.getBuildBrand());
	 	        buildInfo.put("manufacturer", deviceInfo.getBuildManufacturer());
	 	        buildInfo.put("model", deviceInfo.getBuildModel());
	 	        buildInfo.put("release", deviceInfo.getBuildRelease());
	 	        buildInfo.put("sdk", deviceInfo.getBuildSDK());
	 	        buildInfo.put("builddate", deviceInfo.getBuildDateUTC());
	 	        buildInfo.put("id", deviceInfo.getBuildID());
	 	        buildInfo.put("cpuABI", deviceInfo.getBuildCpuABI());
	 	        buildInfo.put("cpuABI2", deviceInfo.getBuildCpuABI2());
	 	        
	        }
	        if(results!=null) {
	        	for (VulnerabilityTestResult vtr : results) {
		        	VulnerabilityInfo vInfo=vtr.getVulnerabilityInfo();
		            JSONObject res = new JSONObject();
		            res.put("name", vInfo.getCveId());
		            
		            if(vtr.isVulnerable()!=null) {
		            	res.put("vulExists", vtr.isVulnerable());
		            }else {
		            	res.put("vulExists", "null");
					}
		            
		            if(vtr.getException()!=null) {
		            	res.put("exception", vtr.getException());
		            }else {
		            	res.put("exception", "null");
					}          		            
	
		            JSONObject detail = new JSONObject();
		            
		            detail.put("altNames",vInfo.getAltNames());
		            detail.put("type", vInfo.getType());
	                detail.put("description", vInfo.getDescription());
	                detail.put("impact", vInfo.getImpact());
	                detail.put("CVSSV2Score", vInfo.getCVSSV2Score());
	                detail.put("location", vInfo.getLocation());
		            res.put("details", detail);
		            testResults.put(res);
		        }
	        }
		        	        
	        combinedResults.put("buildInfo", buildInfo);
	        combinedResults.put("results", testResults);

	     return combinedResults;

	  
	 }

}
