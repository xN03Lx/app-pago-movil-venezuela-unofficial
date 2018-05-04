package noel.pago_movil_vzla.activities;

import android.support.design.widget.TextInputLayout;
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
                Toast.makeText(PaymentActivity.this,  "cod de banco:" + codeBank +" Number: " + codNumber + phoneNumber+ " cedula: " + dni + " Monto: "+ mount, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
