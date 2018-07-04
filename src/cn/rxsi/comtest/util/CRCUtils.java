package cn.rxsi.comtest.util;

public class CRCUtils {

	public String getCrc(byte[] data) {
		int high;
		int flag;
 
		// 16位寄存器，所有数位均为1
		int wcrc = 0xffff;
		for (int i = 0; i < data.length; i++) {
			// 16 位寄存器的高位字节
			high = wcrc >> 8;
			// 取被校验串的一个字节与 16 位寄存器的高位字节进行“异或”运算
			wcrc = high ^ data[i];
 
			for (int j = 0; j < 8; j++) {
				flag = wcrc & 0x0001;
				// 把这个 16 寄存器向右移一位
				wcrc = wcrc >> 1;
				// 若向右(标记位)移出的数位是 1,则生成多项式 1010 0000 0000 0001 和这个寄存器进行“异或”运算
				if (flag == 1)
					wcrc ^= 0xa001;
			}
		}
 
		return Integer.toHexString(wcrc);
}
}
