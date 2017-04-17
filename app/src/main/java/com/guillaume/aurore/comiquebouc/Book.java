package com.guillaume.aurore.comiquebouc;
import android.graphics.Bitmap;

import java.util.ArrayList;

/** Classe Book
 *  les objets livres
 */

public class Book {
    public String nomFichier;
    public ArrayList<Page> pagesDispo;
    public ArrayList<String> TOC;
    public Integer tailleLivre;
    public String titreLivre;
    public ArchiveReader Lecteur;
    public Integer currentPage;
    public Bitmap couverture;

    public Book(String nomFichier) {
        this.nomFichier = nomFichier;
        String extension = nomFichier.substring(nomFichier.lastIndexOf('.')+1);
        if (extension.equals("cbz"))
            this.Lecteur = new ZipReader(nomFichier);
        else if (extension.equals("cbr"))
            this.Lecteur = new RarReader(nomFichier);
        this.TOC = Lecteur.extractTOC();
        this.tailleLivre = this.TOC.size();
        this.titreLivre = this.Lecteur.extractTitle();
        this.currentPage = 0;
        this.pagesDispo = new ArrayList<>(0);
        this.couverture = this.Lecteur.syncRead(0,this, 100);
    }

    public String getTitle(){
        return titreLivre;
    }

    public void setCurrentPage(int nombre){
        currentPage=nombre;
    }

    public void addPage(Page page){
        pagesDispo.add(page);
    }

    public Page getPage(int numero){ return pagesDispo.get(numero);}

    public void toggleReverseMode() {
        java.util.Collections.reverse(this.TOC);
    }


}