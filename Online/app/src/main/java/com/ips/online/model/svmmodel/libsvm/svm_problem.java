package com.ips.online.model.svmmodel.libsvm;

public class svm_problem implements java.io.Serializable
{
    public int l;
    public double[] y;
    public svm_node[][] x;
}