package beamerkun.scalemanager.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MEASUREMENT".
 */
public class Measurement {

    private Long id;
    private java.util.Date date;
    private Float weight;
    private Float bodyFat;
    private Float bodyWater;
    private Float boneWeight;
    private Float muscleMass;
    private Integer visceralFat;
    private Integer BMR;
    private Float BMI;

    public Measurement() {
    }

    public Measurement(Long id) {
        this.id = id;
    }

    public Measurement(Long id, java.util.Date date, Float weight, Float bodyFat, Float bodyWater, Float boneWeight, Float muscleMass, Integer visceralFat, Integer BMR, Float BMI) {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.bodyFat = bodyFat;
        this.bodyWater = bodyWater;
        this.boneWeight = boneWeight;
        this.muscleMass = muscleMass;
        this.visceralFat = visceralFat;
        this.BMR = BMR;
        this.BMI = BMI;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(Float bodyFat) {
        this.bodyFat = bodyFat;
    }

    public Float getBodyWater() {
        return bodyWater;
    }

    public void setBodyWater(Float bodyWater) {
        this.bodyWater = bodyWater;
    }

    public Float getBoneWeight() {
        return boneWeight;
    }

    public void setBoneWeight(Float boneWeight) {
        this.boneWeight = boneWeight;
    }

    public Float getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(Float muscleMass) {
        this.muscleMass = muscleMass;
    }

    public Integer getVisceralFat() {
        return visceralFat;
    }

    public void setVisceralFat(Integer visceralFat) {
        this.visceralFat = visceralFat;
    }

    public Integer getBMR() {
        return BMR;
    }

    public void setBMR(Integer BMR) {
        this.BMR = BMR;
    }

    public Float getBMI() {
        return BMI;
    }

    public void setBMI(Float BMI) {
        this.BMI = BMI;
    }

}
