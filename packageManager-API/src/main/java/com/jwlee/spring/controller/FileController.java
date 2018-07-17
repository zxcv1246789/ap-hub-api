package com.jwlee.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jwlee.spring.service.FileService;
import com.jwlee.spring.service.Impl.FileServiceImpl;

@CrossOrigin(origins = {"*"})
@RestController
public class FileController {
	
	@Autowired
	@Qualifier(FileServiceImpl.BEAN_QUALIFIER)
	private FileService fileService;

	@RequestMapping(value = "/file", method = RequestMethod.GET)
	public String filelist() {
		return fileService.FileList();
	}
	
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public int PostWriteWithFiles(MultipartHttpServletRequest multi) {
		return fileService.UploadedFileSave(multi);
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
    public void filedownload (@RequestParam("type") String type,
    								    @RequestParam("name") String filename,
    									 HttpServletRequest request,
    									 HttpServletResponse response) throws Exception {
        fileService.fileDownload(request, response, filename, type);
    }
	
}
