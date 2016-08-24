package winw.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

public class InetAddressUtilsTest {

	static String IPV4 = "192.168.0.1";
	static String IPV4_RANGE = "192.168.0.1-192.168.0.255";
	static String IPV4_SAMPLE_RANGE = "192.168.0.1-255";
	static String SUB_NET_CIDR = "192.168.0.1/16";

	@Test
	public void testIsIPv4Address() {
		Assert.assertTrue(InetAddressUtils.isIPv4Address(IPV4));
	}

	@Test
	public void testIsIPv4RangeAddress() {
		Assert.assertTrue(InetAddressUtils.isIPv4RangeAddress(IPV4_RANGE));
	}

	@Test
	public void testIsIPv4SimpleRangeAddress() {
		Assert.assertTrue(InetAddressUtils.isIPv4SimpleRangeAddress(IPV4_SAMPLE_RANGE));
	}

	@Test
	public void testIsSubNetCIDRAddress() {
		Assert.assertTrue(InetAddressUtils.isSubNetCIDRAddress(SUB_NET_CIDR));
	}

	@Test
	public void testIsInRange() throws UnknownHostException {
		InetAddress address = InetAddress.getByName(IPV4);
		Assert.assertTrue(InetAddressUtils.isInRange(address, IPV4));
		Assert.assertTrue(InetAddressUtils.isInRange(address, IPV4_RANGE));
		Assert.assertTrue(InetAddressUtils.isInRange(address, IPV4_SAMPLE_RANGE));
		Assert.assertTrue(InetAddressUtils.isInRange(address, SUB_NET_CIDR));
		address = InetAddress.getByName("192.169.0.1");
		Assert.assertFalse(InetAddressUtils.isInRange(address, IPV4));
		Assert.assertFalse(InetAddressUtils.isInRange(address, IPV4_RANGE));
		Assert.assertFalse(InetAddressUtils.isInRange(address, IPV4_SAMPLE_RANGE));
		Assert.assertFalse(InetAddressUtils.isInRange(address, SUB_NET_CIDR));
	}

	@Test
	public void testIsInRangeCase1() throws UnknownHostException {
		Assert.assertTrue(isInEtonenet("180.169.33.101"));// iiie6.etonenet.com
		Assert.assertTrue(isInEtonenet("10.8.10.46"));// ST001

		Assert.assertFalse(isInEtonenet("61.135.169.121"));// www.baidu.com
		Assert.assertFalse(isInEtonenet("10.7.10.46"));// other
		
	}

	private boolean isInEtonenet(String address) throws UnknownHostException {
		String etonenetIP = "10.0.0.0/16,10.1.0.0/16,10.8.0.0/16,"// 公司办公室内网
				+ "218.202.226.92-103,218.202.226.108-123, 218.202.226.132-139,"// 怒江机房移动
				+ "180.169.33.98-102,"// 怒江机房电信
				+ "27.115.100.178-182,"// 怒江机房联通
				+ "210.22.108.74";// 公司办公室外网
		return InetAddressUtils.isInRange(InetAddress.getByName(address), etonenetIP);
	}

	@Test
	public void testIsInRangePerformance() throws UnknownHostException {
		long sTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			testIsInRange();// 8
			testIsInRangeCase1();// 4
		}
		System.out.println("120000 , cost: " + (System.currentTimeMillis() - sTime));
		System.out.println(InetAddress.getByName("www.baidu.com"));
	}
}
