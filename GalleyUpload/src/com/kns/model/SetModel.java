package com.kns.model;

public class SetModel {
	
	private String partnerid;
	private String catagoryid;
	private String catagoryname;
	private String catagorythumb;
	private String noofimages;
	private String Approvalflag;
	private String LiveFlag;
	
	public SetModel(){
		
	}
	
	public SetModel(String partnerid, String catagoryid, String catagoryname, String catagorythumb, String noofimages, String Approvalflag, String LiveFlag){
		
		this.partnerid=partnerid;
		this.catagoryid=catagoryid;
		this.catagoryname=catagoryname;
		this.catagorythumb=catagorythumb;
		this.noofimages=noofimages;
		this.Approvalflag=Approvalflag;
		this.LiveFlag=LiveFlag;
		
	}

	
	
	public String getLiveFlag() {
		return LiveFlag;
	}

	public void setLiveFlag(String liveFlag) {
		LiveFlag = liveFlag;
	}

	public String getCatagoryid() {
		return catagoryid;
	}

	public void setCatagoryid(String catagoryid) {
		this.catagoryid = catagoryid;
	}

	public String getCatagoryname() {
		return catagoryname;
	}

	public void setCatagoryname(String catagoryname) {
		this.catagoryname = catagoryname;
	}

	public String getCatagorythumb() {
		return catagorythumb;
	}

	public void setCatagorythumb(String catagorythumb) {
		this.catagorythumb = catagorythumb;
	}

	public String getNoofimages() {
		return noofimages;
	}

	public void setNoofimages(String noofimages) {
		this.noofimages = noofimages;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}

	public String getApprovalflag() {
		return Approvalflag;
	}

	public void setApprovalflag(String approvalflag) {
		Approvalflag = approvalflag;
	}

	

}
