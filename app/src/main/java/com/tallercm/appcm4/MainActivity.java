package com.tallercm.appcm4;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tallercm.appcm4.model.Anime;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    ByteArrayInputStream inputStream;
    ArrayList<Anime> datos = new ArrayList<Anime>();
    ProgressBar pbSpinner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbSpinner1 = findViewById(R.id.pbSpinner1);

        if(savedInstanceState==null){
            new ConexionHttp().execute("");
        }

    }

    public class ConexionHttp extends AsyncTask<String, Float, String> {

        boolean sinError = true;

        @Override
        protected String doInBackground(String... strings) {
            try{
                StringBuffer fileData = new StringBuffer(4096);

                URL sourceURL = new URL("https://cdn.animenewsnetwork.com/encyclopedia/reports.xml?id=155&type=anime&nlist=20m");

                BufferedReader in = new BufferedReader(new InputStreamReader(sourceURL.openStream()));

                String inputLine;

                while((inputLine = in.readLine())!= null){
                    fileData.append(inputLine);
                }

                in.close();

                inputStream = new ByteArrayInputStream(fileData.toString().getBytes());

                DocumentBuilderFactory dbFabrica = DocumentBuilderFactory.newInstance();

                DocumentBuilder dBuilder = dbFabrica.newDocumentBuilder();
                Document doc = dBuilder.parse(inputStream);

                Element elemento = doc.getDocumentElement();
                elemento.normalize();

                NodeList nList = doc.getElementsByTagName("item");

                for (int i=0; i<nList.getLength(); i++) {

                    Node nodo = nList.item(i);
                    if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                        Element elemento2 = (Element) nodo;
                        Log.d("DATO", "id: " + obtenValor("id",elemento2));
                        Log.d("DATO", "nombre: " + obtenValor("name",elemento2));
                        Log.d("DATO", "tipo: " + obtenValor("type",elemento2));
                        Anime animeTmp = new Anime(Long.valueOf(obtenValor("id",elemento2)),
                                obtenValor("name",elemento2),
                                obtenValor("type",elemento2));
                        datos.add(animeTmp);
                    }
                }


            }catch(Exception e){
                e.printStackTrace();
                sinError = false;
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pbSpinner1.setVisibility(View.GONE);
            if(sinError){
                for(int i=0;i<datos.size();i++){
                    Toast.makeText(MainActivity.this, datos.get(i).getTipo(),Toast.LENGTH_SHORT).show();
                }
            }else{
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Aviso")
                        .setMessage("Servicio no disponible en estos momentos.")
                        .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new ConexionHttp().execute("");
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        new ConexionHttp().execute("");
                    }
                }).setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                        .show();

            }
        }
    }

    private static String obtenValor(String tag, Element elemento) {
        NodeList listaNodos = elemento.getElementsByTagName(tag).item(0).getChildNodes();
        Node nodo = listaNodos.item(0);
        return nodo.getNodeValue();
    }
}
