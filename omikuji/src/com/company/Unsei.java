package com.company;

public abstract class Unsei implements Fortune{
    protected String unsei;
    protected String negaigoto;
    protected String akinai;
    protected String gakumon;
    public abstract void setUnsei();

    public String getUnsei(){
        return unsei;
    }
    public void setUnsei(String unsei) {
        this.unsei=unsei;
    }

    public String getNegaigoto(){
        return negaigoto;
    }
    public void setNegaigoto(String negaigoto) {
        this.negaigoto=negaigoto;
    }

    public String getAkinai(){
        return akinai;
    }
    public void setAkinai(String akinai) {
        this.akinai=akinai;
    }

    public String getGakumon(){
        return gakumon;
    }
    public void setGakumon(String gakumon) {
        this.gakumon=gakumon;
    }

    public String disp() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(DISP_STR, unsei));
        sb.append("\n 願い事 : ");
        sb.append(getNegaigoto());
        sb.append("\n 商い : ");
        sb.append((getAkinai()));
        sb.append("\n 学問 : ");
        sb.append(getGakumon());
        return sb.toString();
    }
}
