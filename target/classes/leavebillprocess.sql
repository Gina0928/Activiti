DROP DATABASE IF EXISTS leavebillprocess;

CREATE DATABASE `leavebillprocess`CHARACTER SET utf8;

USE leavebillprocess;

CREATE TABLE `leavebillprocess`.`tb_leavebill`( 
	`id` INT NOT NULL AUTO_INCREMENT, 
	`days` INT, 
	`content` TEXT, 
	`leaveDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	`remark` VARCHAR(255), 
	`emp_id` INT, 
	`state` INT, 
	PRIMARY KEY (`id`) 
) CHARSET=utf8;

CREATE TABLE `leavebillprocess`.`tb_employee`( 
	`id` INT NOT NULL AUTO_INCREMENT, 
	`name` VARCHAR(50), 
	`password` VARCHAR(50), 
	`email` VARCHAR(100), 
	`role` VARCHAR(50), 
	`emp_id` INT, 
	PRIMARY KEY (`id`) 
) CHARSET=utf8;

/*测试数据*/ 
INSERT tb_employee(id,NAME,PASSWORD,email,role,emp_id) VALUES( 1,'王中军','123','wangzhongjun@163.com','boss',NULL );
INSERT tb_employee(id,NAME,PASSWORD,email,role,emp_id) VALUES( 2,'冯小刚经纪人','123','fengxiaogangManager@163.com','manager',1 );
INSERT tb_employee(id,NAME,PASSWORD,email,role,emp_id) VALUES( 3,'范冰冰经纪人','123','fanbingbingManager@163.com','manager',1);
INSERT tb_employee(id,NAME,PASSWORD,email,role,emp_id) VALUES( 4,'冯小刚','123','fengxiaogang@163.com','user',2 );
INSERT tb_employee(id,NAME,PASSWORD,email,role,emp_id) VALUES( 5,'范冰冰','123','fanbingbing@163.com','user',3);
