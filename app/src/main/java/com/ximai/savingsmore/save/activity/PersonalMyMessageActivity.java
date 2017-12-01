package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.FileSystem.FileSystem;
import com.ximai.savingsmore.library.cache.MyImageLoader;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.PopupWindowFromBottomUtil;
import com.ximai.savingsmore.library.toolbox.PreferencesUtils;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.BaseMessage;
import com.ximai.savingsmore.save.modle.Images;
import com.ximai.savingsmore.save.modle.ListBaseMessage;
import com.ximai.savingsmore.save.modle.MyUserExtInfo;
import com.ximai.savingsmore.save.modle.MyUserInfo;
import com.ximai.savingsmore.save.modle.MyUserInfoUtils;
import com.ximai.savingsmore.save.modle.UpPhoto;
import com.ximai.savingsmore.save.modle.UserParameter;
import com.ximai.savingsmore.save.utils.ImageTools;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/11/30.
 */
public class PersonalMyMessageActivity extends BaseActivity implements View.OnClickListener {
    private ImageView head_image;
    private EditText nickname, xiangxi_address, phone, zhiye, youxiang;
    private TextView sex, diqu;
    private static int PICK_FROM_IMAGE = 1011;
    private static int PICK_FROM_CAMERA = 1022;
    private static int CROP_PHOTO_CODE = 7;
    private PopupWindow setIconWindow;
    private Images images1;
    private List<String> sex_list = new ArrayList<>();
    private UserParameter userParameter = new UserParameter();
    private List<BaseMessage> list = new ArrayList<BaseMessage>();
    private TextView save;
    private MyUserExtInfo myUserExtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_mymessage_activity);
        setCenterTitle("个人信息");
        setLeftBackMenuVisibility(PersonalMyMessageActivity.this, "");
        head_image = (ImageView) findViewById(R.id.head_image);
        nickname = (EditText) findViewById(R.id.nickname);
        xiangxi_address = (EditText) findViewById(R.id.address);
        phone = (EditText) findViewById(R.id.phone);
        sex = (TextView) findViewById(R.id.sex);
        diqu = (TextView) findViewById(R.id.location);
        zhiye = (EditText) findViewById(R.id.job);
        youxiang = (EditText) findViewById(R.id.email);
        if (null != MyUserInfoUtils.getInstance().myUserInfo.Email && !TextUtils.isEmpty(MyUserInfoUtils.getInstance().myUserInfo.Email)) {
            //youxiang.setText(MyUserInfoUtils.getInstance().myUserInfo.Email);
            youxiang.setText("去邮箱查收邮件,并给予确认");
        }
        String email = MyUserInfoUtils.getInstance().myUserInfo.Email;
        MyImageLoader.displayDefaultImage(URLText.img_url + MyUserInfoUtils.getInstance().myUserInfo.PhotoPath, head_image);
        userParameter.PhotoPath = MyUserInfoUtils.getInstance().myUserInfo.PhotoPath;
        if (null != MyUserInfoUtils.getInstance().myUserInfo.UserDisplayName && !TextUtils.isEmpty(MyUserInfoUtils.getInstance().myUserInfo.UserDisplayName)) {
            nickname.setText(MyUserInfoUtils.getInstance().myUserInfo.UserDisplayName);
        }
        if (null != MyUserInfoUtils.getInstance().myUserInfo.Domicile && !TextUtils.isEmpty(MyUserInfoUtils.getInstance().myUserInfo.Domicile)) {
            xiangxi_address.setText(MyUserInfoUtils.getInstance().myUserInfo.Domicile);
        }
        if (null != MyUserInfoUtils.getInstance().myUserInfo.PhoneNumber && !TextUtils.isEmpty(MyUserInfoUtils.getInstance().myUserInfo.PhoneNumber)) {
            phone.setText(MyUserInfoUtils.getInstance().myUserInfo.PhoneNumber);
        }
        if (MyUserInfoUtils.getInstance().myUserInfo.Sex.equals("false")) {
            sex.setText("男");
        } else {
            sex.setText("女");
        }
        userParameter.Sex = MyUserInfoUtils.getInstance().myUserInfo.Sex;
        if (null != MyUserInfoUtils.getInstance().myUserInfo.Area) {
            diqu.setText(MyUserInfoUtils.getInstance().myUserInfo.Province.Name + " " + MyUserInfoUtils.getInstance().myUserInfo.City.Name + " " + MyUserInfoUtils.getInstance().myUserInfo.Area.Name);
        } else if (null != MyUserInfoUtils.getInstance().myUserInfo.Province && null != MyUserInfoUtils.getInstance().myUserInfo.City) {
            diqu.setText(MyUserInfoUtils.getInstance().myUserInfo.Province.Name + " " + MyUserInfoUtils.getInstance().myUserInfo.City.Name);
        }
        userParameter.ProvinceId = MyUserInfoUtils.getInstance().myUserInfo.ProvinceId;
        userParameter.CityId = MyUserInfoUtils.getInstance().myUserInfo.CityId;
        userParameter.AreaId = MyUserInfoUtils.getInstance().myUserInfo.AreaId;
        if (null != MyUserInfoUtils.getInstance().myUserInfo.Post && !TextUtils.isEmpty(MyUserInfoUtils.getInstance().myUserInfo.Post)) {
            zhiye.setText(MyUserInfoUtils.getInstance().myUserInfo.Post);
        }
        save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(this);
        head_image.setOnClickListener(this);
        diqu.setOnClickListener(this);
        sex.setOnClickListener(this);
        sex_list.add("男");
        sex_list.add("女");
        myUserExtInfo = new MyUserExtInfo();
        queryDicNode();
    }

    private void showSetIconWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.leave_message_dialog, null);
        View parentView = LayoutInflater.from(this).inflate(R.layout.personal_mymessage_activity, null);


        setIconWindow = PopupWindowFromBottomUtil.showWindow(contentView, parentView, this);

        Button btnCancel = (Button) contentView.findViewById(R.id.btnCancel);
        Button btnCamera = (Button) contentView.findViewById(R.id.btnCamera);
        Button btnAlbum = (Button) contentView.findViewById(R.id.btnAlbum);

        btnCancel.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
    }

    //打开相册
    private void openAlbum() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openAlbumIntent, PICK_FROM_IMAGE);
    }

    //打开照相机
    private void openCamera() {
        Uri imageUri = null;
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ImageTools.deletePhotoAtPathAndName(FileSystem.getCachesDir(this, true).getAbsolutePath(), PreferencesUtils.getString(this, "tempName"));
        String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        PreferencesUtils.putString(this, "tempName", fileName);
        imageUri = Uri.fromFile(new File(FileSystem.getCachesDir(this, true).getAbsolutePath(), fileName));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, PICK_FROM_CAMERA);
    }

    //图片裁剪
    public void cropImage(Uri uri, int requestCode) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("isCrop", false);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        } else if (requestCode == PICK_FROM_CAMERA || requestCode == PICK_FROM_IMAGE) {

            Uri uri = null;
            if (null != intent && intent.getData() != null) {
                uri = intent.getData();
            } else {
                String fileName = PreferencesUtils.getString(this, "tempName");
                uri = Uri.fromFile(new File(FileSystem.getCachesDir(this, true).getAbsolutePath(), fileName));
            }

            if (uri != null) {
                cropImage(uri, CROP_PHOTO_CODE);
            }
        } else if (requestCode == CROP_PHOTO_CODE) {
            Uri photoUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            try {
                upLoadImage(new File((new URI(photoUri.toString()))), "Photo");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
//            imagePath.add(photoUri.getPath());
//            addImage(imagePath);
        }
    }

    private void upLoadImage(File file, final String type) {
        // a++;
        WebRequestHelper.post(URLText.UPLOAD_IMAGE, RequestParamsPool.upLoad(file, type), new MyAsyncHttpResponseHandler(PersonalMyMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    // b++;
                    JSONObject jsonObject = new JSONObject(result);
                    UpPhoto upPhoto = GsonUtils.fromJson(jsonObject.optString("MainData"), UpPhoto.class);
                    images1 = new Images();
                    images1.ImageId = upPhoto.Id;
                    images1.ImagePath = upPhoto.FilePath;
                    images1.SortNo = upPhoto.SortNo;
                    MyImageLoader.displayDefaultImage(URLText.img_url + upPhoto.FilePath, head_image);
                    userParameter.PhotoId = upPhoto.Id;
                    userParameter.PhotoPath = upPhoto.FilePath;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location:
                PopupWindowFromBottomUtil.showAddress(PersonalMyMessageActivity.this, LayoutInflater.from(PersonalMyMessageActivity.this).inflate(R.layout.business_my_center_activity, null), list, new PopupWindowFromBottomUtil.Listenre1() {
                    @Override
                    public void callBack(String Id1, String Id2, String Id3, String content, PopupWindow popupWindow) {
                        if (null != Id1) {
                            userParameter.ProvinceId = Id1;
                        }
                        if (null != Id2) {
                            userParameter.CityId = Id2;
                        }
                        if (null != Id3) {
                            userParameter.AreaId = Id3;
                        }
                        diqu.setText(content);
                        popupWindow.dismiss();
                    }

                });
                break;
            case R.id.sex:
                PopupWindowFromBottomUtil.shouWindowWithWheel(PersonalMyMessageActivity.this, LayoutInflater.from(PersonalMyMessageActivity.this).inflate(R.layout.business_my_center_activity, null), sex_list, new PopupWindowFromBottomUtil.Listener() {
                    @Override
                    public void confirm(String content, PopupWindow window) {
                        sex.setText(content);
                        if (content.equals("男")) {
                            userParameter.Sex = "false";
                        } else {
                            userParameter.Sex = "true";
                        }
                        window.dismiss();
                    }
                });
                break;
            case R.id.save:
                userParameter.UserDisplayName = nickname.getText().toString();
                userParameter.NickName = nickname.getText().toString();
                userParameter.Domicile = xiangxi_address.getText().toString();
                userParameter.Post = zhiye.getText().toString();
                myUserExtInfo.OfficePhone = phone.getText().toString();
                userParameter.UserExtInfo = myUserExtInfo;
                if (null == MyUserInfoUtils.getInstance().myUserInfo.Email && null != youxiang.getText() && !TextUtils.isEmpty(youxiang.getText())) {
                    bindEmail(youxiang.getText().toString(), PreferencesUtils.getString(BaseApplication.getInstance(), "pwd", null));
                }
                save_message(userParameter);
//                if (null != userParameter.PhotoPath && null != nickname.getText() && TextUtils.isEmpty(nickname.getText()) && null != userParameter.PhotoPath && null != userParameter.ProvinceId && null != userParameter.CityId) {
//                    save_message(userParameter);
//                } else {
//                    Toast.makeText(PersonalMyMessageActivity.this, "信息没有填写完整", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.head_image:
                showSetIconWindow();
                break;
            case R.id.btnCancel:
                setIconWindow.dismiss();
                break;
            case R.id.btnCamera:
                openCamera();
                setIconWindow.dismiss();
                break;
            case R.id.btnAlbum:
                openAlbum();
                setIconWindow.dismiss();
                break;
        }
    }

    private void save_message(UserParameter userParameter) {
        WebRequestHelper.json_post(PersonalMyMessageActivity.this, URLText.SAVE_MESSAGE, RequestParamsPool.savePersonMessage(userParameter), new MyAsyncHttpResponseHandler(PersonalMyMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    String IsSuccess = object.getString("IsSuccess");
                    String message = object.optString("Message");
                    if (IsSuccess.equals("true")) {
                        getUsereInfo();

                    }
                    Toast.makeText(PersonalMyMessageActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    //得到用户的信息
    private void getUsereInfo() {
        WebRequestHelper.json_post(PersonalMyMessageActivity.this, URLText.GET_USERINFO, RequestParamsPool.getUserInfo(), new MyAsyncHttpResponseHandler(PersonalMyMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String MianData = object.optString("MainData");
                MyUserInfoUtils.getInstance().myUserInfo = GsonUtils.fromJson(MianData, MyUserInfo.class);
                finish();
            }
        });

    }

    //查询基础字典
    private void queryDicNode() {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(PersonalMyMessageActivity.this, URLText.QUERYDICNODE, stringEntity, new MyAsyncHttpResponseHandler(PersonalMyMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                ListBaseMessage listBaseMessage = GsonUtils.fromJson(new String(responseBody), ListBaseMessage.class);
                list = listBaseMessage.MainData;
            }
        });
    }

    //绑定邮箱
    private void bindEmail(String email, String password) {
        WebRequestHelper.json_post(PersonalMyMessageActivity.this, URLText.bindemail, RequestParamsPool.bindEmail(email, password), new MyAsyncHttpResponseHandler(PersonalMyMessageActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (object.optString("IsSuccess").equals("true")) {
                    youxiang.setText("去邮箱查收邮件,并给予确认");
                    //Toast.makeText(PersonalMyMessageActivity.this, "去邮箱查收邮件,并给予确认", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(PersonalMyMessageActivity.this, object.optString("Message"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
            }
        });
    }
}
