package com.guillaume.aurore.comiquebouc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import static android.content.Intent.ACTION_VIEW;


public class HomeActivity extends AppCompatActivity implements DeleteBookDialogFragment.DeleteBookDialogListener {

    FloatingActionButton buttonFindFile = null;
    RecyclerView rvLastRead;
    BookAdapter adapter;

    Book lastBook;
    ArrayList<Book> lastReads = new ArrayList<>(3);

    final String CURRENT_PAGE_KEY = "page actuelle";
    final String RETURN_KEY = "com.guillaume.aurore.comiquebouc.return_data";

    private static final int GETFILE_REQUEST_CODE = 1;
    private static final int READER_REQUEST_CODE = 2;

    SharedPreferences sharePref;
    SharedPreferences.Editor editor;

    String lastBookPath1Key = "lastBookPath1";
    String lastBookPath2Key = "lastBookPath2";
    String lastBookPath3Key = "lastBookPath3";
    String lastBookState1Key = "lastBookState1";
    String lastBookState2Key = "lastBookState2";
    String lastBookState3Key = "lastBookState3";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Log.d("ActivityState","Creation");

        sharePref = getPreferences(Context.MODE_PRIVATE);
        if (!sharePref.getString(lastBookPath1Key,"").equals("")) {
            lastBook = new Book(sharePref.getString(lastBookPath1Key,""));
            lastBook.setCurrentPage(sharePref.getInt(lastBookState1Key,1));
            lastReads.add(lastBook);
            Log.d("SharePreference","1 Book restored");
        }
        if (!sharePref.getString(lastBookPath2Key,"").equals("")) {
            lastBook = new Book(sharePref.getString(lastBookPath2Key,""));
            lastBook.setCurrentPage(sharePref.getInt(lastBookState2Key,1));
            lastReads.add(lastBook);
            Log.d("SharePreference","2 Book restored");
        }
        if (!sharePref.getString(lastBookPath3Key,"").equals("")) {
            lastBook = new Book(sharePref.getString(lastBookPath3Key,""));
            lastBook.setCurrentPage(sharePref.getInt(lastBookState3Key,1));
            lastReads.add(lastBook);
            Log.d("SharePreference","3 Book restored");
        }

        // Lookup the recyclerview in activity layout
        rvLastRead = (RecyclerView) findViewById(R.id.rvLastRead);


        // Create adapter passing in the sample user data
        adapter = new BookAdapter(this, lastReads);
        // Attach the adapter to the recyclerview to populate items
        rvLastRead.setAdapter(adapter);
        // Set layout manager to position the items
        rvLastRead.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

        adapter.setOnItemClickListener(new BookAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                openBook(lastReads.get(position).nomFichier, lastReads.get(position).currentPage);
            }

