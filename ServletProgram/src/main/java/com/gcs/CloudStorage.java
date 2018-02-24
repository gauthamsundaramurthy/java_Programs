package com.gcs;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
//import com.google.api.services.storage.Storage;


import java.nio.channels.Channels;
import java.io.InputStream;
import java.io.OutputStream;

//import com.google.api.client.http.InputStreamContent;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.appengine.api.blobstore.BlobKey;

@SuppressWarnings("serial")
public class CloudStorage extends HttpServlet{
  
	private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
		      .initialRetryDelayMillis(10)
		      .retryMaxAttempts(10)
		      .totalRetryPeriodMillis(15000)
		      .build());
	
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	private static final boolean SERVE_USING_BLOBSTORE_API = false;
	  
	private void copy(InputStream input, OutputStream output) throws IOException {
	   try {
	      byte[] buffer = new byte[BUFFER_SIZE];
	      int bytesRead = input.read(buffer);
	      while (bytesRead != -1) {
	        output.write(buffer, 0, bytesRead);
	        bytesRead = input.read(buffer);
	      }
	    } finally {
	      input.close();
	      output.close();
	    }
	  }
	
	// Upload to GCS 
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {
		
		GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
		 
		String bucketName = request.getParameter("bucketName"); 
		String objectName = request.getParameter("objectName"); 

		GcsFilename fileName = new GcsFilename(bucketName,objectName);
		
	    GcsOutputChannel outputChannel = this.gcsService.createOrReplace(fileName,instance);
	    this.copy(request.getInputStream(),Channels.newOutputStream(outputChannel));
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print("Upload to GCS  success!!!");

	}
	
	//Download from GCS
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String bucketName = req.getParameter("bucketName"); 
		String objectName = req.getParameter("objectName"); 

		GcsFilename fileName = new GcsFilename(bucketName,objectName);
	    
		if (SERVE_USING_BLOBSTORE_API) {
	      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	      BlobKey blobKey = blobstoreService.createGsBlobKey(
	          "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
	      blobstoreService.serve(blobKey, resp);
	    } else {
	      GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
	      resp.setHeader("Content-Disposition","attachment; filename=\"" + objectName + "\"");
	      
	      copy(Channels.newInputStream(readChannel), resp.getOutputStream());
	    }
		
	  }
	
}


