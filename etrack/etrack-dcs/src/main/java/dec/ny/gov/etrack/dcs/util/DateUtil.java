package dec.ny.gov.etrack.dcs.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dec.ny.gov.etrack.dcs.exception.DcsException;

public final class DateUtil {

  private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

  private DateUtil() {

  }
  
  /**
   * Convert the Data into yyyyMMddHHmmss format.
   * 
   * @param date - Date to be formatted.
   * 
   * @return - Formatted date string.
   */
  public static String formatDateToString(Date date) {
    logger.debug("Entering formatDateToString()");
    String pattern = "yyyyMMddHHmmss";
    DateFormat df = new SimpleDateFormat(pattern);
    logger.debug("Exiting formatDateToString()");
    return df.format(date);
  }

  /**
   * Convert the yyyyMMddHHmmss formatted string to date.
   * 
   * @param dateString - date string.
   * 
   * @return - Converted date.
   */
  public static Date formatStringToDate(String dateString) {
    logger.debug("Entering formatStringToDate()");
    Date date = new Date();
    try {
      date = new SimpleDateFormat("yyyyMMddHHmmss").parse(dateString);
    } catch (ParseException e) {
      logger.error("Error parsing string to date. Exception message:{}", e.getMessage());
      logger.error(ExceptionUtils.getStackTrace(e));
      throw new DcsException(e.getMessage());
    }
    logger.debug("Exiting formatStringToDate()");
    return date;
  }
}
