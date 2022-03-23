package com.miguelcabezas.tfm.saltour;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {

  private DrawerLayout drawerLayout;
  private FirebaseAuth mAuth;
  TextView nombreUsuario;
  private static final int PICK_IMAGE = 100;
  Uri imageUri;

  // Banderas que indicarán si tenemos permisos
  private boolean tienePermisoCamara = false,
          tienePermisoAlmacenamiento = false;
  private static final int CODIGO_PERMISOS_CAMARA = 1,
          CODIGO_PERMISOS_ALMACENAMIENTO = 2;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    mAuth = FirebaseAuth.getInstance();
    Toast.makeText(getApplicationContext(),"Bienvenido/a"+mAuth.getCurrentUser().getDisplayName(),Toast.LENGTH_LONG).show();
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


    verificarYPedirPermisosDeAlmacenamiento();


    drawerLayout = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.navigation_view);
    navigationView.setNavigationItemSelectedListener(this);

    MenuItem menuItem = navigationView.getMenu().getItem(0);
    onNavigationItemSelected(menuItem);
    menuItem.setChecked(true);

    drawerLayout.addDrawerListener(this);

    View header = navigationView.getHeaderView(0);
    FirebaseUser currentUser = mAuth.getCurrentUser();
    currentUser.reload();
    nombreUsuario=header.findViewById(R.id.user);
    nombreUsuario.setText(currentUser.getDisplayName().toString());

    header.findViewById(R.id.header_title).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(HomeActivity.this, getString(R.string.title_click),
                Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
    int title;

    /*En función de la opción de menú elegida se desplegará un fragment o directamente se realizará la acción (pop-up de cerrar sesión en dicho caso)*/

    switch (menuItem.getItemId()) {
      case R.id.nav_inicio:
        title = R.string.menu_home;
        break;
      case R.id.nav_jugar:
        title = R.string.menu_jugar;
        break;
      case R.id.nav_perfil:
        title = R.string.menu_perfil;
        break;
      case R.id.nav_compartir:
        title = R.string.menu_compartir;
        break;
      case R.id.nav_ayuda:
        title = R.string.menu_ayuda;
        break;
      case R.id.nav_ranking:
        title = R.string.menu_ranking;
        break;
      case R.id.nav_salir:
        title = R.string.menu_salir;
        break;
      default:
        throw new IllegalArgumentException("menu option not implemented!!");
    }

    Fragment fragment = HomeContentFragment.newInstance(getString(title),mAuth.getCurrentUser().getDisplayName());
    getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
            .replace(R.id.home_content, fragment)
            .commit();

    setTitle(getString(title));

    drawerLayout.closeDrawer(GravityCompat.START);

    return true;
  }

  @Override
  public void onDrawerSlide(@NonNull View view, float v) {
    //cambio en la posición del drawer
  }

  @Override
  public void onDrawerOpened(@NonNull View view) {
    //el drawer se ha abierto completamente
   /* Toast.makeText(this, getString(R.string.navigation_drawer_open),
            Toast.LENGTH_SHORT).show();*/
  }

  @Override
  public void onDrawerClosed(@NonNull View view) {
    //el drawer se ha cerrado completamente
  }

  @Override
  public void onDrawerStateChanged(int i) {
    //cambio de estado, puede ser STATE_IDLE, STATE_DRAGGING or STATE_SETTLING
  }

  public void mandarCorreo(View view){
    String cuerpoCorreo="A doubt";


    String email="miguelcabezaspuerto@gmail.com";
    Intent emailIntent = new Intent(Intent.ACTION_SEND);
    emailIntent.setType("text/html");
    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FaQ SalTour");
    emailIntent.putExtra(Intent.EXTRA_TEXT,cuerpoCorreo);

    try {
      view.getContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    } catch (android.content.ActivityNotFoundException ex) {
      Toast msg= Toast.makeText(this,"Sin cliente de correo",Toast.LENGTH_LONG);
      msg.setGravity(Gravity.CENTER, 0, 0);
      msg.show();
    }
  }

  public void onClickImagen(View view){
    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    startActivityForResult(gallery, PICK_IMAGE);
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
      imageUri = data.getData();


      String [] proj={MediaStore.Images.Media.DATA};
      Cursor cursor = managedQuery( imageUri,
              proj, // Which columns to return
              null,       // WHERE clause; which rows to return (all rows)
              null,       // WHERE clause selection arguments (none)
              null); // Order-by clause (ascending by name)
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();

      String path= cursor.getString(column_index);



      FirebaseUser currentUser = mAuth.getCurrentUser();

      FirebaseStorage storage = FirebaseStorage.getInstance();
      StorageReference storageRef = storage.getReference();
      StorageReference userImageRef = storageRef.child("images/"+ currentUser.getEmail().toString()+".jpg");
      Uri file =  Uri.fromFile(new File(path));
      UploadTask uploadTask = userImageRef.putFile(file);
      uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
          Toast.makeText(getApplicationContext(),"FAIL IMAGE",Toast.LENGTH_LONG).show();
        }
      }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
          // ...
          Toast.makeText(getApplicationContext(),"SUCCESS IMAGE",Toast.LENGTH_LONG).show();
        }
      });
    }

  }


  private void verificarYPedirPermisosDeAlmacenamiento() {
    int estadoDePermiso = ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
      // En caso de que haya dado permisos ponemos la bandera en true
      // y llamar al método
      permisoDeAlmacenamientoConcedido();
    } else {
      // Si no, entonces pedimos permisos. Ahora mira onRequestPermissionsResult
      ActivityCompat.requestPermissions(HomeActivity.this,
              new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
              CODIGO_PERMISOS_ALMACENAMIENTO);
    }
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case CODIGO_PERMISOS_CAMARA:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          permisoDeCamaraConcedido();
        } else {
          permisoDeCamaraDenegado();
        }
        break;

      case CODIGO_PERMISOS_ALMACENAMIENTO:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          permisoDeAlmacenamientoConcedido();
        } else {
          permisoDeAlmacenamientoDenegado();
        }
        break;

    }
  }

  private void permisoDeAlmacenamientoConcedido() {
    tienePermisoAlmacenamiento = true;
  }

  private void permisoDeAlmacenamientoDenegado() {

    Log.d("DENEGADO","ALMACENAMIENTO");
  }

  private void permisoDeCamaraConcedido() {
    tienePermisoCamara = true;
  }

  private void permisoDeCamaraDenegado() {
    Log.d("DENEGADO","CÁMARA");
  }

}
