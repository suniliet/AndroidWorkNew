package com.kns.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebHelper {

	public String convertStreamToString(InputStream is) {

	  	   StringBuilder sb = new StringBuilder();
		    BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(is));

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
			    sb.append((line + "\n"));
				}
			} catch (IOException e) {
			e.printStackTrace();
	     } finally {
			 try {
			    is.close();
			 } catch (IOException e) {
			    e.printStackTrace();
			}
	     }
			return sb.toString();
	  	}
}
