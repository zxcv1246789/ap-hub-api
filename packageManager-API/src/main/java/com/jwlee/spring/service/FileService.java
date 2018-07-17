package com.jwlee.spring.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface FileService {

	int UploadedFileSave(MultipartHttpServletRequest multi);
	String FileList();
	void fileDownload(HttpServletRequest request, HttpServletResponse response, String filename, String type) throws Exception;
}
