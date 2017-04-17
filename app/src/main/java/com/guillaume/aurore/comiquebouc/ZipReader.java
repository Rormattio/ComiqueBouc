package com.guillaume.aurore.comiquebouc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Implementation of the ArchiveReader class. This class handle zip archive and perform all the
 * action required to realize the extraction of the data.
 *
 * @see ArchiveReader
 * @see ZipFile
 */

class ZipReader extends ArchiveReader {

    private ZipFile archive;

    ZipReader(String fileName) {
        this.fileName = fileName;
        this.archiveType = "zip";
        try {
            this.archive = new ZipFile(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'obtenir la liste de toute les pages contenues dans l'archive
     * @return ArrayList\<String\> list de tout les fichiers images de l'archive
     */
    @Override
    ArrayList<String> extractTOC() {
        ArrayList<String> tableOfContent = new ArrayList<>();
        ZipEntry entry;
        Enumeration<? extends ZipEntry> entries = archive.entries();

        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            if (!entry.isDirectory()) {
                String extension = "";

                int i = entry.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = entry.getName().substring(i+1).toLowerCase();
                }
                if (extension.equals("png") | extension.equals("jpg")) {
                    tableOfContent.add(entry.getName());
                }
            }
        }
        java.util.Collections.sort(tableOfContent);
        return tableOfContent;
    }

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
    @Override
    public Bitmap syncRead(int indexPage, Book book, int resolution) {
        Log.d("ZipReader","Extraction en cours de la page "+Integer.toString(indexPage));
        ZipEntry entry = archive.getEntry(book.TOC.get(indexPage));
        InputStream input = null;
        try {
            input = archive.getInputStream(entry);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bmp = BitmapFactory.decodeStream(input);
        int nh = (int) ( bmp.getHeight() * (resolution*1.0 / bmp.getWidth()) );
        bmp=Bitmap.createScaledBitmap(bmp, resolution, nh, true);
        Log.d("ZipReader","Extraction terminée");
        return bmp;
    }
}
