【参数】
-t 进行测试
-l 测试时排除导致设备崩溃的测试用例
q-d 显示待测设备信息

【使用示例】
java -jar SSAVDSoftSec.jar [FA68K0310186 | xxx.xxx.xxx.xxx(:5555)] [-t (-l) (-a) | -d] 

测试时可直接 ctrl-c 结束测试，工具会自动完成当前测试样例并生成检测报告再退出程序。

【结果文件解析】
结果文件位于 ./result/yyyyMMdd-mmSS_DeviceModel DeviceID.json，例如./result/20200318-2108_Pixel NZH54D.json。
结果文件为json格式，其中每条结果中包含漏洞的信息和测试结果，如下是两条测试结果样例：
{
  "exception": "null",
  "name": "cve-2016-3866",
  "vulExists": false,
  "details": {
    "altNames": "",
    "CVSSV2Score": "9.3",
    "impact": "",
    "description": "The Qualcomm sound driver in Android allows attackers to gain privileges via a crafted application",
    "location": "Qualcomm sound driver",
    "type": "权限提升"
  }
},
{
  "exception": "null",
  "name": "cve-2016-3867",
  "vulExists": true,
  "details": {
    "altNames": "",
    "CVSSV2Score": "9.3",
    "impact": "",
    "description": "The Qualcomm IPA driver in Android allows attackers to gain privileges via a crafted application",
    "location": "Qualcomm IPA driver",
    "type": "权限提升"
  }
}
每条结果中的"vulExists"键值对表示是否检测出该漏洞，若值为true，则表示存在，false为不存在。注意，若使用工具时未输入参数"-a"则结果只展示存在的漏洞，所有"vulExists"皆为true