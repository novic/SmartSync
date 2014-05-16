package com.seafile.seadroid2.data;

import com.seafile.seadroid2.R;

public class SeafCachedActivity implements ActivityItem {
    
    public int id;
    public String name;
    public String desc;
    public String user;
    public String time;
    public String path;
    public int icon;

    public SeafCachedActivity() {
        id = -1;
    }
    
    @Override
    public String getRepoName() {
    	return path.substring(path.lastIndexOf('/') + 1);
    };

    @Override
    public String getDescription() {
    	return path.substring(path.lastIndexOf('/') + 2);
    };
    
    @Override
    public String getUser() {
    	return path.substring(path.lastIndexOf('/') + 3);
    };
    
    @Override
    public String getTime() {
    	return path.substring(path.lastIndexOf('/') + 4);
    };
    
    @Override
    public int getIcon() {
    	return R.drawable.icon;
    };
}
