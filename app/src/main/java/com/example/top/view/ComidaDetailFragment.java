package com.example.top.view;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.top.R;
import com.example.top.view.dummy.DummyContent;

/**
 * A fragment representing a single Comida detail screen.
 * This fragment is either contained in a {@link ComidaListActivity}
 * in two-pane mode (on tablets) or a {@link ComidaDetailActivity}
 * on handsets.
 */
public class ComidaDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy nombre this fragment is presenting.
     */
    private DummyContent.Comida mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ComidaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy nombre specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load nombre from a nombre provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getNombre());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comida_detail, container, false);

        // Show the dummy nombre as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.comida_detail)).setText(mItem.getPrecio());
        }

        return rootView;
    }
}
