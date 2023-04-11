package pro.simpleapp.radiobox.globals;

import pro.simpleapp.radiobox.items.Radio;



public class RadioGlobal {

    private Radio radio;
    private static RadioGlobal instance;


    private RadioGlobal() {
        radio = new Radio();
    }


    public static RadioGlobal getInstance() {
        if (instance == null) {
            instance = new RadioGlobal();
        }
        return instance;
    }


    public Radio getRadio() {
        return radio;
    }


    public void setRadio(Radio radio) {
        this.radio = radio;
    }


}
