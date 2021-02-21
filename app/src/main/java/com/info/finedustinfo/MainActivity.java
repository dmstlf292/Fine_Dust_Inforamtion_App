package com.info.finedustinfo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.info.finedustinfo.common.AddLocationDialogFragment;
import com.info.finedustinfo.db.LocationRealmObject;
import com.info.finedustinfo.finedust.FineDustContract;
import com.info.finedustinfo.finedust.FineDustFragment;
import com.info.finedustinfo.util.GeoUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {//페이저 어뎁터 만들기

    //여기가 필드 부분이다!!!!
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Pair<Fragment, String>> mFragmentList;
    //위치정보 얻기 위해서
    private FusedLocationProviderClient mFusedLocationClient;
    //위치 권한 설정
    public static final int REQUEST_CODE_FINE_COARSE_PERMISSION = 1000;
    //디비 저장을 위해서!
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //디비 저장을 위해서 초기화 하기
        mRealm = Realm.getDefaultInstance();


        //위치정보 얻기 위해서 초기화 하기
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //위치 추가 버튼 (밑에있는 플러스 모양 버튼)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddLocationDialogFragment.newInstance(new AddLocationDialogFragment.OnClickListener() {
                    @Override
                    public void onOkClicked(final String city) {
                        GeoUtil.getLocationFromName(MainActivity.this, city, new GeoUtil.GeoUtilListener() {
                            @Override
                            public void onSuccess(double lat, double lng) {
                                //디비에 추가하는 코드 작성
                                saveNewCity(lat,lng, city);//디비에 저장
                                addNewFragment(lat,lng,city);//실제화면에 표시
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).show(getSupportFragmentManager(), "dialog");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpViewPager();

        //위치 추가 버튼 (밑에있는 플러스 모양 버튼) 클릭시 동작하는 코드


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    //디비 저장을 위한 INSERT 코드
    private void saveNewCity(double lat, double lng, String city){
        mRealm.beginTransaction();
        LocationRealmObject newLocationRealmObject =
                mRealm.createObject(LocationRealmObject.class);
        newLocationRealmObject.setName(city);
        newLocationRealmObject.setLat(lat);
        newLocationRealmObject.setLng(lng);
        mRealm.commitTransaction();
    }




    private void addNewFragment(double lat, double lng, String city){
        //새로 위도경도 얻으면 프래그먼트 만들고 리스트에 추가를 해줘야한다.
        mFragmentList.add(new Pair<Fragment, String>(
                FineDustFragment.newInstance(lat,lng),city // 리스트에 추가하기
        ));
        //추가를 하면 페이저에 알려줘야한다.
        mViewPager.getAdapter().notifyDataSetChanged();//변경사항을 , 업데이트 사항을 알려주기
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_all_delete) {
            mRealm.beginTransaction();
            mRealm.where(LocationRealmObject.class).findAll().deleteAllFromRealm();//디비 저장된 것을 전부 지우는것
            mRealm.commitTransaction();
            //화면에 저장된 것도 전부 지우기
            setUpViewPager();//다시 세팅하기
            return true;
        }else if(id == R.id.action_delete){//선택된것만 삭제하기
            if (mTabLayout.getSelectedTabPosition() == 0){
                Toast.makeText(this, "현재 위치 탭은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return true;
            }
            mRealm.beginTransaction();
            mRealm.where(LocationRealmObject.class).findAll()
                    .get(mTabLayout.getSelectedTabPosition()-1).deleteFromRealm();//현재 위치부터 계산해줘야한다.
            mRealm.commitTransaction();
            setUpViewPager();//다시 세팅하기
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void setUpViewPager() {
        //뷰페이저 초기화 하는 코드들
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);

        //현재위치를 로드하는 코드
        loadDbData();
        //로드 후에 페이저 어뎁터 생성하기
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList);
        //어뎁터 붙이기
        mViewPager.setAdapter(adapter);
        //탭레이아웃과 연동시키기
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void loadDbData() {//데이터 로드하기
        mFragmentList = new ArrayList<>();//초기화하기
        //현재위치 가져오기
        mFragmentList.add(new Pair<Fragment, String>(
                //1번째 프래그먼트, 실제 현재 위치의 위도 경도 던지기!
                        new FineDustFragment(), "현재 위치"
        ));
        //디비에서 데이터 다 가져와서 차례대로 추가하는 코드 작성 (저장된게 하나씩 꺼내진다)
        RealmResults<LocationRealmObject> realmResults =
                mRealm.where(LocationRealmObject.class).findAll();
        for (LocationRealmObject realmObject : realmResults){
            mFragmentList.add(new Pair<Fragment, String>(
                    //1번째 프래그먼트, 실제 현재 위치의 위도 경도 던지기!
                    FineDustFragment.newInstance(realmObject.getLat(),
                            realmObject.getLng()), realmObject.getName()
            ));
        }
    }

    //위치정보 권한 설정하기
    public void getLastKnownLocation() { // alt+enter 에드퍼미션 체크하기
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //권한체크 하기
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_FINE_COARSE_PERMISSION);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //성공을 했다면 = 위치를 얻은것
                        if (location != null) {
                            //첫번째 아이템 = 현재위치, reload로 받아야 한다 (View라는 인터페이스 안에 정의했음)
                            FineDustContract.View view = (FineDustContract.View) mFragmentList.get(0).first;
                            //위도 경도 전달하기
                            view.reload(location.getLatitude(), location.getLongitude());
                        }
                    }
                });

    }

    //권한을 처리하는 부분
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_FINE_COARSE_PERMISSION){
            if(grantResults.length > 0
                && grantResults[0] ==PackageManager.PERMISSION_GRANTED
                && grantResults[1] ==PackageManager.PERMISSION_GRANTED){
                getLastKnownLocation();//마지막 로케이션을 호출하겠다.
            }
        }
    }





    //어댑터를 내부클래스로 작성하기
    private static class MyPagerAdapter extends FragmentStatePagerAdapter{
        //아이템은 페어 사용하기,   프레그먼트들의 정보 + 지역이름 같이 표시해야함
        private final List<Pair<Fragment, String>> mFragmentList;

        //생성자 만들기
        public MyPagerAdapter(FragmentManager fm,List<Pair<Fragment, String>> fragmentList){
            super(fm);
            mFragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {//프래그먼트를 생성해서 돌려줘야하는 부분
            return mFragmentList.get(position).first;//first는 프레그먼트들의 정보를 의미, seconde는 지역이름
        }

        @Override
        public int getCount() {//사이즈
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).second;//타이틀!지역명!!
        }
    }


}