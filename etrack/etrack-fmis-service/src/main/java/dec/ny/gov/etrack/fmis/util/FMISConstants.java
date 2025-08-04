package dec.ny.gov.etrack.fmis.util;

public interface FMISConstants {
  String NEW_INVOICE = "N";
  String CANCEL_INVOICE = "D";
  Integer PAYMENT_PENDING = 1;
  Integer INVOICE_CANCELLED = 3;
  Integer PAYMENT_RECEIVED = 2;
//  Integer TXN_COMPLETED = 4;
//  Integer FMIS_CONF_UPDATE_ERROR = 5;
  String DATE_FROM = "dateFrom";
  String DATE_TO = "dateTo";
  Integer PAYMENT_RECEIVED_ACTION_TYPE=15;
  Integer INVOICE_CANCELLATION_ACTION_TYPE=16;
  String PAYMENT_RECEIVED_NOTE = "Payment has been received for your permit application(s) for Project ID: ";
}
