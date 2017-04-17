package com.guillaume.aurore.comiquebouc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the ArchiveReader class. This class handle rar archive and perform all the
 * action required to realize the extraction of the data.
 *
 * @see ArchiveReader
 * @see Archive
 */

public class RarReader extends ArchiveReader {

    private Archive archive;

    RarReader (String fileName) {
        this.fileName = fileName;
        this.archiveType = "rar";
        File rar = new File(fileName);
        try {
            archive = new Archive(rar);
        } catch (RarException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'obtenir la liste de toute les pages contenues dans l'archive
     * @return ArrayList\<String\> list de tout les fichiers images de l'archive
     */
    @Override
    public ArrayList<String> extractTOC() {
        ArrayList<String> tOC = new ArrayList<String>(0);
        List<FileHeader> listHeader = archive.getFileHeaders();
        for (int i=0; i < listHeader.size();i++) {
            tOC.add(listHeader.get(i).getFileNameString());
        }
        java.util.Collections.sort(tOC);
        return tOC;
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
    public Bitmap syncRead(int indexPage, Book book, int resolution) {
        Log.d("RarReader","Extraction en cours de la page "+Integer.toString(indexPage));
        List<FileHeader> listHeader = archive.getFileHeaders();
        FileHeader header;
        InputStream in = null;
        header = listHeader.get(indexPage);
        try {
            in = archive.getInputStream(header);
        } catch (RarException | IOException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(in);
        // rescale
        int nh = (int) ( bmp.getHeight() * (resolution*1.0/ bmp.getWidth()) );
        bmp=Bitmap.createScaledBitmap(bmp, resolution, nh, true);
        Log.d("RarReader","Extraction terminée");
        return bmp;
    }
}
