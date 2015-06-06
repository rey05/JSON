package angel.reynaldo.json;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ListActivity {
    private ProgressDialog pDialog;

    //URL del archivo json guardado en un servidor en linea
    private static String url = "http://eulisesrdz.260mb.net/rey/aabbcc.json";

    // Nodos de json que se igualan a los de la base de datos
    static final String KEY_ALUMNOS = "alumnos"; // parent node
    static final String KEY_NOCONTROL = "NoControl"; // parent node
    static final String KEY_NOMBRE = "Nombre";
    static final String KEY_APELLIDOS = "Apellidos";
    static final String KEY_SEMESTRE = "Semestre";
    static final String KEY_CARRERA = "Carrera";
    static final String KEY_EMAIL = "Email";
    static final String KEY_DIRECCION = "Direccion";

    // llamada al arreglo de la tabla alumnos
    JSONArray alumnos = null;

    // Arreglo guardado en en ListView
    ArrayList<HashMap<String, String>> alumnosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Arreglo de la lista de alumnos
        alumnosList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

        //Evento que detecta el elemento del ListView que es presionado
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected ListItem

                //Declaracion de variables para cada dato de cada elemento del ListView
                String nc = ((TextView) view.findViewById(R.id.nc1)).getText().toString();
                String nom = ((TextView) view.findViewById(R.id.n1)).getText().toString();
                String ap = ((TextView) view.findViewById(R.id.a1)).getText().toString();
                String sem = ((TextView) view.findViewById(R.id.sem)).getText().toString();
                String car = ((TextView) view.findViewById(R.id.car)).getText().toString();
                String em = ((TextView) view.findViewById(R.id.em)).getText().toString();
                String dir = ((TextView) view.findViewById(R.id.dir)).getText().toString();

                // Evento para almacenar los datos a enviar a la segunda actividad en un putExtra
                Intent in = new Intent(getApplicationContext(), registroindividual.class);
                in.putExtra(KEY_NOCONTROL, nc);
                in.putExtra(KEY_NOMBRE, nom);
                in.putExtra(KEY_APELLIDOS, ap);
                in.putExtra(KEY_SEMESTRE, sem);
                in.putExtra(KEY_CARRERA, car);
                in.putExtra(KEY_EMAIL, em);
                in.putExtra(KEY_DIRECCION, dir);

                //Inicio de la actividad
                startActivity(in);

            }
        });

        // Calling async task to get json
        new GetContacts().execute();
    }

    //Metodo para mostrar la carga del archivo y sus elementos
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creando instancia del servicio Handler
            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            //Identifica si la llamada del archivo URL es nulo o no
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    //Obteniendo arreglo del archivo JSON
                    alumnos = jsonObj.getJSONArray(KEY_ALUMNOS);

                    //Obteniendo la informacion de todos los contactos mediante un contador
                    for (int i = 0; i < alumnos.length(); i++) {
                        JSONObject c = alumnos.getJSONObject(i);

                        //Llamda de los nodos de la base de datos alumnos
                        String nc = c.getString(KEY_NOCONTROL);
                        String nom = c.getString(KEY_NOMBRE);
                        String ap = c.getString(KEY_APELLIDOS);
                        String sem = c.getString(KEY_SEMESTRE);
                        String car = c.getString(KEY_CARRERA);
                        String em = c.getString(KEY_EMAIL);
                        String dir = c.getString(KEY_DIRECCION);

                        HashMap<String, String> prt = new HashMap<String, String>();

                        //Igualando nodos con las variables que los almacenaran
                        prt.put(KEY_NOCONTROL, nc);
                        prt.put(KEY_NOMBRE, nom);
                        prt.put(KEY_APELLIDOS, ap);
                        prt.put(KEY_SEMESTRE, sem);
                        prt.put(KEY_CARRERA, car);
                        prt.put(KEY_EMAIL, em);
                        prt.put(KEY_DIRECCION, dir);

                        // Agregando cada contacto a una lista
                        alumnosList.add(prt);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            //Adaptador que muestra la ListView con su elemento de la vista correspondiente
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, alumnosList,
                    R.layout.list_alumno, new String[] { KEY_NOCONTROL, KEY_NOMBRE,KEY_APELLIDOS, KEY_SEMESTRE, KEY_CARRERA, KEY_EMAIL, KEY_DIRECCION}, new int[] { R.id.nc1,
                    R.id.n1, R.id.a1, R.id.sem, R.id.car, R.id.em, R.id.dir });
            setListAdapter(adapter);
        }
    }
}
