package com.example.cocktailapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

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

public class MainActivity extends AppCompatActivity {
    private OkHttpClient cliente;
    private ListView lv;
    private ArrayList<String> datos;
    private ArrayList<String> idBebidas;
    private ArrayAdapter<String> adaptador;
    private String server_url;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        logo= findViewById(R.id.imageView);
        cliente = new OkHttpClient();
        lv = (ListView) findViewById(R.id.lv1);
        datos = new ArrayList<String>();
        idBebidas = new ArrayList<String>();
        adaptador = new ArrayAdapter<String>(this, R.layout.estilos, R.id.list_ingredintes,datos);
        lv.setAdapter(adaptador);
        mostrar();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int posicion = i;
                String idBeb=idBebidas.get(posicion);
                Intent intento = new Intent(getApplicationContext(), MainActivity2.class);
                intento.putExtra("id", idBeb);
                startActivity(intento);
            }
        });
    }

    public void mostrar () {
        server_url = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic";
        Request req = new Request.Builder().url(server_url).build();
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
                        JSONArray listaBebidas = jsonObject.getJSONArray("drinks");
                        for (int i=0; i<listaBebidas.length(); i++) {
                            JSONObject bebida = listaBebidas.getJSONObject(i);
                            String nombreBebida = bebida.getString("strDrink");
                            String idBebida = bebida.getString("idDrink");
                            datos.add(nombreBebida);
                            idBebidas.add(idBebida);
                        }
                        runOnUiThread(() -> {
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