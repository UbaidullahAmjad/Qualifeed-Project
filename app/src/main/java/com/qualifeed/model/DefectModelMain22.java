package com.qualifeed.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefectModelMain22 {

    @SerializedName("result")
    @Expose
    private List<Result> result = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public class Result {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("product_defects_id")
        @Expose
        private String productDefectsId;
        @SerializedName("worker_id")
        @Expose
        private String workerId;
        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("productref")
        @Expose
        private String productref;
        @SerializedName("product_type_1")
        @Expose
        private String productType1;
        @SerializedName("product_type_2")
        @Expose
        private String productType2;
        @SerializedName("comment")
        @Expose
        private String comment;
        @SerializedName("date_time")
        @Expose
        private String dateTime;
        @SerializedName("product_defects_image")
        @Expose
        private String productDefectsImage;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProductDefectsId() {
            return productDefectsId;
        }

        public void setProductDefectsId(String productDefectsId) {
            this.productDefectsId = productDefectsId;
        }

        public String getWorkerId() {
            return workerId;
        }

        public void setWorkerId(String workerId) {
            this.workerId = workerId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getProductref() {
            return productref;
        }

        public void setProductref(String productref) {
            this.productref = productref;
        }

        public String getProductType1() {
            return productType1;
        }

        public void setProductType1(String productType1) {
            this.productType1 = productType1;
        }

        public String getProductType2() {
            return productType2;
        }

        public void setProductType2(String productType2) {
            this.productType2 = productType2;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getProductDefectsImage() {
            return productDefectsImage;
        }

        public void setProductDefectsImage(String productDefectsImage) {
            this.productDefectsImage = productDefectsImage;
        }

    }

}

