package com.iconloop.score.token.alice;
import score.Address;
import score.Context;
import score.annotation.External;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonArray;

import scorex.util.ArrayList;
import com.iconloop.score.token.util.Bytes;

import java.math.BigInteger;

import static java.lang.Boolean.parseBoolean;

public class Alice extends IRC31MintBurn {

    private final String name;
    private final String symbol;

    private final Address signerAddress = Address.fromString("hx1f199d7b495ebb2ce3cf704a5dc3c2ac584b6cd3");

    public Alice(String _name, String _symbol){
        this.name = _name;
        this.symbol = _symbol;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @External(readonly=true)
    public String name() {
        return this.name;
    }

    @External(readonly=true)
    public String symbol() {
        return this.symbol;
    }

    @External(readonly=true)
    public Address getSignerAddress(){
        return signerAddress;
    }

    @External(readonly = true)
    public String testBoolean(boolean hellotest){
        if(hellotest){
            return "True";
        }
        return "False";
    }

    @External(readonly = true)
    public String testBoolean2(String hellotest){
        if(parseBoolean(hellotest)){
            return "True";
        }
        return "False";
    }

    //https://icondev.io/getting-started/how-to-use-the-json-rpc-api#how-to-create-transaction-signature
    @External(readonly = true)
    public byte[] convertToBytes(String strJson, boolean withRecovery){
        JsonObject jsonObject= Json.parse(strJson).asObject();
        JsonArray data = jsonObject.get("data").asArray();

        int byteSize = (withRecovery) ? data.size()+1 : data.size();
        byte[] byteArray = new byte[byteSize];
        for(int i=0; i < data.size(); i++) {
            byteArray[i] = (byte) data.get(i).asInt();
        }

        if(withRecovery){
            byteArray[byteSize-1] = (byte) 1;
        }

        return byteArray;
    }

    @External(readonly = true)
    public int getLength(String strJson, boolean withRecovery){
        JsonObject jsonObject= Json.parse(strJson).asObject();
        JsonArray data = jsonObject.get("data").asArray();
        int byteSize = (withRecovery) ? data.size()+1 : data.size();
        byte[] byteArray = new byte[byteSize];
        for(int i=0; i < data.size(); i++) {
            byteArray[i] = (byte) data.get(i).asInt();
        }

        if(withRecovery){
            byteArray[byteSize-1] = (byte) 1;
        }
        return byteArray.length;
    }

    @External(readonly=true)
    public byte getSpecificElement(String strJson, boolean withRecovery, int position) {
        JsonObject jsonObject= Json.parse(strJson).asObject();
        JsonArray data = jsonObject.get("data").asArray();
        int byteSize = (withRecovery) ? data.size()+1 : data.size();
        byte[] byteArray = new byte[byteSize];
        for(int i=0; i < data.size(); i++) {
            byteArray[i] = (byte) data.get(i).asInt();
        }

        if(withRecovery){
            byteArray[byteSize-1] = (byte) 1;
        }
        return byteArray[position];
    }

    @External(readonly = true)
    public byte[] tryRecoverKey(byte[] _msg, byte[] _sig){
        return Context.recoverKey("ecdsa-secp256k1", _msg, _sig, false);
    }

    @External(readonly = true)
    public Address tryPublicAddress(byte[] _msg, byte[] _sig){
        byte[] publicKey = Context.recoverKey("ecdsa-secp256k1", _msg, _sig, false);
        return Context.getAddressFromKey(publicKey);
    }

    @External(readonly = true)
    public byte tryRecoverKeyElement(byte[] _msg, byte[] _sig, int position){
        byte[] recoveredKey = Context.recoverKey("ecdsa-secp256k1", _msg, _sig, false);
        return recoveredKey[position];
    }

    @External(readonly=true)
    public byte test(String _msg, String _member){
        JsonObject jsonObject= Json.parse(_msg).asObject();
        JsonArray data = jsonObject.get(_member).asArray();
        byte[] byteArray = new byte[data.size()];
        for(int i=0; i < data.size(); i++) {
            byteArray[i] = (byte) data.get(i).asInt();
        }
        return byteArray[0];
    }

    @External(readonly=true)
    public String test2(String _msg, String _member){
        JsonObject jsonObject= Json.parse(_msg).asObject();
        JsonArray data = jsonObject.get(_member).asArray();
        byte[] byteArray = new byte[data.size()];
        for(int i=0; i < data.size(); i++) {
            byteArray[i] = (byte) data.get(i).asInt();
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<byteArray.length; i++){
            int d = byteArray[i];
            sb.append(Integer.toString(d));
        }
        return sb.toString();
    }


    @External(readonly=true)
    public boolean IsArrayCheck(String _msg, String _member){
        JsonObject jsonObject= Json.parse(_msg).asObject();
        JsonArray testt = jsonObject.get(_member).asArray();
        if(jsonObject.get(_member).isArray()){
            return true;
        }
        else return false;
    }
}