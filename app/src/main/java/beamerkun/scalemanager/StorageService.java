package beamerkun.scalemanager;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import beamerkun.scalemanager.dao.DaoMaster;
import beamerkun.scalemanager.dao.DaoSession;
import beamerkun.scalemanager.dao.Measurement;
import beamerkun.scalemanager.dao.MeasurementDao;

public class StorageService extends IntentService {

    public StorageService() {
        super("ScaleManager.StorageService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Measurement m = new Measurement(null);
        measurementDao.insert(m);
    }

    private DaoMaster.OpenHelper helper = null;
    private DaoMaster daoMaster = null;
    private DaoSession daoSession = null;
    private MeasurementDao measurementDao = null;
}
