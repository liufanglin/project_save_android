package com.ximai.savingsmore.save.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.ximai.savingsmore.R;
import com.ximai.savingsmore.library.net.MyAsyncHttpResponseHandler;
import com.ximai.savingsmore.library.net.RequestParamsPool;
import com.ximai.savingsmore.library.net.URLText;
import com.ximai.savingsmore.library.net.WebRequestHelper;
import com.ximai.savingsmore.library.toolbox.GsonUtils;
import com.ximai.savingsmore.library.toolbox.LogUtils;
import com.ximai.savingsmore.library.toolbox.PreferencesUtils;
import com.ximai.savingsmore.save.activity.LoginActivity;
import com.ximai.savingsmore.save.activity.SearchActivity;
import com.ximai.savingsmore.save.activity.SearchResultActivity;
import com.ximai.savingsmore.save.common.BaseApplication;
import com.ximai.savingsmore.save.modle.Goods;
import com.ximai.savingsmore.save.modle.GoodsList;
import com.ximai.savingsmore.save.utils.PopuWindowsUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by caojian on 16/11/21.
 */
//地图fragment
public class MapFragment extends Fragment implements View.OnClickListener, LocationSource, AMapLocationListener, com.amap.api.maps2d.AMap.OnMarkerClickListener, com.amap.api.maps2d.AMap.OnMapClickListener, AMap.OnCameraChangeListener {
    private ImageView login;
    private MapView mapView;
    private com.amap.api.maps2d.AMap aMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private Boolean isFirstLoc = true;
    private LocationSource.OnLocationChangedListener mListener;
    private com.amap.api.maps2d.model.MarkerOptions markerOption;
    private View markerimgs;
    private com.amap.api.maps.model.Marker locationMarker;
    private Button marker_button;
    private BitmapDescriptor makerIcon;
    private ImageView toList, search;
    private PopuWindowsUtils popuWindowsUtils;
    private RelativeLayout show_popu;
    private Context context;
    private List<Goods> list = new ArrayList<Goods>();
    private List<Goods> list_gray = new ArrayList<Goods>();
    private List<Goods> list_red = new ArrayList<Goods>();
    private GoodsList goodsList = new GoodsList();
    private CallBack callBack;
    private String city;
    private double Longitude;
    private double Latitude;
    private String qu;
    private String sheng;
    private String shi;
    private List<Marker> mark_list = new ArrayList<>();
    private int marker_position;
    private TextView cuxiao_style;
    private String type = "name";
    private String pageSize;
    private boolean isUpdate;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, null);
        // login = (ImageView) view.findViewById(R.id.login);
        //login.setOnClickListener(this);
        mapView = (MapView) view.findViewById(R.id.map);
        toList = (ImageView) view.findViewById(R.id.list);
        //search= (ImageView) view.findViewById(R.id.search);
        show_popu = (RelativeLayout) view.findViewById(R.id.rent_map_pop);
        mapView.onCreate(savedInstanceState);
        cuxiao_style = (TextView) view.findViewById(R.id.cuxiao_style);
        cuxiao_style.setOnClickListener(this);
        //search.setOnClickListener(this);
        toList.setOnClickListener(this);


        if (aMap == null) {
            aMap = mapView.getMap();
            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();

            settings.setZoomControlsEnabled(false);
            aMap.setLocationSource(this);//设置了定位的监听,这里要实现LocationSource接口
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase

            aMap.setOnCameraChangeListener(this);
            //初始化定位
            mLocationClient = new AMapLocationClient(context);
            //设置定位回调监听，这里要实现AMapLocationListener接口，AMapLocationListener接口只有onLocationChanged方法可以实现，用于接收异步返回的定位结果，参数是AMapLocation类型。
            mLocationClient.setLocationListener(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(2000);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        }
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);
        //pageSize = PreferencesUtils.getString(getActivity(), "number");
        getAllGoods(pageSize);

        return view;
    }

    //    public MapFragment(Context context, CallBack callBack) {
