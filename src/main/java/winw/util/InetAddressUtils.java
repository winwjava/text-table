package winw.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * A collection of utilities relating to InetAddresses.
 * 
 * <Point>
 * Note: now only support IPv4 address.
 * 
 * <Point>
 * TODO Support hostname.
 * 
 * @author sjyao
 *
 */
public final class InetAddressUtils {
	// final field, 0-255.
	private static final String IPV4_FINAL_FIELD_PATTERN_STRING = "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";

	// initial 3 fields, 0-255 followed by , and final field, 0-255.
	private static final String IPV4_BASIC_PATTERN_STRING = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}"
			+ IPV4_FINAL_FIELD_PATTERN_STRING;

	private static final Pattern IPV4_PATTERN = Pattern.compile("^" + IPV4_BASIC_PATTERN_STRING + "$");

	private static final Pattern IPV4_RANGE_PATTERN = Pattern
			.compile("^" + IPV4_BASIC_PATTERN_STRING + "-" + IPV4_BASIC_PATTERN_STRING + "$");

	private static final Pattern IPV4_SIMPLE_RANGE_PATTERN = Pattern
			.compile("^" + IPV4_BASIC_PATTERN_STRING + "-" + IPV4_FINAL_FIELD_PATTERN_STRING + "$");

	private static final Pattern SUB_NET_CIDR_PATTERN = Pattern
			.compile("^" + IPV4_BASIC_PATTERN_STRING + "/(0)?([0-9]|[0-2][0-9]|3[0-2])" + "$");

	private InetAddressUtils() {
	}

	/**
	 * Checks whether the parameter is a valid IPv4 address
	 *
	 * @param input
	 *            the address string to check for validity
	 * @return true if the input parameter is a valid IPv4 address
	 */
	protected static boolean isIPv4Address(final String input) {
		return IPV4_PATTERN.matcher(input).matches();
	}

	protected static boolean isIPv4RangeAddress(final String input) {
		return IPV4_RANGE_PATTERN.matcher(input).matches();
	}

	protected static boolean isIPv4SimpleRangeAddress(final String input) {
		return IPV4_SIMPLE_RANGE_PATTERN.matcher(input).matches();
	}

	protected static boolean isSubNetCIDRAddress(final String input) {
		return SUB_NET_CIDR_PATTERN.matcher(input).matches();
	}

	/**
	 * Returns true if the parameter <code>address</code> is in the range of
	 * <code>limitAddresses</code>.
	 * 
	 * <Point>
	 * The parameter <code>limitAddress</code> support:
	 * 
	 * <ol>
	 * <li>IPv4 address, e.g. "192.168.0.1";
	 * <li>IPv4 Range address, e.g. "192.168.0.1-192.168.0.255" or
	 * "192.168.0.1-255";
	 * <li>SubNet mask address, A CIDR-notation string, e.g. "192.168.0.1/16",
	 * see: http://www.faqs.org/rfcs/rfc1519.html;
	 * </ol>
	 * 
	 * @param address
	 *            A dot-delimited IPv4 address, e.g. "192.168.0.1".
	 * @param limitAddresses
	 *            A dot-delimited IPv4 address, IPv4 Range address, or SubNet
	 *            mask address.Multiple limitAddress separated by ",".
	 * @return True if in range, false otherwise.
	 * @throws UnknownHostException
	 * @throws IllegalArgumentException
	 *             if the parameter is invalid, i.e. does not match n.n.n.n/m
	 *             where n=1-3 decimal digits, m = 1-3 decimal digits in range
	 *             1-32.
	 */
	public static boolean isInRange(InetAddress address, String limitAddresses) throws UnknownHostException {
		if (address == null || limitAddresses == null || limitAddresses.length() == 0) {
			return false;
		}
		long ip = longValue(address);
		String[] limites = limitAddresses.split(",");
		for (String limitAddress : limites) {
			if (isInRange0(ip, limitAddress.trim())) {
				return true;
			}
		}
		return false;
	}

	protected static boolean isInRange0(long address, String limitAddress) throws UnknownHostException {
		if (isIPv4Address(limitAddress)) {// IPv4 address
			return address == longValue(InetAddress.getByName(limitAddress));
		} else if (isIPv4RangeAddress(limitAddress)) {// IPv4 Range address
			String[] limit = limitAddress.split("-");
			long l = longValue(InetAddress.getByName(limit[0]));
			long h = longValue(InetAddress.getByName(limit[1]));
			return l <= address && address <= h;
		} else if (isIPv4SimpleRangeAddress(limitAddress)) {// IPv4 Range
															// address
			String[] limit = limitAddress.split("-");
			long l = longValue(InetAddress.getByName(limit[0]));
			long h = longValue(InetAddress.getByName(limit[0].substring(0, limit[0].lastIndexOf(".") + 1) + limit[1]));
			return l <= address && address <= h;
		} else if (isSubNetCIDRAddress(limitAddress)) {// SubNet mask address
			String[] limit = limitAddress.split("/");
			long subnet = longValue(InetAddress.getByName(limit[0]));
			int cidr = Integer.parseInt(limit[1]);
			return address >>> (32 - cidr) == subnet >>> (32 - cidr);
		}
		return false;
	}

	protected static long longValue(InetAddress address) {// must be IPv4
		byte[] octets = address.getAddress();
		long result = 0;
		for (byte octet : octets) {
			result <<= 8;
			result |= octet & 0xff;
		}
		return result;
	}

}
