package com.ximai.savingsmore.save.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
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
import com.ximai.savingsmore.library.view.HorizontalListView;
import com.ximai.savingsmore.library.view.MyGridView;
import com.ximai.savingsmore.save.common.BaseActivity;
import com.ximai.savingsmore.save.modle.Images;
import com.ximai.savingsmore.save.modle.UpPhoto;
import com.ximai.savingsmore.save.utils.ImageTools;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojian on 16/12/1.
 */
public class IssueCommentActivity extends BaseActivity implements View.OnClickListener {
    private ImageView cha, yiban, mangyi, henmangyi, qianglie;
    private ImageView kouwei1, kouwei2, kouwei3, kouwei4, kouwei5;
    private ImageView fuwu1, fuwu2, fuwu3, fuwu4, fuwu5;
    private ImageView huanjing1, huanjing2, huanjing3, huanjing4, huanjing5;
    private PopupWindow setIconWindow;
    private static int PICK_FROM_IMAGE = 1011;
    private static int PICK_FROM_CAMERA = 1022;
    private static int CROP_PHOTO_CODE = 7;
    //private List<String> imagePath = new ArrayList<String>();
    private List<Images> images = new ArrayList<Images>();
    private LinearLayout comment_image;
    private MyAdapter myAdapter;
    private HorizontalListView horizontalListView;
    private String goodId;
    private String productScore;
    private String sellerScore1, sellerScore2, sellerScore3;
    private String Content;
    private boolean IsAnonymous;
    private EditText comment_conent;
    private TextView wenzi_number;
    private ImageView select;

//    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
//            300, 300);
//    LinearLayout.LayoutParams deleteparams = new LinearLayout.LayoutParams(
//    120,120);
//
//    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_comment_activity);
        setCenterTitle("评论");
        setLeftBackMenuVisibility(IssueCommentActivity.this, "");
        goodId = getIntent().getStringExtra("id");
        addRightTextMenu("发布", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != comment_conent) {
                    Content = comment_conent.getText().toString();
                }
                if (null != Content && Content.length() > 15) {
                    if (null != productScore && !TextUtils.isEmpty(productScore) && null != Content && !TextUtils.isEmpty(Content)) {
                        comitComment(goodId, productScore, sellerScore1, sellerScore2, sellerScore3, Content, images, IsAnonymous);
                    } else {
                        Toast.makeText(IssueCommentActivity.this, "信息没有填写完整", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(IssueCommentActivity.this, "评论内容不能少于15个字", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //comment_image = (LinearLayout) findViewById(R.id.comment_image);
//        gridView= (MyGridView) findViewById(R.id.gridview);
//        myadapter=new Myadapter();
//        gridView.setAdapter(myadapter);
        select = (ImageView) findViewById(R.id.select);
        select.setOnClickListener(this);
        comment_conent = (EditText) findViewById(R.id.comment_conent);
        wenzi_number = (TextView) findViewById(R.id.wenzi_number);
        comment_conent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (comment_conent.length() <= 15) {
                    wenzi_number.setText("还需要输入" + (15 - comment_conent.length()) + "个字,即可发表");
                } else {
                    wenzi_number.setText("还需要输入0个字,即可发表");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cha = (ImageView) findViewById(R.id.cha);
        yiban = (ImageView) findViewById(R.id.yiban);
        mangyi = (ImageView) findViewById(R.id.mangyi);
        henmangyi = (ImageView) findViewById(R.id.henmangyi);
        qianglie = (ImageView) findViewById(R.id.qinglie);
        horizontalListView = (HorizontalListView) findViewById(R.id.myGridview);
        cha.setOnClickListener(this);
        yiban.setOnClickListener(this);
        henmangyi.setOnClickListener(this);
        mangyi.setOnClickListener(this);
        qianglie.setOnClickListener(this);
        kouwei1 = (ImageView) findViewById(R.id.kouwei1);
        kouwei2 = (ImageView) findViewById(R.id.kouwei2);
        kouwei3 = (ImageView) findViewById(R.id.kouwei3);
        kouwei4 = (ImageView) findViewById(R.id.kouwei4);
        kouwei5 = (ImageView) findViewById(R.id.kouwei5);
        fuwu1 = (ImageView) findViewById(R.id.fuwu1);
        fuwu2 = (ImageView) findViewById(R.id.fuwu2);
        fuwu3 = (ImageView) findViewById(R.id.fuwu3);
        fuwu4 = (ImageView) findViewById(R.id.fuwu4);
        fuwu5 = (ImageView) findViewById(R.id.fuwu5);
        huanjing1 = (ImageView) findViewById(R.id.huanjing1);
        huanjing2 = (ImageView) findViewById(R.id.huanjing2);
        huanjing3 = (ImageView) findViewById(R.id.huanjing3);
        huanjing4 = (ImageView) findViewById(R.id.huanjing4);
        huanjing5 = (ImageView) findViewById(R.id.huanjing5);
        kouwei1.setOnClickListener(this);
        kouwei2.setOnClickListener(this);
        kouwei3.setOnClickListener(this);
        kouwei4.setOnClickListener(this);
        kouwei5.setOnClickListener(this);
        fuwu1.setOnClickListener(this);
        fuwu2.setOnClickListener(this);
        fuwu3.setOnClickListener(this);
        fuwu4.setOnClickListener(this);
        fuwu5.setOnClickListener(this);
        huanjing1.setOnClickListener(this);
        huanjing2.setOnClickListener(this);
        huanjing3.setOnClickListener(this);
        huanjing4.setOnClickListener(this);
        huanjing5.setOnClickListener(this);
        myAdapter = new MyAdapter();
        horizontalListView.setAdapter(myAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select:
                if (IsAnonymous) {
                    select.setImageResource(R.mipmap.kuang);
                    IsAnonymous = false;
                } else {
                    select.setImageResource(R.mipmap.select_kuang);
                    IsAnonymous = true;
                }
                break;
            case R.id.cha:
                productScore = "1";
                cha.setBackgroundResource(R.mipmap.cha3);
                yiban.setBackgroundResource(R.mipmap.yiban13);
                mangyi.setBackgroundResource(R.mipmap.manyi13);
                henmangyi.setBackgroundResource(R.mipmap.henmanyi13);
                qianglie.setBackgroundResource(R.mipmap.qianglietuijian13);
                break;
            case R.id.yiban:
                productScore = "2";
                cha.setBackgroundResource(R.mipmap.cha13);
                yiban.setBackgroundResource(R.mipmap.yiban3);
                mangyi.setBackgroundResource(R.mipmap.manyi13);
                henmangyi.setBackgroundResource(R.mipmap.henmanyi13);
                qianglie.setBackgroundResource(R.mipmap.qianglietuijian13);
                break;
            case R.id.mangyi:
                productScore = "3";
                cha.setBackgroundResource(R.mipmap.cha13);
                yiban.setBackgroundResource(R.mipmap.yiban13);
                mangyi.setBackgroundResource(R.mipmap.manyi3);
                henmangyi.setBackgroundResource(R.mipmap.henmanyi13);
                qianglie.setBackgroundResource(R.mipmap.qianglietuijian13);
                break;
            case R.id.henmangyi:
                productScore = "4";
                cha.setBackgroundResource(R.mipmap.cha13);
                yiban.setBackgroundResource(R.mipmap.yiban13);
                mangyi.setBackgroundResource(R.mipmap.manyi13);
                henmangyi.setBackgroundResource(R.mipmap.henmanyi3);
                qianglie.setBackgroundResource(R.mipmap.qianglietuijian13);
                break;
            case R.id.qinglie:
                productScore = "5";
                cha.setBackgroundResource(R.mipmap.cha13);
                yiban.setBackgroundResource(R.mipmap.yiban13);
                mangyi.setBackgroundResource(R.mipmap.manyi13);
                henmangyi.setBackgroundResource(R.mipmap.henmanyi13);
                qianglie.setBackgroundResource(R.mipmap.qianglietuijian3);
                break;
            case R.id.kouwei1:
                sellerScore1 = "1";
                kouwei1.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei2.setBackgroundResource(R.mipmap.xiaolian13);
                kouwei3.setBackgroundResource(R.mipmap.xiaolian13);
                kouwei4.setBackgroundResource(R.mipmap.xiaolian13);
                kouwei5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.kouwei2:
                sellerScore1 = "2";
                kouwei1.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei2.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei3.setBackgroundResource(R.mipmap.xiaolian13);
                kouwei4.setBackgroundResource(R.mipmap.xiaolian13);
                kouwei5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.kouwei3:
                sellerScore1 = "3";
                kouwei1.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei2.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei3.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei4.setBackgroundResource(R.mipmap.xiaolian13);
                kouwei5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.kouwei4:
                sellerScore1 = "4";
                kouwei1.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei2.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei3.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei4.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.kouwei5:
                sellerScore1 = "5";
                kouwei1.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei2.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei3.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei4.setBackgroundResource(R.mipmap.xiaolian3);
                kouwei5.setBackgroundResource(R.mipmap.xiaolian3);
                break;
            case R.id.fuwu1:
                sellerScore2 = "1";
                fuwu1.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu2.setBackgroundResource(R.mipmap.xiaolian13);
                fuwu3.setBackgroundResource(R.mipmap.xiaolian13);
                fuwu4.setBackgroundResource(R.mipmap.xiaolian13);
                fuwu5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.fuwu2:
                sellerScore2 = "2";
                fuwu1.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu2.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu3.setBackgroundResource(R.mipmap.xiaolian13);
                fuwu4.setBackgroundResource(R.mipmap.xiaolian13);
                fuwu5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.fuwu3:
                sellerScore2 = "3";
                fuwu1.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu2.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu3.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu4.setBackgroundResource(R.mipmap.xiaolian13);
                fuwu5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.fuwu4:
                sellerScore2 = "4";
                fuwu1.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu2.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu3.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu4.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.fuwu5:
                sellerScore2 = "5";
                fuwu1.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu2.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu3.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu4.setBackgroundResource(R.mipmap.xiaolian3);
                fuwu5.setBackgroundResource(R.mipmap.xiaolian3);
                break;
            case R.id.huanjing1:
                sellerScore3 = "1";
                huanjing1.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing2.setBackgroundResource(R.mipmap.xiaolian13);
                huanjing3.setBackgroundResource(R.mipmap.xiaolian13);
                huanjing4.setBackgroundResource(R.mipmap.xiaolian13);
                huanjing5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.huanjing2:
                sellerScore3 = "2";
                huanjing1.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing2.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing3.setBackgroundResource(R.mipmap.xiaolian13);
                huanjing4.setBackgroundResource(R.mipmap.xiaolian13);
                huanjing5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.huanjing3:
                sellerScore3 = "3";
                huanjing1.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing2.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing3.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing4.setBackgroundResource(R.mipmap.xiaolian13);
                huanjing5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.huanjing4:
                sellerScore3 = "4";
                huanjing1.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing2.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing3.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing4.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing5.setBackgroundResource(R.mipmap.xiaolian13);
                break;
            case R.id.huanjing5:
                sellerScore3 = "5";
                huanjing1.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing2.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing3.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing4.setBackgroundResource(R.mipmap.xiaolian3);
                huanjing5.setBackgroundResource(R.mipmap.xiaolian3);
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

    private void showSetIconWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_set_icon_popwindow, null);
        View parentView = LayoutInflater.from(this).inflate(R.layout.issue_comment_activity, null);


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
                upLoadImage(new File((new URI(photoUri.toString()))), "Comment");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
//            imagePath.add(photoUri.getPath());
//            addImage(imagePath);
        }
    }


//    private void addImage(List<String> list) {
//        comment_image.removeAllViews();
//        if (list.size() == 0) {
//            LinearLayout layout = new LinearLayout(IssueCommentActivity.this);
//            layout.setOrientation(LinearLayout.HORIZONTAL);
//            layout.setLayoutParams(params);
//            params1.setMargins(10, 0, 10, 0);
//            ImageView add_images = new ImageView(IssueCommentActivity.this);
//            add_images.setLayoutParams(params1);
//            add_images.setScaleType(ImageView.ScaleType.FIT_XY);
//            add_images.setBackgroundResource(R.mipmap.add_image);
//            add_images.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showSetIconWindow();
//                }
//            });
//            layout.addView(add_images);
//            comment_image.addView(layout);
//        } else {
//            for (int i = 0; i < (list.size() / 3) + 1; i++) {
//                LinearLayout layout = new LinearLayout(IssueCommentActivity.this);
//                layout.setOrientation(LinearLayout.HORIZONTAL);
//                layout.setLayoutParams(params);
//                for (int j = 0; j < list.size() - (i * 3) + 1; j++) {
//                    if ((i * 3) + j == list.size()) {
//                        params1.setMargins(20, 10, 20, 10);
//                        ImageView add_images = new ImageView(IssueCommentActivity.this);
//                        add_images.setLayoutParams(params1);
//                        add_images.setScaleType(ImageView.ScaleType.FIT_XY);
//                        add_images.setBackgroundResource(R.mipmap.add_image);
//                        add_images.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                showSetIconWindow();
//                            }
//                        });
//                        if (layout.getChildCount() < 3) {
//                            layout.addView(add_images);
//                        }
//                    } else {
//                        RelativeLayout relativeLayout = new RelativeLayout(IssueCommentActivity.this);
//                        relativeLayout.setLayoutParams(params1);
//                        final ImageView delete = new ImageView(IssueCommentActivity.this);
//                        delete.setLayoutParams(deleteparams);
//                        delete.setScaleType(ImageView.ScaleType.FIT_XY);
//                        final int finalI = i;
//                        delete.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                comment_image.removeAllViews();
//                                imagePath.remove(finalI);
//                                addImage(imagePath);
//                            }
//                        });
//                        delete.setBackgroundResource(R.mipmap.delete3);
//                        ImageView imageView = new ImageView(IssueCommentActivity.this);
//                        relativeLayout.setLayoutParams(params1);
//                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                        MyImageLoader.displayDefaultImage("file://" + list.get(i * 3 + j), imageView);
//                        relativeLayout.addView(imageView);
//                        relativeLayout.addView(delete);
//                        if (layout.getChildCount() < 3) {
//                            layout.addView(relativeLayout);
//                        }
//                    }
//                }
//                comment_image.addView(layout);
//            }
//        }
//    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return images.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position < images.size()) {
                return images.get(position);
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
            convertView = LayoutInflater.from(IssueCommentActivity.this).inflate(R.layout.horizontal_listview_item, null);
            myViewHodel.image = (ImageView) convertView.findViewById(R.id.iamge);
            myViewHodel.delete = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(myViewHodel);
            if (position < images.size()) {
                myViewHodel.delete.setVisibility(View.VISIBLE);
                MyImageLoader.displayDefaultImage(URLText.img_url + images.get(position).ImagePath, myViewHodel.image);
            }
//            else if (position <  images.size()) {
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
                    if (position == images.size()) {
                        showSetIconWindow();
                    }
                }
            });
            myViewHodel.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < images.size()) {
                        images.remove(position);
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

    //提交评论
    private void comitComment(String Id, String ProductScore, String SellerScore, String SellerScore2, String SellerScore3,
                              String Content, final List<Images> images, boolean isAnonymous) {
        WebRequestHelper.json_post(IssueCommentActivity.this, URLText.SUBMIT_COMMENT, RequestParamsPool.submitComment(Id, ProductScore, SellerScore, SellerScore2, SellerScore3, Content, images, isAnonymous), new MyAsyncHttpResponseHandler(IssueCommentActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject object = null;
                try {
                    object = new JSONObject(new String(responseBody));
                    String IsSuccess = object.getString("IsSuccess");
                    String message = object.optString("Message");
                    if (IsSuccess.equals("true")) {
                        Intent intent = new Intent(IssueCommentActivity.this, CommentSuccessActivity.class);
                        if (images.size() > 0) {
                            intent.putExtra("jifen", "2");
                        }
                        startActivity(intent);
                        finish();
                    }
                    Toast.makeText(IssueCommentActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void upLoadImage(File file, final String type) {
        // a++;
        WebRequestHelper.post(URLText.UPLOAD_IMAGE, RequestParamsPool.upLoad(file, type), new MyAsyncHttpResponseHandler(IssueCommentActivity.this) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    // b++;
                    JSONObject jsonObject = new JSONObject(result);
                    UpPhoto upPhoto = GsonUtils.fromJson(jsonObject.optString("MainData"), UpPhoto.class);
                    Images images1 = new Images();
                    images1.ImageId = upPhoto.Id;
                    images1.ImagePath = upPhoto.FilePath;
                    images1.SortNo = upPhoto.SortNo;
                    images.add(images1);
                    myAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
