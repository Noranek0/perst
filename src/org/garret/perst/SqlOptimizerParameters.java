package org.garret.perst;

public class SqlOptimizerParameters {
    public boolean enableCostBasedOptimization;
    public int sequentialSearchCost;
    public int notUniqCost;
    public int eqCost;
    public int eqBoolCost;
    public int eqStringCost;
    public int eqRealCost;
    public int openIntervalCost;
    public int closeIntervalCost;
    public int containsCost;
    public int orCost;
    public int andCost;
    public int isNullCost;
    public int patternMatchCost;
    public int indirectionCost;

    public SqlOptimizerParameters() {
        enableCostBasedOptimization = false;
        sequentialSearchCost = 1000;
        openIntervalCost = 100;
        containsCost = 50;
        orCost = 10;
        andCost = 10;
        isNullCost = 6;
        closeIntervalCost = 5;
        patternMatchCost = 2;
        eqCost = 1;
        eqRealCost = 2;
        eqStringCost = 3;
        eqBoolCost = 200;
        indirectionCost = 2;
        notUniqCost = 1;
    }
}
