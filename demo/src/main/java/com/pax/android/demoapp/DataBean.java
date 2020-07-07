package com.pax.android.demoapp;

public class DataBean {
    private String Merchant;
    private String Reseller;
    private String Acquirer_Type;
    private String Amount;
    private String Purchase_Time;
    private String Op_Number;
    private String Purchase_ID;

    public DataBean(String merchant, String reseller) {
        Merchant = merchant;
        Reseller = reseller;
    }

    public DataBean(String merchant, String reseller, String acquirer_Type, String amount, String purchase_Time, String op_Number, String purchase_ID) {
        Merchant = merchant;
        Reseller = reseller;
        Acquirer_Type = acquirer_Type;
        Amount = amount;
        Purchase_Time = purchase_Time;
        Op_Number = op_Number;
        Purchase_ID = purchase_ID;
    }

    public String getMerchant() {
        return Merchant;
    }

    public void setMerchant(String merchant) {
        Merchant = merchant;
    }

    public String getReseller() {
        return Reseller;
    }

    public void setReseller(String reseller) {
        Reseller = reseller;
    }

    public String getAcquirer_Type() {
        return Acquirer_Type;
    }

    public void setAcquirer_Type(String acquirer_Type) {
        Acquirer_Type = acquirer_Type;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getPurchase_Time() {
        return Purchase_Time;
    }

    public void setPurchase_Time(String purchase_Time) {
        Purchase_Time = purchase_Time;
    }

    public String getOp_Number() {
        return Op_Number;
    }

    public void setOp_Number(String op_Number) {
        Op_Number = op_Number;
    }

    public String getPurchase_ID() {
        return Purchase_ID;
    }

    public void setPurchase_ID(String purchase_ID) {
        Purchase_ID = purchase_ID;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Merchant);
        stringBuffer.append("|");
        stringBuffer.append(Reseller);
        stringBuffer.append("|");
        stringBuffer.append(Acquirer_Type);
        stringBuffer.append("|");
        stringBuffer.append(Amount);
        stringBuffer.append("|");
        stringBuffer.append(Purchase_Time);
        stringBuffer.append("|");
        stringBuffer.append(Op_Number);
        stringBuffer.append("|");
        stringBuffer.append(Purchase_ID);

        return stringBuffer.toString();
    }
}
