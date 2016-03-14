package beamerkun.scalemanager;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class greenDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "beamerkun.scalemanager");
        addMeasurement(schema);
        new DaoGenerator().generateAll(schema, "app/src-gen");
    }

    public static void addMeasurement(Schema schema) {
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
    }
}
