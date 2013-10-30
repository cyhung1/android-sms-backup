package com.kelviomatias.smsbackup.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;

@Root(name = "data")
public class Data {

	@ElementListUnion({ @ElementList(type = Sms.class, entry = "sms", inline = true, required = false) })
	private List<Sms> smsList = new ArrayList<Sms>();

	public List<Sms> getSmsList() {
		return smsList;
	}

	public void setSmsList(List<Sms> smsList) {
		this.smsList = smsList;
	}

}
