package com.example.panaderia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText jetcodigo,jetnombre,jetprecio;
    RadioButton jrbcobertura,jrbbizcocho,jrbadicionales;
    CheckBox jcbdisponible;
    boolean respuesta;
    String codigo,nombre,precio,uso,disponible,ident_doc;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Ocultar barra
        getSupportActionBar().hide();
        jetprecio=findViewById(R.id.etprecio);
        jetnombre=findViewById(R.id.etnombre);
        jetcodigo=findViewById(R.id.etcodigo);
        jcbdisponible=findViewById(R.id.cbdisponible);
        jrbbizcocho=findViewById(R.id.rbbizcocho);
        jrbadicionales=findViewById(R.id.rbadicionales);
        jrbcobertura=findViewById(R.id.rbcobertura);
    }
    public void Guardar(View view){
        codigo=jetcodigo.getText().toString();
        nombre=jetnombre.getText().toString();
        precio=jetprecio.getText().toString();
        if (codigo.isEmpty() || nombre.isEmpty() || precio.isEmpty()){
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else{
            if(jrbbizcocho.isChecked()) {
                uso="Bizcocho";
            }
            else {
                if (jrbcobertura.isChecked()) {
                    uso="Cobertura";
                }
                else{
                    uso="Adicionales";
                }
            }
            // Create a new user with a first and last name
            Map<String, Object> producto = new HashMap<>();
            producto.put("Codigo", codigo);
            producto.put("Nombre",nombre);
            producto.put("Precio",precio);
            producto.put("Uso", uso);
            producto.put("Disponible","si");

            // Add a new document with a generated ID
            db.collection("Inventario")
                    .add(producto)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(MainActivity.this, "Producto Guardado", Toast.LENGTH_SHORT).show();
                            Limpiar();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error adding document", e);
                            Toast.makeText(MainActivity.this, "Error Guardando Producto", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void Buscar(View view){
        respuesta=false;
        codigo=jetcodigo.getText().toString();
        if(codigo.isEmpty()){
            Toast.makeText(this, "Codigo requerido", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else{
            db.collection("Inventario")
                    .whereEqualTo("Codigo",codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    respuesta=true;
                                    if(document.getString("Disponible").equals("no")){
                                        Toast.makeText(MainActivity.this, "El producto existe pero no esta disponible", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        ident_doc=document.getId();//captura id donde esta el codigo
                                        jetnombre.setText(document.getString("Nombre"));
                                        jetprecio.setText(document.getString("Precio"));
                                        if(document.getString("Uso").equals("Cobertura")){
                                            jrbcobertura.setChecked(true);
                                        }
                                        else {
                                            if(document.getString("Uso").equals("Bizcocho")){
                                                jrbbizcocho.setChecked(true);
                                            }
                                            else{
                                                jrbadicionales.setChecked(true);
                                            }
                                        }
                                        //si esta disponible o no
                                        if (document.getString("Disponible").equals("si")){
                                            jcbdisponible.setChecked(true);
                                            disponible="si";
                                        }
                                        else{
                                            jcbdisponible.setChecked(false);
                                            disponible="no";
                                        }
                                    }
                                }
                            } else {
                                //Log.w(TAG, "Error getting documents.", task.getException());
                                //Toast.makeText(MainActivity.this, "No encontrado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    public void Cancelar(View view)
    {
        Limpiar();
    }
    private void Limpiar(){
        jetcodigo.setText("");
        jetnombre.setText("");
        jetprecio.setText("");
        jrbcobertura.setChecked(true);
        jcbdisponible.setChecked(false);
        respuesta=false;
        jetcodigo.requestFocus();
    }
}