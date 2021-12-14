package br.gerenciadordecontacorrente;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class ActivityNovoUser extends AppCompatActivity {

    private EditText contaCorrente;
    private EditText senhaConta;
    private Switch swtStatus;

    Controller ctrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_user);

        contaCorrente = (EditText) findViewById(R.id.inputNovoConta);
        senhaConta = (EditText) findViewById(R.id.inputNovoSenha);
        swtStatus = (Switch) findViewById(R.id.swtVip);
    }



    public void btnCadastrarClick(View view){
        ctrl = new Controller(ActivityNovoUser.this);

        boolean status = swtStatus.isChecked();
        String conta = contaCorrente.getText().toString();
        String senha = senhaConta.getText().toString();

        String erros = ctrl.userValidation(conta, senha);

        if(erros.equals("")) {
            if (ctrl.checkAuth(conta)) {
                //usuario cadastrado
                Toast.makeText(ActivityNovoUser.this, "Conta já cadastrada!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                //cadastrar
                if (ctrl.signIn(conta, senha)) {
                    Correntista correntista = ctrl.pickCorrentista(conta, senha);

                    if (ctrl.createUserAccount(correntista, status)) {
                        Toast.makeText(ActivityNovoUser.this, "Conta cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ActivityNovoUser.this, "Erro ao cadastrar a conta!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityNovoUser.this, "Erro ao cadastrar o correntista!", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(ActivityNovoUser.this, erros, Toast.LENGTH_SHORT).show();
        }
    }

    public void btnVoltarClick(View view){
        finish();
    }
}