//        this.context = context;
//        this.callBack = callBack;
//    }
    public void setDate(Context context, CallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    public MapFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        //pageSize = PreferencesUtils.getString(getActivity(), "number");
        // getAllGoods(pageSize);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.login) {
//            Intent intent = new Intent(getActivity(), LoginActivity.class);
//            startActivity(intent);
//        }
        if (R.id.list == v.getId()) {
            if (null != city && Longitude != 0 && Latitude != 0) {
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra("long", Longitude + "");
                intent.putExtra("lat", Latitude + "");
                intent.putExtra("qu", qu);
                intent.putExtra("shi", shi);
                intent.putExtra("sheng", sheng);
                intent.putExtra("title", city);
                startActivity(intent);
            }
        }
        if (R.id.cuxiao_style == v.getId()) {
            mark_list.clear();
            if (cuxiao_style.getText().toString().equals("商品名称")) {
                cuxiao_style.setText("促销形式");
                type = "name";
                aMap.clear();
                int a = list_red.size();
                for (int i = 0; i < a; i++) {
                    markeradd(list_red.get(i), false);
                }
                int b = list_gray.size();
                for (int i = 0; i < b; i++) {
                    markeradd(list_gray.get(i), true);
                }
            } else {
                cuxiao_style.setText("商品名称");
                type = "style";
                aMap.clear();
                int a = list_red.size();
                for (int i = 0; i < a; i++) {
                    markeradd(list_red.get(i), false);
                }
                int b = list_gray.size();
                for (int i = 0; i < b; i++) {
                    markeradd(list_gray.get(i), true);
                }
            }
        }
//        if(R.id.search==v.getId()){
//            Intent intent = new Intent(getActivity(), SearchActivity.class);
//            startActivity(intent);
//        }

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                BaseApplication.getInstance().Longitude = aMapLocation.getLongitude();
                BaseApplication.getInstance().Latitude = aMapLocation.getLatitude();
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息

                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                //callBack.location(aMapLocation.getCity(), aMapLocation.getDistrict());
                //city = aMapLocation.getCity() + "-" + aMapLocation.getDistrict();

                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                //Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (null != popuWindowsUtils) {
            popuWindowsUtils.dismixss();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final int c = list.size();
        for (int i = 0; i < c; i++) {
            if (!list_gray.contains(list.get(i))) {
                isUpdate = true;
                break;
            }
        }
        if (isUpdate) {
            for (int i = 0; i < mark_list.size(); i++) {
                if (marker.equals(mark_list.get(i))) {
                    marker_position = i;
                    list_gray.add(list.get(i));
                    break;
                }
            }
            aMap.clear();
            mark_list.clear();
            for (int i = 0; i < c; i++) {
                if (list_gray.contains(list.get(i))) {
                    markeradd(list_red.get(i), true);
                } else {
                    markeradd(list_red.get(i), false);
                }
            }
        }
        if (null == popuWindowsUtils) {
            popuWindowsUtils = new PopuWindowsUtils(getActivity(), list, new PopuWindowsUtils.callBack() {
                @Override
                public void call(int posiiton) {
                    if (posiiton != marker_position) {
                        aMap.clear();
                        mark_list.clear();
                        list_gray.add(list.get(posiiton));
                        for (int i = 0; i < c; i++) {
                            if (list_gray.contains(list.get(i))) {
                                markeradd(list_red.get(i), true);
                            } else {
                                markeradd(list_red.get(i), false);
                            }
                        }
                    }
                }
            });
        }

        if (!popuWindowsUtils.isShowing())

        {
            popuWindowsUtils.showAsDropDown(show_popu, marker_position);
        }

        return true;
    }

    /**
     * 获取数据将Marker 添加到相应的经纬度
     */
    private void markeradd(Goods goods, boolean isClick) {
        com.amap.api.maps2d.model.LatLng latlng = new com.amap.api.maps2d.model.LatLng(Double.parseDouble(goods.Latitude), Double.parseDouble(goods.Longitude));
        markerOption = new MarkerOptions();
        markerimgs = FrameLayout.inflate(
                context,
                R.layout.markerimgs, null);

        marker_button = (Button) markerimgs
                .findViewById(R.id.marker_content);
        if (type.equals("name")) {
            marker_button.setText(goods.Name);

        } else {
            marker_button.setText(goods.Preferential);
        }
        marker_button.setTextSize(11);

        if (isClick) {
            marker_button.setBackgroundResource(R.mipmap.map_icon1);
        } else {
            marker_button.setBackgroundResource(R.mipmap.map_icon2);
        }

        // 显示自定义的图标

        makerIcon = BitmapDescriptorFactory
                .fromView(markerimgs);


        // 添加图标的经纬度
        markerOption.position(latlng);

        // 用于传输职位详情id
        // markerOption.title(mapdatainfo.getId() + "");

        markerOption.icon(makerIcon).anchor(0, 1);
        mark_list.add(aMap.addMarker(markerOption));

    }

    //得到所有的商品
    private void getAllGoods(String PageSize) {
        aMap.clear();
        list.clear();
        if (null == PageSize) {
            PageSize = "20";
        }
        WebRequestHelper.json_post(context, URLText.GET_GOODS, RequestParamsPool.getAllGoods("true", null, null, null, null, null, 1, Integer.parseInt(PageSize), false, false, false, false, "", "", "", "", "", "", ""), new MyAsyncHttpResponseHandler(context) {
            @Override
            public void onResponse(int statusCode, Header[] headers, byte[] responseBody) {
                goodsList = GsonUtils.fromJson(new String(responseBody), GoodsList.class);
                if (goodsList.IsSuccess) {
                    if (null != goodsList.MainData) {
                        list = goodsList.MainData;
                        list_red.addAll(list);
                        for (int i = 0; i < list.size(); i++) {
                            markeradd(list.get(i), false);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition position) {
        LatLng latLng = position.target;
        Longitude = latLng.longitude;
        Latitude = latLng.latitude;
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> lists = null;
        try {
            lists = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer stringBuffer = new StringBuffer();
        Address address = null;
        if (lists != null) {
            int size = lists.size();
            if (size >= 1) {
                // get(0)表示poi列表，get(1)表示road列表，get(2)表示cross列表
                if (size == 1) {
                    //地图被缩小到一定程度后，只会返回一个poi列表,其他的没有
                    address = lists.get(0);
                    // address = poi.get(0);
                } else if (size > 1) {
                    List<Address> road = (List<Address>) lists.get(1);
                    address = road.get(0);
                }
                String location = address.getLocality() + address.getSubLocality();
                city = location;
                qu = address.getSubLocality();
                String area = address.getSubLocality();//省或直辖市
                String loc = address.getLocality();//地级市或直辖市
                sheng = address.getAdminArea();
                shi = loc;
                // String mloc=address.getm
                callBack.location(location, latLng.latitude, latLng.longitude);
//                    String subLoc = address.getSubLocality();//区或县或县级市
//                    String thf = address.getThoroughfare();//道路
//                    String subthf = address.getSubThoroughfare();//道路号
//                    String fn = address.getFeatureName();//标志性建筑,当道路为null时显示
//                    if (area != null)
//                        stringBuffer.append(area);
//                    if (loc != null && !area.equals(loc))
//                        stringBuffer.append(loc);
//                    if (subLoc != null)
//                        stringBuffer.append(subLoc);
//                    if (thf != null)
//                        stringBuffer.append(thf);
//                    if (subthf != null)
//                        stringBuffer.append(subthf);
//                    if ((thf == null && subthf == null) && fn != null && !subLoc.equals(fn))
//                        stringBuffer.append(fn + "附近");
            }
        }
    }

    public interface CallBack {
        void location(String city, double Longitude, double Latitude);
    }

}
