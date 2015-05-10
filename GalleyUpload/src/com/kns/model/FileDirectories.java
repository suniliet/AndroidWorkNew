package com.kns.model;

public class FileDirectories {
	
	private String bucket_id;
	private String bucket_name;
	private String bucket_date;
	private String bucket_profile;
	private String Bucket_size;
	private int fileid;
	private boolean isfile;
	
	public FileDirectories(){
		
	}
	
	public FileDirectories(String bucketid, String bucketname, String bucketdate, String bucketprofile, String bucketsize, int fileid, boolean isfile){
		
		this.bucket_id=bucketid;
		this.bucket_name=bucketname;
		this.bucket_date=bucketdate;
		this.bucket_profile=bucketprofile;
		this.Bucket_size=bucketsize;
		this.fileid=fileid;
		this.isfile=isfile;
	}

	
	

	public int getFileid() {
		return fileid;
	}

	public void setFileid(int fileid) {
		this.fileid = fileid;
	}

	public boolean isIsfile() {
		return isfile;
	}

	public void setIsfile(boolean isfile) {
		this.isfile = isfile;
	}

	public String getBucket_id() {
		return bucket_id;
	}

	public void setBucket_id(String bucket_id) {
		this.bucket_id = bucket_id;
	}

	public String getBucket_name() {
		return bucket_name;
	}

	public void setBucket_name(String bucket_name) {
		this.bucket_name = bucket_name;
	}

	public String getBucket_date() {
		return bucket_date;
	}

	public void setBucket_date(String bucket_date) {
		this.bucket_date = bucket_date;
	}

	public String getBucket_profile() {
		return bucket_profile;
	}

	public void setBucket_profile(String bucket_profile) {
		this.bucket_profile = bucket_profile;
	}

	public String getBucket_size() {
		return Bucket_size;
	}

	public void setBucket_size(String bucket_size) {
		Bucket_size = bucket_size;
	}

	
}
