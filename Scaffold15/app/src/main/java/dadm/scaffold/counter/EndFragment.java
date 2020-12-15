package dadm.scaffold.counter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dadm.scaffold.BaseFragment;
import dadm.scaffold.R;
import dadm.scaffold.ScaffoldActivity;


public class EndFragment extends BaseFragment {
    public EndFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_end, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments().getBoolean("victory")){
            double score = getArguments().getDouble("score", 0.0);
            ((TextView)view.findViewById(R.id.end_title)).setText("CRÉDITOS CONSEGUIDOS:");
            ((TextView)view.findViewById(R.id.score)).setText(String.format("%03.1f Créditos ECTS", score));

            if(score <= 0)
                ((TextView)view.findViewById(R.id.paragraph)).setText("Te tienes que esforzar mucho más...");
            else
                ((TextView)view.findViewById(R.id.paragraph)).setText(String.format("Buen trabajo, tu puntuación equivale a %01.1f asignaturas superadas", score / 6));
        }
        else{
            ((TextView)view.findViewById(R.id.end_title)).setText("HAS FRACASADO");
            ((TextView)view.findViewById(R.id.score)).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.paragraph)).setText("No has podido evitar que la descarga de MyApps tenga errores...");
        }

        view.findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScaffoldActivity)getActivity()).startGame();
            }
        });

        view.findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ScaffoldActivity)getActivity()).backToMenu();
            }
        });
    }


    @Override
    public boolean onBackPressed() {
        ((ScaffoldActivity)getActivity()).backToMenu();
        return true;
    }


}
