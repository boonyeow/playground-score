package com.iconloop.score.token.alice;

import score.ByteArrayObjectWriter;
import score.Context;
import score.ObjectReader;
import score.ObjectWriter;

import java.math.BigInteger;
import java.util.Map;

public class ProjectInfo {
    public String name;
    public String thumbnailSrc;
    public String description;
    public String details;
    public BigInteger fundingGoal;
    public BigInteger pricePerNFT;
    public BigInteger startTimestamp;
    public BigInteger endTimestamp;

    public ProjectInfo(String name){
        this.name = name;
        this.thumbnailSrc = "";
        this.description = "";
        this.details = "";
        this.fundingGoal = BigInteger.ZERO;
        this.pricePerNFT = BigInteger.ZERO;
        this.startTimestamp = BigInteger.ZERO;
        this.endTimestamp = BigInteger.ZERO;
    }

    public ProjectInfo(){
        this("");
    }

    public void setName(String name){
        this.name = name;
    }

    public void setThumbnailSrc(String thumbnailSrc){
        this.thumbnailSrc = thumbnailSrc;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setDetails(String details){
        this.details = details;
    }

    public void setFundingGoal(BigInteger fundingGoal){
        this.fundingGoal = fundingGoal;
    }

    public void setPricePerNFT(BigInteger pricePerNFT){
        this.pricePerNFT = pricePerNFT;
    }

    public void setStartTimestamp(BigInteger startTimestamp){
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(BigInteger endTimestamp){
        this.endTimestamp = endTimestamp;
    }

    public static void writeObject(ObjectWriter writer, ProjectInfo obj){
        obj.writeObject(writer);
    }
    public static ProjectInfo readObject(ObjectReader reader){
        ProjectInfo obj = new ProjectInfo();
        reader.beginList();
        obj.name = reader.readString();
        obj.thumbnailSrc = reader.readString();
        obj.description = reader.readString();
        obj.details = reader.readString();
        obj.fundingGoal = reader.readBigInteger();
        obj.pricePerNFT = reader.readBigInteger();
        obj.startTimestamp = reader.readBigInteger();
        obj.endTimestamp = reader.readBigInteger();
        reader.end();
        return obj;
    }
    public void writeObject(ObjectWriter writer){
        writer.beginList(8);
        writer.write(this.name);
        writer.write(this.thumbnailSrc);
        writer.write(this.description);
        writer.write(this.details);
        writer.write(this.fundingGoal);
        writer.write(this.pricePerNFT);
        writer.write(this.startTimestamp);
        writer.write(this.endTimestamp);
        writer.end();
    }

    public static ProjectInfo fromBytes(byte[] bytes) {
        ObjectReader reader = Context.newByteArrayObjectReader("RLPn", bytes);
        return ProjectInfo.readObject(reader);
    }

    public byte[] toBytes() {
        ByteArrayObjectWriter writer = Context.newByteArrayObjectWriter("RLPn");
        ProjectInfo.writeObject(writer, this);
        return writer.toByteArray();
    }

    public Map<String, Object> toMap(){
        return Map.of(
                "name",name,
                "thumbnailSrc", thumbnailSrc,
                "description", description,
                "details", details,
                "fundingGoal", fundingGoal,
                "pricePerNFT", pricePerNFT,
                "startTimestamp", startTimestamp,
                "endTimestamp", endTimestamp
        );
    }
}
