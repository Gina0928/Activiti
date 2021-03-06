package com.activity.demo.model;

import java.io.Serializable;

/**
 * 用户表
 */
public class Employee implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5387830097537079932L;
	private int id;//主键ID
	private String name;//用户名
	private String password;//密码
	private String email;//电子邮箱
	private String role;//角色
	private Employee manager;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Employee getManager() {
		return manager;
	}
	public void setManager(Employee manager) {
		this.manager = manager;
	}
	
	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", password="
				+ password + ", email=" + email + ", role=" + role
				+ ", manager=" + manager + "]";
	}
}

