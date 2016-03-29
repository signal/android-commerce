package co.signal.commerce;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.common.collect.Lists;

import co.signal.serverdirect.ProfileData;
import co.signal.serverdirect.ProfileDataCallback;
import co.signal.serverdirect.api.SignalProfileStore;

public class ProfileDataActivity extends BaseActivity implements ProfileDataCallback {

  private List<ProfileGroup> groups = Lists.newArrayList();
  private ProfileDataAdapter adapter;

  @Inject
  SignalProfileStore profileStore;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_data);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    updateProfileData(profileStore.getProfileData());
    ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandList);
    adapter = new ProfileDataAdapter(this);
    expListView.setAdapter(adapter);
  }

  @Override
  public void onUpdate(ProfileData profileData) {
    updateProfileData(profileData);
    adapter.notifyDataSetChanged();
  }

  private void updateProfileData(ProfileData profileData) {
    groups.clear();
    long id = 1L;

    ProfileGroup group = new ProfileGroup(id++, "UIDs");
    for (Map.Entry<String,String> entry : profileData.getUids().entrySet()) {
      group.addItem(new ProfileChild(id++, entry.getKey(), entry.getValue()));
    }
    groups.add(group);

    group = new ProfileGroup(id++, "Data");
    for (Map.Entry<String,String> entry : profileData.getData().entrySet()) {
      group.addItem(new ProfileChild(id++, entry.getKey(), entry.getValue()));
    }
    groups.add(group);

    group = new ProfileGroup(id++, "Attributes");
    group.addItem(new ProfileChild(id++, "Site ID", profileData.getSiteId()));
    group.addItem(new ProfileChild(id++, "Created", getDateString(profileData.getCreateDate())));
    group.addItem(new ProfileChild(id++, "Modified", getDateString(profileData.getModifiedDate())));
    group.addItem(new ProfileChild(id++, "Expires", getDateString(profileData.getExpireDate())));
    groups.add(group);
  }

  private String getDateString(long timestamp) {
    Date date = new java.util.Date(timestamp);
    return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
  }

  public class ProfileGroup {
    private long id;
    private String name;
    private List<ProfileChild> items = Lists.newArrayList();

    public ProfileGroup(long id, String name) {
      this.id = id;
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public List<ProfileChild> getItems() {
      return items;
    }

    public void addItem(ProfileChild item) {
      this.items.add(item);
    }
  }

  public class ProfileChild {
    long id;
    private String key;
    private String value;

    public ProfileChild(long id, String name, String value) {
      this.id = id;
      this.key = name;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }
  }

  public class ProfileDataAdapter extends BaseExpandableListAdapter {
    private Context context;

    public ProfileDataAdapter(Context context) {
      this.context = context;
    }

    @Override
    public int getGroupCount() {
      // Always 3: attributes, uids, data
      return 3;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
      return groups.get(groupPosition).getItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
      return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
      return groups.get(groupPosition).getItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
      return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
      return 0;
    }

    @Override
    public boolean hasStableIds() {
      return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
      ProfileGroup group = (ProfileGroup)getGroup(groupPosition);
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.profile_group, null);
      }
      TextView tv = (TextView) convertView.findViewById(R.id.textGroup);
      tv.setText(group.getName());
      return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
      ProfileGroup group = (ProfileGroup)getGroup(groupPosition);
      ProfileChild child = group.getItems().get(childPosition);
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.profile_child, null);
      }
      TextView tv = (TextView) convertView.findViewById(R.id.textChildKey);
      tv.setText(child.getKey());
      tv = (TextView) convertView.findViewById(R.id.textChildValue);
      tv.setText(child.getValue());
      return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
      return false;
    }
  }
}
