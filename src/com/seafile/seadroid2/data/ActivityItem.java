package com.seafile.seadroid2.data;

import java.util.Date;

public interface ActivityItem {
	
	public String getRepoName();

    public String getDescription();
    
    public String getUser();
    
    public Date getTime();

}
