package com.qualifeed.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductTypeModel {

    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("product_id")
        @Expose
        public String productId;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("date_time")
        @Expose
        public String dateTime;

        @SerializedName("product_type_1")
        @Expose
        public String productType1;

        @SerializedName("product_type_2")
        @Expose
        public String productType2;

    }

}

