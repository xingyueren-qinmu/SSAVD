【参数】
-t  进行测试
-d 显示待测设备信息
-l  测试时排除导致设备崩溃的测试用例

【使用示例】
java -jar SSAVDSoftSec.jar [FA68K0310186 | xxx.xxx.xxx.xxx(:5555)] [-t | -d] -l

测试时可直接 ctrl-c 结束测试，工具会自动完成当前测试样例并生成检测报告再退出程序。