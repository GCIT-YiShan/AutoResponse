package com.cokelime.bryan.autoresponse;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.Fragment;

import com.cokelime.bryan.autoresponse.receiver.PhoneStateReceiver;

public class MyStatusFragment extends Fragment implements MyIntentActions{


    public MyStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_status, container, false);

        Button available = (Button) v.findViewById(R.id.available);

        Button block = (Button) v.findViewById(R.id.block);

        Button partialBlock = (Button) v.findViewById(R.id.partialBlock);


        available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), PhoneCallBlockService.class);

                getActivity().stopService(i);

            }
        });

        partialBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), PhoneCallBlockService.class);
                i.setAction(MyIntentActions.ACTION_BLOCK_PARTIAL);

                getActivity().startService(i);
            }
        });


        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), PhoneCallBlockService.class);
                i.setAction(MyIntentActions.ACTION_BLOCK_ALL_CALL);

                getActivity().startService(i);

            }
        });



        return v;
    }


}
