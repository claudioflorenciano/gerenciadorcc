package br.gerenciadordecontacorrente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Correntista correntista;
    private EditText inputConta;
    private EditText inputSenha;
    private Controller ctrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        inputConta = (EditText) findViewById(R.id.inputConta);
        inputSenha = (EditText) findViewById(R.id.inputSenha);
    }

    @Override
    protected void onResume() {
        super.onResume();

        inputConta.setText("");
        inputSenha.setText("");
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menuNovoUsu:
                Intent cadastro = new Intent(MainActivity.this, ActivityNovoUser.class);
                startActivity(cadastro);
                break;

            case R.id.menuFechar:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btnLogarClick(View view){
        String contaCorrente = inputConta.getText().toString();
        String senha = inputSenha.getText().toString();
        ctrl = new Controller(MainActivity.this);

        String erros = ctrl.userValidation(contaCorrente, senha);

        if(erros.equals("")) {
            if (ctrl.checkAuth(contaCorrente, senha)) {
                correntista = ctrl.pickCorrentista(contaCorrente, senha);
                Conta conta = ctrl.pickAccount(correntista);

                Intent activityCorrentista = new Intent(MainActivity.this, ActivityCorrentista.class);

                activityCorrentista.putExtra("conta", conta);

                startActivity(activityCorrentista);

                Toast.makeText(MainActivity.this, "Login efetuado!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Conta Inexistente, crie uma conta!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, erros, Toast.LENGTH_SHORT).show();
        }

    }
}