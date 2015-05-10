package com.kns.model;

public class CustomRequestModel {
	
	private String PartnerID;
	private String ConsumerID;
	private String CustomName;
	private String CustomRequest;
	private String CustomAmount;
	private String Approval_Flag;
	private String RequestFormID;
	
	public CustomRequestModel(){
		
	}
	
	public CustomRequestModel(String partnerid, String consumerid, String customname, String customrequest, String customamount, String approvalflag, String RequestFormID){
		
		this.PartnerID=partnerid;
		this.ConsumerID=consumerid;
		this.CustomName=customname;
		this.CustomRequest=customrequest;
		this.CustomAmount=customamount;
		this.Approval_Flag=approvalflag;
		this.RequestFormID=RequestFormID;
	}

	public String getPartnerID() {
		return PartnerID;
	}

	public void setPartnerID(String partnerID) {
		PartnerID = partnerID;
	}

	public String getConsumerID() {
		return ConsumerID;
	}

	public void setConsumerID(String consumerID) {
		ConsumerID = consumerID;
	}

	public String getCustomName() {
		return CustomName;
	}

	public void setCustomName(String customName) {
		CustomName = customName;
	}

	public String getCustomRequest() {
		return CustomRequest;
	}

	public void setCustomRequest(String customRequest) {
		CustomRequest = customRequest;
	}

	public String getCustomAmount() {
		return CustomAmount;
	}

	public void setCustomAmount(String customAmount) {
		CustomAmount = customAmount;
	}

	public String getApproval_Flag() {
		return Approval_Flag;
	}

	public void setApproval_Flag(String approval_Flag) {
		Approval_Flag = approval_Flag;
	}

	public String getRequestFormID() {
		return RequestFormID;
	}

	public void setRequestFormID(String requestFormID) {
		RequestFormID = requestFormID;
	}

	
}
