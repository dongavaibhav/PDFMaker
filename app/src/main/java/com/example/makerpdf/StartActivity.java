package com.example.makerpdf;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.material.navigation.NavigationView;
import com.itextpdf.text.BaseColor;

import yuku.ambilwarna.AmbilWarnaDialog;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button createPdf;
    private Button selectedAll;
    NavigationView navigationView;
    private int currentColor;
    private RelativeLayout relativeLayout;
    private BaseColor colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        currentColor = ContextCompat.getColor(this, R.color.colorAccent);

        createPdf = (Button) findViewById(R.id.create_Pdf);
        selectedAll = (Button) findViewById(R.id.selected_all);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, ChooseActivity.class);
                startActivity(intent);
            }
        });

        selectedAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(StartActivity.this, GeneratedPdfs.class), 0);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.title_text)).setText("Image To PDF");

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle r0 = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);

            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };
        drawerLayout.addDrawerListener(r0);
        r0.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        displaySelectedScreen(menuItem.getItemId());
        return true;
    }

    private void displaySelectedScreen(int i) {
        switch (i) {
            case R.id.generated_pdfs:
                startActivity(new Intent(StartActivity.this, GeneratedPdfs.class));
                break;
            case R.id.aboutus:
                aboutUs();
                break;
            case R.id.background:
                openDialog(true);
                break;
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
    }

    public void aboutUs() {
        new BottomDialog.Builder(this)
                .setTitle("About Us!")
                .setContent("We invite developers of PDF solutions; companies that work with PDF in document management and enterprise content management; interested individuals; and users who want to advance their implementation of PDF technology in their organizations, to join, learn from and contribute to our efforts.")
                .setPositiveText("OK")
                .setPositiveBackgroundColorResource(R.color.colorPrimary)
                .setPositiveTextColorResource(android.R.color.white)
                .onPositive(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(BottomDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void openDialog(boolean supportsAlpha) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                Global.getInstance().setColor(new BaseColor(currentColor));
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }
}
