package com.activity.demo.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("")
public class SendAction{
	public String execute(){
		return "aindex";
	}
}
