package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.ximai.savingsmore.library.toolbox.UsePicker;
import com.ximai.savingsmore.library.view.Form_item;
import com.ximai.savingsmore.library.view.HorizontalListView;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.BaseMessage;
import com.ximai.savingsmore.save.modle.BusinessScopes;
import com.ximai.savingsmore.save.modle.Images;
import com.ximai.savingsmore.save.modle.ListBaseMessage;
import com.ximai.savingsmore.save.modle.MyDictNode;
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
 * Created by caojian on 16/12/10.
 */
//注册第四步
public class FourStepRegisterActivity extends BaseActivity implements View.OnClickListener {
    private Form_item create_date, good_type, good_range, address;
    private TextView crete_date;
    List<String> list_type = new ArrayList<String>();
    private static int PICK_FROM_IMAGE = 1011;
    private static int PICK_FROM_CAMERA = 1022;
    private static int CROP_PHOTO_CODE = 7;
    private PopupWindow setIconWindow;
    private boolean isslinece, isZhengshu, isItem, isheadImage;
    private ImageView slience_image, zhengshu_iamge;
    private RelativeLayout lisence_item, zhegnshu_item;
    private HorizontalListView horizontalListView;
    private List<String> imagePath = new ArrayList<String>();
    private MyAdapter myAdapter;
    private EditText xixiang_adress, store_name, phone_number, website, weChat, zhizhao_number, zhegnshu_number, lianxiren, position, xiliren_number;
    private List<BaseMessage> list = new ArrayList<BaseMessage>();
    private List<BaseMessage> base = new ArrayList<BaseMessage>();
    private List<BaseMessage> good_one_classify = new ArrayList<BaseMessage>();
    private List<BaseMessage> good_two_classify = new ArrayList<BaseMessage>();
    List<String> range1 = new ArrayList<String>();
    List<String> range2 = new ArrayList<String>();
    private String dizhi = "";
    private List<Images> images = new ArrayList<Images>();
    private UserParameter userParameter;
    private MyUserExtInfo myUserExtInfo;
    private MyDictNode myDictNode;
    private BusinessScopes myBusinessScopes;
    private String xukezheng_path, zhizhao_path, tuoxiang_path;
    private List<String> shangpu_path = new ArrayList<String>();
    private TextView save_message;
    private List<BusinessScopes> list1;
    private List<Images> list_images;
    private TextView xukezheng_text;
    int a = 0, b = 0;
    private boolean isXuke = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.four_step_resgirst);
        // setLeftBackMenuVisibility(FourStepRegisterActivity.this, "");
        setCenterTitle("注册");
        create_date = (Form_item) findViewById(R.id.create_time);
        create_date.setOnClickListener(this);
        good_type = (Form_item) findViewById(R.id.good_type);
        good_range = (Form_item) findViewById(R.id.range);
        address = (Form_item) findViewById(R.id.adress);
        address.setOnClickListener(this);

        good_type.setOnClickListener(this);
        good_range.setOnClickListener(this);
        list_type.add("提袋商品");
        list_type.add("非提袋商品");
        slience_image = (ImageView) findViewById(R.id.liscense_image);
        zhengshu_iamge = (ImageView) findViewById(R.id.zhegnshu_image);
        lisence_item = (RelativeLayout) findViewById(R.id.liscense_item);
        zhegnshu_item = (RelativeLayout) findViewById(R.id.zhengshu_item);
        lisence_item.setOnClickListener(this);
        zhegnshu_item.setOnClickListener(this);
        horizontalListView = (HorizontalListView) findViewById(R.id.myGridview);
        myAdapter = new MyAdapter();
        horizontalListView.setAdapter(myAdapter);
        store_name = (EditText) findViewById(R.id.store_name);
        phone_number = (EditText) findViewById(R.id.phone_number);
        website = (EditText) findViewById(R.id.website);
        weChat = (EditText) findViewById(R.id.wechat);
        zhizhao_number = (EditText) findViewById(R.id.license_number);
        zhegnshu_number = (EditText) findViewById(R.id.zhengshu_number);
        lianxiren = (EditText) findViewById(R.id.linkman_name);
        position = (EditText) findViewById(R.id.linkman_position);
        xiliren_number = (EditText) findViewById(R.id.linkman_number);
        xixiang_adress = (EditText) findViewById(R.id.xiangxi_adress);
        save_message = (TextView) findViewById(R.id.submit);
        xukezheng_text = (TextView) findViewById(R.id.xukezheng_text);
        save_message.setOnClickListener(this);
        list_images = new ArrayList<Images>();


        if (null != MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo && null != MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.Images) {
            //images = MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.Images;
        }
        tuoxiang_path = MyUserInfoUtils.getInstance().myUserInfo.PhotoPath;
        store_name.setText(MyUserInfoUtils.getInstance().myUserInfo.ShowName);
        if (null != MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo) {
            website.setText(MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.WebSite);
            zhizhao_path = MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.BusinessLicensePath;
            MyImageLoader.displayDefaultImage(URLText.img_url + MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.BusinessLicensePath, slience_image);
            MyImageLoader.displayDefaultImage(URLText.img_url + MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.LicenseKeyPath, zhengshu_iamge);
            xukezheng_path = MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.LicenseKeyPath;
            if (MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.IsBag) {
                good_type.setmTvRight("提袋商品");
            } else {
                good_type.setmTvRight("非提袋商品");
            }
            phone_number.setText(MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.OfficePhone);
            zhizhao_number.setText(MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.BusinessLicenseNumber);
            zhegnshu_number.setText(MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.LicenseKeyNumber);
        }
        if (null != MyUserInfoUtils.getInstance().myUserInfo.Province) {
            dizhi = dizhi + MyUserInfoUtils.getInstance().myUserInfo.Province.Name + " ";
        }
        if (null != MyUserInfoUtils.getInstance().myUserInfo.City) {
            dizhi = dizhi + MyUserInfoUtils.getInstance().myUserInfo.City.Name + " ";
        }
        if (null != MyUserInfoUtils.getInstance().myUserInfo.Area) {
            dizhi = dizhi + MyUserInfoUtils.getInstance().myUserInfo.Area.Name;
        }
        address.setmTvRight(dizhi);
        xixiang_adress.setText(MyUserInfoUtils.getInstance().myUserInfo.Domicile);
        weChat.setText(MyUserInfoUtils.getInstance().myUserInfo.WeChat);
        create_date.setmTvRight(MyUserInfoUtils.getInstance().myUserInfo.CreateDateName);
        lianxiren.setText(MyUserInfoUtils.getInstance().myUserInfo.UserName);
        position.setText(MyUserInfoUtils.getInstance().myUserInfo.Post);
        xiliren_number.setText(MyUserInfoUtils.getInstance().myUserInfo.PhoneNumber);
        if (null != MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes && MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes.size() > 0) {
            good_range.setmTvRight(MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes.get(0).DictNode.Name);
        }
        userParameter = new UserParameter();
        myUserExtInfo = new MyUserExtInfo();
        myDictNode = new MyDictNode();
        myBusinessScopes = new BusinessScopes();
        list1 = new ArrayList<BusinessScopes>();
        if (null != MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo) {
            myUserExtInfo.BusinessLicensePath = MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.BusinessLicensePath;
            myUserExtInfo.BusinessLicenseId = MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.BusinessLicenseId;
            myUserExtInfo.LicenseKeyPath = MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.LicenseKeyPath;
            myUserExtInfo.LicenseKeyId = MyUserInfoUtils.getInstance().myUserInfo.UserExtInfo.LicenseKeyId;
        }
        if (MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes.size() > 0) {
            myBusinessScopes.DictNodeId = MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes.get(0).DictNodeId;
        }
        userParameter.ProvinceId = MyUserInfoUtils.getInstance().myUserInfo.ProvinceId;
        userParameter.CityId = MyUserInfoUtils.getInstance().myUserInfo.CityId;
        userParameter.AreaId = MyUserInfoUtils.getInstance().myUserInfo.AreaId;
        // userParameter.BusinessScopes = MyUserInfoUtils.getInstance().myUserInfo.BusinessScopes;
        queryDicNode();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (store_name.getText() == null || TextUtils.isEmpty(store_name.getText())) {
                    Toast.makeText(FourStepRegisterActivity.this, "请填写店铺名称", Toast.LENGTH_SHORT).show();
                } else if (phone_number.getText() == null || TextUtils.isEmpty(phone_number.getText())) {
                    Toast.makeText(FourStepRegisterActivity.this, "请填写固定电话", Toast.LENGTH_SHORT).show();
                } else if (address.getmTvRightText() == null || TextUtils.isEmpty(address.getmTvRightText())) {
                    Toast.makeText(FourStepRegisterActivity.this, "请填写店铺地址", Toast.LENGTH_SHORT).show();
                } else if (xixiang_adress.getText() == null || TextUtils.isEmpty(xixiang_adress.getText())) {
                    Toast.makeText(FourStepRegisterActivity.this, "请填写店铺详细", Toast.LENGTH_SHORT).show();
                } else if (good_type.getmTvRightText() == null || TextUtils.isEmpty(good_type.getmTvRightText()) || good_type.getmTvRightText().equals("请选择")) {
                    Toast.makeText(FourStepRegisterActivity.this, "请填写商品类别", Toast.LENGTH_SHORT).show();
                } else if (good_range.getmTvRightText() == null || TextUtils.isEmpty(good_range.getmTvRightText()) || good_range.getmTvRightText().equals("请选择")) {
                    Toast.makeText(FourStepRegisterActivity.this, "请填写主营范围", Toast.LENGTH_SHORT).show();
                } else if (zhizhao_path == null || TextUtils.isEmpty(zhizhao_path)) {
                    Toast.makeText(FourStepRegisterActivity.this, "请填写营业执照", Toast.LENGTH_SHORT).show();
                } else if (isXuke && (xukezheng_path == null || TextUtils.isEmpty(xukezheng_path))) {
                    Toast.makeText(FourStepRegisterActivity.this, "请填写许可证", Toast.LENGTH_SHORT).show();
                } else {
                    myUserExtInfo.StoreName = store_name.getText().toString();
                    myUserExtInfo.OfficePhone = phone_number.getText().toString();
                    myUserExtInfo.WebSite = website.getText().toString();
                    userParameter.WeChat = weChat.getText().toString();
                    userParameter.Domicile = xixiang_adress.getText().toString();
                    myUserExtInfo.FoundingDate = create_date.getmTvRightText();
                    myUserExtInfo.BusinessLicenseNumber = zhizhao_number.getText().toString();
                    myUserExtInfo.LicenseKeyNumber = zhegnshu_number.getText().toString();
                    userParameter.UserDisplayName = lianxiren.getText().toString();
                    userParameter.Post = position.getText().toString();
                    //list_images.addAll(images);
                    myUserExtInfo.Images = images;
                    if (good_type.getmTvRightText().equals("提袋商品")) {
                        myUserExtInfo.IsBag = "true";
                    } else {
                        myUserExtInfo.IsBag = "false";
                    }
                    list1.add(myBusinessScopes);
                    userParameter.BusinessScopes = list1;
                    userParameter.UserExtInfo = myUserExtInfo;
                    if (a == b) {
                        if (null != weChat.getText().toString() && !TextUtils.isEmpty(weChat.getText().toString())) {
                            bindEmail(weChat.getText().toString(), PreferencesUtils.getString(BaseApplication.getInstance(), "pwd", null));
                        }
                        save_message(userParameter);
                    }
                }
                break;
            case R.id.create_time:
                UsePicker.showYearMonthDay(FourStepRegisterActivity.this, new UsePicker.CallBack() {
                    @Override
                    public void callBack(String time) {
                        create_date.getmTvRight_().setText(time);

                    }
                }, create_date.getmTvRightText());
                break;
            case R.id.head_image:
                isslinece = false;
                isItem = false;
                isheadImage = true;
                isZhengshu = false;
                showSetIconWindow();
                break;

            case R.id.good_type:
                PopupWindowFromBottomUtil.shouWindowWithWheel(FourStepRegisterActivity.this, LayoutInflater.from(FourStepRegisterActivity.this).inflate(R.layout.business_my_center_activity, null), list_type, new PopupWindowFromBottomUtil.Listener() {
                    @Override
                    public void confirm(String content, PopupWindow window) {
                        good_type.getmTvRight_().setText(content);
                        window.dismiss();
                    }
                });
                break;
            case R.id.range:
                if (good_type.getmTvRightText().equals("提袋商品")) {
                    PopupWindowFromBottomUtil.shouRange(FourStepRegisterActivity.this, LayoutInflater.from(FourStepRegisterActivity.this).inflate(R.layout.business_my_center_activity, null), good_one_classify, new PopupWindowFromBottomUtil.Listener2() {
                        @Override
                        public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
                            good_range.getmTvRight_().setText(content);
//                            myDictNode.Name = Id1.Name;
//                            myDictNode.SortNo = Id1.SortNo;
//                            myDictNode.Id=Id1.Id;
                            myBusinessScopes.DictNodeId = Id1.Id;
                            popupWindow.dismiss();
                        }
                    });
                } else {
                    PopupWindowFromBottomUtil.shouRange(FourStepRegisterActivity.this, LayoutInflater.from(FourStepRegisterActivity.this).inflate(R.layout.business_my_center_activity, null), good_two_classify, new PopupWindowFromBottomUtil.Listener2() {
                        @Override
                        public void callBack(BaseMessage Id1, String content, PopupWindow popupWindow) {
                            good_range.getmTvRight_().setText(content);
//                            myDictNode.Name = Id1.Name;
//                            myDictNode.SortNo = Id1.SortNo;
//                            myDictNode.Id=Id1.Id;
                            if (Id1.Name.equals("美食")) {
                                xukezheng_text.setText("许可证图片(必填)");
                                isXuke = true;
                            } else {
                                isXuke = false;
                                xukezheng_text.setText("许可证图片");
                            }
                            myBusinessScopes.DictNodeId = Id1.Id;
                            popupWindow.dismiss();
                        }
                    });
                }
                break;
            case R.id.adress:
                PopupWindowFromBottomUtil.showAddress(FourStepRegisterActivity.this, LayoutInflater.from(FourStepRegisterActivity.this).inflate(R.layout.business_my_center_activity, null), list, new PopupWindowFromBottomUtil.Listenre1() {
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
                        address.getmTvRight_().setText(content);
                        popupWindow.dismiss();
                    }

                });
                break;
            case R.id.liscense_item:
                isslinece = true;
                isItem = false;
                isheadImage = false;
                isZhengshu = false;
                showSetIconWindow();
                break;
            case R.id.zhengshu_item:
                isItem = false;
                isheadImage = false;
                isslinece = false;
                isZhengshu = true;
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

    private void showSetIconWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_set_icon_popwindow, null);
        View parentView = LayoutInflater.from(this).inflate(R.layout.business_my_center_activity, null);


        setIconWindow = PopupWindowFromBottomUtil.showWindow(contentView, parentView, this);

        Button btnCancel = (Button) contentView.findViewById(R.id.btnCancel);
        Button btnCamera = (Button) contentView.findViewById(R.id.btnCamera);
        Button btnAlbum = (Button) contentView.findViewById(R.id.btnAlbum);

        btnCancel.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
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
            if (isslinece) {
                MyImageLoader.displayDefaultImage("file://" + photoUri.getPath(), slience_image);
                zhizhao_path = photoUri.toString();
                try {
                    upLoadImage(new File((new URI(photoUri.toString()))), "BusinessLicense");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (isZhengshu) {
                MyImageLoader.displayDefaultImage("file://" + photoUri.getPath(), zhengshu_iamge);
                xukezheng_path = photoUri.toString();
                try {
                    upLoadImage(new File((new URI(photoUri.toString()))), "LicenseKey");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                //upLoadImage(new File((new URI(photoUri.toString()))), "Photo");
            } else if (isItem) {
                if (images.size() < 10) {
                    shangpu_path.add(photoUri.toString());
                    try {
                        upLoadImage(new File((new URI(photoUri.toString()))), "Seller");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FourStepRegisterActivity.this, "最多只能上传9张图片", Toast.LENGTH_SHORT).show();
                }
            }
            //addImage(imagePath);
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return images.size() + imagePath.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position < images.size()) {
                return images.get(position);
            }
            if (position < (imagePath.size() + images.size())) {
                return imagePath.get(position + images.size());
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            MyViewHodel myViewHodel;
            myViewHodel = new MyViewHodel();
            convertView = LayoutInflater.from(FourStepRegisterActivity.this).inflate(R.layout.horizontal_listview_item, null);
            myViewHodel.image = (ImageView) convertView.findViewById(R.id.iamge);
            myViewHodel.delete = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(myViewHodel);
            if (position < images.size()) {
                myViewHodel.delete.setVisibility(View.VISIBLE);
                MyImageLoader.displayDefaultImage(URLText.img_url + images.get(position).ImagePath, myViewHodel.image);
            }
//            else if (position < (imagePath.size() + images.size())) {
//                myViewHodel.delete.setVisibility(View.VISIBLE);
//                MyImageLoader.displayDefaultImage("file://" + imagePath.get(position - images.size()), myViewHodel.image);
//            }
            else {
                // myViewHodel.image.setImageDrawable(R.drawable.add_image);
                myViewHodel.image.setBackgroundResource(R.mipmap.add_image);
                myViewHodel.delete.setVisibility(View.GONE);
            }
            myViewHodel.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == (imagePath.size() + images.size())) {
                        showSetIconWindow();
                        isslinece = false;
                        isZhengshu = false;
                        isheadImage = false;
                        isItem = true;
                    }
                }
            });
            myViewHodel.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < images.size()) {
                        images.remove(position);
                    } else {
                        imagePath.remove(position - images.size());
                    }
                    myAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    private class MyViewHodel {
        private ImageView image;
        private ImageView delete;

    }

    //查询基础字典
    private void queryDicNode() {
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebRequestHelper.json_post(FourStepRegisterActivity.this, URLText.QUERYDICNODE, stringEntity, new MyAsyncHttpResponseHandler(FourStepRegisterActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                ListBaseMessage listBaseMessage = GsonUtils.fromJson(new String(responseBody), ListBaseMessage.class);
                list = listBaseMessage.MainData;
                for (int i = 0; i < list.size(); i++) {
                    if (null == list.get(i).ParentId) {
                        base.add(list.get(i));
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).IsBag.equals("true") && null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("ba335639-52c2-4e8d-8d2b-faf8ed097418")) {
                        good_one_classify.add(list.get(i));
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).IsBag.equals("false") && null != list.get(i).Name && null != list.get(i).ParentId && null != list.get(i).Id && list.get(i).ParentId.equals("ba335639-52c2-4e8d-8d2b-faf8ed097418")) {
                        good_two_classify.add(list.get(i));
                    }
                }
            }
        });
    }

    private void upLoadImage(File file, final String type) {
        a++;
        WebRequestHelper.post(URLText.UPLOAD_IMAGE, RequestParamsPool.upLoad(file, type), new MyAsyncHttpResponseHandler(FourStepRegisterActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    b++;
                    JSONObject jsonObject = new JSONObject(result);
                    UpPhoto upPhoto = GsonUtils.fromJson(jsonObject.optString("MainData"), UpPhoto.class);
                    if (type.equals("Photo")) {
                        userParameter.PhotoId = upPhoto.Id;
                        userParameter.PhotoPath = upPhoto.FilePath;
                    } else if (type.equals("BusinessLicense")) {
                        myUserExtInfo.BusinessLicenseId = upPhoto.Id;
                        myUserExtInfo.BusinessLicensePath = upPhoto.FilePath;
                    } else if (type.equals("LicenseKey")) {
                        myUserExtInfo.LicenseKeyId = upPhoto.Id;
                        myUserExtInfo.LicenseKeyPath = upPhoto.FilePath;
                    } else {
                        Images images1 = new Images();
                        images1.ImageId = upPhoto.Id;
                        images1.ImagePath = upPhoto.FilePath;
                        images.add(images1);
                        myAdapter.notifyDataSetChanged();
                        //list_images.add(images);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void save_message(UserParameter userParameter) {
        WebRequestHelper.json_post(FourStepRegisterActivity.this, URLText.SAVE_MESSAGE, RequestParamsPool.saveMessage(userParameter), new MyAsyncHttpResponseHandler(FourStepRegisterActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    String IsSuccess = object.getString("IsSuccess");
                    String message = object.optString("Message");
                    if (IsSuccess.equals("true")) {
                        getUsereInfo();
                        Intent intent = new Intent(FourStepRegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    Toast.makeText(FourStepRegisterActivity.this, "成功入驻", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    //得到用户的信息
    private void getUsereInfo() {
        WebRequestHelper.json_post(FourStepRegisterActivity.this, URLText.GET_USERINFO, RequestParamsPool.getUserInfo(), new MyAsyncHttpResponseHandler(FourStepRegisterActivity.this) {
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

    //绑定邮箱
    private void bindEmail(String email, String password) {
        WebRequestHelper.json_post(FourStepRegisterActivity.this, URLText.bindemail, RequestParamsPool.bindEmail(email, password), new MyAsyncHttpResponseHandler(FourStepRegisterActivity.this) {
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
                    save_message(userParameter);
                }
                // Toast.makeText(PersonalMyMessageActivity.this, object.optString("Message"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
            }
        });
    }

}
