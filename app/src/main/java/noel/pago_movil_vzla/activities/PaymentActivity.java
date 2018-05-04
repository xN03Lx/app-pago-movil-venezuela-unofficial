package noel.pago_movil_vzla.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import noel.pago_movil_vzla.R;
import noel.pago_movil_vzla.resources.Bank;

public class PaymentActivity extends AppCompatActivity {

    MaterialBetterSpinner spinnerBank;
    MaterialBetterSpinner spinnerCodPhone;
    String[] SPINNER_COD_PHONE = {"0412","0424","0412","0426", "0416"};
    private int iconBank;
    private String codeBank;
    private EditText inputDni, inputNumber, inputMount;
    private TextInputLayout inputLayoutDni, inputLayoutNumber, inputLayoutMount;
    private Button btnPaid;
    private String smsMessage;

    private final int SMS_SEND_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        inputDni = (EditText) findViewById(R.id.input_dni);
        inputNumber = (EditText) findViewById(R.id.input_number);
        inputMount = (EditText) findViewById(R.id.input_mount);

        btnPaid = (Button) findViewById(R.id.btn_paid);

        spinnerBank = (MaterialBetterSpinner)findViewById(R.id.spinner_bank);
        ArrayAdapter<String> adapterSpinnerBank = new ArrayAdapter<String>(PaymentActivity.this, android.R.layout.simple_dropdown_item_1line, Bank.banks);
        spinnerBank.setAdapter(adapterSpinnerBank);

        spinnerCodPhone = (MaterialBetterSpinner) findViewById(R.id.spinner_cod_phone);
        ArrayAdapter<String> adapterSpinnerPhone = new ArrayAdapter<String>(PaymentActivity.this, android.R.layout.simple_dropdown_item_1line, SPINNER_COD_PHONE);
        spinnerCodPhone.setAdapter(adapterSpinnerPhone);


        //obtener el codigo del banco a traves del valor del spinner
        spinnerBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String nameBank = spinnerBank.getText().toString();
                for (int i=0; i < Bank.banks.length; i++){
                    if(nameBank.equals(Bank.banks[i])){
                        codeBank = Bank.codes[i];
                    }
                }
                Toast.makeText(getApplicationContext(),  "cod de banco:" + codeBank, Toast.LENGTH_SHORT).show();

            }
        });


        //Accion del boton pagar
        btnPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codNumber = spinnerCodPhone.getText().toString();
                String phoneNumber = inputNumber.getText().toString();
                String mount = inputMount.getText().toString();
                String dni = inputDni.getText().toString();
                smsMessage = "PAGAR " + codeBank + " " + codNumber+phoneNumber+ " " + dni + " " + mount;

                //validacion inputs
                if(codNumber != null &&  !codNumber.isEmpty() && phoneNumber != null && !phoneNumber.isEmpty()
                && mount != null &&  !mount.isEmpty() && dni != null && !dni.isEmpty()){
                    //comprobar version actual de android que estamos corriendo
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (CheckPermission(Manifest.permission.SEND_SMS)){ //Acepto el permiso
                            if (ActivityCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) return;
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.putExtra("address", "2662");
                            smsIntent.putExtra("sms_body", smsMessage);
                            startActivity(smsIntent);

                        }else {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)){  //No se le ha preguntado aun el permiso

                                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_CODE);

                            }else {
                                //ha denagado el permiso
                                Toast.makeText(PaymentActivity.this,  "Por favor establesca el permiso requerido!", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                i.addCategory(Intent.CATEGORY_DEFAULT);
                                i.setData(Uri.parse("package" + getPackageName()));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                startActivity(i);

                            }
                        }

                    }else {
                        OlderVersions();
                    }

                }else{
                    Toast.makeText(PaymentActivity.this,  "Los campos no pueden estar vacios!", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(PaymentActivity.this,  "cod de banco:" + codeBank +" Number: " + codNumber + phoneNumber+ " cedula: " + dni + " Monto: "+ mount, Toast.LENGTH_SHORT).show();
            }

            // Metodo las versiones menores a 6
            private void OlderVersions(){
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                if (CheckPermission(Manifest.permission.SEND_SMS)){
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", "2662");
                    smsIntent.putExtra("sms_body", smsMessage);
                    startActivity(smsIntent);
                } else {
                    Toast.makeText(PaymentActivity.this, "Usted declino el permiso para enviar sms", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //Metodo para comprobar permiso en la nuevas versiones
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //comprobacion de permisos
        switch (requestCode){
            case SMS_SEND_CODE:
                String permission = permissions[0];
                int result = grantResults[0];

                if (permission.equals(Manifest.permission.SEND_SMS)){
                    //comprobacion de estado del permiso
                    if (result == PackageManager.PERMISSION_GRANTED){

                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", "2662");
                        smsIntent.putExtra("sms_body", smsMessage);
                        startActivity(smsIntent);

                    }else {
                        Toast.makeText(PaymentActivity.this, "Usted ha declinado el permiso de enviar sms", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }

    }

    //Comprobar si tenemos un permiso
    private boolean CheckPermission(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
