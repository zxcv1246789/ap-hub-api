package com.jwlee.spring.service.Impl;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Formatter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jwlee.spring.domain.Log;
import com.jwlee.spring.repository.LogRepository;
import com.jwlee.spring.service.FileService;

@Service(FileServiceImpl.BEAN_QUALIFIER)
public class FileServiceImpl implements FileService {
	public static final String BEAN_QUALIFIER = "com.jwlee.spring.service.Impl.FileServiceImpl";
	public String fileUrl = System.getProperty("user.dir") + File.separator + "file" + File.separator;

	private static Logger logger = LogManager.getLogger(FileServiceImpl.class);
	
	@Autowired
	private LogRepository logRepository;
	
	public int UploadedFileSave(MultipartHttpServletRequest multi) {

		String newFileName = ""; // 업로드 되는 파일명

		File dir = new File(fileUrl);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}

		Iterator<String> files = multi.getFileNames();

		while (files.hasNext()) {

			String uploadFile = files.next();
			MultipartFile mFile = multi.getFile(uploadFile);
			if (mFile.getOriginalFilename().equals("")) {
				logger.info("업로드할 파일이 없습니다.");
			} else {
				String fileName = mFile.getOriginalFilename();
				if (fileName.indexOf(".zip") == -1) {
					logger.info(".zip 파일 형식이 아닙니다.");
					return -1;
				}
				logger.info("실제 파일 이름 : " + fileName);
				newFileName = fileName;

				try {
					mFile.transferTo(new File(fileUrl + newFileName));
					
					MessageDigest md5 = MessageDigest.getInstance("MD5");
					String MD5Code = MkfileMd5(md5, fileUrl + newFileName);
					logger.info("MD5Code : " + MD5Code);
					
					FileWriter fw = new FileWriter(fileUrl + newFileName.replace(".zip", ".md5")); // 절대주소 경로 가능
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(MD5Code);
					bw.newLine(); // 줄바꿈
					bw.close();
					logger.info("MD5 저장 완료, Addr : " + multi.getRemoteAddr());
					Log log = new Log();
					log.setContent(newFileName + "파일이 업로드 되었습니다. = " + multi.getRemoteAddr());
					log.setDate(LocalDateTime.now());
					log.setType("upload");
					logRepository.save(log);
					
				} catch (Exception e) {
					e.printStackTrace();
					return -2;
				}
			}
		}
		return 1;
	}

	public String FileList() {
		File path = new File(fileUrl);
		String list = "";
		String[] filelist = path.list();
		if (filelist.equals(null)) {
			return null;
		}
		for (int a = 0; a < filelist.length; a++) {
			logger.info(filelist[a]);
			list += filelist[a];
			if (a != filelist.length - 1) {
				list += ",";
			}
		}
		return list;
	}

	
	public void fileDownload(HttpServletRequest request, HttpServletResponse response, String filename, String type) throws Exception {
		// 파일 업로드된 경로
		String FileExtension = null;
		if (type.equals("package")) {
			FileExtension = ".zip";
		} else if (type.equals("hash")) {
			FileExtension = ".md5";
		}
		try {
			String url = fileUrl;
			url += "/";
			String savePath = url;
			String fileName = filename + FileExtension;

			// 실제 내보낼 파일명
			String oriFileName = filename + FileExtension;
			InputStream in = null;
			OutputStream os = null;
			File file = null;
			boolean skip = false;
			String client = "";

			// 파일을 읽어 스트림에 담기
			try {
				file = new File(savePath, fileName);
				in = new FileInputStream(file);
			} catch (FileNotFoundException fe) {
				skip = true;
			}

			client = request.getHeader("User-Agent");

			// 파일 다운로드 헤더 지정
			response.reset();
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Description", "JSP Generated Data");
			response.addHeader("Access-Control-Allow-Origin", "*");
			
			if (!skip) {
				// IE
				if (client.indexOf("MSIE") != -1) {
					response.setHeader("Content-Disposition", "attachment; filename=\""
							+ java.net.URLEncoder.encode(oriFileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
					// IE 11 이상.
				} else if (client.indexOf("Trident") != -1) {
					response.setHeader("Content-Disposition", "attachment; filename=\""
							+ java.net.URLEncoder.encode(oriFileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
				} else {
					// 한글 파일명 처리
					response.setHeader("Content-Disposition",
							"attachment; filename=\"" + new String(oriFileName.getBytes("UTF-8"), "ISO8859_1") + "\"");
					response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
				}
				response.setHeader("Content-Length", "" + file.length());
				os = response.getOutputStream();
				byte b[] = new byte[(int) file.length()];
				int leng = 0;
				while ((leng = in.read(b)) > 0) {
					os.write(b, 0, leng);
				}
				Log log = new Log();
				log.setContent(oriFileName + "파일이 다운로드 되었습니다. = " + request.getLocalAddr());
				log.setDate(LocalDateTime.now());
				log.setType("download");
				logRepository.save(log);
			} else {
				response.setContentType("text/html;charset=UTF-8");
				logger.info("파일이 없습니다.");
			}
			in.close();
			os.close();
		} catch (Exception e) {
			logger.info("ERROR : " + e.getMessage());
		}

	}
	
	private String MkfileMd5(MessageDigest algorithm, String filename) throws Exception {

		FileInputStream fis = new FileInputStream(new File(filename));
		BufferedInputStream bis = new BufferedInputStream(fis);
		@SuppressWarnings("resource")
		DigestInputStream dis = new DigestInputStream(bis, algorithm);

		// read the file and update the hash calculation
		while (dis.read() != -1)
			;

		// get the hash value as byte array
		byte[] hash = algorithm.digest();

		return byteArray2Hex(hash);

	}

	private static String byteArray2Hex(byte[] hash) {
		@SuppressWarnings("resource")
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

}
