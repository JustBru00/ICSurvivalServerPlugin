package net.introvertscove.survivalserver.plugin.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeFormatter {	
	public static String getTimeStampFrom(long systimemills) {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	    Date time = Date.from(Instant.ofEpochMilli(systimemills));
	    String strDate = sdfDate.format(time);
	    return strDate;
	}

}
