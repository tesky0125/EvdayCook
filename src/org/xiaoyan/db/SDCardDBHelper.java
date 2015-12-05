package org.xiaoyan.db;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * ������sd���ϴ������ݿ���࣬final��2012-07-10
 * 
 * @author xiaoyan
 * 
 */
public class SDCardDBHelper {
	// SD��·��
	private String sdPath = "";

	// ���о�̬�ֶ���ʹ��ʱָ��

	// db�ļ��������SD�������·��
	private static final String PATHNAME = "evdaycook/";
	// db�ļ����ļ���
	private static final String FILENAME = "evdaycook.db";
	// db�е����ݿ�����
	private static final String TABLENAME = "evdaycook";

	// �������ֶ�
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

	// db·��/db�ļ���Ӧ��FILE
	private File dbPath = null;
	private File dbFile = null;

	/**
	 * SD���ϲ������ݿ��ļ���
	 */
	public SDCardDBHelper() {
		sdPath = getSDPath();
		dbPath = new File(sdPath + PATHNAME);
		dbFile = new File(sdPath + PATHNAME + FILENAME);
	}

	/**
	 * ��ʼ�����ݿ⣬�״ν������ݿ�ʹ��
	 */
	public void init() {
		if (checkResource()) {
			createTable();
		}
	}

	/**
	 * ��ȡ��ǰ�ֻ�SD��·��
	 * 
	 * @return
	 */
	private String getSDPath() {
		return Environment.getExternalStorageDirectory().toString() + "/";
	}

	/**
	 * �ж�SD���Ƿ��Ѿ��ҽӺ�
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
	 * ���db��Դ���ж��Ƿ����db·����db�ļ�������������򴴽�
	 * 
	 * @return
	 */
	private boolean checkResource() {
		// ����Ӧ�ж�SD���Ƿ�����
		if (!isSDMounted()) {
			System.exit(-1);// �����쳣�˳�
		}
		// ���ݿ��ļ�·���Ƿ����
		if (!dbPath.exists()) {
			if (!dbPath.mkdirs()) {
				return false;
			}
		}
		// ���ݿ��ļ��Ƿ����
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
	 * �������ݿ��
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
	 * ������ݿ���Ƿ����
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
	 * ��ʱ�����������޸����ݿ��
	 */
	public void execSQL() {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		db.execSQL(ALTER_ADD_COL);
	}

	// ���ݿ�������ⲿʹ��

	/**
	 * ��ѯ��������
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
			//ʹ�����ע��ر�
		}
		return cursor;
	}
	/**
	 * ������ѯ����
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
			//ʹ�����ע��ر�
		}
		return cursor;
	}
	/**
	 * ����һ������
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
	 * ��������
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
	 * ɾ��һ������
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
	 * ɾ����������
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
