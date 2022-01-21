package com.company;

public abstract class Unsei implements Fortune{
    protected String unsei;
    protected String negaigoto;
    protected String akinai;
    protected String gakumon;
    public abstract void setUnsei();

    public void setUnsei(String unsei) {
        this.unsei=unsei;
    }
    public void setNegaigoto(String negaigoto) {
        this.negaigoto=negaigoto;
    }
    public void setAkinai(String akinai) {
        this.akinai=akinai;
    }
    public void setGakumon(String gakumon) {
        this.gakumon=gakumon;
    }
}
