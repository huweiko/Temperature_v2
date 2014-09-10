/*
 * �ļ�����AreaTreeNode.java
 * ���ܣ��豸��Ϣ�Ķ���
 * ���ߣ�huwei
 * ����ʱ�䣺2013-10-25
 * 
 * 
 * 
 * */
package com.refeved.monitor.struct;

import java.util.List;

public class AreaTreeNode extends TreeNode {
	private List<Device> mDevices = null;

	public AreaTreeNode(TreeNode parent, String id, String name,
			boolean isOpen, List<TreeNode> children, List<Device> devices) {
		super(parent, id, name, isOpen, children);
		
		mDevices = devices;
	}

	public List<Device> getmDevices() {
		return mDevices;
	}

	public void setmDevices(List<Device> mDevices) {
		this.mDevices = mDevices;
	}
	
	public void addDevice(Device device){
		this.mDevices.add(device);
	}

}
