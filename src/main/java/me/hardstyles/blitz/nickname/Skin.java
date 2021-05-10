package me.hardstyles.blitz.nickname;

public class Skin {


    private String skinValue;
    private String skinSignature;

    public Skin(String skinValue, String skinSignature) {
        this.skinSignature = skinSignature;
        this.skinValue = skinValue;
    }
    public String getSkinValue() {
        return skinValue;
    }

    public void setSkinValue(String skinValue) {
        this.skinValue = skinValue;
    }

    public String getSkinSignature() {
        return skinSignature;
    }

    public void setSkinSignature(String skinSignature) {
        this.skinSignature = skinSignature;
    }
}
