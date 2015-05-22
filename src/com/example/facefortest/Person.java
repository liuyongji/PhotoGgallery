package com.example.facefortest;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Person extends BmobObject{
	private String name;
    private BmobFile file;
    private boolean like;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BmobFile getFile() {
		return file;
	}
	public void setFile(BmobFile file) {
		this.file = file;
	}
	public boolean isLike() {
		return like;
	}
	public void setLike(boolean like) {
		this.like = like;
	}

    
    

}
