package com.jwlee.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jwlee.spring.domain.Log;
import com.jwlee.spring.service.LogService;
import com.jwlee.spring.service.Impl.LogServiceImpl;

@CrossOrigin(origins = {"*"})
@RestController
public class LogController {

	@Autowired
	@Qualifier(LogServiceImpl.BEAN_QUALIFIER)
	private LogService logService;
	
	@RequestMapping(value = "/log", method = RequestMethod.GET)
	public List<Log> filelist() {
		return logService.AllList();
	}
	
}
