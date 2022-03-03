package com.company;

import java.io.IOException;
import java.util.ResourceBundle;

public interface Fortune {
    ResourceBundle rb = ResourceBundle.getBundle("fortune");
    String DISP_STR = rb.getString("disp_str");
    String disp() throws IOException;
}



