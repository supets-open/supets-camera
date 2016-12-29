package com.supets.pet.multiimageselector.model;


import java.io.Serializable;

/**
 * 图片实体
 * Created by Nereo on 2015/4/7.
 */
public class Image implements Serializable {

    public String id;
    public String path;
    public String name;
    public String mThumb;
    public long time;

    public Image(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }


    public Image(){
    }

    public Image(String path, String name, long time,String mThumb){
        this.path = path;
        this.name = name;
        this.time = time;
        this.mThumb=mThumb;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return this.id.equalsIgnoreCase(other.id);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
