package com.qualifeed.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QrCodeModel implements Serializable {

    @SerializedName("result")
    @Expose
    public Result result;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public class Result implements Serializable {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("code")
        @Expose
        public String code;
        @SerializedName("qr_image")
        @Expose
        public String qrImage;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("sub_admin_id")
        @Expose
        public String subAdminId;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("part_number")
        @Expose
        public String partNumber;
        @SerializedName("part_placement")
        @Expose
        public String partPlacement;
        @SerializedName("car_type")
        @Expose
        public String carType;



    }

}


