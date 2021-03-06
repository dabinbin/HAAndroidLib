package ha.custcom.webview.lib.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import ha.custcom.webview.lib.R;

/**
 * by bin
 * 装载fragment通用类
 */
public class CommonActivity extends SimpleBaseActivity {
    public static final String CLAZ = "clas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Class clz = (Class) intent.getSerializableExtra(CLAZ);
        Fragment fragment = null;
        try {
            fragment = (Fragment) Class.forName(clz.getName()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
    }

    public static void launch(Context context, Class clz, Intent intent) {
        intent.setClass(context, CommonActivity.class);
        intent.putExtra(CLAZ, clz);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_common;
    }
}
