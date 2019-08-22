package com.example.mobilehealthprototype;

public class DiseaseProb implements Comparable<DiseaseProb>{
    String umls, dname;
    float prob;

    public DiseaseProb(String umls, String dname, float prob){
        this.umls = umls;
        this.dname = dname;
        this.prob = prob;
    }

    @Override
    public String toString(){
        return umls + " ; " + dname + " ; " + prob;
    }

    @Override
    public int compareTo(DiseaseProb dp){
        float result = this.prob - dp.prob;
        if(result == 0){
            return 0;
        }else if (result < 0){
            return -1;
        }else{
            return 1;
        }
    }

    public String getUmls(){
        return umls;
    }

    public String getDisease(){
        return dname;
    }

    public float getProb(){
        return prob;
    }

    public void setProb(float new_prob){
        this.prob = new_prob;
    }
}