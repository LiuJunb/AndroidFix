package xmg.com.androidfix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.R.attr.password;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText edit_username,edit_password;
    private Button btn_login;
    private String username=null,password=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 修复bug的代码
                 */
                username = edit_username.getText().toString().trim();
                password = edit_password.getText().toString().trim();
                /**
                 * 修复bug的代码
                 */
                if(username.equals("")||password.equals("")){
                    Toast.makeText(MainActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(password.equals("123")&&username.equals("liujun"))
                    Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(MainActivity.this,"用户名与密码出错",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //为了应用重新启动，再次执行MainApplication中onCreate方法中的代码
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
