package com.guillaume.aurore.comiquebouc;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**  Activité qui gère le reader.
 *  Contient les sous classes MyAdapter et MyFragment
 *
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class ReaderActivity extends FragmentActivity{
    // Variables statiques
    static int NUM_ITEMS;    // Nombre de pages simples dans le reader
    static int NUM_ITEMS2;   // Nombre de doubles pages dans le reader
    static int NB_PAGES;     // donne le nombre de pages que l'on veut afficher par écran
    static int CURRENT_PAGE;  // page d'ouverture de l'activité
    static final String CURRENT_PAGE_KEY = "page actuelle";
    final String RETURN_KEY = "com.guillaume.aurore.comiquebouc.return_data";
    private static final int READER_REQUEST_CODE = 2;
    static Boolean REVERSE_MODE;


    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    private static String SHARED_PREFERENCE_KEY = "com.guillaume.aurore.comiquebouc.preferences";
    private static String NB_PAGE_KEY = "nb_page";
    private static String REVERSE_MODE_KEY = "reverse_mode";

    // déclarations
    static ViewPager viewComic;
    static MyAdapter comicAdapter;
    static Book livre;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /** méthode appelée à la création de l'activité.
         *
         *  Récupère l'intent de lecture, ouvre le livre et lancer le pager adapter.
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        //Récupération de l'intent
        Intent intent = getIntent();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_KEY,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        NB_PAGES = sharedPreferences.getInt(NB_PAGE_KEY,1);
        REVERSE_MODE = sharedPreferences.getBoolean(REVERSE_MODE_KEY,false);

        //On extrait le path de l'Uri renvoyé par l'intent
        livre = new Book(intent.getData().getPath());
        livre.setCurrentPage(intent.getIntExtra(CURRENT_PAGE_KEY,0));
        Toast.makeText(this,"currentPage = "+Integer.toString(livre.currentPage),Toast.LENGTH_SHORT).show();


        NUM_ITEMS = livre.tailleLivre ;
        NUM_ITEMS2 = (int) Math.ceil(livre.tailleLivre*1.0/2);

        // mode de lecture japonaise
        if (REVERSE_MODE) {
            livre.toggleReverseMode();
            CURRENT_PAGE = NUM_ITEMS - livre.currentPage -1;
        }
        else {
            CURRENT_PAGE = livre.currentPage;
        }


        // création du livre et du pager adapter
        viewComic = (ViewPager) findViewById(R.id.pager);
        comicAdapter = new MyAdapter(getSupportFragmentManager());
        viewComic.setAdapter(comicAdapter);
        viewComic.setCurrentItem(CURRENT_PAGE/NB_PAGES);

    }


    //Renvoie les infos de lecture au homescreen
    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        if (NB_PAGES==1) {
            if (REVERSE_MODE) {
                bundle.putInt(CURRENT_PAGE_KEY,NUM_ITEMS - viewComic.getCurrentItem() -1);
                Log.d("1Rev",Integer.toString(viewComic.getCurrentItem()));
            }
            else {
                bundle.putInt(CURRENT_PAGE_KEY,viewComic.getCurrentItem());
                Log.d("1Nor",Integer.toString(viewComic.getCurrentItem()));
            }
        }
        else {
            if (REVERSE_MODE) {
                bundle.putInt(CURRENT_PAGE_KEY, NUM_ITEMS - viewComic.getCurrentItem()*2 -1);
                Log.d("2Rev",Integer.toString(viewComic.getCurrentItem()));
            }
            else {
                bundle.putInt(CURRENT_PAGE_KEY,viewComic.getCurrentItem()*2);
                Log.d("3Rev",Integer.toString(viewComic.getCurrentItem()));
            }
        }

        Intent mIntent = new Intent();
        mIntent.putExtra(RETURN_KEY,bundle);
        setResult(RESULT_OK, mIntent);
        super.onBackPressed();
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {
        /** Classe de FragmentStatePagerAdapter:
         *  définit un pager adapter efficace pour gérer l'affichage de nombreuses pages.
         *   -> seuls 3 fragments gardés en mémoire à la fois dans ce pager adapter.
         */

        public static int pos = 0;


        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        @Override
        public ImageFragment getItem(int position) {
            return ImageFragment.newInstance(position);
        }

        public static int getPosition(){
            return pos;
        }

        public static void setPosition(int i){
            MyAdapter.pos=i;
        }

        @Override
        public int getCount() {
            if (NB_PAGES==1) {
                return NUM_ITEMS;
            }
            else {
                return NUM_ITEMS2;
            }
        }

    }

    public static class ImageFragment extends Fragment {
        /** Classe pour définir nos fragments.
         *  -> des bitmap, les pages du book.
         */

        public Bitmap mImage;
        private int mImageNum;   // position dans le pager
        public TouchImageView mImageView;

        static ImageFragment newInstance(int num) {

            ImageFragment f = new ImageFragment();

            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            /**  Méthode appelée à la création de la view du fragment (fragment courant)
             *   appelle l'inflater sur le layout XML correspondant et rempli la view avec la page
             *   et le textView avec le numéro de page
             *   Plusieurs listener sur tous les boutons de navigation
             */

            View v = (View) inflater.inflate(
                    R.layout.fragment_pager_list, container, false);

            mImageView = (TouchImageView) v.findViewById(R.id.trololo);
            mImageView.setImageBitmap(mImage);

            // récupère toutes les views
            TextView tv = (TextView) v.findViewById(R.id.text);
            ImageView tabView= (ImageView) v.findViewById(R.id.tab);
            ImageView goLeft = (ImageView) v.findViewById(R.id.goLeft);
            ImageView goRight = (ImageView) v.findViewById(R.id.goRight);
            ImageView jap = (ImageView) v.findViewById(R.id.jap_chgt);

            // listener sur le bouton pour passer en mode japonais (lecture de droite à gauche) / revenir en lecture classique
            jap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (REVERSE_MODE) {
                        REVERSE_MODE = false;
                        editor.putBoolean(REVERSE_MODE_KEY, false);
                        editor.commit();
                        livre.toggleReverseMode();
                        if (NB_PAGES==1) {
                            viewComic.setCurrentItem(NUM_ITEMS - mImageNum-1, true);
                        }
                        else {
                            viewComic.setCurrentItem(NUM_ITEMS2 - mImageNum -1, true);
                        }
                    }
                    else {
                        REVERSE_MODE = true;
                        editor.putBoolean(REVERSE_MODE_KEY, true);
                        editor.commit();
                        livre.toggleReverseMode();

                        if (NB_PAGES==1) {
                            viewComic.setCurrentItem(NUM_ITEMS - mImageNum-1, true);
                        }
                        else {
                            viewComic.setCurrentItem(NUM_ITEMS2 - mImageNum -1 , true);
                        }
                    }
                }
            });

            if (NB_PAGES==1) {

                // change to 1 page mode to 2 pages mode
                tabView.setImageResource(R.drawable.deuxpages);

                if (REVERSE_MODE) {
                    tv.setText("Page " + Integer.toString(NUM_ITEMS - mImageNum));
                }
                else {
                    tv.setText("Page " + Integer.toString(mImageNum + 1));
                }

                tabView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.img_clicked));
                        NB_PAGES = 2;
                        editor.putInt(NB_PAGE_KEY, NB_PAGES);
                        editor.commit();
                        int n = (int) mImageNum / 2;
                        comicAdapter.notifyDataSetChanged();
                        viewComic.setCurrentItem(n, false);
                    }
                });

                // Go first page on long click on goLeft button
                goLeft.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        viewComic.setCurrentItem(0, true);
                        return true;
                    }
                });

                // Go last page on long click on goRight button
                goRight.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        viewComic.setCurrentItem(NUM_ITEMS, true);
                        return true;
                    }
                });

                // Go previous page on click on goLeft button
                goLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mImageNum > 0) {
                            // v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.img_clicked));
                            viewComic.setCurrentItem(mImageNum - 1, true);
                        }
                    }
                });

                // Go next page on goRight button
                goRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mImageNum < NUM_ITEMS) {
                            // v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.img_clicked));
                            viewComic.setCurrentItem(mImageNum + 1, true);
                        }
                    }
                });


            }
            else {
                // change to 2 page mode to 1 page mode
                tabView.setImageResource(R.drawable.unepage);
                if (REVERSE_MODE) {
                    tv.setText("Page " + Integer.toString(NUM_ITEMS - mImageNum*2));
                }
                else {
                    tv.setText("Page " + Integer.toString(mImageNum * 2 + 1));
                }

                tabView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.img_clicked));
                        NB_PAGES=1;
                        editor.putInt(NB_PAGE_KEY,NB_PAGES);
                        editor.commit();
                        comicAdapter.notifyDataSetChanged();

                        viewComic.setCurrentItem(mImageNum*2, false);
                    }
                });

                // Go first page on long click on goLeft button
                goLeft.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        viewComic.setCurrentItem(0, true);
                        return true;

                    }

                });

                // Go last page on long click on goRight button
                goRight.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        viewComic.setCurrentItem(NUM_ITEMS2, true);
                        return true;

                    }

                });

                // Go previous page on click on goLeft button
                goLeft.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mImageNum>0) {
                            // v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.img_clicked));
                            viewComic.setCurrentItem(mImageNum - 1, true);
                        }

                    }
                });

                // Go next page on goRight button
                goRight.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mImageNum<NUM_ITEMS) {
                            // v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.img_clicked));
                            viewComic.setCurrentItem(mImageNum + 1, true);
                        }

                    }
                });

            }

            return v;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            /** Méthode appelée à la création du fragment
             *  donc le fragment n+1 quand le fragment n devient le fragment courant.
             *  le fragment n-2 est lui supprimé (si on allait vers la droite)
             */

            super.onCreate(savedInstanceState);
            mImageNum = getArguments() != null ? getArguments().getInt("num") : 1;

            if (NB_PAGES==2) {
                // On 2 pages mode: create a bitmap with the 2 pages by concatenating them
                if (mImageNum*2+1<NUM_ITEMS2*2) {
                    Bitmap bmp1 = livre.Lecteur.syncRead(mImageNum * 2, livre, 512);
                    Bitmap bmp2;
                    if (mImageNum*2+1  < NUM_ITEMS) {
                        bmp2 = livre.Lecteur.syncRead(1 + mImageNum * 2, livre, 512);
                        int height = bmp1.getHeight();
                        int width = bmp1.getWidth() + bmp2.getWidth();
                        mImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        Canvas cbImage = new Canvas(mImage);
                        cbImage.drawBitmap(bmp1, 0f, 0f, null);
                        cbImage.drawBitmap(bmp2, bmp1.getWidth(), 0f, null);
                    } else {
                        mImage=bmp1;
                    }

                }
            }
            else {
                // 1 page mode: open only the right page
                mImage=livre.Lecteur.syncRead(mImageNum, livre, 1024);
            }


        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }
    }



}