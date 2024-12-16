package com.example.cocktailapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {

    private TextView titulo;
    private ListView lv2;
    private ArrayList<String> datos;
    private ArrayAdapter<String> adaptador;
    private String server_url;
    private String idBebida;
    private OkHttpClient cliente;
    private TextView instrucciones;
    private ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cliente = new OkHttpClient();
        titulo = findViewById(R.id.tvNombre);
        foto = findViewById(R.id.ivFoto);
        instrucciones = findViewById(R.id.tvInstru);
        lv2 = (ListView) findViewById(R.id.lv2);
        datos = new ArrayList<String>();
        adaptador = new ArrayAdapter<String>(this, R.layout.estilo2, R.id.lis_ingredientes,datos);
        lv2.setAdapter(adaptador);
        idBebida = getIntent().getStringExtra("id");
        mostrar();
    }

    public void mostrar () {
        server_url = "https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=";
        String req_url = server_url+idBebida;
        Request req = new Request.Builder().url(req_url).build();
        cliente.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("No hay respuesta");
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray bebidas = jsonObject.getJSONArray("drinks");
                        JSONObject bebida = bebidas.getJSONObject(0);
                        String nombre = bebida.getString("strDrink");
                        String instru = bebida.getString("strInstructions");
                        String imageUrl = bebida.getString("strDrinkThumb");

                        for (int i=0; i<15;i++) {
                            String numIngrediente = "strIngredient"+(i+1);
                            String ingrediente = bebida.getString(numIngrediente);
                            if (ingrediente!="null") {
                                String cantidad = "strMeasure"+(i+1);
                                String cant = bebida.getString(cantidad);
                                datos.add((i+1)+":  "+ingrediente+ "     --->   "+cant);
                            }
                        }
                        runOnUiThread(() -> {
                            Glide.with(getApplicationContext()).load(imageUrl).circleCrop().into(foto);
                            titulo.setText(nombre);
                            instrucciones.setText(instru);
                            adaptador.notifyDataSetChanged();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}