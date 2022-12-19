package com.qualifeed.retrofit;








import com.qualifeed.adapter.TrainTypeAdapter;
import com.qualifeed.model.DefactModel;
import com.qualifeed.model.DefectModelMain;
import com.qualifeed.model.DefectModelMain22;
import com.qualifeed.model.DefectModelMainCopy;
import com.qualifeed.model.GetDefactModel;
import com.qualifeed.model.LoginModel;
import com.qualifeed.model.ProductTypeModel;
import com.qualifeed.model.ProductTypeTrainModel;
import com.qualifeed.model.QrCodeModel;
import com.qualifeed.model.TaskModel;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface QualifeedInterface {

    @FormUrlEncoded
    @POST("login")
    Call<LoginModel> userLogin (@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("forgot_password")
    Call<Map<String, String>> forgotPassword(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("code_match")
    Call<QrCodeModel> getCodeInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("add_product_type")
    Call<Map<String,String>> putCode(@FieldMap Map<String, String> params);





    /*@Multipart
    @POST("update_profile")
    Call<SignupModel> profileUpdate(
            @Part("user_name") RequestBody username,
            @Part("email") RequestBody email,
            @Part("mobile") RequestBody mobile,
            @Part("phone_code") RequestBody country_code,
            @Part("user_id") RequestBody user_id,
            @Part MultipartBody.Part file);





    @GET("suggestStore")
    Call<SuggestedModel> getSuggestStore(@Header("Authorization") String auth);*/



    @GET("get_defective_item")
    Call<DefactModel> getDefact();



    @Multipart
    @POST("add_defact_request")
    Call<Map<String,String>> addDefact(
            @Part("user_id") RequestBody user_id,
            @Part("defact_id") RequestBody defact_id,
            @Part("product_id") RequestBody product_id,
            @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("get_defact_request")
    Call<GetDefactModel> getDefactListttt(@FieldMap Map<String, String> params);




    @Multipart
    @POST("add_defact_problem")
    Call<Map<String,String>> scrapAdd(
            @Part("user_id") RequestBody user_id,
            @Part("product_id") RequestBody product_id,
            @Part("comment") RequestBody comment,
            @Part MultipartBody.Part file);



    @Multipart
    @POST("add_defact_share_image")
    Call<Map<String,String>> uplloadImageDefact(
            @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("get_product_type")
    Call<ProductTypeModel> getProductType(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_product_defects_list")
    Call<DefectModelMain> getDefectListt1(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_product_defects_list")
    Call<DefectModelMainCopy> getDefectListt(@FieldMap Map<String, String> params);

    @Multipart
    @POST("product_defects_image")
    Call<Map<String,String>> addDefectMain(
            @Part("worker_id") RequestBody user_id,
            @Part("product_defects_id") RequestBody product_defects_id,
          /*  @Part("product_id") RequestBody product_id,*/
            @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("delete_product_defects_request")
    Call<Map<String,String>> decrementDefect(@FieldMap Map<String, String> params);


    @Multipart
    @POST("add_scrap")
    Call<Map<String,String>> scrapAddMain(
            @Part("productref") RequestBody productref,
            @Part("product_type_1") RequestBody product_type_1,
            @Part("product_type_2") RequestBody product_type_2,
            @Part("date") RequestBody date,
            @Part("worker_id") RequestBody worker_id,
            @Part("comment") RequestBody comment,
            @Part MultipartBody.Part file);


    @Multipart
    @POST("add_suspect_defect")
    Call<Map<String,String>> addSuspectDefect(
            @Part("title") RequestBody title,
            @Part("productref") RequestBody productref,
            @Part("product_type_1") RequestBody product_type_1,
            @Part("product_type_2") RequestBody product_type_2,
            @Part("date") RequestBody date,
            @Part("worker_id") RequestBody worker_id,
            @Part("comment") RequestBody comment,
            @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("add_scan_timer")
    Call<Map<String,String>> addRepair(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("get_task_by_worker")
    Call<TaskModel> getTaskList(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("add_product_task")
    Call<Map<String,String>> addTask(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("update_task_status")
    Call<Map<String,String>> doneTask(@FieldMap Map<String, String> params);




    @Multipart
    @POST("add_product_repair")
    Call<Map<String,String>> repairProduct(
            @Part("productref") RequestBody productref,
            @Part("product_type_1") RequestBody product_type_1,
            @Part("product_type_2") RequestBody product_type_2,
            @Part("date") RequestBody date,
            @Part("worker_id") RequestBody worker_id,
            @Part("timer") RequestBody comment,
            @Part MultipartBody.Part file);



    @FormUrlEncoded
    @POST("add_scan_timer")
    Call<Map<String,String>> startTimerrrr(@FieldMap Map<String, String> params);



    @GET("get_all_product_type")
    Call<ProductTypeTrainModel> getAllTraining();


    @FormUrlEncoded
    @POST("get_product_defects_list_new")
    Call<DefectModelMain> getDefectListtCopy(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_product_defects_list_new_new")
    Call<DefectModelMain22> getDefectListtCopy22(@FieldMap Map<String, String> params);


    @GET("get_product_type_team")
    Call<ProductTypeModel> getProductType();

}
