package com.linkage.mobile72.sh.datasource;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.linkage.mobile72.sh.data.AccountChild;
import com.linkage.mobile72.sh.data.AccountData;
import com.linkage.mobile72.sh.data.ClassRoom;
import com.linkage.mobile72.sh.data.Contact;
import com.linkage.mobile72.sh.data.Group;
import com.linkage.mobile72.sh.data.OLConfig;
import com.linkage.mobile72.sh.data.Person;
import com.linkage.mobile72.sh.data.SchoolData;
import com.linkage.mobile72.sh.data.StudentAtten;
import com.linkage.mobile72.sh.data.Subject;
import com.linkage.mobile72.sh.data.http.JXBean;

public class DataHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "EduCloud_JS.db";
	private static final int DATABASE_VERSION = 160;
	private static DataHelper instance;
	private Dao<AccountData, Integer> accountData = null;
	private Dao<AccountChild, Integer> accountChildDao = null;
	private Dao<SchoolData, Integer> schoolData = null;
	private Dao<Contact, Integer> contactData = null;
	private Dao<ClassRoom, Integer> classroomData = null;
	private Dao<OLConfig, Integer> olConfigDao = null;
	private Dao<JXBean, Integer> JXBeanDao = null;
	private Dao<StudentAtten, Integer> studentAttenData = null;
	private Dao<Subject, Integer> subjectDao = null;

	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, AccountData.class);
			TableUtils.createTable(connectionSource, AccountChild.class);
			TableUtils.createTable(connectionSource, SchoolData.class);
			TableUtils.createTable(connectionSource, Contact.class);
			TableUtils.createTable(connectionSource, ClassRoom.class);
			TableUtils.createTable(connectionSource, Person.class);
			TableUtils.createTable(connectionSource, Group.class);
			TableUtils.createTable(connectionSource, JXBean.class);
            TableUtils.createTable(connectionSource, OLConfig.class);
            TableUtils.createTable(connectionSource, StudentAtten.class);
            TableUtils.createTable(connectionSource, Subject.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static synchronized DataHelper getHelper(Context context) {
		if (instance == null) {
			synchronized (DataHelper.class) {
				if (instance == null)
					instance = new DataHelper(context);
			}
		}

		return instance;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int arg2, int arg3) {
		try {
			TableUtils.dropTable(connectionSource, AccountData.class, true);
			TableUtils.dropTable(connectionSource, AccountChild.class, true);
			TableUtils.dropTable(connectionSource, SchoolData.class, true);
			TableUtils.dropTable(connectionSource, Contact.class, true);
			TableUtils.dropTable(connectionSource, ClassRoom.class, true);
			TableUtils.dropTable(connectionSource, Person.class, true);
			TableUtils.dropTable(connectionSource, Group.class, true);
			TableUtils.dropTable(connectionSource, JXBean.class, true);
			TableUtils.dropTable(connectionSource, OLConfig.class, true);
			TableUtils.dropTable(connectionSource, StudentAtten.class, true);
			TableUtils.dropTable(connectionSource, Subject.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		super.close();
		accountData = null;
	}

	public Dao<AccountData, Integer> getAccountDao() throws SQLException {
		if (accountData == null)
			accountData = getDao(AccountData.class);
		return accountData;
	}
	
	public Dao<AccountChild, Integer> getAccountChildDao() throws SQLException {
		if (accountChildDao == null)
			accountChildDao = getDao(AccountChild.class);
		return accountChildDao;

	}

	public Dao<SchoolData, Integer> getSchoolData() throws SQLException {
		if (schoolData == null)
			schoolData = getDao(SchoolData.class);
		return schoolData;
	}

	public Dao<Contact, Integer> getContactData() throws SQLException {
		if (contactData == null)
			contactData = getDao(Contact.class);
		return contactData;
	}

	public Dao<ClassRoom, Integer> getClassRoomData() throws SQLException {
		if (classroomData == null)
			classroomData = getDao(ClassRoom.class);
		return classroomData;
	}
	
	public Dao<OLConfig,Integer> getOLConfigDao() throws SQLException {
		if (olConfigDao == null)
			olConfigDao = getDao(OLConfig.class);
		return olConfigDao;
	}

	public Dao<JXBean, Integer> getJXBeanDao() throws SQLException {
		if (JXBeanDao == null)
			JXBeanDao = getDao(JXBean.class);
		return JXBeanDao;

	}
	
	public Dao<StudentAtten, Integer> getStudentAttenData() throws SQLException {
        if (studentAttenData == null)
            studentAttenData = getDao(StudentAtten.class);
        return studentAttenData;
    }

	public Dao<Subject, Integer> getSubjectDao() throws SQLException {
		if (subjectDao == null)
			subjectDao = getDao(Subject.class);
        return subjectDao;
	}
	
}
