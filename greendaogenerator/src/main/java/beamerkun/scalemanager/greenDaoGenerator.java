package beamerkun.scalemanager;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class greenDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(2, "beamerkun.scalemanager.dao");

        schema.enableKeepSectionsByDefault();

        Entity measurement = addMeasurement(schema);
        Entity user = addUser(schema);

        Property userId = measurement.addLongProperty("userId").notNull().getProperty();
        ToMany userToMeasurement = user.addToMany(measurement, userId);
        userToMeasurement.setName("measurements");

        new DaoGenerator().generateAll(schema, "app/src-gen");
    }

    public static Entity addMeasurement(Schema schema) {
        Entity measurement = schema.addEntity("Measurement");

        measurement.addIdProperty();
        measurement.addDateProperty("date");
        measurement.addFloatProperty("weight");
        measurement.addFloatProperty("bodyFat");
        measurement.addFloatProperty("bodyWater");
        measurement.addFloatProperty("boneWeight");
        measurement.addFloatProperty("muscleMass");
        measurement.addIntProperty("visceralFat");
        measurement.addIntProperty("BMR");
        measurement.addFloatProperty("BMI");

        return measurement;
    }

    public static Entity addUser(Schema schema) {
        Entity user = schema.addEntity("User");

        user.addIdProperty();
        user.addIntProperty("height");
        user.addDateProperty("birthday");
        user.addBooleanProperty("isMale");

        return user;
    }
}
