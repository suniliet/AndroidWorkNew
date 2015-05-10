package com.sunil.selectmutiple;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class RegistrationActivity extends Activity implements OnClickListener{

	private static final String TAG="RegistrationActivity";
	private Context context=null;
	private EditText edittext_fname;
	private EditText edittext_lname;
	private EditText edittext_username;
	private EditText edittext_email;
	private EditText edittext_password;
	private EditText edittext_phone;
	private EditText edittext_affilatedid;
	private TextView txt_affilate;
	private ImageButton btn_register;
	private ImageButton btn_back;
	private ImageButton btn_talepic;
	private ImageView imageview_pic;
	private ProgressDialog prodialog;
	
	private String firstname;
	private String lastname;
	private String username;
	private String email;
	private String password; 
	private String phone;
	
	final static int CAMERA_RESULT = 0;
	final static int GALLERY_RESULT = 1;
	private AlertDialog alert = null;
	int isCameraOrGallery = 0;
	String uploadImagePath=null;
	private boolean ispicture=false;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	String AffiliateID="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrationscreen);
		
		context=this;
		edittext_fname=(EditText)findViewById(R.id.Editext_firstname);
		edittext_lname=(EditText)findViewById(R.id.editText_lastname);
		edittext_username=(EditText)findViewById(R.id.editText_user);
		edittext_email=(EditText)findViewById(R.id.editText_email);
		edittext_password=(EditText)findViewById(R.id.editText_reg_psw);
		edittext_phone=(EditText)findViewById(R.id.editText_confirm_ph);
		edittext_affilatedid=(EditText)findViewById(R.id.editText_affilateid);
		txt_affilate=(TextView)findViewById(R.id.textView_affilateid);
		btn_register=(ImageButton)findViewById(R.id.imageButton_register);
		btn_back=(ImageButton)findViewById(R.id.imageButton_back);
		btn_talepic=(ImageButton)findViewById(R.id.button_takeoic);
		imageview_pic=(ImageView)findViewById(R.id.imageView_profile_partner);
		btn_back.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		btn_talepic.setOnClickListener(this);
		
	/*	Bundle bundle=getIntent().getExtras();
		if (bundle!=null) {
			
			String jsonresponse=bundle.getString("JSONAPI");
			if (jsonresponse != null) {
				
				try {
					
					JSONObject jsonobj=new JSONObject(jsonresponse);
					String status=jsonobj.getString("Status");
					if (status.trim().equalsIgnoreCase("Success")) {
						
						 AffiliateID=jsonobj.getString("AffiliateID");
						if (AffiliateID != null) {
							
							txt_affilate.setText(AffiliateID);
						}
						
					}
					
					
				}catch (Exception e) {
					e.printStackTrace();
			}
		}
			
		}*/
	}


	@Override
	public void onClick(View arg0) {
		
		if (arg0==btn_register) {
			
			AffiliateID=edittext_affilatedid.getText().toString().trim();
			firstname=edittext_fname.getText().toString().trim();
			lastname=edittext_lname.getText().toString().trim();
			username=edittext_username.getText().toString().trim();
			email=edittext_email.getText().toString().trim();
			password=edittext_password.getText().toString().trim();
			phone=edittext_phone.getText().toString().trim();
			
			if (AffiliateID.length() < 1) {
				ImageUtil.showAlert(RegistrationActivity.this, getResources().getString(R.string.affiliateid_reqiured));
			}
			/*else if (AffiliateID.length() < 6) {
				ImageUtil.showAlert(RegistrationActivity.this, "Affiliate_ID is required limit of 6 digits.");
			}*/
			else if (firstname.length() < 1) {
				ImageUtil.showAlert(RegistrationActivity.this, "First Name is required.");
			}
			else if (lastname.length() < 1) {
				ImageUtil.showAlert(RegistrationActivity.this, "Last Name is required.");
			}
			else if (username.length() < 1) {
				ImageUtil.showAlert(RegistrationActivity.this, "Store Name is required.");
			}
			else if (email.length() < 1) {
				ImageUtil.showAlert(RegistrationActivity.this, "Email is required.");
			}
			else if (isEmailValid(email) == false) {
				ImageUtil.showAlert(RegistrationActivity.this, "Invalid Email.");
			}
			
			else if (phone.length() < 1) {
				ImageUtil.showAlert(RegistrationActivity.this, "Phone is required.");
			}
			//PHONE.matcher(input).matches();
			else if (isPhoneValid(phone)==false) {
				
				ImageUtil.showAlert(RegistrationActivity.this, "Invalid Phone");
			}
			else if (password.length() < 1) {
				ImageUtil.showAlert(RegistrationActivity.this, "Password is required");
			}
			else if (ispicture==false) {
				ImageUtil.showAlert(RegistrationActivity.this, "Please select the profile picture");
			}
			
			else{
				
				boolean isinternet=ImageUtil.isInternetOn(context);
				if (isinternet) {
					prodialog=ProgressDialog.show(context, "", "Registering...");
					RegistrationTask task=new RegistrationTask();
					task.execute();
				}
				else{
					ImageUtil.showAlert(RegistrationActivity.this, getResources().getString(R.string.internet_error));
				}
				
			}
		}
		else if (btn_back==arg0) {
			
			finish();
		}
		else if (btn_talepic==arg0) {
			showDialogButtonClick();
		}
		
	}
	
	
	public boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	} 
	
	public boolean isPhoneValid(CharSequence phone) {
		return android.util.Patterns.PHONE.matcher(phone).matches();
	} 
	
	 private class RegistrationTask extends AsyncTask<String, Void, String> {
			String response = "";
			
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String regid=Prefs.getString(ImageConstant.PROPERTY_REG_ID, "");
				
				String url=ImageConstant.REGISTRATIONURL;
				//String url="http://23.21.71.132/Gallery_download/register.php";
				HttpClient client = new DefaultHttpClient();
			    HttpPost post = new HttpPost(url);
			    MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();        
			    mpEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				
		
		        try {
		        	if (uploadImagePath != null) {
		        		
		        		File file = new File(uploadImagePath);
		        		FileBody cbFile = new FileBody(file);         
				        mpEntity.addPart("fileName", cbFile);
					}
		        
					mpEntity.addTextBody("fname", firstname);
					mpEntity.addTextBody("lname", lastname);
					mpEntity.addTextBody("email", email);
					mpEntity.addTextBody("uname", username);
					mpEntity.addTextBody("psword", password);
					mpEntity.addTextBody("phoneno", phone);
					mpEntity.addTextBody("Register_ID", regid);
					mpEntity.addTextBody("Affiliate_ID", AffiliateID);
					
					HttpEntity yourEntity = mpEntity.build();
				    post.setEntity(yourEntity);
				    HttpResponse  response1 = client.execute(post);
				    HttpEntity resEntity = response1.getEntity();
				    response=EntityUtils.toString(resEntity);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		      
			 return response;
			
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					try{
						
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Partner join successfully")) {
							
							// do here what you here.
							edittext_affilatedid.setText("");
							edittext_email.setText("");
							edittext_fname.setText("");
							edittext_lname.setText("");
							edittext_password.setText("");
							edittext_username.setText("");
							edittext_phone.setText("");
							imageview_pic.setImageResource(R.drawable.ic_pic);
							ispicture=false;
							
							showAlert(RegistrationActivity.this, status);
						}
						else if (status.trim().equalsIgnoreCase("unable to upload the file")) {
							ImageUtil.showAlert(RegistrationActivity.this, status);
						}
						else if (status.trim().equalsIgnoreCase("EmailId/UserName already Registered")) {
							ImageUtil.showAlert(RegistrationActivity.this, "EmailId/Store Name already Registered");
						}
						else if (status.trim().equalsIgnoreCase("EmailId/UserName/Affiliate_ID already Registered")) {
							ImageUtil.showAlert(RegistrationActivity.this, "EmailId/Store Name/Affiliate_ID already Registered");
						}
					}catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					
					ImageUtil.showAlert(RegistrationActivity.this, "Unable to connect to server. Please try again.");
				}

			}
		}
	 
	 private void showDialogButtonClick() {
			try {
				Log.i(TAG, "show Dialog for camera and gallery");
		
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Choose");
				
				final CharSequence[] choiceList = { "Take New Photo","Choose Existing Photo" };
				int selected = -1; // does not select anything

				builder.setSingleChoiceItems(choiceList, selected,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int itemNO) {

								Log.v(TAG, "You selected: " + itemNO);
								if (itemNO == 0) {
									alert.dismiss();
									isCameraOrGallery = 1;
									

									Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
									startActivityForResult(i, CAMERA_RESULT);

								} else if (itemNO == 1) {
									alert.dismiss();

									isCameraOrGallery = 2;
									Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									startActivityForResult(i, GALLERY_RESULT);

								}

							}
						}).setCancelable(false);
			      	builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});

				alert = builder.create();
				alert.show();

			} catch (Exception ex) {
				Log.v(TAG + ".showDialogButtonClick", "Exception  is " + ex);
				
			}
		}
	 
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
			super.onActivityResult(requestCode, resultCode, resultData);
			//Log.v(TAG + ".onActivityResult", "onActivityResult");
			ImageUtil.galleryLog(TAG, "onActivityResult");
			
			ispicture=true;
			try {

				if (isCameraOrGallery == 1 && resultData != null) {
					Bundle extras = resultData.getExtras();
				
					Bitmap bmp = (Bitmap) extras.get("data");
					Uri tempUri = getImageUri(getApplicationContext(), bmp);
					uploadImagePath=getRealPathFromURI(tempUri);
				   
					//imagepath=resultData.getExtras().get("data").
					imageview_pic.setImageBitmap(bmp);
					//Uri selectedImageUri = resultData.getData();
					//String path = FileUtils.getPath(context, uri)
					//uploadImagePath=getPath(context, selectedImageUri);
					//Log.v(TAG, "image path is: "+ uploadImagePath);
					ImageUtil.galleryLog(TAG, "image path is: "+ uploadImagePath);

				} else if (isCameraOrGallery == 2 && resultData != null) {
					
					Uri selectedImage = resultData.getData();
					
					if (Build.VERSION.SDK_INT < 19) {
						
						uploadImagePath=getRealPathFromURI(selectedImage);
						//Toast.makeText(context, "Path is: "+uploadImagePath, Toast.LENGTH_LONG).show();
						//Log.v(TAG, "image path is: "+ uploadImagePath);
						ImageUtil.galleryLog(TAG, "image path is: "+ uploadImagePath);
						Bitmap bitmapUploadImage=decodeSampledBitmapFromPath(uploadImagePath, 200, 200);
						//Bitmap bitmapUploadImage = BitmapFactory.decodeFile(uploadImagePath);
						imageview_pic.setImageBitmap(bitmapUploadImage);
					}
					else{
						 ParcelFileDescriptor parcelFileDescriptor;
			                try {
			                    parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImage, "r");
			                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			                    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			                    parcelFileDescriptor.close();
			                    imageview_pic.setImageBitmap(image);
			                    
			                    Uri tempUri = getImageUri(getApplicationContext(), image);
								uploadImagePath=getRealPathFromURI(tempUri);
								//Log.v(TAG, "image path is: "+ uploadImagePath);
								ImageUtil.galleryLog(TAG, "image path is: "+ uploadImagePath);
								//Bitmap bitmapUploadImage=decodeSampledBitmapFromPath(uploadImagePath, 200, 200);
								//Bitmap bitmapUploadImage = BitmapFactory.decodeFile(uploadImagePath);
								//imageview_pic.setImageBitmap(bitmapUploadImage);

			                } catch (FileNotFoundException e) {
			                    e.printStackTrace();
			                } catch (IOException e) {
			                    e.printStackTrace();
			                }
					}
					
					
				} 

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		
		public Uri getImageUri(Context inContext, Bitmap inImage) {
		    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		    String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		    return Uri.parse(path);
		}

		public String getRealPathFromURI(Uri uri) {
		    Cursor cursor = getContentResolver().query(uri, null, null, null, null); 
		    cursor.moveToFirst(); 
		    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		    return cursor.getString(idx); 
		}
		/* public String getRealPathFromURI(Uri contentUri)
		    {
		        try
		        {
		            String[] proj = {MediaStore.Images.Media.DATA};
		            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		            cursor.moveToFirst();
		            return cursor.getString(column_index);
		        }
		        catch (Exception e)
		        {
		            return contentUri.getPath();
		        }
		    }*/
		 public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
			 
		        final BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inJustDecodeBounds = true;
		        BitmapFactory.decodeFile(path, options);
		    
		      //  Log.v(TAG, "before compression width: "+options.outWidth);
		       // Log.v(TAG, "before compression heigth: "+options.outHeight);
		        
		    	ImageUtil.galleryLog(TAG, "before compression width: "+options.outWidth);
		    	ImageUtil.galleryLog(TAG, "before compression heigth: "+options.outHeight);
		        
		        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		 
		        // Decode bitmap with inSampleSize set
		        options.inJustDecodeBounds = false;
		        Bitmap bmp = BitmapFactory.decodeFile(path, options);
		        return bmp;
		        }
		 
		    public static int calculateInSampleSize(BitmapFactory.Options options,
		            int reqWidth, int reqHeight) {
		 
		        final int height = options.outHeight;
		        final int width = options.outWidth;
		        int inSampleSize = 1;
		 
		        if (height > reqHeight || width > reqWidth) {
		            if (width > height) {
		                inSampleSize = Math.round((float) height / (float) reqHeight);
		            } else {
		                inSampleSize = Math.round((float) width / (float) reqWidth);
		             }
		         }
		         return inSampleSize;
		        }
		    
		    public void showAlert(Activity activity, String message) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				//builder.setCustomTitle(title); 
				builder.setMessage(message);
				
				builder.setCancelable(false);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						
						Intent intent=new Intent(RegistrationActivity.this, LoginNewActvity.class);
						startActivity(intent);
						finish();

					}

				});

				AlertDialog alert = builder.create();
				alert.show();
				TextView messageText = (TextView) alert.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
			}
		    
		    	@TargetApi(19)
				@SuppressLint("NewApi")
				public String getPath(Context context, final Uri uri) {

			        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

			        // DocumentProvider
			        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			            // ExternalStorageProvider
			            if (isExternalStorageDocument(uri)) {
			                final String docId = DocumentsContract.getDocumentId(uri);
			                final String[] split = docId.split(":");
			                final String type = split[0];

			                if ("primary".equalsIgnoreCase(type)) {
			                    return Environment.getExternalStorageDirectory() + "/" + split[1];
			                }

			                // TODO handle non-primary volumes
			            }
			            // DownloadsProvider
			            else if (isDownloadsDocument(uri)) {

			                final String id = DocumentsContract.getDocumentId(uri);
			                final Uri contentUri = ContentUris.withAppendedId(
			                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

			                return getDataColumn(context, contentUri, null, null);
			            }
			            // MediaProvider
			            else if (isMediaDocument(uri)) {
			                final String docId = DocumentsContract.getDocumentId(uri);
			                final String[] split = docId.split(":");
			                final String type = split[0];

			                Uri contentUri = null;
			                if ("image".equals(type)) {
			                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			                } else if ("video".equals(type)) {
			                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			                } else if ("audio".equals(type)) {
			                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			                }

			                final String selection = "_id=?";
			                final String[] selectionArgs = new String[] {
			                        split[1]
			                };

			                return getDataColumn(context, contentUri, selection, selectionArgs);
			            }
			        }
			        // MediaStore (and general)
			        else if ("content".equalsIgnoreCase(uri.getScheme())) {
			            return getDataColumn(context, uri, null, null);
			        }
			        // File
			        else if ("file".equalsIgnoreCase(uri.getScheme())) {
			            return uri.getPath();
			        }

			        return null;
			    }
		    
		    /**
		     * Get the value of the data column for this Uri. This is useful for
		     * MediaStore Uris, and other file-based ContentProviders.
		     *
		     * @param context The context.
		     * @param uri The Uri to query.
		     * @param selection (Optional) Filter used in the query.
		     * @param selectionArgs (Optional) Selection arguments used in the query.
		     * @return The value of the _data column, which is typically a file path.
		     */
		    public static String getDataColumn(Context context, Uri uri, String selection,
		            String[] selectionArgs) {

		        Cursor cursor = null;
		        final String column = "_data";
		        final String[] projection = {
		                column
		        };

		        try {
		            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
		                    null);
		            if (cursor != null && cursor.moveToFirst()) {
		                final int column_index = cursor.getColumnIndexOrThrow(column);
		                return cursor.getString(column_index);
		            }
		        } finally {
		            if (cursor != null)
		                cursor.close();
		        }
		        return null;
		    }
		    

		    /**
		     * @param uri The Uri to check.
		     * @return Whether the Uri authority is ExternalStorageProvider.
		     */
		    public static boolean isExternalStorageDocument(Uri uri) {
		        return "com.android.externalstorage.documents".equals(uri.getAuthority());
		    }

		    /**
		     * @param uri The Uri to check.
		     * @return Whether the Uri authority is DownloadsProvider.
		     */
		    public static boolean isDownloadsDocument(Uri uri) {
		        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
		    }

		    /**
		     * @param uri The Uri to check.
		     * @return Whether the Uri authority is MediaProvider.
		     */
		    public static boolean isMediaDocument(Uri uri) {
		        return "com.android.providers.media.documents".equals(uri.getAuthority());
		    }
}
