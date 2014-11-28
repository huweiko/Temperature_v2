package com.refeved.monitor.struct;

public class SpecimenInfo {
	private String mSpecimenDes;
	private String mSpecimenNum;
	public SpecimenInfo(String SpecimenDes,String SpecimenNum) {
		// TODO Auto-generated constructor stub
		this.mSpecimenDes = SpecimenDes;
		this.mSpecimenNum = SpecimenNum;
	}

	public String getmSpecimenNum() {
		return mSpecimenNum;
	}

	public void setmSpecimenNum(String mSpecimenNum) {
		this.mSpecimenNum = mSpecimenNum;
	}

	public String getmSpecimenDes() {
		return mSpecimenDes;
	}

	public void setmSpecimenDes(String mSpecimenDes) {
		this.mSpecimenDes = mSpecimenDes;
	}
}
