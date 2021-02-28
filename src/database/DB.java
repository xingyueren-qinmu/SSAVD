package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import util.Config;
import vulerability.VulnerabilityInfo;

public class DB {

	/**连接数据库的URL
	 * jdbc协议:数据库子协议:主机:端口/连接的数据库
	 */
	private String url = "jdbc:mysql://localhost:3306/AndroidVulnerability";
//			"?characterEncoding=utf8" +
//			"&useSSL=true";
	//用户名
	private String user = "root";
	//密码
	private String password = "toor";
	//连接
	private Connection conn;
	//table名
	private static String table_name = "vulnerabilityinfo_copy";
//	private static String table_name = "vulnerabilityinfo";

	public DB(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//2.连接到具体的数据库
			conn = DriverManager.getConnection(url, user, password);
			//3.创建连接语句
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//根据cve编号查找漏洞信息
	public VulnerabilityInfo getVulnerabilityInfo(String cveid) {
		VulnerabilityInfo vulnerability=new VulnerabilityInfo();
		//创建statement类对象，用来执行SQL语句！！
		try {
			Statement stat = conn.createStatement();
			if(stat!=null) {				
				//要执行的SQL语句
				String sql = "SELECT * FROM " + table_name + " WHERE CVEId='"+cveid+"'";
//				System.out.println(sql);
				//ResultSet类，用来存放获取的结果集！！
				ResultSet rs = stat.executeQuery(sql);
				while(rs.next()){
					setVulnerability(vulnerability, rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vulnerability;
	}

	private void setVulnerability(VulnerabilityInfo vulnerability, ResultSet rs) throws SQLException {
		vulnerability.setCveId(rs.getString("cveId"));
		vulnerability.setAltNames(rs.getString("altNames"));
		vulnerability.setType(rs.getString("type"));
		vulnerability.setDescription(rs.getString("description"));
		vulnerability.setImpact(rs.getString("impact"));
		vulnerability.setLocation(rs.getString("location"));
		vulnerability.setCVSSV2Score(rs.getString("CVSSV2Score"));

		vulnerability.setTestType(rs.getString("testType"));
		vulnerability.setPocName(rs.getString("pocName"));
//		System.out.println(vulnerability.getPocName());
		HashMap<String, String> isVulneralityHash=new HashMap<>();
		String[] IsVulnerality=rs.getString("isVulnerality").split(";");
		for(int i=0;i<IsVulnerality.length;i++) {
			String isVulnerality=IsVulnerality[i];
			String[] isVulneralityArray=isVulnerality.split(":");
			if(isVulneralityArray[0].equals("Not Vulnerable"))
				isVulneralityHash.put("Not Vulnerable", isVulneralityArray[1]);
			else if (isVulneralityArray[0].equals("error"))
				isVulneralityHash.put("error", isVulneralityArray[1]);
			else if (isVulneralityArray[0].equals("Vulnerable")){
				try {
					Integer.valueOf(isVulneralityArray[1]);
				} catch (NumberFormatException e){
					if(isVulneralityArray[1].equals("reboot"))
						vulnerability.setParticularOutcome(Config.PARTICULAR_OUTCOME_REBOOT);
					if(isVulneralityArray[1].equals("shutdown"))
						vulnerability.setParticularOutcome(Config.PARTICULAR_OUTCOME_SHUTDOWN);
				}
				isVulneralityHash.put("Vulnerable", isVulneralityArray[1]);

			}

		}
		vulnerability.setIsVulnerality(isVulneralityHash);
	}

	//查找全部漏洞信息
	public ArrayList<VulnerabilityInfo> getAllVulnerabilities() {
		ArrayList<VulnerabilityInfo> vulnerabilitys=new ArrayList<VulnerabilityInfo>();
		//创建statement类对象，用来执行SQL语句！！
		try {
			Statement stat = conn.createStatement();
			if(stat!=null) {
				//要执行的SQL语句
				String sql = "SELECT * FROM " + table_name + " ORDER BY isVulnerality DESC";
				//ResultSet类，用来存放获取的结果集！！
				ResultSet rs = stat.executeQuery(sql);
				while(rs.next()){
					VulnerabilityInfo vulnerability=new VulnerabilityInfo();
					setVulnerability(vulnerability, rs);
					vulnerabilitys.add(vulnerability);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vulnerabilitys;
	}

	//测试
	public static void main(String args[]) {
		DB db=new DB();
		VulnerabilityInfo vulnerability=db.getVulnerabilityInfo("cve-2011-1149");
		System.out.println(vulnerability.getTestType());
		HashMap<String, String> isVulneralityHash=vulnerability.getIsVulnerality();
		System.out.println(isVulneralityHash.get("Vulnerable")+isVulneralityHash.get("Not Vulnerable")+isVulneralityHash.get("Error"));
	}
	
}