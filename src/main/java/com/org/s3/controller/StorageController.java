package com.org.s3.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.org.s3.service.StorageService;

@RestController
@RequestMapping("/file")
public class StorageController
{
	private final StorageService service;

	public StorageController(StorageService service)
	{
		this.service = service;
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file)
	{
		return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
	}

	@GetMapping("/download/{fileName}")
	public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName)
	{
		byte[] data = service.downloadFile(fileName);
		ByteArrayResource resource = new ByteArrayResource(data);
		return ResponseEntity
				.ok()
				.contentLength(data.length)
				.header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

	@DeleteMapping("/delete/{fileName}")
	public ResponseEntity<String> deleteFile(@PathVariable String fileName)
	{
		return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
	}
}
