package com.seafile.seadroid2.ui;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.seafile.seadroid2.BrowserActivity;
import com.seafile.seadroid2.CertsManager;
import com.seafile.seadroid2.ConcurrentAsyncTask;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.Utils;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafActivity;

public class ActivitiesFragment extends SherlockListFragment {
	private static final String DEBUG_TAG = "ActivitiesFragment";

	private View mProgressContainer;

	private View mListContainer;
	private TextView mErrorText;
	private ActivityItemAdapter adapter;
	private ListView mList;
	private TextView mEmptyView;

	private DataManager getDataManager() {
		return mActivity.getDataManager();
	}

	private BrowserActivity mActivity = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.activities_fragment, container,
				false);
		mList = (ListView) root.findViewById(android.R.id.list);
		mEmptyView = (TextView) root.findViewById(android.R.id.empty);
		mListContainer = root.findViewById(R.id.listContainer);
		mErrorText = (TextView) root.findViewById(R.id.error_message);
		mProgressContainer = root.findViewById(R.id.progressContainer);

		return root;
	}

	@Override
	public void onPause() {
		Log.d(DEBUG_TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(DEBUG_TAG, "onActivityCreated");

		adapter = new ActivityItemAdapter(mActivity);
		setListAdapter(adapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(DEBUG_TAG, "ActivitiesFragment Attached");
		mActivity = (BrowserActivity) activity;
	}

	@Override
	public void onStart() {
		Log.d(DEBUG_TAG, "ActivitiesFragment onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.d(DEBUG_TAG, "ActivitiesFragment onStop");
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(DEBUG_TAG, "ActivitiesFragment onResume");
		// refresh the view (loading data)
		refreshView();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDetach() {
		mActivity = null;
		super.onDetach();
	}

	public void refreshView() {
		refreshView(false);
	}

	@SuppressLint("NewApi")
	public void refreshView(boolean forceRefresh) {
		if (mActivity == null)
			return;

		mErrorText.setVisibility(View.GONE);
		mListContainer.setVisibility(View.VISIBLE);

		navToActivitiesView(forceRefresh);

		mActivity.supportInvalidateOptionsMenu();
	}

	public void navToActivitiesView(boolean forceRefresh) {
		// mActivity.disableUpButton();
		if (!Utils.isNetworkOn() || !forceRefresh) {
			List<SeafActivity> activities = getDataManager()
					.getActivitiesFromCache();
			if (activities != null) {
				updateAdapterWithActivities(activities);
				return;
			}
		}

		// load repos in background
		showLoading(true);
		ConcurrentAsyncTask.execute(new LoadTask(getDataManager()));
	}

	private class LoadTask extends AsyncTask<Void, Void, List<SeafActivity>> {
		SeafException err = null;
		DataManager dataManager;

		public LoadTask(DataManager dataManager) {
			this.dataManager = dataManager;
		}

		@Override
		protected List<SeafActivity> doInBackground(Void... params) {
			try {
				return dataManager.getActivitiesFromServer();
			} catch (SeafException e) {
				err = e;
				return null;
			}
		}

		private void displaySSLError() {
			if (mActivity == null)
				return;

			showError(R.string.ssl_error);
		}

		private void resend() {
			if (mActivity == null)
				return;

			ConcurrentAsyncTask.execute(new LoadTask(dataManager));
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(List<SeafActivity> sa) {
			if (mActivity == null)
				// this occurs if user navigation to another activity
				return;

			// Prompt the user to accept the ssl certificate
			if (err == SeafException.sslException) {
				SslConfirmDialog dialog = new SslConfirmDialog(
						dataManager.getAccount(),
						new SslConfirmDialog.Listener() {
							@Override
							public void onAccepted(boolean rememberChoice) {
								Account account = dataManager.getAccount();
								CertsManager.instance().saveCertForAccount(
										account, rememberChoice);
								resend();
							}

							@Override
							public void onRejected() {
								displaySSLError();
							}
						});
				dialog.show(getFragmentManager(), SslConfirmDialog.FRAGMENT_TAG);
				return;
			}

			if (err != null) {
				err.printStackTrace();
				Log.i(DEBUG_TAG,
						"failed to load activities: " + err.getMessage());
				showError(R.string.error_when_load_repos);
				return;
			}

			if (sa != null) {
				Log.d(DEBUG_TAG, "Load activities number " + sa.size());
				updateAdapterWithActivities(sa);
				showLoading(false);
			} else {
				Log.i(DEBUG_TAG, "failed to load activities");
				showError(R.string.error_when_load_repos);
			}
		}
	}

	private void showError(int strID) {
		showError(mActivity.getResources().getString(strID));
	}

	private void showError(String msg) {
		mProgressContainer.setVisibility(View.GONE);
		mListContainer.setVisibility(View.GONE);

		adapter.clear();
		adapter.notifyChanged();

		mErrorText.setText(msg);
		mErrorText.setVisibility(View.VISIBLE);
	}

	private void showLoading(boolean show) {
		mErrorText.setVisibility(View.GONE);
		if (show) {
			mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
					mActivity, android.R.anim.fade_in));
			mListContainer.startAnimation(AnimationUtils.loadAnimation(
					mActivity, android.R.anim.fade_out));

			mProgressContainer.setVisibility(View.VISIBLE);
			mListContainer.setVisibility(View.INVISIBLE);
		} else {
			mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
					mActivity, android.R.anim.fade_out));
			mListContainer.startAnimation(AnimationUtils.loadAnimation(
					mActivity, android.R.anim.fade_in));

			mProgressContainer.setVisibility(View.GONE);
			mListContainer.setVisibility(View.VISIBLE);
		}
	}

	private void addActivitiesToAdapter(List<SeafActivity> activities) {
		if (activities == null)
			return;

		for (SeafActivity activity : activities)
			adapter.add(activity);
	}

	private void updateAdapterWithActivities(List<SeafActivity> activities) {
		adapter.clear();
		if (activities.size() > 0) {
			addActivitiesToAdapter(activities);
			adapter.notifyChanged();
			mList.setVisibility(View.VISIBLE);
			mEmptyView.setVisibility(View.GONE);
		} else {
			mList.setVisibility(View.GONE);
			mEmptyView.setText(R.string.no_repo);
			mEmptyView.setVisibility(View.VISIBLE);
		}
	}
}
