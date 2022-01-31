package com.company;

import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Properties;

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

    public String disp() throws IOException {
//인터페이스로만 읽어들여야 하므로 추상클래스에서는 프로퍼티를 읽어오지 않아도 OK!
//        Properties p = new Properties();
//        String path = Unsei.class.getResource("fortune.properties").getPath();
//        p.load(new FileReader(path));
//        String DISP_STR = p.getProperty("disp_str");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(DISP_STR, getUnsei()));
        sb.append("\n願い事 : ");
        sb.append(getNegaigoto());
        sb.append("\n商い : ");
        sb.append((getAkinai()));
        sb.append("\n学問 : ");
        sb.append(getGakumon());
        return sb.toString();
    }
}
