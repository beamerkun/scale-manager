package beamerkun.scalemanager;

import android.content.Context;

import beamerkun.scalemanager.dao.DaoMaster;
import beamerkun.scalemanager.dao.DaoSession;
import beamerkun.scalemanager.dao.Measurement;
import beamerkun.scalemanager.dao.MeasurementDao;

public class StorageHelper {

    public static StorageHelper sInstance = null;

    private StorageHelper(Context context) {
        helper = new DaoMaster.DevOpenHelper(context, "scalemanager-db", null);
        daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
        measurementDao = daoSession.getMeasurementDao();
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

    public void saveMeasurement(Measurement measurement) {
        measurementDao.insert(measurement);

        System.out.println(measurementDao.queryBuilder().count());
    }
}
