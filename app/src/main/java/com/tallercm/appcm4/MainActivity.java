package com.tallercm.appcm4;

import android.content.DialogInterface;
import android.media.MediaDrm;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.tallercm.appcm4.model.Cursos;

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

    private ByteArrayInputStream inputStream;
    private ArrayList<Cursos> datos = new ArrayList<Cursos>();
    private ListView lvListaCursos;
    int[] images = {R.drawable.sql, R.drawable.excelcalc, R.drawable.macro, R.drawable.java, R.drawable.php, R.drawable.hack, R.drawable.programming, R.drawable.word, R.drawable.photoshop, R.drawable.mysql};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                URL sourceURL = new URL("https://serverbpw.com/cm/2019-2/cursos.php");

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
                        Log.d("DATO", "nombre: " + obtenValor("nombre",elemento2));
                        Log.d("DATO", "tipo: " + obtenValor("sede",elemento2));
                        Log.d("DATO", "fechai: " + obtenValor("fechainic",elemento2));
                        Log.d("DATO", "fechaf: " + obtenValor("fechafin",elemento2));
                        Cursos curso = new Cursos(Long.valueOf(obtenValor("id",elemento2)),
                                obtenValor("nombre",elemento2),
                                obtenValor("sede",elemento2),
                                obtenValor("fechainic", elemento2),
                                obtenValor("fechafin", elemento2));
                        datos.add(curso);
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
            if(!sinError){
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

            }else{
                try {
                    lvListaCursos = findViewById(R.id.lvListaCursos);
                    Adaptador adaptador = new Adaptador(MainActivity.this, datos, images);
                    lvListaCursos.setAdapter(adaptador);
                    Toast.makeText(MainActivity.this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show();
                } catch (Exception ex){
                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }
    }

    private static String obtenValor(String tag, Element elemento) {
        NodeList listaNodos = elemento.getElementsByTagName(tag).item(0).getChildNodes();
        Node nodo = listaNodos.item(0);
        return nodo.getNodeValue();
    }
}
