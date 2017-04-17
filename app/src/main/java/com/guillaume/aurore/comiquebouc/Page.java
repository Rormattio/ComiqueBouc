package com.guillaume.aurore.comiquebouc;

import android.graphics.Bitmap;

/** Classe Page, un page manipulée dans un book
 */

public class Page {
    public int indicePage;
    public Bitmap image;

    Page(int indice, Bitmap image) {
        this.image = image;
        this.indicePage = indice;
    }

    public Page(Integer indice) {
        indicePage=indice;
    }



}
