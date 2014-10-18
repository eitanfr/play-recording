package models;


public class Record {

	private String name;
	private long date;
	private long size;
	private boolean checked;

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
