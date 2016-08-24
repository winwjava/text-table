package winw.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import winw.util.CitizenIdentificationNumberUtil;

/**
 * CitizenIdentificationNumberUtilTest
 *
 * @author sjyao
 */
public class CitizenIdentificationNumberUtilTest {

	@Test
	public void testIsValid() {
		assertTrue(CitizenIdentificationNumberUtil.isValid("510723197801231912"));
		assertTrue(CitizenIdentificationNumberUtil.isValid("51072319870313665X"));
		assertTrue(CitizenIdentificationNumberUtil.isValid("510723198009155906"));
		assertTrue(CitizenIdentificationNumberUtil.isValid("510723198101192080"));

		// 为空
		assertFalse(CitizenIdentificationNumberUtil.isValid(null));
		assertFalse(CitizenIdentificationNumberUtil.isValid(""));

		// 位数
		assertFalse(CitizenIdentificationNumberUtil.isValid("5107231978011912"));
		assertFalse(CitizenIdentificationNumberUtil.isValid("5107231987031363665X"));

		// 地址码
		assertFalse(CitizenIdentificationNumberUtil.isValid("500723198101192080"));
		assertFalse(CitizenIdentificationNumberUtil.isValid("510799198101192080"));

		// 出生日期码
		assertFalse(CitizenIdentificationNumberUtil.isValid("510723190001192080"));
		assertFalse(CitizenIdentificationNumberUtil.isValid("510723198113612080"));

		// 顺序码
		assertFalse(CitizenIdentificationNumberUtil.isValid("510723198101196280"));

		// 15 位
		assertTrue(CitizenIdentificationNumberUtil.isValid("320311770706001"));
		assertTrue(CitizenIdentificationNumberUtil.isValid("320311770706002"));

		// 性能测试
		long sTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			assertTrue(CitizenIdentificationNumberUtil.isValid("510723198101192080"));
			assertTrue(CitizenIdentificationNumberUtil.isValid("320311770706002"));
		}
		// 1000 个 18 位的号码和 1000 个 15 位的号码，耗时小于 1 秒。
		assertTrue((System.currentTimeMillis() - sTime) < 1000);
	}

	public static String generateId() {
		Random random = new Random();
		int year = 1980 + random.nextInt(10);
		int month = random.nextInt(11);
		if (month == 0)
			month = 12;
		int day = 0;
		while (true) {
			day = random.nextInt(31);
			if (!((day == 0 || (month == 4 || month == 6 || month == 9 || month == 11) && day > 30)
					|| (month == 2 && (((year) % 4 > 0 && day > 28) || day > 29)))) {
				break;
			}
		}
		return generateId(year, month, day);
	}

	public static String generateId(int year, int month, int day) {
		// TODO validate year,month,day
		// 市/区/县的名称与之对应的编号，目前仅支持江苏省
		String areaCodes[] = { "320102", "320103", "320104", "320105", "320106", "320107", "320111", "320113", "320114",
				"320115", "320116", "320124", "320125", "320202", "320203", "320204", "320205", "320206", "320211",
				"320281", "320282", "320302", "320303", "320304", "320305", "320311", "320321", "320322", "320323",
				"320324", "320381", "320382", "320402", "320404", "320405", "320411", "320412", "320481", "320482",
				"320502", "320503", "320504", "320505", "320506", "320507", "320581", "320582", "320583", "320584",
				"320585", "320602", "320611", "320612", "320621", "320623", "320681", "320682", "320684", "320703",
				"320705", "320706", "320721", "320722", "320723", "320724", "320802", "320803", "320804", "320811",
				"320826", "320829", "320830", "320831", "320902", "320903", "320921", "320922", "320923", "320924",
				"320925", "320981", "320982", "321002", "321003", "321011", "321023", "321081", "321084", "321088",
				"321088", "321102", "321111", "321112", "321181", "321182", "321183", "321202", "321203", "321281",
				"321282", "321283", "321284", "321302", "321311", "321322", "321323", "321324" };
		Random random = new Random();
		String areaCode = areaCodes[random.nextInt(areaCodes.length)];
		String birthday = String.valueOf(year * 10000 + month * 100 + day);
		String randomCode = String.valueOf(1000 + random.nextInt(999)).substring(1);
		String verify = getVerify(areaCode + birthday + randomCode);
		String ret = areaCode + birthday + randomCode + verify;
		return ret;
	}

	public static String getVerify(String cardId) {
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(cardId.charAt(i))) * Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];

		return strVerifyCode;
	}

	@Test
	public void testGenerateId() {
		System.out.println(generateId());
		System.out.println(generateId(1991, 1, 1));
	}
}
