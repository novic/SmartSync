package com.seafile.seadroid2.data;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.seafile.seadroid2.R;

/**
 * SeafActivity
 * 
 * @author novic_dev
 */
public class SeafActivity implements ActivityItem {

	public String id; // repo id
	public String name;
	public String description;
	public String user;
	public String mtime; // the last modification time

	static SeafActivity fromJson(JSONObject obj) {
		SeafActivity act = new SeafActivity();
		try {
			act.id = obj.getString("repo_id");
			act.name = obj.getString("repo_name");
			act.description = obj.getString("desc");
			act.user = obj.getString("nick");
			long mt = obj.getLong("time");
			act.mtime = new Date(mt).toLocaleString();
			return act;
		} catch (JSONException e) {
			return null;
		}
	}

	public SeafActivity() {

	}

	public String getID() {
		return id;
	}

	public String getRepoName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUser() {
		return user;
	}

	public String getTime() {
		return mtime;
	}

	public int getIcon() {
		return R.drawable.info;
	}

}
