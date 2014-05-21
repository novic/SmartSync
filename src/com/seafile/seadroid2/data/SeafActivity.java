package com.seafile.seadroid2.data;

import java.util.Date;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeadroidApplication;

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

	private static Context context = SeadroidApplication.getAppContext();

	static SeafActivity fromJson(JSONObject obj) {
		SeafActivity act = new SeafActivity();
		try {
			act.id = obj.getString("repo_id");
			act.name = obj.getString("repo_name");

			/**
			 * Split and localize the Description text TODO:
			 */
			StringTokenizer tokens = new StringTokenizer(obj.getString("desc"),
					" ");
			String action = tokens.nextToken();
			String objname = tokens.nextToken();

			if (action.equalsIgnoreCase("Added")) {
				if (objname.equalsIgnoreCase("directory")) {
					action = context.getResources().getString(
							R.string.action_dir_added);
					objname = context.getResources().getString(
							R.string.action_dir);
				} else {
					action = context.getResources().getString(
							R.string.action_added);
				}

			} else if (action.equalsIgnoreCase("Removed")) {
				if (objname.equalsIgnoreCase("directory")) {
					action = context.getResources().getString(
							R.string.action_dir_removed);
					objname = context.getResources().getString(
							R.string.action_dir);
				} else {
					action = context.getResources().getString(
							R.string.action_removed);
				}

			} else if (action.equalsIgnoreCase("Renamed")) {
				if (objname.equalsIgnoreCase("directory")) {
					action = context.getResources().getString(
							R.string.action_dir_renamed);
					objname = context.getResources().getString(
							R.string.action_dir);
				} else {
					action = context.getResources().getString(
							R.string.action_renamed);
				}

			} else if (action.equalsIgnoreCase("Changed")) {
				if (objname.equalsIgnoreCase("directory")) {
					action = context.getResources().getString(
							R.string.action_dir_changed);
					objname = context.getResources().getString(
							R.string.action_dir);
				} else {
					action = context.getResources().getString(
							R.string.action_changed);
				}
			}

			act.description = action + " " + objname;
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
