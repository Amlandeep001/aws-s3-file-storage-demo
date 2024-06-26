package com.org.s3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class StorageService
{
	private final String bucketName;
	private final AmazonS3 s3Client;

	public StorageService(@Value("${application.bucket.name}") String bucketName, AmazonS3 s3Client)
	{
		this.bucketName = bucketName;
		this.s3Client = s3Client;
	}

	public String uploadFile(MultipartFile file)
	{
		File fileObj = convertMultiPartFileToFile(file);
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
		fileObj.delete();
		return "File uploaded : " + fileName;
	}

	public byte[] downloadFile(String fileName)
	{
		S3Object s3Object = s3Client.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = s3Object.getObjectContent();
		try
		{
			byte[] content = IOUtils.toByteArray(inputStream);
			return content;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String deleteFile(String fileName)
	{
		s3Client.deleteObject(bucketName, fileName);
		return fileName + " removed ...";
	}

	private File convertMultiPartFileToFile(MultipartFile file)
	{
		File convertedFile = new File(file.getOriginalFilename());
		try(FileOutputStream fos = new FileOutputStream(convertedFile))
		{
			fos.write(file.getBytes());
		}
		catch(IOException e)
		{
			log.error("Error converting multipartFile to file", e);
		}
		return convertedFile;
	}
}
