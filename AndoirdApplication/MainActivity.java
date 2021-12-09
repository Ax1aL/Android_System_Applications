package com.example.ProjectADMD;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ListView list;
    MyListView adapter;
    boolean appFound=true;
    int sortType=0;
    boolean opened=false;
    Drawable playStoreIcon=null;

    ArrayList<String> mainTitles = new ArrayList<>();
    ArrayList<Drawable> imageId =new ArrayList<>();
    ArrayList<String> packageName=new ArrayList<>();
    ArrayList<Long> times=new ArrayList<>();

    ArrayList<String> queryTitles = new ArrayList<>();
    ArrayList<Drawable> queryImageId =new ArrayList<>();
    ArrayList<String> queryPackageName=new ArrayList<>();
    ArrayList<Long> queryTimes=new ArrayList<>();


    public void swap(int pos1,int pos2,ArrayList<String> mainTitles,ArrayList<String> packageName,
                     ArrayList<Drawable> imageId,ArrayList<Long> times){
        String tempTitle=mainTitles.get(pos1);
        String tempPack=packageName.get(pos1);
        Drawable tempImg=imageId.get(pos1);
        Long tempTime=times.get(pos1);

        mainTitles.set(pos1,mainTitles.get(pos2));
        packageName.set(pos1,packageName.get(pos2));
        imageId.set(pos1,imageId.get(pos2));
        times.set(pos1,times.get(pos2));

        mainTitles.set(pos2,tempTitle);
        packageName.set(pos2,tempPack);
        imageId.set(pos2,tempImg);
        times.set(pos2,tempTime);
    }

    public void sortByName(boolean desc){

        for(int i=0;i<mainTitles.size()-1;++i){
            for(int j=i+1;j<mainTitles.size();++j){
                if(!desc) {
                    if (mainTitles.get(i).compareTo(mainTitles.get(j)) > 0) swap(i, j,mainTitles,packageName,imageId,times);
                }else {
                    if (mainTitles.get(i).compareTo(mainTitles.get(j)) < 0) swap(i, j,mainTitles,packageName,imageId,times);
                }
            }
        }
        for(int i=0;i<queryTitles.size()-1;++i){
            for(int j=i+1;j<queryTitles.size();++j){
                if(!desc) {
                    if (queryTitles.get(i).compareTo(queryTitles.get(j)) > 0) swap(i, j,queryTitles,queryPackageName,queryImageId,queryTimes);
                }else {
                    if (queryTitles.get(i).compareTo(queryTitles.get(j)) < 0) swap(i, j,queryTitles,queryPackageName,queryImageId,queryTimes);
                }
            }
        }

    }
    public void sortByTime(boolean desc){

        for(int i=0;i<mainTitles.size()-1;++i){
            for(int j=i+1;j<mainTitles.size();++j){
                if(!desc) {
                    if (times.get(i) < times.get(j)) swap(i, j,mainTitles,packageName,imageId,times);
                }else {
                    if (times.get(i) > times.get(j)) swap(i, j,mainTitles,packageName,imageId,times);
                }
            }
        }

        for(int i=0;i<queryTitles.size()-1;++i){
            for(int j=i+1;j<queryTitles.size();++j){
                if(!desc) {
                    if (queryTimes.get(i) < queryTimes.get(j)) swap(i, j,queryTitles,queryPackageName,queryImageId,queryTimes);
                }else {
                    if (queryTimes.get(i) > queryTimes.get(j)) swap(i, j,queryTitles,queryPackageName,queryImageId,queryTimes);
                }
            }
        }

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner sorting=(Spinner) findViewById(R.id.sorting);
        ArrayAdapter<CharSequence> sortAdapter=ArrayAdapter.createFromResource(this,R.array.sorting, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorting.setAdapter(sortAdapter);
        sorting.setOnItemSelectedListener(this);

        final PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> packages = pm.queryIntentActivities(intent,0);
        UsageStatsManager mUsageStatsManager = (UsageStatsManager)MainActivity.this.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        if(!checkUsageStat()) {
            Intent usageAccess = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usageAccess.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usageAccess);
            Toast.makeText(this, "App needs permission!", Toast.LENGTH_SHORT).show();
        }

        List<UsageStats> usg= mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,cal.getTimeInMillis(),System.currentTimeMillis());

        for (ResolveInfo packageInfo : packages) {
            if(packageInfo.activityInfo.applicationInfo.packageName.equals(this.getPackageName()))continue;
            if(packageInfo.loadLabel(pm).toString().equals("Play Store"))playStoreIcon=packageInfo.loadIcon(pm);
            for(int x=0;x<usg.size();++x){
                if(usg.get(x).getPackageName().equals(packageInfo.activityInfo.applicationInfo.packageName)){
                    times.add(usg.get(x).getLastTimeUsed());
                    mainTitles.add(packageInfo.loadLabel(pm).toString());
                    packageName.add(packageInfo.activityInfo.applicationInfo.packageName);
                    imageId.add(packageInfo.loadIcon(pm));
                    break;
                }
            }
        }



        queryTitles= (ArrayList<String>)mainTitles.clone();
        queryImageId = (ArrayList<Drawable>)imageId.clone();
        queryPackageName= (ArrayList<String>)packageName.clone();
        queryTimes=(ArrayList<Long>)times.clone();

        adapter= new MyListView(this, queryTitles, queryImageId);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);




        //OPEN APP FROM LIST--------------------------------------------
        list.setOnItemClickListener((parent, view, position, id) -> {
            if(appFound){
                opened=true;
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(queryPackageName.get(position));
                startActivity(launchIntent);
                Toast.makeText(getApplicationContext(), queryTitles.get(position), Toast.LENGTH_SHORT).show();
            }else{
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=" +
                        queryPackageName.get(position))));
            }
        });

        SearchView search=(SearchView) findViewById(R.id.search_bar);
        //SEARCH QUERY------------------------------------------------------------
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                appFound=false;

                if(newText.equals("")){
                    appFound=true;
                    queryTitles= (ArrayList<String>)mainTitles.clone();
                    queryImageId = (ArrayList<Drawable>)imageId.clone();
                    queryPackageName= (ArrayList<String>)packageName.clone();
                    queryTimes=(ArrayList<Long>)times.clone();
                    adapter= new MyListView(MainActivity.this, queryTitles, queryImageId);
                    list = (ListView) findViewById(R.id.list);
                    list.setAdapter(adapter);
                    return false;
                }

                queryTitles.clear();
                queryImageId.clear();
                queryPackageName.clear();
                queryTimes.clear();

                for(int x = 0; x< mainTitles.size(); ++x){
                    String quer=newText.toLowerCase();
                    String title= mainTitles.get(x).toLowerCase();
                    if(title.length()<quer.length())continue;
                    title=title.substring(0,quer.length());
                    if(title.equals(quer)){
                        queryTitles.add(mainTitles.get(x));
                        queryImageId.add(imageId.get(x));
                        queryPackageName.add(packageName.get(x));
                        queryTimes.add(times.get(x));
                    }
                }

                if(queryTitles.size()==0){
                    queryTitles.add("Search on store: "+newText);
                    if(playStoreIcon!=null){queryImageId.add(playStoreIcon);}
                    else{queryImageId.add(getDrawable(R.drawable.ic_launcher_background));}
                    queryPackageName.add(newText);
                }else appFound=true;

                adapter= new MyListView(MainActivity.this, queryTitles, queryImageId);
                list = (ListView) findViewById(R.id.list);
                list.setAdapter(adapter);

                return false;
            }
        });


    }

    public boolean checkUsageStat(){
        try{
            PackageManager pack=getPackageManager();
            ApplicationInfo appInfo=pack.getApplicationInfo(getPackageName(),0);
            AppOpsManager appOps=(AppOpsManager) getSystemService(APP_OPS_SERVICE);
            int mode=appOps.checkOpNoThrow(appOps.OPSTR_GET_USAGE_STATS,appInfo.uid,appInfo.packageName);
            return mode==AppOpsManager.MODE_ALLOWED;
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this,"Give permission",Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       switch(position){
           case 0:sortByName(false);break;
           case 1:sortByName(true);break;
           case 2:sortByTime(false);break;
           case 3:sortByTime(true);break;
       }
       sortType=position;
        adapter= new MyListView(MainActivity.this, queryTitles, queryImageId);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}



}