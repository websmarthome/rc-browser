package triaina.injector.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import triaina.injector.TriainaInjector;
import triaina.injector.TriainaInjectorFactory;

public abstract class TriainaFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	TriainaInjector injector = TriainaInjectorFactory.getInjector(getActivity());
    	injector.injectMembersWithoutViews(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TriainaInjector injector = TriainaInjectorFactory.getInjector(getActivity());
        injector.injectViewMembers(this);
    }
}
