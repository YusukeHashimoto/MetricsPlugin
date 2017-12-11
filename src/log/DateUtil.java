package log;

import java.util.Calendar;

public class DateUtil {
	private static Calendar cal;

	public static String currentTimeInString() {
		StringBuilder sb = new StringBuilder();
		cal = Calendar.getInstance();
		sb.append(cal.get(Calendar.YEAR));
		sb.append('-');
		sb.append(cal.get(Calendar.MONTH) + 1);
		sb.append('-');
		sb.append(cal.get(Calendar.DAY_OF_MONTH));
		sb.append('-');
		sb.append(cal.get(Calendar.HOUR_OF_DAY));
		sb.append('-');
		sb.append(cal.get(Calendar.MINUTE));
		sb.append('-');
		sb.append(cal.get(Calendar.SECOND));
		return sb.toString();
	}
}
