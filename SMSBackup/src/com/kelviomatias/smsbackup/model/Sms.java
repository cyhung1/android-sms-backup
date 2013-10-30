package com.kelviomatias.smsbackup.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "sms")
public class Sms {

	public static final int SMS_INBOX = 1;

	public static final int SMS_OUTBOX = 2;

	public static final String ID = "_id";



	public static final String _ID = "_id";
	
	public static final String THREAD_ID = "thread_id";

    public static final String PERSON = "person";

    @SuppressWarnings("UnusedDeclaration")
    public static final int MESSAGE_TYPE_ALL = 0;

    public static final int MESSAGE_TYPE_INBOX = 1;

    public static final int MESSAGE_TYPE_SENT = 2;

    public static final int MESSAGE_TYPE_DRAFT = 3;

    @SuppressWarnings("UnusedDeclaration")
    public static final int MESSAGE_TYPE_OUTBOX = 4;

    @SuppressWarnings("UnusedDeclaration")
    public static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages

    @SuppressWarnings("UnusedDeclaration")
    public static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send later
	
	public static final String PROTOCOL = "protocol";
	public static final String ADDRESS = "address";
	public static final String DATE = "date";
	public static final String TYPE = "type";
	public static final String SUBJECT = "subject";
	public static final String BODY = "body";
	public static final String TOA = "toa";
	public static final String SC_TOA = "sc_toa";
	public static final String SERVICE_CENTER = "service_center";
	public static final String READ = "read";
	public static final String LOCKED = "locked";
	public static final String DATE_SENT = "date_sent";
	public static final String CONTACT_NAME = "contact_name";
	public static final String SEEN = "seen";
	public static final String DELETABLE = "deletable";
	public static final String HIDDEN = "hidden";
	public static final String GROUP_ID = "group_id";
	public static final String DELIVERY_DATE = "delivery_date";
	public static final String ERROR_CODE = "error_code";
	public static final String RESERVED = "reserved";
	public static final String APP_ID = "app_id";
	public static final String MSG_ID = "msg_id";
	public static final String CALLBACK_NUMBER = "callback_number";
	public static final String TELESERVICE_ID = "teleservice_id";
	public static final String LINK_URL = "link_url";
	public static final String STATUS = "status";
	public static final String REPLY_PATH_PRESENT = "reply_path_present";
	public static final String PRI = "pri";
	public static final String GROUP_TYPE = "group_type";

	@Attribute(required = false, name = PROTOCOL)
	private int protocol;

	@Attribute(required = false, name = ADDRESS)
	private String address;

	@Attribute(required = false, name = DATE)
	private long date;

	@Attribute(required = false, name = TYPE)
	private int type;

	@Attribute(required = false, name = SUBJECT)
	private String subject;

	@Attribute(required = false, name = BODY)
	private String body;
	
	@Attribute(required = false, name = THREAD_ID)
	private String threadId;

	//@Attribute(required = false, name = TOA)
	//private String toa;

	//@Attribute(required = false, name = SC_TOA)
	//private String scToa;

	@Attribute(required = false, name = SERVICE_CENTER)
	private String serviceCenter;

	@Attribute(required = false, name = READ)
	private String read;

	@Attribute(required = false, name = STATUS)
	private String status;

	@Attribute(required = false, name = LOCKED)
	private String locked;

	@Attribute(required = false, name = DATE_SENT)
	private long dateSent;

	@Attribute(required = false, name = CONTACT_NAME)
	private String contactName;


	@Attribute(required = false, name = DELIVERY_DATE)
	private String deliveryDate;


	
	
	
	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getServiceCenter() {
		return serviceCenter;
	}

	public void setServiceCenter(String serviceCenter) {
		this.serviceCenter = serviceCenter;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}

	public String getLocked() {
		return locked;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}

	public long getDateSent() {
		return dateSent;
	}

	public void setDateSent(long dateSent) {
		this.dateSent = dateSent;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	//public String getToa() {
	//	return toa;
	//}

	//public void setToa(String toa) {
	//	this.toa = toa;
	//}

	/*public String getScToa() {
		return scToa;
	}

	public void setScToa(String scToa) {
		this.scToa = scToa;
	}*/

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
