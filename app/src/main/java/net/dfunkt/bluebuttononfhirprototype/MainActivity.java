package net.dfunkt.bluebuttononfhirprototype;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.hl7.fhir.instance.model.Patient;

import java.util.logging.Logger;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;

public class MainActivity extends AppCompatActivity {

    private static final FhirContext FHIR_DSTU_2_CONTEXT = FhirContext.forDstu2Hl7Org();
    //private static final String SERVER_BASE_URL = "http://localhost:8080/hapi-fhir-jpaserver-example/baseDstu2";
    private static final String SERVER_BASE_URL = "http://bluebuttonhapi-test.hhsdevcloud.us/baseDstu2";
    private static final IGenericClient RESTFUL_CLIENT = FHIR_DSTU_2_CONTEXT.newRestfulGenericClient(SERVER_BASE_URL);
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName(), null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog progressDialog;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = ProgressDialog.show(MainActivity.this, null, "Retrieving data from server", true, false);
                    }
                    @Override
                    protected Void doInBackground(Void... params) {
                        Patient patient = RESTFUL_CLIENT
                                .read()
                                .resource(Patient.class)
                                .withId("147462")
                                .execute();
                        System.out.println("patient =" + patient + "=");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progressDialog.dismiss();
                    }
                }.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
