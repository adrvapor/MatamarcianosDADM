package dadm.scaffold.counter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dadm.scaffold.BaseFragment;
import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;


public class MainMenuFragment extends BaseFragment{

    private static final int NUMBER_OF_CUSTOM_SHIPS = 3;
    public int shipIndex = 0;

    public MainMenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScaffoldActivity)getActivity()).startGame();
            }
        });

        view.findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        view.findViewById(R.id.custom_r).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIdx(1);
            }
        });

        view.findViewById(R.id.custom_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIdx(-1);
            }
        });

        changeIdx(0);
    }

    public void changeIdx(int i){
        shipIndex += i;
        if(shipIndex >= NUMBER_OF_CUSTOM_SHIPS)
            shipIndex = 0;
        else if(shipIndex < 0){
            shipIndex = NUMBER_OF_CUSTOM_SHIPS - 1;
        }

        switch (shipIndex){
            case 0:
            default:
                ((ImageView)getView().findViewById(R.id.ship)).setImageResource(R.drawable.customship);
                break;
            case 1:
                ((ImageView)getView().findViewById(R.id.ship)).setImageResource(R.drawable.customshipred);
                break;
            case 2:
                ((ImageView)getView().findViewById(R.id.ship)).setImageResource(R.drawable.customshipwhite);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
        return true;
    }
}
