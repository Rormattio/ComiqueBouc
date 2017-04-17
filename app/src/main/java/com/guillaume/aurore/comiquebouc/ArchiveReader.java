package com.guillaume.aurore.comiquebouc;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * <h1>ArchiveReader</h1>
 * Abstract class defining the core function that an archive reader must contain.
 */

abstract class ArchiveReader extends Thread {
    String fileName;
    String archiveType;

    /**
     * Extrait la page d'indice indexPage dans la liste retournée par extractTOC et renvoie un
     * bitmap la représentant. pour limiter la charge mémoire, le facteur res permet de choisir une
     * réduction de la résolution de l'image d'origine.
     *
     * @param indexPage
     * @param book
     * @param resolution
     * @return {@link Bitmap Bitmap}
     */
    abstract Bitmap syncRead(int indexPage, Book book, int resolution);

    /**
     * Permet d'obtenir la liste de toute les pages contenues dans l'archive
     * @return ArrayList\<String\> list de tout les fichiers images de l'archive
     */
    abstract ArrayList<String> extractTOC();

    String extractTitle() {
        int pos;
        String title = fileName;
        pos = title.lastIndexOf('/');
        if (pos != -1)
            title = title.substring(pos + 1);

        if (title.substring(title.length() - 4, title.length()).equals(".cbz")){
            title = title.substring(0, title.length() - 4);
        }
        else if (title.substring(title.length() - 4, title.length()).equals(".cbr")){
            title = title.substring(0, title.length() - 4);
        }

        pos = title.indexOf(')');
        if (pos != -1)
            title = title.substring(0, pos + 1);

        return title;
    }
}
