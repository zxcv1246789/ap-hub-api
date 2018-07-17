package com.jwlee.spring.service.Impl;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jwlee.spring.domain.Log;
import com.jwlee.spring.repository.LogRepository;
import com.jwlee.spring.service.LogService;

@Service(LogServiceImpl.BEAN_QUALIFIER)
public class LogServiceImpl implements LogService{
	public static final String BEAN_QUALIFIER = "com.jwlee.spring.service.Impl.LogServiceImpl";
	
	private static Logger logger = LogManager.getLogger(FileServiceImpl.class);
	
	@Autowired
	private LogRepository logRepository;
	
	public List<Log> AllList() {
		logger.info("Log 목록 return...");
		return logRepository.findAll();
	}
}
