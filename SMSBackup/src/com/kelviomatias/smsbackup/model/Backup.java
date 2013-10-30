package com.kelviomatias.smsbackup.model;

import java.io.File;
import java.util.Date;

public class Backup {

	private String name;
	private File file;
	private Date createdAt;

	public Backup(File file) {

		this.name = file.getName()
				.substring(0, file.getName().lastIndexOf("."));
		this.file = file;
		this.createdAt = new Date(file.lastModified());
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
