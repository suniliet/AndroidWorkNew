package com.kns.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.sunil.selectmutiple.CustomGallery;


public class DoUpload {
	
	//http://toolongdidntread.com/android/android-multipart-post-with-progress-bar/
	private static final String TAG="DoUpload";
	private final ProgressListener listener;
	
	
	public DoUpload(final ProgressListener listener)
	{
		super();
		this.listener = listener;
	}
	
	public static interface ProgressListener
	{
		void transferred(long num);
	}
	
	public  String postFile(ArrayList<CustomGallery> filelist, String member_id,  final ProgressListener listener) throws Exception {

		Log.v(TAG, "file size is: "+filelist.size());
	    HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost("http://23.21.71.132/KNSGallery/image_fileupload.php");
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
	    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

	    for (int i = 1; i <= filelist.size(); i++) {
	    	CustomGallery gallery=filelist.get(i-1);
	    	String filename=gallery.sdcardPath;
	    	Log.v(TAG, "file name: "+filename);
	    	File file = new File(filename);
		    FileBody fb = new FileBody(file);
		    builder.addPart("file"+i, fb);  
		}
	   /* final File file = new File(fileName);
	    FileBody fb = new FileBody(file);*/

	   // builder.addPart("file", fb);  
	    builder.addTextBody("member_id", member_id);
	    builder.addTextBody("count", String.valueOf(filelist.size()));
	    
	    final HttpEntity yourEntity = builder.build();

	    class ProgressiveEntity implements HttpEntity {
	        @Override
	        public void consumeContent() throws IOException {
	            yourEntity.consumeContent();                
	        }
	        @Override
	        public InputStream getContent() throws IOException,
	                IllegalStateException {
	            return yourEntity.getContent();
	        }
	        @Override
	        public Header getContentEncoding() {             
	            return yourEntity.getContentEncoding();
	        }
	        @Override
	        public long getContentLength() {
	            return yourEntity.getContentLength();
	        }
	        @Override
	        public Header getContentType() {
	            return yourEntity.getContentType();
	        }
	        @Override
	        public boolean isChunked() {             
	            return yourEntity.isChunked();
	        }
	        @Override
	        public boolean isRepeatable() {
	            return yourEntity.isRepeatable();
	        }
	        @Override
	        public boolean isStreaming() {             
	            return yourEntity.isStreaming();
	        } // CONSIDER put a _real_ delegator into here!

	        @Override
	        public void writeTo(OutputStream outstream) throws IOException {

	            class ProxyOutputStream extends FilterOutputStream {
	            	private final ProgressListener listener;
	            	private long transferred;
	                public ProxyOutputStream(OutputStream proxy, final ProgressListener listener) {
	                    super(proxy);    
	                    this.listener = listener;
	        			this.transferred = 0;
	                   
	                }
	                public void write(int idx) throws IOException {
	                    out.write(idx);
	                }
	                public void write(byte[] bts) throws IOException {
	                    out.write(bts);
	                    this.transferred++;
	        			this.listener.transferred(this.transferred);
	                }
	                public void write(byte[] bts, int st, int end) throws IOException {
	                	this.transferred += end;
	        			this.listener.transferred(this.transferred);
	                    out.write(bts, st, end);
	                }
	                public void flush() throws IOException {
	                    out.flush();
	                }
	                public void close() throws IOException {
	                    out.close();
	                }
	            } // CONSIDER import this class (and risk more Jar File Hell)

	            class ProgressiveOutputStream extends ProxyOutputStream {
	            	
	                public ProgressiveOutputStream(OutputStream proxy) {
	                    super(proxy, listener);
	                   
	                }
	                public void write(byte[] bts, int st, int end) throws IOException {

	                    // FIXME  Put your progress bar stuff here!
	                	
	                    out.write(bts, st, end);
	                }
	            }

	            yourEntity.writeTo(new ProgressiveOutputStream(outstream));
	        }

	    };
	    ProgressiveEntity myEntity = new ProgressiveEntity();

	    post.setEntity(myEntity);
	    HttpResponse response = client.execute(post);        

	    return getContent(response);

	} 
	
	public  String getContent(HttpResponse response) throws IOException {
	    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    String body = "";
	    String content = "";

	    while ((body = rd.readLine()) != null) 
	    {
	        content += body + "\n";
	    }
	    return content.trim();
	}
}
