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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Patient;

import java.util.logging.Logger;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;

public class MainActivity extends AppCompatActivity {

    private static final FhirContext FHIR_DSTU_2_CONTEXT = FhirContext.forDstu2Hl7Org();
    private static final String SERVER_BASE_URL = "http://bluebuttonhapi-test.hhsdevcloud.us/baseDstu2";
    private static final IGenericClient RESTFUL_CLIENT = FHIR_DSTU_2_CONTEXT.newRestfulGenericClient(SERVER_BASE_URL);
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName(), null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, Patient>() {
                    private ProgressDialog progressDialog;
                    private String patientId;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        patientId = ((FloatingLabelEditText)findViewById(R.id.patient_id_edit)).getInputWidgetText().toString();
                        progressDialog = ProgressDialog.show(MainActivity.this, null, "Retrieving data from server", true, false);
                    }
                    @Override
                    protected Patient doInBackground(Void... params) {
                        Patient patient = null;
                        try {
                            patient = RESTFUL_CLIENT
                                    .read()
                                    .resource(Patient.class)
                                    .withId(patientId)
                                    .encodedJson()
                                    .execute();
                        } catch (Exception exception) {
                            LOGGER.severe(exception.toString());
                        }
                        LOGGER.info("patient =" + patient + "=");
                        return patient;
                    }

                    @Override
                    protected void onPostExecute(Patient patient) {
                        super.onPostExecute(patient);
                        progressDialog.dismiss();
                        TextView textView = (TextView)MainActivity.this.findViewById(R.id.patient_view);
                        if(null == patient) {
                            Toast.makeText(MainActivity.this, "No patient with ID '" + patientId + "' found", Toast.LENGTH_LONG).show();
                            textView.setVisibility(View.INVISIBLE);
                        } else {
                            String string = "";
                            for(HumanName name : patient.getName()) {
                                string += "GIVEN NAME: " + name.getGiven().get(0) + "\n";
                                string += "FAMILY NAME: " + name.getFamily().get(0) + "\n";
                            }
                            string += "BIRTH DATE: " + patient.getBirthDate() + "\n";
                            LOGGER.info("string =" + string + "=");
                            textView.setText(string);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                }.execute();
            }
        });
    }
}
