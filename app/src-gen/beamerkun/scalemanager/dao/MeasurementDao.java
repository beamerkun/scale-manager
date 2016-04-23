package beamerkun.scalemanager.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import beamerkun.scalemanager.dao.Measurement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MEASUREMENT".
*/
public class MeasurementDao extends AbstractDao<Measurement, Long> {

    public static final String TABLENAME = "MEASUREMENT";

    /**
     * Properties of entity Measurement.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Date = new Property(1, java.util.Date.class, "date", false, "DATE");
        public final static Property Weight = new Property(2, Float.class, "weight", false, "WEIGHT");
        public final static Property BodyFat = new Property(3, Float.class, "bodyFat", false, "BODY_FAT");
        public final static Property BodyWater = new Property(4, Float.class, "bodyWater", false, "BODY_WATER");
        public final static Property BoneWeight = new Property(5, Float.class, "boneWeight", false, "BONE_WEIGHT");
        public final static Property MuscleMass = new Property(6, Float.class, "muscleMass", false, "MUSCLE_MASS");
        public final static Property VisceralFat = new Property(7, Integer.class, "visceralFat", false, "VISCERAL_FAT");
        public final static Property BMR = new Property(8, Integer.class, "BMR", false, "BMR");
        public final static Property BMI = new Property(9, Float.class, "BMI", false, "BMI");
    };


    public MeasurementDao(DaoConfig config) {
        super(config);
    }
    
    public MeasurementDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MEASUREMENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"DATE\" INTEGER," + // 1: date
                "\"WEIGHT\" REAL," + // 2: weight
                "\"BODY_FAT\" REAL," + // 3: bodyFat
                "\"BODY_WATER\" REAL," + // 4: bodyWater
                "\"BONE_WEIGHT\" REAL," + // 5: boneWeight
                "\"MUSCLE_MASS\" REAL," + // 6: muscleMass
                "\"VISCERAL_FAT\" INTEGER," + // 7: visceralFat
                "\"BMR\" INTEGER," + // 8: BMR
                "\"BMI\" REAL);"); // 9: BMI
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MEASUREMENT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Measurement entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(2, date.getTime());
        }
 
        Float weight = entity.getWeight();
        if (weight != null) {
            stmt.bindDouble(3, weight);
        }
 
        Float bodyFat = entity.getBodyFat();
        if (bodyFat != null) {
            stmt.bindDouble(4, bodyFat);
        }
 
        Float bodyWater = entity.getBodyWater();
        if (bodyWater != null) {
            stmt.bindDouble(5, bodyWater);
        }
 
        Float boneWeight = entity.getBoneWeight();
        if (boneWeight != null) {
            stmt.bindDouble(6, boneWeight);
        }
 
        Float muscleMass = entity.getMuscleMass();
        if (muscleMass != null) {
            stmt.bindDouble(7, muscleMass);
        }
 
        Integer visceralFat = entity.getVisceralFat();
        if (visceralFat != null) {
            stmt.bindLong(8, visceralFat);
        }
 
        Integer BMR = entity.getBMR();
        if (BMR != null) {
            stmt.bindLong(9, BMR);
        }
 
        Float BMI = entity.getBMI();
        if (BMI != null) {
            stmt.bindDouble(10, BMI);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Measurement readEntity(Cursor cursor, int offset) {
        Measurement entity = new Measurement( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // date
            cursor.isNull(offset + 2) ? null : cursor.getFloat(offset + 2), // weight
            cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3), // bodyFat
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // bodyWater
            cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5), // boneWeight
            cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6), // muscleMass
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // visceralFat
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // BMR
            cursor.isNull(offset + 9) ? null : cursor.getFloat(offset + 9) // BMI
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Measurement entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDate(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setWeight(cursor.isNull(offset + 2) ? null : cursor.getFloat(offset + 2));
        entity.setBodyFat(cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3));
        entity.setBodyWater(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setBoneWeight(cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5));
        entity.setMuscleMass(cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6));
        entity.setVisceralFat(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setBMR(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setBMI(cursor.isNull(offset + 9) ? null : cursor.getFloat(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Measurement entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Measurement entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
