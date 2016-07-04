package com.secondry.SpinnerAdapters;

/**
 * Created by Administrator on 23/06/2016.
 */
public class Model {

    String ModelId;

    public Model(String modelId, String modelaName) {
        ModelId = modelId;
        ModelaName = modelaName;
    }

    public String getModelId() {
        return ModelId;
    }

    public void setModelId(String modelId) {
        ModelId = modelId;
    }

    public String getModelaName() {
        return ModelaName;
    }

    String ModelaName;

    public String toString()
    {
        return ModelaName;
    }
}
