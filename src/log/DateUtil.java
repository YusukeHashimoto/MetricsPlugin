package log;

import java.util.Calendar;

public class DateUtil {
	private static Calendar cal;

	/**
	 * split with '-'
	 * @return
	 */
	public static String currentTimeInString() {
		StringBuilder sb = new StringBuilder();
		cal = Calendar.getInstance();
		sb.append(cal.get(Calendar.YEAR));
		sb.append('-');

		int month = cal.get(Calendar.MONTH) + 1;
		sb.append(month < 10 ? "0" + month : month);
		sb.append('-');

		int day = cal.get(Calendar.DAY_OF_MONTH);
		sb.append(day < 10 ? "0" + day : day);
		sb.append('-');

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		sb.append(hour < 10 ? "0" + hour : hour);
		sb.append('-');

		int min = cal.get(Calendar.MINUTE);
		sb.append(min < 10 ? "0" + min : min);
		sb.append('-');

		int sec = cal.get(Calendar.SECOND);
		sb.append(sec < 10 ? "0" + sec : sec);

		return sb.toString();
	}
	
	public static String currentTimeInString2() {
		StringBuilder sb = new StringBuilder();
		cal = Calendar.getInstance();
		sb.append(cal.get(Calendar.YEAR));
		sb.append(' ');

		int month = cal.get(Calendar.MONTH) + 1;
		sb.append(month < 10 ? "0" + month : month);
		sb.append('/');

		int day = cal.get(Calendar.DAY_OF_MONTH);
		sb.append(day < 10 ? "0" + day : day);
		sb.append(' ');

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		sb.append(hour < 10 ? "0" + hour : hour);
		sb.append(':');

		int min = cal.get(Calendar.MINUTE);
		sb.append(min < 10 ? "0" + min : min);
		sb.append(':');

		int sec = cal.get(Calendar.SECOND);
		sb.append(sec < 10 ? "0" + sec : sec);

		return sb.toString();
	}
}
