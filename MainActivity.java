package com.majdalden.al_raadforteachingsupport.ui.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.majdalden.al_raadforteachingsupport.R;
import com.majdalden.al_raadforteachingsupport.controller.AddNewsController;
import com.majdalden.al_raadforteachingsupport.data.ConstantsOfApp;
import com.majdalden.al_raadforteachingsupport.interfaces.AppCallback;
import com.majdalden.al_raadforteachingsupport.model.News;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.AboutFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.AddExamFragment.AddExamFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.AddNewsFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.ChatFragment.ChatFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.EditProfileFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.ExamFragment.ExamFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.MessageFragment.MessageFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.NewsRecyclerView.NewsFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.NotificationsFragment.NotificationsFragment;
import com.majdalden.al_raadforteachingsupport.ui.Fragment.WebPostFragment;

import java.io.IOException;

import cn.jzvd.JZVideoPlayer;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity mainActivity;
    private boolean isReadQrCode = false;
    public static boolean isFinishTimeExam;
    public static boolean isReadDate;
    private boolean newsIsVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isFinishTimeExam = false;
        mainActivity = this;


        newsIsVisible = true;
        TextView toolbar_title_post = toolbar.findViewById(R.id.toolbar_title_post);
        toolbar_title_post.setTypeface(SetFontToView(this, "fonts/droid_arabic_kufi_regular.ttf"));
        toolbar_title_post.setText(R.string.last_session);
        toolbar_title_post.setTextSize(COMPLEX_UNIT_SP, 20);
        toolbar_title_post.setTextColor(Color.WHITE);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (ConstantsOfApp.person != null && ConstantsOfApp.person.getTypeAccount() != null
                && ConstantsOfApp.person.getTypeAccount().equalsIgnoreCase("Teacher")) {
            navigationView.getMenu().findItem(R.id.add_exam_navigation).setVisible(true);
            navigationView.getMenu().findItem(R.id.add_news_navigation).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.add_exam_navigation).setVisible(false);
            navigationView.getMenu().findItem(R.id.add_news_navigation).setVisible(false);
        }

        toolbar.setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDataFromPerson();
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
//        InitialSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (newsIsVisible) {
            InitialSettings();
        }
    }

    private void InitialSettings() {
        if (isReadQrCode) {
            if(WebPostFragment.getInstance() !=null && WebPostFragment.getInstance().isVisible()){
                WebPostFragment.getInstance().RefreshData();
            }else{
                LoadFragment(WebPostFragment.getInstance(), R.id.content_fragment, "");
            }
            isReadQrCode = false;
        } else {
            ConstantsOfApp.newsController.GoToDetails(getIntent());
        }

        LoadDataFromPerson();

    }

    private void LoadDataFromPerson() {
        final CircleImageView civ_photo_profile_nav_bar = findViewById(R.id.civ_photo_profile_nav_bar);

        if (ConstantsOfApp.person != null) {
            if (civ_photo_profile_nav_bar != null) {
                if (ConstantsOfApp.person.getPhotoBitmap() != null) {
                    civ_photo_profile_nav_bar.setImageBitmap(ConstantsOfApp.person.getPhotoBitmap());
                } /*else if (ConstantsOfApp.person.getPhotoUri() != null
                        && !ConstantsOfApp.person.getPhotoUri().isEmpty()) {
                    *//*String selectedFilePath = FilePath.getPath(MainActivity.this
                            , Uri.parse(ConstantsOfApp.person.getPhotoUri()));
                    final File file = new File(selectedFilePath);*//*
                    civ_photo_profile_nav_bar.setImageURI(Uri.parse(new File
                            (Uri.parse(ConstantsOfApp.person.getPhotoUri()).getPath()).getPath()));
                }*/ else if (ConstantsOfApp.person.getPhotoUrl() != null
                        && !ConstantsOfApp.person.getPhotoUrl().isEmpty()) {
                    ConstantsOfApp.IsInternetAvailable(this, new AppCallback<Boolean>() {
                        @Override
                        public void callback(Boolean data) {
                            if (data) {
                                ConstantsOfApp.constantsOfApp.GetImageBitmap(MainActivity.this
                                        , ConstantsOfApp.person.getPhotoUrl(),
                                        new AppCallback<Bitmap>() {
                                            @Override
                                            public void callback(Bitmap bitmap) {
                                                if (bitmap != null) {
//                                                    ConstantsOfApp.person.setPhotoUri(civ_photo_profile_nav_bar.getTag().toString());
                                                    civ_photo_profile_nav_bar.setImageBitmap(bitmap);
                                                    ConstantsOfApp.person.setPhotoBitmap(bitmap);
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
            TextView tv_name_nav_bar = findViewById(R.id.tv_name_nav_bar);
            if (tv_name_nav_bar != null) {
                if (ConstantsOfApp.person.getFullName() != null &&
                        !ConstantsOfApp.person.getFullName().equalsIgnoreCase("")) {
                    tv_name_nav_bar.setText(ConstantsOfApp.person.getFullName());

                } else if (ConstantsOfApp.person.getUserName() != null &&
                        !ConstantsOfApp.person.getUserName().equalsIgnoreCase("")) {
                    tv_name_nav_bar.setText(ConstantsOfApp.person.getUserName());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (JZVideoPlayer.backPress()) {
                return;
            }
            if (ExamFragment.getInstance().isVisible() && ExamFragment.getInstance().isExam() && !isFinishTimeExam) {
                AlertDialog.Builder builderAlert = new AlertDialog.Builder(this
                        , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builderAlert.setMessage(getString
                        (R.string.Are_you_sure_you_want_to_finish_the_exam));
                builderAlert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float grade = ConstantsOfApp.examController.GetGrade(ExamFragment.getInstance().rowExamAdapter,
                                ExamFragment.getExam());
                        ConstantsOfApp.examController.SaveGrade(MainActivity.this
                                , ExamFragment.getExam(), grade);
                        isFinishTimeExam = true;
                        MainActivity.getInstance().onBackPressed();
                    }
                });
                builderAlert.setNegativeButton(R.string.no, null);

                builderAlert.show();
            } else if (NewsFragment.getInstance().isVisible()) {
                finish();
            } else {
                super.onBackPressed();
            }
            isFinishTimeExam = false;
            if (getSupportFragmentManager().getBackStackEntryCount() < 1 && !NewsFragment.getInstance().isVisible()) {
                InitialSettings();
            } else {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        final boolean[] statusDrawerLayout = {false};

        if (ExamFragment.getInstance().isVisible() && ExamFragment.getInstance().isExam()) {
            //Theme_DeviceDefault_Light_Dialog_Alert.
            AlertDialog.Builder builderAlert = new AlertDialog.Builder(this
                    , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builderAlert.setMessage(getString
                    (R.string.Are_you_sure_you_want_to_finish_the_exam));
            builderAlert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    float grade = ConstantsOfApp.examController.GetGrade(ExamFragment.getInstance().rowExamAdapter,
                            ExamFragment.getExam());
                    ConstantsOfApp.examController.SaveGrade(MainActivity.this
                            , ExamFragment.getExam(), grade);
                    onOptionsItemSelected(id);
                    statusDrawerLayout[0] = true;
                }
            });
            builderAlert.setNegativeButton(R.string.no, null);

            builderAlert.show();
        } else {
            onOptionsItemSelected(id);
            statusDrawerLayout[0] = true;
        }

        return statusDrawerLayout[0];
    }

    public void onOptionsItemSelected(int id) {
        if (id == R.id.action_notification) {
            this.LoadFragment(NotificationsFragment.getInstance(), R.id.content_fragment, "");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int id = item.getItemId();
        final boolean[] statusDrawerLayout = {false};

        if (ExamFragment.getInstance().isVisible() && ExamFragment.getInstance().isExam() && id != R.id.exam_navigation) {
            AlertDialog.Builder builderAlert = new AlertDialog.Builder(this
                    , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builderAlert.setMessage(getString
                    (R.string.Are_you_sure_you_want_to_finish_the_exam));
            builderAlert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    float grade = ConstantsOfApp.examController.GetGrade(ExamFragment.getInstance().rowExamAdapter,
                            ExamFragment.getExam());
                    ConstantsOfApp.examController.SaveGrade(MainActivity.this
                            , ExamFragment.getExam(), grade);
                    onNavigationItemSelected(id);
                    statusDrawerLayout[0] = true;
                }
            });
            builderAlert.setNegativeButton(R.string.no, null);

            builderAlert.show();
        } else {
            onNavigationItemSelected(id);
            statusDrawerLayout[0] = true;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return statusDrawerLayout[0];
    }

    public void onNavigationItemSelected(int id) {

        if (id == R.id.main_screen_navigation) {
            this.LoadFragment(NewsFragment.getInstance(), R.id.content_fragment, "");
        } else if (id == R.id.add_exam_navigation) {
            this.LoadFragment(AddExamFragment.getInstance(), R.id.content_fragment, "");
        } else if (id == R.id.exam_navigation) {
            if (!ExamFragment.getInstance().isVisible()) {
                ConstantsOfApp.examController.ShowAllExam(MainActivity.this);
            }
//            this.LoadFragment(ExamFragment.getInstance(), R.id.content_fragment, "");
        } else if (id == R.id.read_qr_navigation) {

            if ((ContextCompat.checkSelfPermission(MainActivity.this
                    , Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        ConstantsOfApp.CAMERA_PERMISSIONS_REQUEST);
            } else {
                Intent intentQR = new Intent(MainActivity.this, BarcodeActivity.class);
                startActivity(intentQR);
            }


        } else if (id == R.id.Message_navigation) {
            this.LoadFragment(ChatFragment.getInstance(), R.id.content_fragment, "");
        } else if (id == R.id.add_news_navigation) {
            AddNewsController.uriVideoUpload = null;
            AddNewsController.uriImageUpload = null;
            this.LoadFragment(AddNewsFragment.getInstance(), R.id.content_fragment, "");
        } else if (id == R.id.about_navigation) {
            this.LoadFragment(AboutFragment.getInstance(), R.id.content_fragment, "");
        }

    }

    public void LoadFragment(Fragment fragment, int contentFragment, String Tag) {

        if (fragment != null) {
            if (!fragment.isVisible()) {

                if (fragment instanceof NewsFragment) {
                    newsIsVisible = true;
                } else {
                    newsIsVisible = false;
                }
                if (getSupportFragmentManager().getBackStackEntryCount() > 1 && fragment instanceof ChatFragment) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    if (!(fragment instanceof MessageFragment)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                        getSupportFragmentManager().popBackStack();
                    }
                    getSupportFragmentManager().popBackStack();
                }
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.replace(contentFragment, fragment);
                    fragmentTransaction.addToBackStack(fragment.toString() + System.currentTimeMillis());
                    fragmentTransaction.commit();
                }
            }
        }

    }

    public void EditProfile(View view) {
        LoadFragment(EditProfileFragment.getInstance(), R.id.content_fragment, "EditProfileFragment");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public static Typeface SetFontToView(Context context, String fontPath) {
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && !data.equals(null)
                && data.getData() != null && !data.getData().equals(null)) {
            if (requestCode == ConstantsOfApp.GET_VIDEO_INTENT_ADD_NEWS) {

                AddNewsController.uriVideoUpload = null;
                AddNewsController.uriVideoUpload = data.getData();

            } else if (requestCode == ConstantsOfApp.GET_IMAGE_INTENT_ADD_NEWS) {

                AddNewsController.uriImageUpload = null;
                AddNewsController.uriImageUpload = data.getData();
            } else if (requestCode == ConstantsOfApp.GET_IMAGE_INTENT_ADD_EXAM) {

                Uri uri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    AddExamFragment.getInstance().getMPagerAdapter()
                            .getRowAddExamFragments().get(AddExamFragment.getInstance()
                            .getMPager().getCurrentItem()).SetImageToImageView(bitmap);
                    AddExamFragment.getInstance().getMPagerAdapter()
                            .getRowAddExamFragments().get(AddExamFragment.getInstance()
                            .getMPager().getCurrentItem()).setUriImage(uri);
                    AddExamFragment.getInstance().getMPagerAdapter()
                            .notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == ConstantsOfApp.GET_IMAGE_INTENT_EDIT_PROFILE) {

                Uri uri = data.getData();
                EditProfileFragment.getInstance().SetUriPhoto(uri);

            } else if (requestCode == ConstantsOfApp.GET_VIDEO_INTENT_MESSAGE) {
                Uri uri = data.getData();
                if (uri != null && uri.getPath().length() > 0) {
                    MessageFragment.getInstance().SendMessage("", "", uri.toString());
                }
            } else if (requestCode == ConstantsOfApp.GET_IMAGE_INTENT_MESSAGE) {

                Uri uri = data.getData();
                if (uri != null && uri.getPath().length() > 0) {

                    MessageFragment.getInstance().SendMessage("", uri.toString(), "");
                }
            }
        } else if (requestCode == ConstantsOfApp.GET_VIDEO_INTENT_ADD_NEWS) {
            AddNewsController.uriVideoUpload = null;
        } else if (requestCode == ConstantsOfApp.GET_IMAGE_INTENT_ADD_NEWS) {
            AddNewsController.uriImageUpload = null;
        } else if (requestCode == ConstantsOfApp.GET_IMAGE_INTENT_ADD_EXAM) {
            AddExamFragment.getInstance().getMPagerAdapter()
                    .getRowAddExamFragments().get(AddExamFragment.getInstance()
                    .getMPager().getCurrentItem()).SetImageToImageView(null);
            AddExamFragment.getInstance().getMPagerAdapter()
                    .notifyDataSetChanged();
            AddExamFragment.getInstance().getMPagerAdapter()
                    .getRowAddExamFragments().get(AddExamFragment.getInstance()
                    .getMPager().getCurrentItem()).setUriImage(null);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantsOfApp.CAMERA_PERMISSIONS_REQUEST: {
                if (grantResults != null && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intentQR = new Intent(MainActivity.this, BarcodeActivity.class);
                    startActivity(intentQR);
                } else {
                    Toast.makeText(MainActivity.this
                            , R.string.to_read_QR_please_taking_permissions_use_camera, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

    }

    public void StartWebPost(News news) {
        WebPostFragment.getInstance().setNews(news);
        isReadQrCode = true;
    }


    public static MainActivity getInstance() {
        return mainActivity;
    }

}
