package beamerkun.scalemanager.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import beamerkun.scalemanager.dao.Measurement;

import beamerkun.scalemanager.dao.MeasurementDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig measurementDaoConfig;

    private final MeasurementDao measurementDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        measurementDaoConfig = daoConfigMap.get(MeasurementDao.class).clone();
        measurementDaoConfig.initIdentityScope(type);

        measurementDao = new MeasurementDao(measurementDaoConfig, this);

        registerDao(Measurement.class, measurementDao);
    }
    
    public void clear() {
        measurementDaoConfig.getIdentityScope().clear();
    }

    public MeasurementDao getMeasurementDao() {
        return measurementDao;
    }

}
