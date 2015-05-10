package com.kns.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import android.util.Log;

public class CustomMultiPartEntity implements HttpEntity{

	private final ProgressListener listener;
	private static final String TAG="CustomMultiPartEntity";
	final HttpEntity yourEntity;
	public CustomMultiPartEntity(final HttpEntity yourEntity, final ProgressListener listener)
	{
		super();
		this.yourEntity=yourEntity;
		this.listener = listener;
	}
	
	@Override
	public void consumeContent() throws IOException {
	
		yourEntity.consumeContent();                
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
	
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
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		
		 class ProxyOutputStream extends FilterOutputStream {
		    	private final ProgressListener listener;
		    	private long transferred;
		        public ProxyOutputStream(final OutputStream proxy, final ProgressListener listener) {
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
		    } 
		 

         class ProgressiveOutputStream extends ProxyOutputStream {
        	private long transferred;
        	private final ProgressListener listener;
             public ProgressiveOutputStream(OutputStream proxy, final ProgressListener listener) {
                 super(proxy, listener);
                 this.listener = listener;
     			  this.transferred = 0;
             }
             public void write(byte[] bts, int st, int end) throws IOException {

                 // FIXME  Put your progress bar stuff here!
            		this.transferred += end;
            		//Log.v(TAG, "transfered file :"+this.transferred);
					this.listener.transferred(this.transferred);
                    out.write(bts, st, end);
             }
         }
         yourEntity.writeTo(new ProgressiveOutputStream(outstream, listener));
	
	}

	public static interface ProgressListener
	{
		void transferred(long num);
	}
	
  
}
