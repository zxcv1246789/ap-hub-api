package com.jwlee.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwlee.spring.domain.Log;


public interface LogRepository extends JpaRepository<Log, Integer>{
	
	
}
