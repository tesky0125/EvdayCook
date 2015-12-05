package org.xiaoyan.db;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * 用于在sd卡上创建数据库的类，final版2012-07-10
 * 
 * @author xiaoyan
 * 
 */
public class SDCardDBHelper {
	// SD卡路径
	private String sdPath = "";

	// 下列静态字段需使用时指定

	// db文件保存相对SD卡的相对路径
	private static final String PATHNAME = "evdaycook/";
	// db文件的文件名
	private static final String FILENAME = "evdaycook.db";
	// db中的数据库表表名
	private static final String TABLENAME = "evdaycook";

	// 创建表字段
	private static final String SQL_CREATE_TABLE = " CREATE TABLE " + TABLENAME
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT , "
			+ " title VARCHAR(20) NOT NULL , " + " counts INTEGER , "
			+ " favor INTEGER , " + " type INTEGER , "
			+ " time VARCHAR(20) ); ";

	private static final String ALTER_ADD_COL = "ALTER TABLE " + TABLENAME
			+ " ADD favor INTEGER";
	// private static final String SQL_ALTER_TABLE_DROP =
	// "ALTER TABLE "+TABLENAME+" DROP COLUMN favor";
	// private static final String SQL_ALTER_TABLE_ALTER =
	// "ALTER TABLE "+TABLENAME+" ALTER favor VARCHAR(10)";

	// db路径/db文件对应的FILE
	private File dbPath = null;
	private File dbFile = null;

	/**
	 * SD卡上操作数据库文件类
	 */
	public SDCardDBHelper() {
		sdPath = getSDPath();
		dbPath = new File(sdPath + PATHNAME);
		dbFile = new File(sdPath + PATHNAME + FILENAME);
	}

	/**
	 * 初始化数据库，首次建立数据库使用
	 */
	public void init() {
		if (checkResource()) {
			createTable();
		}
	}

	/**
	 * 获取当前手机SD卡路径
	 * 
	 * @return
	 */
	private String getSDPath() {
		return Environment.getExternalStorageDirectory().toString() + "/";
	}

	/**
	 * 判断SD卡是否已经挂接好
	 * 
	 * @return
	 */
	private boolean isSDMounted() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查db资源，判断是否存在db路径和db文件，如果不存在则创建
	 * 
	 * @return
	 */
	private boolean checkResource() {
		// 首先应判断SD卡是否正常
		if (!isSDMounted()) {
			System.exit(-1);// 程序异常退出
		}
		// 数据库文件路径是否存在
		if (!dbPath.exists()) {
			if (!dbPath.mkdirs()) {
				return false;
			}
		}
		// 数据库文件是否存在
		if (!dbFile.exists()) {
			try {
				if (!dbFile.createNewFile()) {
					return false;
				}
			} catch (IOException e) {
			}
		}
		return true;
	}

	/**
	 * 创建数据库表
	 */
	private void createTable() {
		// TODO Auto-generated method stub
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			if (!isTableExist(TABLENAME)) {
				db.execSQL(SQL_CREATE_TABLE);
			}
		} catch (Exception e) {
			System.out.println("DBHelper-->Create Table Exception.");
		} finally {
			db.close();
		}

	}

	/**
	 * 检查数据库表是否存在
	 * 
	 * @param tabName
	 * @return
	 */
	private boolean isTableExist(String tabName) {
		if (tabName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			String sql = "select count(*) as cnt from sqlite_master where type ='table' and name ='"
					+ tabName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					return true;
				}
			}
		} catch (Exception e) {

		} finally {
			cursor.close();
			db.close();
		}
		return false;
	}

	/**
	 * 临时函数，用来修改数据库表
	 */
	public void execSQL() {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		db.execSQL(ALTER_ADD_COL);
	}

	// 数据库操作，外部使用

	/**
	 * 查询所有数据
	 * 
	 * @return
	 */
	public Cursor query() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			cursor = db.query(TABLENAME, new String[] { "_id", "title",
					"counts","type","favor" }, null, null, null, null, "_id desc");
		} catch (Exception e) {
			System.out.println("DBHelper-->Query Exception.");
		} finally {
			// cursor.close();
			// db.close();
			//使用完后注意关闭
		}
		return cursor;
	}
	/**
	 * 按类别查询数据
	 * 
	 * @return
	 */
	public Cursor query(int type) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			cursor = db.query(TABLENAME, new String[] { "_id", "title",
					"counts","type","favor" }, 
					" (type =  ?) ", new String[] { type+"" },
					null, null, "_id desc");
		} catch (Exception e) {
			System.out.println("DBHelper-->Query Exception.");
		} finally {
			// cursor.close();
			// db.close();
			//使用完后注意关闭
		}
		return cursor;
	}
	/**
	 * 增加一条数据
	 * 
	 * @param values
	 * @return
	 */
	public long insert(ContentValues values) {
		// TODO Auto-generated method stub
		long rowId = -1;
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			rowId = db.insert(TABLENAME, null, values);
		} catch (Exception e) {
			System.out.println("DBHelper-->Insert Exception.");
		} finally {
			db.close();
		}
		return rowId;
	}

	/**
	 * 更新数据
	 */
	public long update(long id, ContentValues values) {
		long res = -1;
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			res = db.update(TABLENAME, values, "_id=?", new String[] { String
					.valueOf(id) });
		} catch (Exception e) {
			System.out.println("DBHelper-->Update Exception.");
		} finally {
			db.close();
		}
		return res;
	}

	/**
	 * 删除一条数据
	 * 
	 * @param id
	 * @return
	 */
	public int delete(long id) {
		int res = -1;
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			res = db.delete(TABLENAME, "_id=?", new String[] { String
					.valueOf(id) });
		} catch (Exception e) {
			System.out.println("DBHelper-->Delete Exception.");
		} finally {
			db.close();
		}
		return res;
	}

	/**
	 * 删除多条数据
	 * 
	 * @param idList
	 */
	public void deleteMulti(List<Long> idList) {
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			for (Long id : idList) {
				db.delete(TABLENAME, "_id=?",
						new String[] { String.valueOf(id) });
			}
		} catch (Exception e) {
			System.out.println("DBHelper-->Delete Exception.");
		} finally {
			db.close();
		}
	}

}