            @Override
            public void onLongItemClick(int position, View v) {
                DialogFragment deleteBookDialogFragment = new DeleteBookDialogFragment();
                Bundle args = new Bundle();
                args.putInt("position",position);
                args.putString("title",lastReads.get(position).titreLivre);
                deleteBookDialogFragment.setArguments(args);
                deleteBookDialogFragment.show(getSupportFragmentManager(), "delete_book");
            }
        });


        buttonFindFile = (FloatingActionButton) findViewById(R.id.findFileButton);
        buttonFindFile.setOnClickListener(getFile);
    }

    /*fonction executé lorsque l'on appuie sur le bouton parcourir
    elle va demander à trouver un fichier cbz puis va lancer le reader*/
    private View.OnClickListener getFile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int permissionCheck=0;
            //Check for permission and request it if not granted
            if (ContextCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(getBaseContext(), "La permission de lecture est nécessaire pour accéder aux comic book", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(HomeActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            permissionCheck);
                }
                else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(HomeActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            permissionCheck);
                }
            }

            else {
                try {
                    Intent getFilename = new Intent(Intent.ACTION_GET_CONTENT);
                    getFilename.setType("*/*");
                    startActivityForResult(getFilename, GETFILE_REQUEST_CODE);
                } catch (ActivityNotFoundException exp) {
                    Toast.makeText(getBaseContext(), "No file manager found in your device", Toast.LENGTH_SHORT).show();
                }

            }

        }
    };

    private void openBook(String path, int index) {
        Log.d("ActivityState","Opening book "+path);
        int pos = path.lastIndexOf(".");
        if (pos!=-1) {
            String ext = path.substring(pos);
            if (ext.equals(".cbz") || ext.equals(".cbr")) {
                //On ajoute le book dans la liste des books lus
                int i;
                Boolean added = false;
                lastBook = new Book(path);
                for (i=0;i<lastReads.size();i++) {
                    if (lastBook.getTitle().equals(lastReads.get(i).getTitle())) {
                        lastBook.setCurrentPage(lastReads.get(i).currentPage);
                        lastReads.remove(i);
                        adapter.notifyItemRemoved(i);
                        lastReads.add(0,lastBook);
                        adapter.notifyItemInserted(0);
                        added = true;
                    }
                }
                if (!added) {
                    if (lastReads.size()>=3) {
                        lastReads.remove(2);
                        adapter.notifyItemRemoved(2);
                    }
                    lastReads.add(0,lastBook);
                    adapter.notifyItemInserted(0);
                }

                //Intent explicite qui lance le reader
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("comics");
                builder.path(path);
                Uri call = builder.build();
                Intent launchReader = new Intent(ACTION_VIEW, call, getBaseContext(), ReaderActivity.class);
                launchReader.putExtra(CURRENT_PAGE_KEY,index);
                startActivityForResult(launchReader, READER_REQUEST_CODE);
            }
            else {
                Toast.makeText(this, "The file is not a comicbook archive. Please select an other file", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "The file is not a comicbook archive. Please select an other file", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogPositiveClick(DeleteBookDialogFragment dialog) {
        Log.d("Delete","lastReads.size() = "+Integer.toString(lastReads.size()));
        lastReads.remove(dialog.position);
        adapter.notifyItemRemoved(dialog.position);
        Log.d("Delete","lastReads.size() = "+Integer.toString(lastReads.size()));
    }

    @Override
    public void onDialogNegativeClick(DeleteBookDialogFragment dialog) {
        Log.d("Dialog","Negative");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GETFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String path = uri.getLastPathSegment();
                path = path.substring(path.indexOf(":")+1);
                path = Environment.getExternalStorageDirectory().getPath()+"/" +path;
                openBook(path,0);
            }
        }

        if (requestCode == READER_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    int bookState = data.getBundleExtra(RETURN_KEY).getInt(CURRENT_PAGE_KEY);
                    Log.d("Debug","bookState= "+Integer.toString(bookState));
                    lastBook = lastReads.get(0);
                    lastBook.setCurrentPage(bookState);
                    lastReads.set(0,lastBook);
                    adapter.notifyItemChanged(0);
                    Log.d("LastBook",lastBook.getTitle());

                }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        editor = sharePref.edit();
        if (lastReads.size()>=1) {
            editor = sharePref.edit();
            editor.putString(lastBookPath1Key, lastReads.get(0).nomFichier);
            editor.putInt(lastBookState1Key, lastReads.get(0).currentPage);

            if (lastReads.size() >= 2) {
                editor.putString(lastBookPath2Key, lastReads.get(1).nomFichier);
                editor.putInt(lastBookState2Key, lastReads.get(1).currentPage);

                if (lastReads.size() >= 3) {
                    editor.putString(lastBookPath3Key, lastReads.get(2).nomFichier);
                    editor.putInt(lastBookState3Key, lastReads.get(2).currentPage);
                }
                else {
                    editor.remove(lastBookPath3Key)
                            .remove(lastBookState3Key);
                }
            }
            else {
                editor.remove(lastBookPath2Key)
                        .remove(lastBookState2Key)
                        .remove(lastBookPath3Key)
                        .remove(lastBookState3Key);
            }
        }
        else {
            editor.remove(lastBookState1Key)
                    .remove(lastBookPath1Key)
                    .remove(lastBookPath2Key)
                    .remove(lastBookPath3Key)
                    .remove(lastBookState2Key)
                    .remove(lastBookState3Key);
        }
        editor.commit();
        Log.d("SaveState", "StateSaved");

        //Appelle la superclasse pour sauvegarder la hiérarchie de view
        super.onSaveInstanceState(outState);
    }
}
