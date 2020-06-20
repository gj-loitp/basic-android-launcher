package com.example.akki.basiclauncher;

import static android.R.attr.x;

/**
 * Created by akshay on 18-10-2017.
 */

public class SortApps {
    public void exchange_sort(Pac[] pacs) {
        int i, j;
        Pac temp;

        for(i = 0; i<pacs.length-1; i++ ) {
            for (j = i+1; j<pacs.length; j++ ) {
                if (pacs[i].label.compareToIgnoreCase(pacs[j].label)>0) {
                    temp = pacs[i];
                    pacs[i] = pacs[j];
                    pacs[j] = temp;
                }
            }
        }
    }
}
