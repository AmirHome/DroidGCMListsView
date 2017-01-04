package com.amirhome.droidgcmlistsview;

import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by AmirHome.com on 12/29/2016.
 */
public class CustomFilter extends Filter {

    MyAdapter adapter;
    ArrayList<Player> filterList;


    public CustomFilter(ArrayList<Player> filterList, MyAdapter adapter) {
        this.adapter = adapter;
        this.filterList = filterList;

    }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
//        Log.d("AmirHomeLog", constraint.toString());

        //CHECK CONSTRAINT VALIDITY
        if (constraint != null && constraint.length() > 0) {
            //CHANGE TO UPPER
//            constraint = constraint.toString();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Player> filteredPlayers = new ArrayList<>();

            // Iterate in the original List and add it to filter list...
            for (int i = 0; i < filterList.size(); i++)
                //CHECK
                switch (constraint.toString()) {
                    case "btnNew":
                        if (filterList.get(i).getStatus().toLowerCase().equals(DetailActivity.ONAYLI_BEKLIYOR.toLowerCase()))
                            //ADD PLAYER TO FILTERED PLAYERS
                            filteredPlayers.add(filterList.get(i));
                        break;

                    case "btnDeliveryWating":
                        if (filterList.get(i).getStatus().toLowerCase().equals(DetailActivity.TESLIM_BEKLIYOR.toLowerCase()))
                            //ADD PLAYER TO FILTERED PLAYERS
                            filteredPlayers.add(filterList.get(i));
                        break;
                    case "btnPenalty":
                        if (filterList.get(i).getStatus().toLowerCase().contains("Reject".toLowerCase()))
                            //ADD PLAYER TO FILTERED PLAYERS
                            filteredPlayers.add(filterList.get(i));
                        break;
                    case "btnRejected":
                        if (filterList.get(i).getStatus().toLowerCase().equals("Reject".toLowerCase()))
                            //ADD PLAYER TO FILTERED PLAYERS
                            filteredPlayers.add(filterList.get(i));
                        break;
                    case "btnDelivered":
                        if (filterList.get(i).getStatus().toLowerCase().equals("Delivered".toLowerCase()))
                            //ADD PLAYER TO FILTERED PLAYERS
                            filteredPlayers.add(filterList.get(i));
                        break;
                    case "btnCustomerRejected":
                        if (filterList.get(i).getStatus().toLowerCase().contains("Reject_".toLowerCase()))
                            //ADD PLAYER TO FILTERED PLAYERS
                            filteredPlayers.add(filterList.get(i));
                        break;
                }

/*            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getStatus().toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }*/

            results.count = filteredPlayers.size();
            results.values = filteredPlayers;
        } else {
            results.count = filterList.size();
            results.values = filterList;

        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.players = (ArrayList<Player>) results.values;

        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
