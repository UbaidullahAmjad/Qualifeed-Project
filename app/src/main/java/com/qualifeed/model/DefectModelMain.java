package com.qualifeed.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefectModelMain {

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
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("defect_count")
        @Expose
        public String defectCount;
        @SerializedName("product_defects_image")
        @Expose
        public String productDefectsImage;
        @SerializedName("product_defects_request")
        @Expose
        public List<ProductDefectsRequest> productDefectsRequest = null;

        public class ProductDefectsRequest {

            @SerializedName("id")
            @Expose
            public String id;
            @SerializedName("product_defects_id")
            @Expose
            public String productDefectsId;
            @SerializedName("product_id")
            @Expose
            public String productId;
            @SerializedName("image")
            @Expose
            public String image;
            @SerializedName("date_time")
            @Expose
            public String dateTime;



        }

    }
}




