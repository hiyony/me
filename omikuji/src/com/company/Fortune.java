package com.company;

import java.io.IOException;
import java.util.ResourceBundle;

public interface Fortune {
    //프로퍼티 파일로부터
    ResourceBundle rb = ResourceBundle.getBundle("fortune");
    String DISP_STR = rb.getString("disp_str");
    String disp() throws IOException;
}
