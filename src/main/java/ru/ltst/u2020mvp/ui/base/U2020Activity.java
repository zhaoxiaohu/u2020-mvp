package ru.ltst.u2020mvp.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import ru.ltst.u2020mvp.R;
import ru.ltst.u2020mvp.U2020App;
import ru.ltst.u2020mvp.U2020Component;
import ru.ltst.u2020mvp.ui.AppContainer;
import ru.ltst.u2020mvp.ui.navigation.ActivityScreenSwitcher;
import ru.ltst.u2020mvp.ui.navigation.ToolbarPresenter;

public abstract class U2020Activity<Component> extends ActionBarActivity {

    @Inject
    AppContainer appContainer;
    @Inject
    ActivityScreenSwitcher activityScreenSwitcher;
    @Inject
    ToolbarPresenter toolbarPresenter;

    private Component component;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            onExtractParams(params);
        }
        super.onCreate(savedInstanceState);

        U2020App app = U2020App.get(this);
        component = component(app.component());
        doInject(component);

        ViewGroup container = appContainer.get(this);

        final LayoutInflater layoutInflater = getLayoutInflater();
        layoutInflater.inflate(R.layout.toolbar_container_view, container);
        toolbar = ButterKnife.findById(this, R.id.top_activity_toolbar);
        setSupportActionBar(toolbar);
        ViewGroup contentContainer = ButterKnife.findById(this, R.id.content_container);
        layoutInflater.inflate(layoutId(), contentContainer);

        if (savedInstanceState != null) {
            presenter().onRestore(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityScreenSwitcher.attach(this);
        toolbarPresenter.attach(toolbar);
    }

    @Override
    protected void onStop() {
        activityScreenSwitcher.detach();
        toolbarPresenter.detach();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter().onSave(outState);
    }

    @Override
    protected void onDestroy() {
        component = null;
        super.onDestroy();
    }

    protected void onExtractParams(@NonNull Bundle params) {
        // default no implemetation
    }

    public Component getComponent() {
        return component;
    }

    protected abstract void doInject(Component component);
    protected abstract Component component(U2020Component component);
    protected abstract @LayoutRes int layoutId();
    protected abstract ViewPresenter<? extends View> presenter();
}
