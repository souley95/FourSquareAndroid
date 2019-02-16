package com.foursquare.takehome;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private  RecyclerView rvRecyclerView;
    private  PersonAdapter personAdapter;
    private PriorityQueue<Person> filter;
    private ArrayList<Person> recyclerViewData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvRecyclerView = findViewById(R.id.visitor_list);
        rvRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //We first initialize a priority queue to sort Visitor by their ARRIVAL time only. The final list will be an arrayList for recyclerView purpose.
        //We're going to use priority queue over ArrayList because of its O(1) insertion and deletion efficiency over ArrayList's O(n)

        filter = new PriorityQueue<>(new Comparator<Person>() {
            @Override
            public int compare(Person person, Person t1) {

                /**
                 * Sort the visitor entry in ascending order of their ARRIVAL time
                 */
                return Long.compare(person.getArriveTime(),t1.getArriveTime());
            }
        });


        //Initialize an ArrayList to be set as the RecyclerView Adapter data and the list that will hold all the visit including no visit time
        recyclerViewData = new ArrayList<>();

        VenueFetcher fetcher = new VenueFetcher(this);
        fetcher.execute();
    }



    /**
     * Fakes a data fetch and parses json from assets/people.json
     */
    private static class VenueFetcher extends AsyncTask<Void, Void, Venue>  {
        private final WeakReference<MainActivity> activityWeakReference;

        public VenueFetcher(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Venue doInBackground(Void... params) {
            return activityWeakReference != null ? VenueStore.get().getVenue(activityWeakReference.get()) : null;
        }

        @Override
        protected void onPostExecute(Venue venue) {
            if (activityWeakReference == null || venue == null) {
                return;
            }

            MainActivity mainActivity = activityWeakReference.get();


            //We first add all visitor in the Priority Queue
            for(Person p : venue.getVisitors()){

                mainActivity.filter.add(p);
            }


            //Get opening and closing time of the venue
            Long opening = venue.getOpenTime();
            Long closing = venue.getCloseTime();

            //Pop the first visitor at he venue and compare he's arrival time to opening time
            //If it's after opening time, we add an empty visitor from opening to first arrival to the final list
            Person first = mainActivity.filter.poll();

            if(first.getArriveTime() > opening){
                Person empty = new Person("No Visitor",opening,first.getArriveTime());
                mainActivity.recyclerViewData.add(empty);
                mainActivity.recyclerViewData.add(first);
            }
            else{
                mainActivity.recyclerViewData.add(first);
            }

            //We track the latest departure from the venue so we know when to add an empty visit to the list
            Long lastDeparture = first.getLeaveTime();

            //Pop the rest of the visitor from the priority queue and compare their arrival time to the latest recorded departure
            //If it's before, then there was always someone at the venue until the current visitor arrival
            //if it's after , we add an empty visitor from last departure recorded to current visitor arrival time
            // We then compare the new added departure time to the last record and make update if necessary
            while(!mainActivity.filter.isEmpty()){

                Person p = mainActivity.filter.poll();

                if(p.getArriveTime() < lastDeparture){
                    mainActivity.recyclerViewData.add(p);
                }
                else if (p.getArriveTime() > lastDeparture){
                    Person empty = new Person("No Visitor",lastDeparture,p.getArriveTime());
                    mainActivity.recyclerViewData.add(empty);
                    mainActivity.recyclerViewData.add(p);
                }

                if (lastDeparture < p.getLeaveTime()){

                    lastDeparture = p.getLeaveTime();
                }

            }

            //After adding all the visitor , we compare the last departure to the closing time of the venue and see if there was a period with no visitor
            if (lastDeparture < closing){

                first = new Person("No Visitor",lastDeparture,closing);
                mainActivity.recyclerViewData.add(first);
            }



            mainActivity.personAdapter = new PersonAdapter(mainActivity.recyclerViewData);
            mainActivity.rvRecyclerView.setAdapter(mainActivity.personAdapter);



        }




    }
}
