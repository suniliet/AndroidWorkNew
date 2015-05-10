package com.kns.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.drive.internal.SetResourceParentsRequest;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kns.model.CategoryModel;
import com.kns.model.ImageModel;
import com.kns.model.Pending_Uploadurl_model;
import com.kns.model.RequestedSetModel;
import com.kns.model.VideoModel;
import com.kns.util.ImageUtil;


public class DBHelper extends OrmLiteSqliteOpenHelper{

	private static final String TAG="DBHelper";
	private static final String DATABASE_NAME = "galleryupload.db";
	private static final int DATABASE_VERSION = 2;
	private RuntimeExceptionDao<ImageModel, String> imageRuntimeDao = null;
	private RuntimeExceptionDao<VideoModel, String> videoRuntimeDao = null;
	private RuntimeExceptionDao<CategoryModel, String> categoryRuntimeDao = null;
	private RuntimeExceptionDao<RequestedSetModel, String> setrequestRuntimeDao = null;
	private RuntimeExceptionDao<Pending_Uploadurl_model, String> pendinguploadRuntimeDao = null;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	// adding new pendng table
	
	public RuntimeExceptionDao<Pending_Uploadurl_model, String> pendinguploaddao() {
		if (pendinguploadRuntimeDao == null) {
			pendinguploadRuntimeDao = getRuntimeExceptionDao(Pending_Uploadurl_model.class);
		}
		return pendinguploadRuntimeDao;
  	}
	
	
	 public int addPendingUploaddata(Pending_Uploadurl_model details)
	 	{
	 		RuntimeExceptionDao<Pending_Uploadurl_model, String> dao = pendinguploaddao();
	 		int i = dao.create(details);
	 		return i;
	 	}
	 
		public List<Pending_Uploadurl_model> getPendingUpload()
		{
			//Log.v(TAG, "GetDataVolumeChange call");
			RuntimeExceptionDao<Pending_Uploadurl_model, String> simpleDao = pendinguploaddao();
			List<Pending_Uploadurl_model> list = simpleDao.queryForAll();
			return list;
		}
		
		public List<Pending_Uploadurl_model> getPendinguploadType(String uploadtype) throws SQLException
		{
			Log.v(TAG, "getPendinguploadType call");
			RuntimeExceptionDao<Pending_Uploadurl_model, String> simpleDao = pendinguploaddao();
			 QueryBuilder<Pending_Uploadurl_model, String> qb = simpleDao.queryBuilder();
			 Where<Pending_Uploadurl_model, String> where = qb.where();
			 where.eq("upload_type", uploadtype);
			 PreparedQuery<Pending_Uploadurl_model> preparedQuery = qb.prepare();
			 List<Pending_Uploadurl_model> list = simpleDao.query(preparedQuery);
			return list;
		}
	
