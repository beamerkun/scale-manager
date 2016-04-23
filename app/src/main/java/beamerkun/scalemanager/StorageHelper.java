package beamerkun.scalemanager;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import beamerkun.scalemanager.dao.DaoMaster;
import beamerkun.scalemanager.dao.DaoSession;
import beamerkun.scalemanager.dao.Measurement;
import beamerkun.scalemanager.dao.MeasurementDao;
import beamerkun.scalemanager.dao.User;
import beamerkun.scalemanager.dao.UserDao;

public class StorageHelper {

    public static StorageHelper sInstance = null;

    private StorageHelper(Context context) {
        helper = new DaoMaster.DevOpenHelper(context, "scalemanager-db", null);
        daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
        measurementDao = daoSession.getMeasurementDao();
        userDao = daoSession.getUserDao();

        // TODO remove these when we get all sorted out
        measurementDao.deleteAll();
        userDao.deleteAll();

        User test_user = new User();

        Date birthday = new Date();
        try {
            birthday = new SimpleDateFormat("dd-mm-yyyy").parse("08-05-1992");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        test_user.setBirthday(birthday);
        test_user.setHeight(182);
        test_user.setIsMale(true);
        userDao.insert(test_user);
    }

    static StorageHelper getInstance(Context context) {
        if(sInstance == null)
            sInstance = new StorageHelper(context);
        return sInstance;
    }

    private DaoMaster.OpenHelper helper = null;
    private DaoMaster daoMaster = null;
    private DaoSession daoSession = null;
    private MeasurementDao measurementDao = null;
    private UserDao userDao = null;

    public void saveMeasurement(Measurement measurement) {
        measurementDao.insert(measurement);
    }

    public User getUser() {
        // TODO proper implementation
        return userDao.queryBuilder().list().get(0);
    }
}
