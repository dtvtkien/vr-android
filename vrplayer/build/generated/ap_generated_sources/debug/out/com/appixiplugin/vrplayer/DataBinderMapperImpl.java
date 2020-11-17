package com.appixiplugin.vrplayer;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.appixiplugin.vrplayer.databinding.ActivityVrPlayerBindingImpl;
import com.appixiplugin.vrplayer.databinding.VrMediaControllerBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYVRPLAYER = 1;

  private static final int LAYOUT_VRMEDIACONTROLLER = 2;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(2);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.appixiplugin.vrplayer.R.layout.activity_vr_player, LAYOUT_ACTIVITYVRPLAYER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.appixiplugin.vrplayer.R.layout.vr_media_controller, LAYOUT_VRMEDIACONTROLLER);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYVRPLAYER: {
          if ("layout/activity_vr_player_0".equals(tag)) {
            return new ActivityVrPlayerBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_vr_player is invalid. Received: " + tag);
        }
        case  LAYOUT_VRMEDIACONTROLLER: {
          if ("layout/vr_media_controller_0".equals(tag)) {
            return new VrMediaControllerBindingImpl(component, new View[]{view});
          }
          throw new IllegalArgumentException("The tag for vr_media_controller is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case LAYOUT_VRMEDIACONTROLLER: {
          if("layout/vr_media_controller_0".equals(tag)) {
            return new VrMediaControllerBindingImpl(component, views);
          }
          throw new IllegalArgumentException("The tag for vr_media_controller is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(3);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "activity");
      sKeys.put(2, "mediaPlayer");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(2);

    static {
      sKeys.put("layout/activity_vr_player_0", com.appixiplugin.vrplayer.R.layout.activity_vr_player);
      sKeys.put("layout/vr_media_controller_0", com.appixiplugin.vrplayer.R.layout.vr_media_controller);
    }
  }
}