		public void DeletePendingRow(String fileurl)
		{
			RuntimeExceptionDao<Pending_Uploadurl_model, String> dao = pendinguploaddao();
			DeleteBuilder<Pending_Uploadurl_model, String> deleteBuilder = dao.deleteBuilder();
			try {
				deleteBuilder.where().eq("fileurl", fileurl);
				deleteBuilder.delete();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		public void DeletePendingUploadtype()
		{
			RuntimeExceptionDao<Pending_Uploadurl_model, String> dao = pendinguploaddao();
			List<Pending_Uploadurl_model> list = dao.queryForAll();
			dao.delete(list);
		}
	
	
	// adding new set id
	
	public RuntimeExceptionDao<RequestedSetModel, String> requestsetdao() {
		if (setrequestRuntimeDao == null) {
			setrequestRuntimeDao = getRuntimeExceptionDao(RequestedSetModel.class);
		}
		return setrequestRuntimeDao;
  	}
	
	
	 public int addRequestSet(RequestedSetModel details)
	 	{
	 		RuntimeExceptionDao<RequestedSetModel, String> dao = requestsetdao();
	 		int i = dao.create(details);
	 		return i;
	 	}
	 
		public List<RequestedSetModel> getReqSetData()
		{
			//Log.v(TAG, "GetDataVolumeChange call");
			RuntimeExceptionDao<RequestedSetModel, String> simpleDao = requestsetdao();
			List<RequestedSetModel> list = simpleDao.queryForAll();
			return list;
		}
		
		public List<RequestedSetModel> getRequestsetId(String setid) throws SQLException
		{
			Log.v(TAG, "getRequestsetId call");
			RuntimeExceptionDao<RequestedSetModel, String> simpleDao = requestsetdao();
			 QueryBuilder<RequestedSetModel, String> qb = simpleDao.queryBuilder();
			 Where<RequestedSetModel, String> where = qb.where();
			 where.eq("setid", setid);
			 PreparedQuery<RequestedSetModel> preparedQuery = qb.prepare();
			 List<RequestedSetModel> list = simpleDao.query(preparedQuery);
			return list;
		}
	
		
		public void DeleteRequestSet()
		{
			RuntimeExceptionDao<RequestedSetModel, String> dao = requestsetdao();
			List<RequestedSetModel> list = dao.queryForAll();
			dao.delete(list);
		}
	
	// adding new set id
	
	
	public RuntimeExceptionDao<CategoryModel, String> categorydao() {
		if (categoryRuntimeDao == null) {
			categoryRuntimeDao = getRuntimeExceptionDao(CategoryModel.class);
		}
		return categoryRuntimeDao;
  	}
	
	
	 public int addCategory(CategoryModel details)
	 	{
	 		RuntimeExceptionDao<CategoryModel, String> dao = categorydao();
	 		int i = dao.create(details);
	 		return i;
	 	}
	 
		public List<CategoryModel> GetCategoryData()
		{
			//Log.v(TAG, "GetDataVolumeChange call");
			RuntimeExceptionDao<CategoryModel, String> simpleDao = categorydao();
			List<CategoryModel> list = simpleDao.queryForAll();
			return list;
		}
		
		public List<CategoryModel> GetCategoryId(String catid) throws SQLException
		{
			Log.v(TAG, "GetCategoryId call");
			RuntimeExceptionDao<CategoryModel, String> simpleDao = categorydao();
			 QueryBuilder<CategoryModel, String> qb = simpleDao.queryBuilder();
			 Where<CategoryModel, String> where = qb.where();
			 where.eq("cat_id", catid);
			 PreparedQuery<CategoryModel> preparedQuery = qb.prepare();
			 List<CategoryModel> list = simpleDao.query(preparedQuery);
			return list;
		}
	
		
		public void DeleteCategory()
		{
			RuntimeExceptionDao<CategoryModel, String> dao = categorydao();
			List<CategoryModel> list = dao.queryForAll();
			dao.delete(list);
		}
	 	
	
	public RuntimeExceptionDao<VideoModel, String> videodao() {
		if (videoRuntimeDao == null) {
			videoRuntimeDao = getRuntimeExceptionDao(VideoModel.class);
		}
		return videoRuntimeDao;
  	}
	
	
	 public int addVideo(VideoModel details)
	 	{
	 		RuntimeExceptionDao<VideoModel, String> dao = videodao();
	 		int i = dao.create(details);
	 		return i;
	 	}
	 
		public List<VideoModel> GetVideoData()
		{
			//Log.v(TAG, "GetDataVolumeChange call");
			RuntimeExceptionDao<VideoModel, String> simpleDao = videodao();
			List<VideoModel> list = simpleDao.queryForAll();
			return list;
		}
		
		public List<VideoModel> GetVideoId(String url) throws SQLException
		{
			Log.v(TAG, "GetVideoId call");
			RuntimeExceptionDao<VideoModel, String> simpleDao = videodao();
			 QueryBuilder<VideoModel, String> qb = simpleDao.queryBuilder();
			 Where<VideoModel, String> where = qb.where();
			 where.eq("videoeurl", url);
			 PreparedQuery<VideoModel> preparedQuery = qb.prepare();
			 List<VideoModel> list = simpleDao.query(preparedQuery);
			return list;
		}
	
		
		public void DeleteVideo()
		{
			RuntimeExceptionDao<VideoModel, String> dao = videodao();
			List<VideoModel> list = dao.queryForAll();
			dao.delete(list);
		}
	 	
	
	
	public RuntimeExceptionDao<ImageModel, String> imagedao() {
		if (imageRuntimeDao == null) {
			imageRuntimeDao = getRuntimeExceptionDao(ImageModel.class);
		}
		return imageRuntimeDao;
  	}
	
	
	 public int addImage(ImageModel details)
	 	{
		 
		   ImageUtil.galleryLog(TAG, "addImage called");
	 		RuntimeExceptionDao<ImageModel, String> dao = imagedao();
	 		int i = dao.create(details);
	 		return i;
	 	}
	 
		public List<ImageModel> GetImageData()
		{
			//Log.v(TAG, "GetDataVolumeChange call");
			RuntimeExceptionDao<ImageModel, String> simpleDao = imagedao();
			List<ImageModel> list = simpleDao.queryForAll();
			return list;
		}
		
		public List<ImageModel> GetImageUrl(String url) throws SQLException
		{
			Log.v(TAG, "GetImageUrl call");
			RuntimeExceptionDao<ImageModel, String> simpleDao = imagedao();
			 QueryBuilder<ImageModel, String> qb = simpleDao.queryBuilder();
			 Where<ImageModel, String> where = qb.where();
			 where.eq("imageurl", url);
			 PreparedQuery<ImageModel> preparedQuery = qb.prepare();
			 List<ImageModel> list = simpleDao.query(preparedQuery);
			return list;
		 }
	
		
		public void DeleteImage()
		{
			RuntimeExceptionDao<ImageModel, String> dao = imagedao();
			List<ImageModel> list = dao.queryForAll();
			dao.delete(list);
		}
	 	
	
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource connectionSource) {
	
		try {
			TableUtils.createTable(connectionSource, ImageModel.class);
			TableUtils.createTable(connectionSource, VideoModel.class);
			TableUtils.createTable(connectionSource, CategoryModel.class);
			TableUtils.createTable(connectionSource, RequestedSetModel.class);
			TableUtils.createTable(connectionSource, Pending_Uploadurl_model.class);
			} catch (SQLException e) {
				Log.e(DBHelper.class.getName(), "Can't create database", e);
				throw new RuntimeException(e);
			}
			
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int arg2, int arg3) {
		
		try {
			TableUtils.dropTable(connectionSource, ImageModel.class, true);
			TableUtils.dropTable(connectionSource, VideoModel.class, true);
			TableUtils.dropTable(connectionSource, CategoryModel.class, true);
			TableUtils.dropTable(connectionSource, RequestedSetModel.class, true);
			TableUtils.dropTable(connectionSource, Pending_Uploadurl_model.class, true);
			onCreate(db, connectionSource);
			} catch (SQLException e) {
			Log.e(DBHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
}
	
